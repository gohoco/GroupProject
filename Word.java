

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.htmlparser.util.ParserException;
import jdbm.RecordManager;
import jdbm.helper.FastIterator;

class Posting implements Serializable {
	public String page_id;
	public int freq;
	public Vector<Integer> position;

	Posting(String doc, int freq, Vector<Integer> position) {
		this.page_id = doc;
		this.freq = freq;
		this.position = position;
	}
}

public class Word {
	private HashStruc wordID;
	private HashStruc bodyWord;
	private HashStruc titleWord;
	private HashStruc invertedBodyWord;
	private HashStruc invertedTitleWord;
	private HashStruc word;
	private RecordManager recman;
	int wordCount;
	private long pageSize;
	
	/**constructor**/
	public Word(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		wordID = new HashStruc(recman,"wordID"); //(word,wordid)
		bodyWord = new HashStruc(recman,"bodyWord");//(wordid, Vector<posting>)
		titleWord = new HashStruc(recman,"titleWord");//(wordid, Vector<posting>)
		invertedBodyWord = new HashStruc(recman, "invertedBodyWord");//(pageid, Vector<InvertPosting>)
		invertedTitleWord = new HashStruc(recman, "invertedTitleWord");//(pageid, Vector<InvertPosting>)
		word = new HashStruc(recman, "word");//(wordID, word)
		wordCount = wordID.getSize();//number of uniword
	}
	
	/**insert a new word**/
	public String insertWord(String uniword) throws IOException
	{
		String id;
		if(wordID.getEntry(uniword) == null){
			id = String.format("%08d", wordCount++);
			
			wordID.addEntry(uniword, id);
			word.addEntry(id, uniword);
			
			return id;
		}else{
			id = (String)wordID.getEntry(uniword);
			return id;
		}
	}
	
	/**get word_id**/
	public String getWordID(String stemword) throws IOException
	{
		if(stemword == null || stemword.equals(""))
			return null;
		
		if(wordID.getEntry(stemword) == null)
			return null;
		
		return  (String) wordID.getEntry(stemword);
	}
	
	
	/**if the list exist. add the new word to the end, else create a new list
	 * @param word_pos **/
	public void insertWordTF(String word_id, String page_id, int word_tf, Vector<Integer> word_pos, boolean isBody) throws IOException
	{
		Vector<Posting> postingList;
		if(isBody)
		{
			if(bodyWord.getEntry(word_id) != null)
			{
				postingList = (Vector<Posting>) bodyWord.getEntry(word_id);
				postingList.add(new Posting(page_id,word_tf,word_pos));
				bodyWord.addEntry(word_id, postingList);
				//((Vector<Posting>) wordTF.getEntry(word_id)).add(new Posting(page_id,word_tf));
			}
			else
			{
				postingList = new Vector<Posting>();
				postingList.add(new Posting(page_id,word_tf,word_pos));
				bodyWord.addEntry(word_id, postingList);
			}
		}
		else
		{
			if(titleWord.getEntry(word_id) != null)
			{
				postingList = (Vector<Posting>) titleWord.getEntry(word_id);
				postingList.add(new Posting(page_id,word_tf,word_pos));
				titleWord.addEntry(word_id, postingList);
			}
			else
			{
				postingList = new Vector<Posting>();
				postingList.add(new Posting(page_id,word_tf,word_pos));
				titleWord.addEntry(word_id, postingList);
			}
		}

	}
	
