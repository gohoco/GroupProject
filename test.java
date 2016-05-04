
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
		
		FastIterator fi = page_storage.getIteratorForPageID();
		String key;
		PageInfoStruct pis;
		System.out.println("Start printing...");
		//int counter = 1;
		while((key = (String)fi.next()) != null)
		{
			//out.print("Page[" + counter++ + "] ");
			pis = page_storage.getPage(key);
			if(pis==null)
				out.println("pis is null  " + key);
			out.println(pis.getTitle());
			out.println(pis.getURL());
			out.println(pis.getLastModification() + ", " + pis.getPageSize());
			//word_storage.printTopFiveFeq(page_storage.getId(key));  // printwriter [out] cannot use this function to output the result to txt
			word_storage.printTopFiveFeq2(key, out);
			
			Vector<String> temp = (Vector<String>) parent_ChildLink.getChild().getEntry(key);
			//out.println( "It has the following " + temp.size() + " child(s) :");
			for(int i = 0; i < temp.size(); i++)		
				out.println(temp.get(i));
			out.println("-------------------------------------------------------------------------------------------");	
			out.println("-------------------------My Parent---------------------------------------------------------");
			Vector<String> temp2 = (Vector<String>) parent_ChildLink.getParent().getEntry(key);
			//out.println( "It has the following " + temp.size() + " child(s) :");
			for(int i = 0; i < temp2.size(); i++)		
				out.println(temp2.get(i));
			out.println("-------------------------------------------------------------------------------------------");
		}
		System.out.println("Word list and its ID:");
		word_storage.printall();
		System.out.println("Page list and its ID:");
		page_storage.printall();
		out.close();
		
		parent_ChildLink.calculateScore(page_storage);
		parent_ChildLink.printScore();
		
		System.out.println("Finished -> Please check the spider_result.txt");
			
	}

}
