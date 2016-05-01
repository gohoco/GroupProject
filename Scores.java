import IRUtilities.*;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.io.IOException;
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

import jdbm.helper.FastIterator;

public class Scores {
	public Vector<Double> scores;
	public Vector<String> keyID;
	public Vector<String> urls;
	public Vector<Integer> maxtf;
	public Vector<Double> sqrtdoclength;
	//public Vector<String> trial;
	
	
	
	public Vector<String> gettop50()
	{
		Double[] abc = new Double[scores.size()];
		scores.toArray(abc);
		Arrays.sort(abc);
		Vector<String> a = new Vector<String>(0);
		Vector<Double> b = (Vector<Double>) scores.clone();
		int x = scores.size();
		for(int i = 0; i < 50; i++)
		{
			if((x-i-1)<0)
				break;
			double y = abc[x-1-i];
			if(y == 0)
				break;
			int z = b.indexOf(y);
			a.add(keyID.get(z));
			b.setElementAt(-1.0, z);
		}
		return a;
	}
	
	public int pdf(String x)
	{
		try{
			RecordManager recman = RecordManagerFactory.createRecordManager("searchEngine");
			Page page_storage = new Page(recman);
			Word word_storage = new Word(recman);
			FastIterator fi = page_storage.getIteratorForPageID();
			String key;
			PageInfoStruct pis;
			int y = 0;
			while((key = (String)fi.next()) != null)
			{
				pis = page_storage.getPage(key);
				
				Vector<String> abc = new Vector<String>(0);
				Vector<String> abcid = new Vector<String>(0);
				for(String s: x.split(" "))
				{
					abc.add(s);
					abcid.add(word_storage.getWordID(s));
				}
				if(!abcid.contains(null))
				{
					Vector<Vector<Integer>> abcd = new Vector<Vector<Integer>>(0);
					
					for(int j = 0; j<abc.size(); j++)
					{
						Vector<Posting> abcde = word_storage.getBodyWord(abcid.get(j));
						for(int k=0; k<abcde.size(); k++)
						{
							if(abcde.get(k).page_id.equals(key))
							{
								abcd.add(abcde.get(k).position);
								break;
							}
						}
					}
					if(abcd.size() == abc.size())
					{
						for(int l=0; l<abcd.get(0).size(); l++)
						{
							boolean check = true;
							for(int m=0; m<abcd.size(); m++)
							{
								if(!abcd.get(m).contains(abcd.get(0).get(l) + m))
								{
									check = false;
									break;
								}
							}
							if(check)
							{
								y++;
								break;
							}
						}
					}
				}
			}
			return y;
		}
		catch (Exception e){
			return 0;
		}
	}
	
