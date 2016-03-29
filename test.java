import IRUtilities.*;

import java.util.Date;
import java.util.Vector;

import org.htmlparser.util.ParserException;

public class test
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
	
	public static void main (String[] args)
	{
		
		try
		{
			Vector<String> ab = new Vector<String>(0);
			
			Crawler crawler = new Crawler("http://www.cse.ust.hk");
			ab.addElement(crawler.geturl());
			Vector<String> cd = crawler.getClink();
			test x = new test();
			Vector<String> ef = x.spider(ab, cd, 300);
			for(int i = 0; i < ef.size(); i++)		
				System.out.println(ef.get(i));
			System.out.println("");
			
			

			Vector<String> words = crawler.extractWords();		
			
			System.out.println("Words in "+crawler.geturl()+":");
			for(int i = 0; i < words.size(); i++)
				System.out.print(words.get(i)+" ");
			System.out.println("\n\n");
			
			System.out.println("Stem word and frequency");
			Vector<String> word1 = crawler.getuniwords();
			Vector<Integer> freq = crawler.getfreq();
			for(int i = 0; i < word1.size(); i++)
			{
				System.out.print(word1.get(i)+" ");
				System.out.print(freq.get(i)+" ");
			}
			System.out.println("\n\n");

			/*
			Vector<String> links = crawler.extractLinks();
			System.out.println("Links in "+crawler.geturl()+":");
			for(int i = 0; i < links.size(); i++)		
				System.out.println(links.get(i));
			System.out.println("");
			*/
			
			System.out.println(crawler.getTitle());
			System.out.println(new Date(crawler.getLastModifiedDate()));
			System.out.println(crawler.getSize());
			
		}
		catch (Exception e)
        {
			e.printStackTrace ();
        }

	}
}