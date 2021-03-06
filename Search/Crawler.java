package Search;



import IRUtilities.*;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.util.Arrays;
import java.util.Vector;

import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
//import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
//import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.StringTokenizer;

import org.htmlparser.beans.LinkBean;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import org.htmlparser.tags.TitleTag;


public class Crawler
{
	private String url;
	private long length;
	private double hubWeight;
	private double authWeight;
	
	public Vector<String> parentLink;
	public Vector<String> childLink;
	public Vector<String> words;
	public Vector<String> uniwords;
	public Vector<Integer> freq;
	public Vector<String> posi;
	
	
	
	Crawler(String _url)
	{
		url = _url;
		length = 0;
		hubWeight = 1;
		authWeight = 1;
		
		// extract links in url and return them
		// ADD YOUR CODES HERE
		LinkBean lb = new LinkBean();
		lb.setURL(url);
		URL[] abc = lb.getLinks();
		Vector<String> v = new Vector<String>(0);
		for(int i=0; i<abc.length; i++){
			String a = abc[i].toString();
			if(!v.contains(a))
				v.addElement(a);
		}
		childLink = v;
		parentLink = new Vector<String>();
		
		StringBean sb = new StringBean ();
		sb.setLinks (false);
		sb.setURL (url);
		Vector<String> v1 = new Vector<String>(0);
		StringTokenizer a = new StringTokenizer(sb.getStrings());
		while (a.hasMoreTokens()) {
			String b = a.nextToken();
			v1.addElement(b);
			length += b.length();
	    }
		words = v1;
		
		uniwords = new Vector<String>(0);
		freq = new Vector<Integer>(0);
		posi = new Vector<String>(0);
		for(int i = 0; i< words.size(); i++)
		{
			StopStem x = new StopStem("stopwords.txt");
			String y = x.stem(words.get(i));
			if(y==null || y=="" || x.isStopWord(y))
				continue;
			
			if(!uniwords.contains(y))
			{
				uniwords.addElement(y);
				freq.addElement(1);
				posi.addElement(Integer.toString(i));
			}
			else
			{
				int z = uniwords.indexOf(y);
				freq.setElementAt(freq.get(z)+1, z);
				posi.setElementAt(posi.get(z) + "," + Integer.toString(i), z);
			}
		}
	}
	
	public Vector<Integer> getPosi(int x)
	{
		Vector<Integer> a = new Vector<Integer>(0);
		int b = posi.size();
		if(x < b)
		{
			String y = posi.get(x);
			int c = freq.get(x);
			for(String p: y.split(",",c))
			{
				a.addElement(Integer.parseInt(p));
			}
		}
		return(a);
	}
	
	public Vector<String> extractWords() throws ParserException
	{
		return words;
		
	}
	public Vector<String> extractLinks() throws ParserException
	{
		
		return childLink;
	}
	
	public Vector<Integer> getpopword()
	{
		Integer[] abc = new Integer[freq.size()];
		freq.toArray(abc);
		Arrays.sort(abc);
		Vector<Integer> a = new Vector<Integer>(5);
		Vector<Integer> b = (Vector<Integer>) freq.clone();
		int x = freq.size();
		for(int i = x-1; i > x-6; i--)
		{
			if(x<0)
				break;
			int y = abc[i];
			int z = b.indexOf(y);
			a.add(z);
			b.setElementAt(-1, z);
		}
		return a;
	}
	
	public String getTitle()
	{
		try
		{
			
			Parser abc = new Parser (url);
	        NodeFilter nFilter = new NodeClassFilter(TitleTag.class);
	        NodeList nList = abc.parse(nFilter);
	        Node[] nodes = nList.toNodeArray();
	        String title ="";
	        for(int i=0;i<nodes.length;i++){
	           Node node = nodes[i];
	           if(node instanceof TitleTag)
	           {
	               TitleTag titlenode = (TitleTag) node;
	               title = titlenode.getTitle();
	               break;
	           }
	        }
	        if(title.length() == 0)
	        	title += "No title";
	        return title;
		}
		catch (Exception e)
		{
			return new String("Not found");
		}
	}
	
