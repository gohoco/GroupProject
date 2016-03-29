/* --
COMP4321 Lab2 Exercise
Student Name:
Student ID:
Section:
Email:
*/
import IRUtilities.*;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

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
		//System.out.println(a.countTokens());
		while (a.hasMoreTokens()) {
			String b = a.nextToken();
			v1.addElement(b);
			length += b.length();
			//System.out.println(b);
	    }
		words = v1;
		
		uniwords = new Vector<String>(0);
		freq = new Vector<Integer>(0);
		posi = new Vector<String>(0);
		for(int i = 0; i< words.size(); i++)
		{
			StopStem x = new StopStem("stopwords.txt");
			String y = x.stem(words.get(i));
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
		/*
		// extract words in url and return them
		// use StringTokenizer to tokenize the result from StringBean
		// ADD YOUR CODES HERE
		StringBean sb = new StringBean ();
		sb.setLinks (false);
		sb.setURL (url);
		Vector<String> v = new Vector<String>(0);
		StringTokenizer a = new StringTokenizer(sb.getStrings());
		System.out.println(a.countTokens());
		while (a.hasMoreTokens()) {
			String b = a.nextToken();
			v.addElement(b);
			length += b.length();
			//System.out.println(b);
	    }

		//v.addElement(sb.getStrings());
		//System.out.println(sb.getStrings());
		 
		 */
		return words;
		
	}
	public Vector<String> extractLinks() throws ParserException
	{
		/*
		// extract links in url and return them
		// ADD YOUR CODES HERE
		LinkBean lb = new LinkBean();
	    lb.setURL(url);
	    URL[] abc = lb.getLinks();
		Vector<String> v = new Vector<String>();
		for(int i=0; i<abc.length; i++){
			String a = abc[i].toString();
			v.addElement(a);
	    }
	    */
		
		return childLink;
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
	           }
	        }
	        
	        return title;
		}
		catch (Exception e)
		{
			return null;
		}
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
			Vector<Integer> posi = crawler.getPosi(1);
			for(int i = 0; i < posi.size(); i++)
			{
				System.out.print(posi.get(i)+" ");
			}
			System.out.println("\n");
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
			System.out.println(new Date(crawler.getLastModifiedDate()));
			System.out.println(crawler.getSize());
			
			//-----------
			for(int i = 0; i < word1.size(); i++)
			{
				word_storage.insertWord(word1.get(i));
				if(word_storage.getWordID(word1.get(i))==null)
					continue;
				word_storage.insertWordTF(word_storage.getWordID(word1.get(i)), page_storage.getId(crawler.geturl()), freq.get(i), posi, true);
			}
			word_storage.printall();
			
		}
		catch (ParserException e)
        {
			e.printStackTrace ();
        }
		recman.commit();
		recman.close();

	}
}