package Search;


import java.io.IOException;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

import jdbm.RecordManager;
import jdbm.helper.FastIterator;
/*
 * 
 * 
 * This table stores (page_id -> pageInfoStruct)
 */
public class Page {
	
	private RecordManager recman;
	private HashStruc pageInfo;//(pageID, PageInfoStruct)
	private HashStruc pageId;//(page,pageID)
	private HashStruc page;//(pageID,page)
	private int pageCount;
	
	public Page(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		pageInfo = new HashStruc(recman,"pageInfo");//(pageID, PageInfoStruct)
		pageId = new HashStruc(recman,"pageID");//(page,pageID)
		page = new HashStruc(recman, "page");//(pageID,page)
		pageCount = getSize();
	}
	
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
			page.addEntry(id, url);
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
	
	public FastIterator getIteratorForPageID() throws IOException
	{
		//return the keys
		return page.getIterator();
	}
	
	public PageInfoStruct getPage(String id) throws IOException{
		return (PageInfoStruct) pageInfo.getEntry(id);
	}
	
	public void printall() throws IOException
	{
		FastIterator fi = page.getIterator();
		String key;
		while((key = (String)fi.next()) != null){
			System.out.println(key + " = " + page.getEntry(key));
		}
	}
}