	Scores(Vector<String> a, Vector<String> b)
	{
		scores = new Vector<Double>(0);
		urls = new Vector<String>(0);
		keyID = new Vector<String>(0);
		maxtf = new Vector<Integer>(0);
		sqrtdoclength = new Vector<Double>(0);
		//trial = new Vector<String>(0);
		
		try{
			RecordManager recman = RecordManagerFactory.createRecordManager("searchEngine");
			Page page_storage = new Page(recman);
			Word word_storage = new Word(recman);
			FastIterator fi = page_storage.getIteratorForPageID();
			String key;
			PageInfoStruct pis;
			
			while((key = (String)fi.next()) != null)
			{
				pis = page_storage.getPage(key);
				if(pis==null)
					System.out.println("pis is null  " + key);
				keyID.addElement(key);
				urls.addElement(pis.getURL());
				
				/*input sqrtdoclength of all pages*/
				Vector<InvertPosting> ibw = word_storage.getIBodyWord(key);
				String title = pis.getTitle();
				Vector<String> title1 = new Vector<String> (0);
				for(String s: title.split(" "))
				{
					title1.add(s);
				}
				
				int x = 0;
				double y = 0;
				String z = "0 0";
				double pscore = 0;
				if(ibw != null && ibw.size() > 0)
				{
					for(int i = 0; i<ibw.size(); i++)
					{
						int freq = ibw.get(i).freq;
						if(freq > x)
						{
							x=freq;
						}
					}
				}
				maxtf.add(x);
				
				/*input sqrtdoclength of all pages*/
				if(ibw != null && ibw.size() > 0)
				{
					for(int i = 0; i<ibw.size(); i++)
					{
						int freq = ibw.get(i).freq;
						
						z=ibw.get(i).word_id;
						String k = word_storage.getWordID(z);
						Vector<Posting> abc = word_storage.getBodyWord(k);
						int df = 0;
						if(abc != null)
							df = abc.size();
						
						if(x!=0 && df!=0)
							y += Math.pow(((double)freq/x)*Math.log((double)300/df)/Math.log(2), 2);
						
					}
				}
				sqrtdoclength.add(Math.sqrt(y));
				
				//calculation of scores
				int qlength = a.size()+b.size();
				double base = Math.sqrt(y) * Math.sqrt(qlength);
				for(int i = 0; i<a.size(); i++)
				{
					String k = word_storage.getWordID(a.get(i));
					if(k != null && ibw != null && ibw.size() > 0)
					{
						int freq = 0;
						for(int j = 0; j<ibw.size(); j++)
						{
							if(ibw.get(j).word_id.equals(a.get(i)))
							{
								freq = ibw.get(j).freq;
								break;
							}
						}
						Vector<Posting> abc = word_storage.getBodyWord(k);
						int df = 0;
						if(abc != null)
							df = abc.size();
						
						if(x!=0 && df!=0)
							pscore += ((double)freq/x)*Math.log((double)300/df)/Math.log(2);
					}
				}
				
				/*phrase search*/
				for(int i = 0; i<b.size(); i++)
				{
					int df = pdf(b.get(i));
					if(x == 0)
						break;
					if(df == 0)
						continue;
					double pscore1 = 0;
					int ptf = 0;
					Vector<String> abc = new Vector<String>(0);
					Vector<String> abcid = new Vector<String>(0);
					for(String s: b.get(i).split(" "))
					{
						abc.add(s);
						abcid.add(word_storage.getWordID(s));
					}
					if(!abcid.contains(null))
					{
						Vector<Vector<Integer>> abcd = new Vector<Vector<Integer>>(0);
						
						for(int j = 0; j<abc.size(); j++)
						{
							Vector<Posting> abcde = word_storage.getBodyWord(abcid.get(j));
							for(int k=0; k<abcde.size(); k++)
							{
								if(abcde.get(k).page_id.equals(key))
								{
									abcd.add(abcde.get(k).position);
									break;
								}
							}
						}
						if(abcd.size() == abc.size())
						{
							for(int l=0; l<abcd.get(0).size(); l++)
							{
								boolean check = true;
								for(int m=0; m<abcd.size(); m++)
								{
									if(!abcd.get(m).contains(abcd.get(0).get(l) + m))
									{
										check = false;
										break;
									}
								}
								if(check)
									ptf++;
							}
						}
						pscore1 += ((double)ptf/x)*Math.log((double)300/df)/Math.log(2);
					}
					pscore += pscore1;
				}
				int favor = 0;
				for(int i = 0; i< a.size(); i++)
				{
					if(title1.contains(a.get(i)))
					{
						pscore *= 1.5;
						favor = 1;
						break;
					}
				}
				for(int i = 0; i< b.size(); i++)
				{
					if(favor == 1)
						break;
					Vector<String> title2 = new Vector<String>(0);
					for(String s: b.get(i).split(" "))
					{
						title2.add(s);
					}
					boolean check1 = true;
					for(int j = 0; j < title2.size(); j++)
					{
						if(!title1.contains(title2.get(i)))
							check1 = false;
					}
					if(check1)
					{
						pscore *= 1.5;
						break;
					}
				}
				
				double okscore = 0;
				if(base != 0)
					okscore = pscore/base;
				scores.add(okscore);
				
				
			}
			//Vector<Posting> abc = word_storage.getBodyWord(word_storage.getWordID("cvil"));
			//if(abc != null)
			//	System.out.println(abc.size());
			//System.out.println(pdf("kong abcde"));
			/*
			for(int i = 0; i< sqrtdoclength.size(); i++)
			{
				System.out.println(i +" "+  maxtf.get(i) + "  " + sqrtdoclength.get(i));
			}
			*/
			/*
			for(int i = 0; i< scores.size(); i++)
				System.out.println(scores.get(i));
			
			//page_storage.printall();
			System.out.println(word_storage.getWordID("abcde"));
			if(word_storage.getWordID("abcde") == null)
				System.out.println("abc");
			System.out.println(a.size() + " " + b.size());
			*/
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Vector<String> a = new Vector<String>(0);
		Vector<String> b = new Vector<String>(0);
		a.add("0");
		//a.add("1");
		//a.add("abcde");
		//b.add("0 1");
		//b.add("1");
		Scores x = new Scores(a, b);
		//System.out.println(Math.pow(3, 2));
		Vector<String> y = x.gettop50();
		System.out.println(y.size());
		//for(int i = 0; i< y.size(); i++)
			//System.out.println(y.get(i));
		
		//System.out.println(Math.log(8)/Math.log(0));
		//System.out.println(0/0);
	}

}
