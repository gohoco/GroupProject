package groupCOMP4321;

import java.util.*;
import java.io.*;
import jdbm.*;
import jdbm.helper.FastIterator;

public class PageRank {
	private static RecordManager recman;
	private static HashStruc parent; //The parent i have
	private static HashStruc child; //The Child the url have
	private static HashStruc pageRankScore; // Store the page rank score
	
	public PageRank(RecordManager myRecman) throws IOException{		
		recman = myRecman;
		parent = new HashStruc(myRecman,"parentLink");
		child = new HashStruc(myRecman,"childLink");
		pageRankScore = new HashStruc(myRecman,"Score");
		//Vector<String> parentLink = new Vector<String>(0);
		//parentLink.addElement("The main page should not have any parent links !!!");
		//parent.addEntry("00000000", parentLink);
	}
	
	public void insertParent(String me, String myURLParent) throws IOException{
		if(parent.getEntry(me) == null){
			Vector<String> parentLink = new Vector<String>(0);
			parentLink.addElement(myURLParent);
			parent.addEntry(me, parentLink);
		}else{
			Vector<String> tempParentLink = (Vector<String>)parent.getEntry(me);
			if(!tempParentLink.contains(myURLParent)){
				parent.delEntry(me);
				tempParentLink.addElement(myURLParent);
				parent.addEntry(me, tempParentLink);
			}
		}	
	}
	
	public void insertChild(String URLParent, Vector<String> URLChild, String urlID) throws IOException{
		if(child.getEntry(urlID) == null){
			child.addEntry(urlID, URLChild);
		}else{
			child.delEntry(urlID);
			child.addEntry(urlID, URLChild);
		}
		
	}
	
	public HashStruc getParent(){
		return parent;
	}
	
	public HashStruc getChild(){
		return child;
	}
	
	public HashStruc getScore(){
		return pageRankScore;
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
	
	public static void calculateScore(Page myPage) throws IOException{
		double dFactor = 0.85;
		int turn = 20;
		HashStruc temp = new HashStruc(recman,"tempScore");
		pageRankScore = new HashStruc(recman,"Score");
		
		//initialize
		FastIterator fi = child.getIterator();
		String key;
		while((key = (String)fi.next()) != null){
			double initScore = 1;
			temp.addEntry(key, initScore);
			pageRankScore.addEntry(key, initScore);
		}
		
		//Start looping
		for(int i=0; i<turn; i++){
			fi = child.getIterator();
			
			while((key = (String)fi.next()) != null){
				//Get the set of one of the PR(A), key equals to me
				Vector<String> tempVector = (Vector<String>) parent.getEntry(key);
				double partialScore = 0;
				
				//Calculate the partial score of one of the A
				for(int j = 0; j < tempVector.size(); j++){	
					String myParentURL = tempVector.get(j);
					String urlID = myPage.getId(myParentURL);
					
					if(urlID != null){
						Vector<String> tempChild = (Vector<String>) child.getEntry(urlID);
						//Get C(T)
						int linkOfParent = 0;
						for(int k = 0; k < tempChild.size(); k++){	
							linkOfParent++;
						}
						//Get PR(T)
						double parentScore = (double)temp.getEntry(urlID);
						
						partialScore = partialScore + (parentScore / linkOfParent);
					}
				}
				
				double totalScore = (1-dFactor) + dFactor*partialScore;
				pageRankScore.delEntry(key);
				pageRankScore.addEntry(key, totalScore);
			}
			
			//Update temp Score
			FastIterator fi2 = pageRankScore.getIterator();
			String key2;
			
			while((key2 = (String)fi2.next()) != null){
				temp.delEntry(key2);
				double score2 = (double)pageRankScore.getEntry(key2);
				temp.addEntry(key2, score2);
			}
			
		}
		//end of looping
		//end of function
	}
	
	public static void printScore() throws IOException{
		FastIterator fi = pageRankScore.getIterator();
		int count = 1;
		String key;
		while((key = (String)fi.next()) != null){
			System.out.print("[ " + count + " ]   " + key + " has the score : ");
			double temp = (double) pageRankScore.getEntry(key);
			System.out.println(temp);
			
			count++;
		}
	}
	
	//It is Url not UrlID
	public void deleteChildParent(String Url, Page myPage) throws IOException{
		String tempURLID = myPage.getId(Url);
		
		Vector<String> myChild = (Vector<String>)child.getEntry(tempURLID);
		//Deleting the parent hashstruct which contains the target as the parent
		for(int i = 0; i < myChild.size(); i++){	
			String temp = myChild.get(i);
			String tempID = myPage.getId(temp);
			
			Vector<String> tempParent = (Vector<String>)parent.getEntry(tempID);
			tempParent.removeElement(Url);
			parent.delEntry(tempID);
			parent.addEntry(tempID, tempParent);
			
		}
		
		//Deleting the child hashstruct which contains the target as the child
		Vector<String> myParent = (Vector<String>)parent.getEntry(tempURLID);
		//Deleting the parent hashstruct which contains the target as the parent
		for(int i = 0; i < myParent.size(); i++){	
			String temp = myParent.get(i);
			String tempID = myPage.getId(temp);
			
			Vector<String> tempChild = (Vector<String>)child.getEntry(tempID);
			tempChild.removeElement(Url);
			child.delEntry(tempID);
			child.addEntry(tempID, tempChild);
		}
		
		parent.delEntry(tempURLID);
		child.delEntry(tempURLID);
	}
}
