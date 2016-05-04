package Search;


import IRUtilities.*;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.htmlparser.util.ParserException;

public class Spider
{
	
	public Vector<String> child(Vector<String> a, Vector<String> b, int c)
	{
		Vector<String> x = new Vector<String>(0);
		int y = c - a.size();
		for(int i = 0; i<b.size(); i++)
		{
			Crawler d = new Crawler(b.get(i));
			Vector<String> e = d.getClink();
			for(int j=0; j<e.size();j++)
			{
				if(!a.contains(e.get(j)) && !x.contains(e.get(j)))
					x.addElement(e.get(j));
			}
			if(x.size() >= y)
				break;
		}
		
		return x;
	}
	
	public Vector<String> spider(Vector<String> a, Vector<String> b, int c)
	{
		Vector<String> d = b;
		Vector<String> e = a;
		for(int i = 0; i<b.size() && e.size()<c; i++)
		{
			if(!e.contains(b.get(i)))
				e.addElement(b.get(i));
		}
		//d = child(a,d,c);
		//System.out.println(d.size());
		
		while(d.size()!=0 && a.size() < c)
		{
			d = child(e,d,c);
			int g = e.size()+d.size();
			if(g < c)
			{
				for(int i = 0; i<d.size(); i++)
				{
					if(!e.contains(d.get(i)))
						e.addElement(d.get(i));
				}
			}
			else
			{
				int f = c-e.size();
				for(int i = 0; i<f; i++)
				{
					if(!e.contains(d.get(i)))
						e.addElement(d.get(i));
				}
			}	
		}
		
		return e;
	}
	
	public static void main (String[] args) throws IOException
	{
		RecordManager recman = RecordManagerFactory.createRecordManager("searchEngine");
		Word word_storage = new Word(recman);
		Page page_storage = new Page(recman);
		PageRank findParentAndChild = new PageRank(recman);
		
		try
		{
			Vector<String> ab = new Vector<String>(0);
			
			Crawler crawler = new Crawler("http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/testpage.htm");
			ab.addElement(crawler.geturl());
			Vector<String> cd = crawler.getClink();
			Spider x = new Spider();
			Vector<String> ef = x.spider(ab, cd, 300);

			PageInfoStruct pagestruc;
			Vector<String> uni_word;
			Vector<Integer> freq_uni_word;
			Vector<String> title_word;//no freq
			Vector<Integer> freq_title_word;
//-------------------------------insert word + page--------------	
			for(int i = 0; i < ef.size(); i++)
			{
				crawler = new Crawler(ef.get(i));
				System.out.println("Inserting for page "+ crawler.geturl());
				
				page_storage.insertPage(crawler.geturl());
				pagestruc = new PageInfoStruct(crawler.geturl(), crawler.getTitle(), crawler.getLastModifiedDate(), crawler.getSize());
				page_storage.insertPageInfo(page_storage.getId(crawler.geturl()), pagestruc);
				uni_word = crawler.getuniwords();
				freq_uni_word = crawler.getfreq();
				title_word = crawler.getTitle1();//no freq for title is provided
				freq_title_word = crawler.getTitleFreq();
				
				for(int j = 0; j < uni_word.size(); j++)
				{
					word_storage.insertWord(uni_word.get(j));
					word_storage.insertWordTF(word_storage.getWordID(uni_word.get(j)), page_storage.getId(crawler.geturl()), freq_uni_word.get(j), crawler.getPosi(j), true);
					word_storage.insertInvertedWord(page_storage.getId(crawler.geturl()), uni_word.get(j), freq_uni_word.get(j), true);
				}
				
//				for(int j = 0; j < title_word.size(); j++)
//				{
//					System.out.println("url: "+ crawler.geturl());
//					System.out.println("word: "+title_word.get(j)+" freq: "+freq_title_word.get(j));
//					word_storage.insertInvertedWord(page_storage.getId(crawler.geturl()), title_word.get(j), freq_title_word.get(j), false);
//				}
				
			}
			//word_storage.printall();
			recman.commit();
			page_storage.printall();
			System.out.println("....................inserting their childs and parents.....................");
			
			
	
			for(int i = 0; i < ef.size(); i++){
				crawler = new Crawler(ef.get(i));
				String myID = page_storage.getId(ef.get(i));
				findParentAndChild.insertChild(ef.get(i), crawler.extractLinks(), myID);
				
				//Insert Parent
				Vector<String> insertParent = (Vector<String>)crawler.extractLinks();
				for(int j=0; j<insertParent.size() ; j++){
					String url = insertParent.get(j);
					String urlID = page_storage.getId(url);
					if(urlID != null)
						findParentAndChild.insertParent(urlID, ef.get(i));
				}
			}
			
			System.out.println("...................................................");
			System.out.println("...................................................");
			System.out.println("...................................................");
			System.out.println("........Finised! Please Run the test.java..........");
			
			recman.commit();
			recman.close();
			
		}
		catch (Exception e)
        {
			e.printStackTrace ();
        }

	}
}