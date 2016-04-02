

import java.io.IOException;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

import jdbm.RecordManager;
import jdbm.helper.FastIterator;
/**
 * 
 * 
 * This table stores (page_id -> pageInfoStruct)
 */
public class Page {
	
	private RecordManager recman;
	private HashStruc pageInfo;//(pageID, PageInfoStruct)
	private HashStruc pageId;//(page,pageID)
	private int pageCount;
	
	public Page(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		pageInfo = new HashStruc(recman,"pageInfo");
		pageId = new HashStruc(recman,"pageID");
		pageCount = getSize();
	}
	
//	/** Given PageInfoStruct object, insert into hashtable*/
//	public void insertElement(String page_id, PageInfoStruct new_page, long page_size) throws IOException
//	{
//		new_page.setPageSize(page_size);				// set page_size if not set
//		if( new_page.setPageId(page_id) )				// set page_id if not set
//		{
//			pageInfo.addEntry(page_id, new_page);		// add entry only if page_id is set successfully
//		}
//	}
	
	public long getLastModificationLong(String url) throws IOException
	{
		String page_id = (String)pageId.getEntry(url);
		PageInfoStruct pi = (PageInfoStruct) pageInfo.getEntry(page_id);
		if(pi != null)
			return pi.getLastModificationLong();
		else
			return 0;
	}
	
	public HashStruc getName()
	{
		return pageInfo;
	}
	
	public int getSize() throws IOException{
		int counter = pageId.getSize();
		return counter;
	}
	
	public String getId(String url) throws IOException
	{
		return (String)pageId.getEntry(url);
	}
	
	public void insertPage(String url) throws IOException{
		String id;
		if(pageId.getEntry(url) == null){
			id = String.format("%08d", pageCount++);
			pageId.addEntry(url, id);
		}
	}
	
	public void insertPageInfo(String id, PageInfoStruct new_page) throws IOException{
		pageInfo.addEntry(id, new_page);
	}
	
	public FastIterator getIterator() throws IOException
	{
		//return the keys
		return pageId.getIterator();
	}
	
	public PageInfoStruct getPage(String id) throws IOException{
		return (PageInfoStruct)pageInfo.getEntry(id);
	}
}

