package groupCOMP4321;

import java.io.IOException;
import java.util.Vector;
import java.io.PrintWriter;
import java.io.File;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		RecordManager recman = RecordManagerFactory.createRecordManager("searchEngine");
		Word word_storage = new Word(recman);
		Page page_storage = new Page(recman);
		PageRank parent_ChildLink = new PageRank(recman);
		File file = new File("spider_result.txt");
		PrintWriter out = new PrintWriter(file);
		
		FastIterator fi = page_storage.getIterator();
		String key;
		PageInfoStruct pis;
		System.out.println("Start printing...");
		int counter = 1;
		while((key = (String)fi.next()) != null)
		{
			out.print("Page[" + counter++ + "] ");
			pis = page_storage.getPage(page_storage.getId(key));
			if(pis==null)
				out.println("pis is null  " + key);
			out.println(pis.getTitle());
			out.println(pis.getURL());
			out.println(pis.getLastModification() + ", " + pis.getPageSize());
			//word_storage.printTopFiveFeq(page_storage.getId(key));  // printwriter [out] cannot use this fuction to output the result to txt
			Vector<InvertPosting> ip = null;
			if(word_storage.getInvertedBodyWord().getEntry(page_storage.getId(key)) != null){
				ip = (Vector<InvertPosting>)word_storage.getInvertedBodyWord().getEntry(page_storage.getId(key));
				
				for(InvertPosting iplist : ip)
					out.print(iplist.word_id + " " + iplist.freq + "; ");
				
				out.println();
			}
			
			Vector<String> temp = (Vector<String>) parent_ChildLink.getChild().getEntry(page_storage.getId(key));
			out.println( "It has the following " + temp.size() + " child(s) :");
			for(int i = 0; i < temp.size(); i++)		
				out.println(temp.get(i));
			out.println("-------------------------------------------------------------------------------------------");
			
		}
		out.close();
		System.out.println("Finished -> Please check the spider_result.txt");
			
			
	}

}
