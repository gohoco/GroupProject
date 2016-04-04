package groupCOMP4321;

import java.util.*;
import java.io.*;
import jdbm.*;
import jdbm.helper.FastIterator;

public class PageRank {
	private RecordManager recman;
	private static HashStruc parent; //The parent i have
	private static HashStruc child; //The Child the url have
	
	public PageRank(RecordManager myRecman) throws IOException{		
		recman = myRecman;
		parent = new HashStruc(myRecman,"parentLink");
		child = new HashStruc(myRecman,"childLink");
		Vector<String> parentLink = new Vector<String>(0);
		parentLink.addElement("http://www.cse.ust.hk should not have any parent links !!!");
		parent.addEntry("00000000", parentLink);
	}
	
	public void insertParent(Vector<String> clusterOfChild, String myURLParent, Page myPage) throws IOException{
		for(int i=0; i<clusterOfChild.size() ; i++){
			String url = clusterOfChild.get(i);
			String urlID = myPage.getId(url);
			if(urlID != null){
					if(parent.getEntry(urlID) == null){
						Vector<String> parentLink = new Vector<String>(0);
						parentLink.addElement(myURLParent);
						parent.addEntry(urlID, parentLink);
					}else{
						Vector<String> tempParentLink = (Vector<String>)parent.getEntry(urlID);
						if(!tempParentLink.contains(myURLParent)){
							parent.delEntry(urlID);
							tempParentLink.addElement(myURLParent);
							parent.addEntry(urlID, tempParentLink);
						}
					}
			}
			
		}
		
	}
	
	public void insertChild(String URLParent, Vector<String> URLChild, Page myPage) throws IOException{
		String urlID = myPage.getId(URLParent);
		if(child.getEntry(urlID) == null){
			child.addEntry(urlID, URLChild);
			insertParent(URLChild, URLParent, myPage);
		}else{
			child.delEntry(urlID);
			child.addEntry(urlID, URLChild);
			insertParent(URLChild, URLParent, myPage);
		}
		
	}
	
	public HashStruc getParent(){
		return parent;
	}
	
	public HashStruc getChild(){
		return child;
	}
	
	public static void printParent() throws IOException
	{
		FastIterator fi = parent.getIterator();
		int count = 1;
		String key;
		
		while((key = (String)fi.next()) != null){
			System.out.println("[ "  + count +  " ]   " + key + " has the following parent :");
			Vector<String> temp = (Vector<String>)parent.getEntry(key);
			for(int i = 0; i < temp.size(); i++)		
				System.out.println(temp.get(i));
			
			count++;
		}
	}
	
	public static void printChild() throws IOException
	{
		FastIterator fi = child.getIterator();
		int count = 1;
		String key;
		while((key = (String)fi.next()) != null){
			System.out.println("[ " + count + " ]   " + key + " has the following child :");
			Vector<String> temp = (Vector<String>) child.getEntry(key);
			for(int i = 0; i < temp.size(); i++)		
				System.out.println(temp.get(i));
			
			count++;
		}
	}
	
	
}