	public Vector<String> getTitle1()
	{
		try
		{
			
			Parser abc = new Parser (url);
	        NodeFilter nFilter = new NodeClassFilter(TitleTag.class);
	        NodeList nList = abc.parse(nFilter);
	        Node[] nodes = nList.toNodeArray();
	        String title ="";
	        for(int i=0;i<nodes.length;i++){
	           Node node = nodes[i];
	           if(node instanceof TitleTag)
	           {
	               TitleTag titlenode = (TitleTag) node;
	               title = titlenode.getTitle();
	               break;
	           }
	        }
	        Vector<String> title1 = new Vector<String>(0);
	        for(String s: title.split(" "))
	        {
	        	title1.addElement(s);
	        }
	        if(title1.size() == 0)
	        	title1.addElement("No title");
	        return title1;
		}
		catch (Exception e)
		{
			Vector<String> k = new Vector<String>(0);
			k.addElement("Not found");
			return k;
		}
	}
	
	public Vector<String> getTitleUniwords()
	{
		String title = getTitle();
		String a = new String("No title");
		String b = new String("Not found");
		Vector<String> titlewords = new Vector<String>(0);
		if(!title.equals(a) && !title.equals(b))
		{
			for(String s: title.split(" "))
	        {
				StopStem x = new StopStem("stopwords.txt");
				String y = x.stem(s);
				if(y==null || y=="" || x.isStopWord(y))
					continue;
	        	if(!titlewords.contains(y))
	        		titlewords.addElement(y);
	        }
		}
		return titlewords;
	}
	
	public Vector<Integer> getTitleFreq()
	{
		String title = getTitle();
		String a = new String("No title");
		String b = new String("Not found");
		Vector<String> titlewords = new Vector<String>(0);
		Vector<Integer> titlefreq = new Vector<Integer>(0);
		if(!title.equals(a) && !title.equals(b))
		{
			for(String s: title.split(" "))
	        {
				StopStem x = new StopStem("stopwords.txt");
				String y = x.stem(s);
				if(y==null || y=="" || x.isStopWord(y))
					continue;
				if(!titlewords.contains(y))
				{
					titlewords.addElement(y);
					titlefreq.addElement(1);
				}
				else
				{
					int z = titlewords.indexOf(y);
					titlefreq.setElementAt(titlefreq.get(z)+1, z);
				}
	        }
		}
		return titlefreq;
	}
	
	public Vector<String> getTitlePosi()
	{
		String title = getTitle();
		String a = new String("No title");
		String b = new String("Not found");
		Vector<String> titlewords = new Vector<String>(0);
		Vector<String> titleposi = new Vector<String>(0);
		if(!title.equals(a) && !title.equals(b))
		{
			int i = -1;
			for(String s: title.split(" "))
	        {
				i++;
				StopStem x = new StopStem("stopwords.txt");
				String y = x.stem(s);
				if(y==null || y=="" || x.isStopWord(y))
					continue;
				if(!titlewords.contains(y))
				{
					titlewords.addElement(y);
					titleposi.addElement(Integer.toString(i));
				}
				else
				{
					int z = titlewords.indexOf(y);
					titleposi.setElementAt(titleposi.get(z) + "," + Integer.toString(i), z);
				}
	        }
		}
		return titleposi;
	}
	
