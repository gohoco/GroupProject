

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
/*
 *  This is the Data Structure class stores the (url, title, pageId, pageSize, last modification date).
 */
public class PageInfoStruct implements Serializable{
	private String m_url;
	private String m_title;
	private long m_lastModification;
	private long m_size;
	
	public PageInfoStruct(String url, String title, long lastModification, long size){
		m_url = url;
		m_title = title;
		m_lastModification = lastModification;
		m_size = size;
	}
	
	public long getLastModificationLong()
	{
		return m_lastModification;
	}

	public Date getLastModification()
	{
		if( m_lastModification == 0 ) return null;
		Date date = new Date(m_lastModification);
		return date;
	}
	
	public long getPageSize()
	{
		return m_size;
	}
	
	public String getTitle()
	{
		return m_title;
	}
	
	public String getURL()
	{
		return m_url;
	}
	
	
	public boolean setPageSize(long page_size)
	{
		if(m_size <= 0)
		{
			m_size = page_size;
			return true;
		}
		return false;
	}

}
