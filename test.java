import java.io.IOException;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		RecordManager recman = RecordManagerFactory.createRecordManager("searchEngine");
		Word word_storage = new Word(recman);
		Page page_storage = new Page(recman);
		
		FastIterator fi = page_storage.getIterator();
		String key;
		PageInfoStruct pis;
		System.out.println("Start printing...");
		int counter = 1;
		while((key = (String)fi.next()) != null)
		{
			System.out.println("Page: " + counter++);
			pis = page_storage.getPage(page_storage.getId(key));
			if(pis==null)
				System.out.println("pis is null  " + key);
			System.out.println(pis.getTitle());
			System.out.println(pis.getURL());
			System.out.println(pis.getLastModification() + ", " + pis.getPageSize());
			word_storage.printTopFiveFeq(page_storage.getId(key));
			System.out.println("-------------------------------------------------------------------------------------------");
			
		}
		System.out.println("Finished");
			
			
	}

}
