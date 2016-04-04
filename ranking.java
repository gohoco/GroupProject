

import java.util.*;
import java.io.*;
import jdbm.*;
import jdbm.helper.FastIterator;

public class ranking {
	private RecordManager recman;
	private static HashStruc parent; //The parent i have
	private static HashStruc child; //The Child the url have
	
	public ranking(RecordManager myRecman) throws IOException{		
		recman = myRecman;
		parent = new HashStruc(recman,"parentLink");
		child = new HashStruc(recman,"childLink");
	}
	
	public void insertParent(Vector<String> clusterOfChild, String myURLParent) throws IOException{
		for(int i=0; i<clusterOfChild.size() ; i++){
			String url = clusterOfChild.get(i);
			if(url != "http://www.cse.ust.hk/"){
				if(parent.getEntry(url) == null){
					Vector<String> parentLink = new Vector<String>(0);
					parentLink.addElement(myURLParent);
					parent.addEntry(url, parentLink);
				}else{
					Vector<String> tempParentLink = (Vector<String>)parent.getEntry(url);
					if(!tempParentLink.contains(myURLParent)){
						parent.delEntry(url);
						tempParentLink.addElement(myURLParent);
						parent.addEntry(url, tempParentLink);
					}
				}
			}else{
				if(parent.getEntry(url) == null){
					String urlNoParent = url + " have no parent links !!!" ;
					Vector<String> parentLink = new Vector<String>(0);
					parentLink.addElement(urlNoParent);
					parent.addEntry(url, parentLink);
				}
			}
		}
		
	}
	
	public void insertChild(String URLParent, Vector<String> URLChild ) throws IOException{
		if(child.getEntry(URLParent) == null){
			child.addEntry(URLParent, URLChild);
		}else{
				child.delEntry(URLParent);
				child.addEntry(URLParent, URLChild);
				insertParent(URLChild, URLParent);
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
		int count = 0;
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
		int count = 0;
		String key;
		while((key = (String)fi.next()) != null){
			System.out.println("[ " + count + " ]   " + key + " has the following child :");
			Vector<String> temp = (Vector<String>)child.getEntry(key);
			for(int i = 0; i < temp.size(); i++)		
				System.out.println(temp.get(i));
			
			count++;
		}
	}
	
	
}
