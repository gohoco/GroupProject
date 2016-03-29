
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.htmlparser.util.ParserException;
import jdbm.RecordManager;

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
		String id = String.format("%08d", wordCount++);
		
		wordID.addEntry(uniword, id);
		word.addEntry(id, uniword);
		return id;
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
			list.add(new InvertPosting(word, tf));
			invertedBodyWord.addEntry(page_id, list);
		}
		else
		{
			list = new Vector<InvertPosting>();
			list.add(new InvertPosting(word, tf));
			invertedBodyWord.addEntry(page_id, list);
		}
	}
	
//	public void build(String page_id, String url, boolean isBody) throws ParserException, IOException
//	{
//		Vector<String> words = new Vector<String>();
//		if(isBody)
//		{
//			words = Indexer.extractWords(url);
//			pageSize = words.size();
//			/*
//			for(String i:words)
//				System.out.print(i + " ");
//			System.out.println("==================================");*/
//		}
//		else
//		{
//			String title = Indexer.extractTitle(url);
//	        String[] temp = title.split("\\W+");
//
//			for(int i = 0; i < temp.length; i++)
//				words.add(temp[i].toLowerCase());
//		}
//		
//		HashMap<String, Integer> word_tf = new HashMap<String, Integer>();
//		HashMap<String, Vector<Integer> > word_pos = new HashMap<String, Vector<Integer> >();
//		for(int i = 0; i < words.size(); i++)
//		{
//			/**
//			 * Create a new word_id when the word appears first time
//			 * otherwise, get the existing word_id**/
//			String word_id;
//			if(getWordID(words.get(i)) == null)
//				word_id = insertWord(words.get(i));
//			else
//				word_id = getWordID(words.get(i));
//			
//			if(word_id == null)
//				continue;
//			/**
//			 * everytime encounter a word
//			 * update the corresponding term frequency
//			 * and insert to the inverted talbe if it is the first time.
//			 */
//			
//			int tf;
//			Vector<Integer> pos;
//			if(word_tf.get(word_id) == null)
//			{
//				tf = 1;
//				pos = new Vector<Integer>();
//				pos.add(i);
//			}
//			else
//			{
//				tf = word_tf.get(word_id) + 1;
//				pos = word_pos.get(word_id);
//				pos.add(i);
//			}
//			
//			word_tf.put(word_id, tf);
//			word_pos.put(word_id, pos);
//		}
//		
//		/**
//		 * insert the result above to term frequency table
//		 * */
//		Iterator it = word_tf.entrySet().iterator();
//	    while (it.hasNext()) {
//	        Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
//	        insertWordTF(pairs.getKey(), page_id, pairs.getValue(), word_pos.get(pairs.getKey()), isBody);
//			insertInvertedWord(page_id, pairs.getKey(), pairs.getValue(), isBody);
//	        it.remove(); // avoids a ConcurrentModificationException
//	    }
//	}
//	
//	/** the main function for this class
//	 *  record the term_freq table, word_id table, word_inverted table**/
//	public void indexWordInfo(String page_id, String url) throws ParserException, IOException
//	{
//		//body
//		build(page_id, url, true);
//		
//		//title
//		build(page_id, url, false);
//	}
	
	public long getPageSize()
	{
		return pageSize;
	}
	
	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();				
	} 

}
