package Search;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class SearchEngine {

	private String stopwordtxt;
	private RecordManager recman;
	private Page page_storage;
	private PageRank pagerank;
	private Word word_storage;
	private Scores pageScore;
	
	public SearchEngine(String stopwordpath) throws IOException{
		stopwordtxt = stopwordpath;
		recman = RecordManagerFactory.createRecordManager("/opt/tomcat/webapps/intrasearch/searchEngine");
		//		recman = RecordManagerFactory.createRecordManager("/opt/tomcat/webapps/intrasearch/searchEngine");
		page_storage = new Page(recman);
		pagerank = new PageRank(recman);
		word_storage = new Word(recman);
		pageScore = null;
	}
	
	public PageInfoStruct getPageInfoStruct(String id) throws IOException{
		PageInfoStruct pis = page_storage.getPage(id);
		return pis;
	}
	
	public Vector<InvertPosting> getTopFiveFeqInvertPosting(String id) throws IOException{
		Vector<InvertPosting> ip = word_storage.getTopFiveFeq(id);
		return ip;
	}
	
	public Vector<String> getParentLink(String id) throws IOException{
		Vector<String> pl = pagerank.getParent(id);
		return pl;
	}
	
	public Vector<String> getChildLink(String id) throws IOException{
		Vector<String> cl = pagerank.getChild(id);
		return cl;
	}
	
	public double getScore(String id){
		if(pageScore != null)
			return pageScore.getScore(id);
		return -1;
	}
	
	public Vector<String> search(String input) throws IOException{
		
		Vector<String> query = new Vector<String>();
		StopStem ss = new StopStem(stopwordtxt);
		
		//-----extract phrase----------------
		Vector<String> phrase = new Vector<String>();
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(input);
		while (m.find()) {
		  //System.out.println(m.group(1));
		  phrase.add(m.group(1));
		}
		
//		for(int i=0; i<phrase.size();i++){
//			System.out.println(i+": "+phrase.get(i));
//		}
		//------------extract word------------
		Vector<String> singleWord = new Vector<String>();
		String[] sw = input.split("\"([^\"]*)\"");
		for(int i=0; i<sw.length; i++)
		{
			String[] sw2 = sw[i].split(" ");
			for(int j=0; j<sw2.length;j++)
				if(!sw2[j].equals(" ")&&!sw2[j].equals(""))
					singleWord.add(sw2[j]);
		}
		
//		for(String temp: singleWord)
//			System.out.println(temp);
		
		//--------stop and stem------------------
		String[] temp = null;
		String temp_stopstem = "";
		for(int i=0; i<phrase.size();i++)
		{
			temp = phrase.get(i).split(" ");
			if(temp!=null && temp.length!=0)
			{
				for(int j=0; j<temp.length; j++)
				{
					if(ss.isStopWord(temp[j]))
						continue;
					else
					{
						temp_stopstem += ss.stem(temp[j]);
						if(j!=temp.length-1)
							temp_stopstem += " ";
					}
				}
				if(!phrase.get(i).equals(temp_stopstem))
					phrase.set(i, temp_stopstem);
			}
			
			temp_stopstem = "";
						
		}
		
		for(int i=0; i<phrase.size();i++){
			System.out.println(i+": "+phrase.get(i));
		}
		
		for(int i=0; i<singleWord.size();i++)
		{
			if(ss.isStopWord(singleWord.get(i)))
				{
					singleWord.remove(i);
				}
			else
				singleWord.set(i, ss.stem(singleWord.get(i)));
		}
		
		for(String temp2: singleWord)
			System.out.println(temp2);
		
//		System.out.println(singleWord.size());
		//---------------combine result---------------
//		for(String phrase_word: phrase)
//			query.add(phrase_word);
//		for(String single_word: singleWord)
//			query.add(single_word);
//		
//		for(String q: query)
//			System.out.println(q);
		
		pageScore = new Scores(singleWord, phrase);
		Vector<String> result = pageScore.gettop50();
		
		return result;
	}
	
	
	public static void main (String[] args) throws IOException{
		SearchEngine se = new SearchEngine("stopwords.txt");
		Vector<String> result = se.search("\"Hong Kong\"");
		for(String r:result)
		{
			PageInfoStruct pis = se.getPageInfoStruct(r);
			System.out.println(pis.getTitle());
			System.out.println(pis.getURL());
			System.out.print(pis.getLastModification() + ", ");
			System.out.println(pis.getPageSize());
			
			Vector<InvertPosting> ip = se.getTopFiveFeqInvertPosting(r);
			for(InvertPosting in:ip)
			{
				System.out.print(in.word_id +" "+in.freq+";");
			}
			System.out.println();
			System.out.println("<--------P L---------->");
			Vector<String> pl = se.getParentLink(r);
			for(String p:pl)
				System.out.println(p);
			System.out.println("<--------C L---------->");			
			Vector<String> cl = se.getChildLink(r);
			for(String c:cl)
				System.out.println(c);
			
		}
		
	}
}