	public long getLastModifiedDate()
	{
		try
		{
			URL web = new URL(url);
			URLConnection abc = web.openConnection();
			if(abc.getLastModified() != 0)
				return abc.getLastModified();
			else
				return abc.getDate();
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	public long getSize()
	{
		try
		{
			URL web = new URL(url);
			URLConnection abc = web.openConnection();
			if(abc.getLastModified() > 0)
				return abc.getContentLength();
			else
				return length;
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	public double getHweight()
	{
		return hubWeight;
	}
	
	public double getAweight()
	{
		return authWeight;
	}
	
	public Vector<String> getPlink()
	{
		return parentLink;
	}
	
	public Vector<String> getClink()
	{
		return childLink;
	}
	
	public void addPlink(String link)
	{
		if(!parentLink.contains(link))
			parentLink.addElement(link);
	}
	
	
	
	public void setHweight(double a)
	{
		hubWeight = a;
	}
	
	public void setAweight(double a)
	{
		authWeight = a;
	}
	
	public Vector<String> getuniwords()
	{
		return uniwords;
	}
	
	public Vector<Integer> getfreq()
	{
		return freq;
	}
	
	public String geturl()
	{
		return url;
	}
	
	public static void main (String[] args) throws IOException
	{
		RecordManager recman = RecordManagerFactory.createRecordManager("searchEngine");
		Word word_storage = new Word(recman);
		Page page_storage = new Page(recman);
		PageRank findParentAndChild = new PageRank(recman);
		
		try
		{
			Crawler crawler = new Crawler("http://www.cse.ust.hk");

			page_storage.insertPage(crawler.geturl());
			PageInfoStruct pagestruc = new PageInfoStruct(crawler.geturl(), crawler.getTitle(), crawler.getLastModifiedDate(), crawler.getSize());
			page_storage.insertPageInfo(page_storage.getId(crawler.geturl()), pagestruc);
			
			Vector<String> words = crawler.extractWords();		
			
			System.out.println("Words in "+crawler.url+":");
			for(int i = 0; i < words.size(); i++)
				System.out.print(words.get(i)+" ");
			System.out.println("\n\n");
			
			Vector<String> word1 = crawler.getuniwords();
			Vector<Integer> freq = crawler.getfreq();

			for(int i = 0; i < word1.size(); i++)
			{
				System.out.print(word1.get(i)+" ");
				System.out.print(freq.get(i)+" ");
			}
			System.out.println("\n\n");

	
			Vector<String> links = crawler.extractLinks();
			System.out.println(links.size());
			System.out.println("Links in "+crawler.url+":");
			for(int i = 0; i < links.size(); i++)		
				System.out.println(links.get(i));
			System.out.println("");
			System.out.println(crawler.getTitle());
			Vector<String> title = crawler.getTitle1();
			for(int i = 0; i < title.size(); i++)		
				System.out.print(title.get(i));

			System.out.println("");
			Vector<String> titleword = crawler.getTitleUniwords();
			Vector<Integer> titlefreq = crawler.getTitleFreq();
			Vector<String> titleposi = crawler.getTitlePosi();
			for(int i = 0; i < titleword.size(); i++)
				System.out.println(titleword.get(i) + " " + titlefreq.get(i) + " " + titleposi.get(i));
			System.out.println("\n");
			System.out.println(new Date(crawler.getLastModifiedDate()));
			System.out.println(crawler.getSize());
			Vector<Integer> pop = crawler.getpopword();
			for(int i = 0; i < pop.size(); i++)
				System.out.println(word1.get(pop.get(i)) + " " + freq.get(pop.get(i)) + " "+ pop.get(i));
			System.out.println("");
			
			
			for(int i = 0; i < word1.size(); i++)
			{
				word_storage.insertWord(word1.get(i));
				word_storage.insertWordTF(word_storage.getWordID(word1.get(i)), page_storage.getId(crawler.geturl()), freq.get(i), crawler.getPosi(i), true);
			}
			word_storage.printall();
			
			
			System.out.println("..................Wait.................................");
			for(int i=0; i<30 ; i++){
				System.out.println(page_storage.getId(links.get(i)));
			}


			System.out.println(".......................................................");
			System.out.println(".......................................................");
			System.out.println(".......................................................");

			System.out.println("You should run spider.java first and then run test.java");
			
		}
		catch (ParserException e)
        {
			e.printStackTrace ();
        }
		recman.commit();
		recman.close();

	}
}