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
		
		try
		{
			Vector<String> ab = new Vector<String>(0);
			
			Crawler crawler = new Crawler("http://www.cse.ust.hk");
			ab.addElement(crawler.geturl());
			Vector<String> cd = crawler.getClink();
			Spider x = new Spider();
			Vector<String> ef = x.spider(ab, cd, 30);
//----------------------insert word + page for cse.ust.hk-------------
			System.out.println("Inserting for page "+ crawler.geturl());
			page_storage.insertPage(crawler.geturl());
			PageInfoStruct pagestruc = new PageInfoStruct(crawler.geturl(), crawler.getTitle(), crawler.getLastModifiedDate(), crawler.getSize());
			page_storage.insertPageInfo(page_storage.getId(crawler.geturl()), pagestruc);
			
			Vector<String> uni_word = crawler.getuniwords();
			Vector<Integer> freq_uni_word = crawler.getfreq();
			Vector<String> title_word = crawler.getTitle1();//no freq
			for(int i = 0; i < uni_word.size(); i++)
			{
				word_storage.insertWord(uni_word.get(i));
				word_storage.insertWordTF(word_storage.getWordID(uni_word.get(i)), page_storage.getId(crawler.geturl()), freq_uni_word.get(i), crawler.getPosi(i), true);
				word_storage.insertInvertedWord(page_storage.getId(crawler.geturl()), uni_word.get(i), freq_uni_word.get(i), true);
			}
			
			
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
				for(int j = 0; j < uni_word.size(); j++)
				{
					word_storage.insertWord(uni_word.get(j));
					word_storage.insertWordTF(word_storage.getWordID(uni_word.get(j)), page_storage.getId(crawler.geturl()), freq_uni_word.get(j), crawler.getPosi(j), true);
					word_storage.insertInvertedWord(page_storage.getId(crawler.geturl()), uni_word.get(j), freq_uni_word.get(j), true);
				}
				
			}
			word_storage.printall();
			page_storage.printall();
				
//			Vector<String> words = crawler.extractWords();		
//			
//			System.out.println("Words in "+crawler.geturl()+":");
//			for(int i = 0; i < words.size(); i++)
//				System.out.print(words.get(i)+" ");
//			System.out.println("\n\n");
//			
//			System.out.println("Stem word and frequency");
//			Vector<String> word1 = crawler.getuniwords();
//			Vector<Integer> freq = crawler.getfreq();
//			for(int i = 0; i < word1.size(); i++)
//			{
//				System.out.print(word1.get(i)+" ");
//				System.out.print(freq.get(i)+" ");
//			}
//			System.out.println("\n\n");
//
//			/*
//			Vector<String> links = crawler.extractLinks();
//			System.out.println("Links in "+crawler.geturl()+":");
//			for(int i = 0; i < links.size(); i++)		
//				System.out.println(links.get(i));
//			System.out.println("");
//			*/
//			
//			System.out.println(crawler.getTitle());
//			System.out.println(new Date(crawler.getLastModifiedDate()));
//			System.out.println(crawler.getSize());
			
			recman.commit();
			recman.close();
			
		}
		catch (Exception e)
        {
			e.printStackTrace ();
        }

	}
}