	/**if the list exist. add the new word to the end, else create a new list**/
	public void insertInvertedWord(String page_id, String word, int tf, boolean isBody) throws IOException
	{
		Vector<InvertPosting> list;
		if(isBody)
		{
			list = (Vector<InvertPosting>) invertedBodyWord.getEntry(page_id);
		}
		else
		{
			list = (Vector<InvertPosting>) invertedTitleWord.getEntry(page_id);
		}
			
		if(list != null)
		{
			int check = 0;
			InvertPosting newPost = new InvertPosting(word, tf);
			for(InvertPosting p: list)
			{
				if(word.equals(p.word_id) && tf==p.freq)
					check = 1;
			}
			if(check == 0)
			{
				list.add(newPost);
				invertedBodyWord.addEntry(page_id, list);
			}
		}
		else
		{
			list = new Vector<InvertPosting>();
			list.add(new InvertPosting(word, tf));
			invertedBodyWord.addEntry(page_id, list);
		}
	}
	
	
	public long getPageSize()
	{
		return pageSize;
	}
	
	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();				
	} 
	
	public void printall() throws IOException
	{
		FastIterator fi = wordID.getIterator();
		String key;
		while((key = (String)fi.next()) != null){
			System.out.println(key + " = " + wordID.getEntry(key));
		}
	}
	
	public void printTopFiveFeq(String pageid) throws IOException
	{
		Vector<InvertPosting> ip = null;
		if(invertedBodyWord.getEntry(pageid) != null)
			ip = (Vector<InvertPosting>)invertedBodyWord.getEntry(pageid);
		else
			return;
		
//		Vector<InvertPosting> sorted_ip = new Vector<InvertPosting>();
//		InvertPosting rank1 = null;
//		InvertPosting rank2 = null;
//		InvertPosting rank3 = null;
//		InvertPosting rank4 = null;
//		InvertPosting rank5 = null;
//		for(InvertPosting iplist : ip)
//		{
//			if(rank1 == null)
//				rank1 = iplist;
//			else if(rank2 == null && iplist.freq<=rank1.freq)
//				rank2 = iplist;
//			else if(rank3 == null)
//				rank3 = iplist;
//			else if(rank4 == null)
//				rank4 = iplist;
//			else if(rank5 == null)
//				rank5 = iplist;
//			else
//			{
//				if(rank1.freq < iplist.freq)
//					rank1 = iplist;
//				else if(rank2.freq < iplist.freq)
//					rank2 = iplist;
//				else if(rank3.freq < iplist.freq)
//					rank3 = iplist;
//				else if(rank4.freq < iplist.freq)
//					rank4 = iplist;
//				else if(rank5.freq < iplist.freq)
//					rank5 = iplist;
//			}
//		}
//
//		if(rank1 != null)
//			sorted_ip.add(rank1);
//		if(rank2 != null)
//			sorted_ip.add(rank2);
//		if(rank3 != null)
//			sorted_ip.add(rank3);
//		if(rank4 != null)
//			sorted_ip.add(rank4);
//		if(rank5 != null)
//			sorted_ip.add(rank5);
//		
//		for(int i=1; i < sorted_ip.size() ; i++)
//		{
//			InvertPosting temp = sorted_ip.get(i);
//			int j;
//			for(j=i-1; j >= 0 && temp.freq > sorted_ip.get(j).freq ; j--)
//				sorted_ip.set(j+1, sorted_ip.get(j));
//			sorted_ip.set(j+1, temp);
//		}
		
		for(InvertPosting iplist : ip)
		{
			System.out.print(iplist.word_id + " " + iplist.freq + "; ");
		}
		System.out.println();

	}
	
	public void printTopFiveFeq2(String pageid, PrintWriter aa) throws IOException
	{
		Vector<InvertPosting> ip = null;
		if(invertedBodyWord.getEntry(pageid) != null)
			ip = (Vector<InvertPosting>)invertedBodyWord.getEntry(pageid);
		else
			return;
		
		Vector<InvertPosting> sorted_ip = new Vector<InvertPosting>();
		InvertPosting rank1 = null;
		InvertPosting rank2 = null;
		InvertPosting rank3 = null;
		InvertPosting rank4 = null;
		InvertPosting rank5 = null;
		for(InvertPosting iplist : ip)
		{
			if(rank1 == null)
				rank1 = iplist;
			else if(rank2 == null && iplist.freq<=rank1.freq)
				rank2 = iplist;
			else if(rank3 == null)
				rank3 = iplist;
			else if(rank4 == null)
				rank4 = iplist;
			else if(rank5 == null)
				rank5 = iplist;
			else
			{
				if(rank1.freq < iplist.freq)
					rank1 = iplist;
				else if(rank2.freq < iplist.freq)
					rank2 = iplist;
				else if(rank3.freq < iplist.freq)
					rank3 = iplist;
				else if(rank4.freq < iplist.freq)
					rank4 = iplist;
				else if(rank5.freq < iplist.freq)
					rank5 = iplist;
			}
		}

		if(rank1 != null)
			sorted_ip.add(rank1);
		if(rank2 != null)
			sorted_ip.add(rank2);
		if(rank3 != null)
			sorted_ip.add(rank3);
		if(rank4 != null)
			sorted_ip.add(rank4);
		if(rank5 != null)
			sorted_ip.add(rank5);
		
		for(int i=1; i < sorted_ip.size() ; i++)
		{
			InvertPosting temp = sorted_ip.get(i);
			int j;
			for(j=i-1; j >= 0 && temp.freq > sorted_ip.get(j).freq ; j--)
				sorted_ip.set(j+1, sorted_ip.get(j));
			sorted_ip.set(j+1, temp);
		}
		
		for(InvertPosting iplist : sorted_ip)
		{
			aa.print(iplist.word_id + " " + iplist.freq + "; ");
		}
		aa.println();

	}


}
