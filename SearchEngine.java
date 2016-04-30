import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchEngine {

	public Vector<String> search(String input){
		
		Vector<String> query = new Vector<String>();
		StopStem ss = new StopStem("");
		
		//-----extract phrase----------------
		Vector<String> phrase = new Vector<String>();
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(input);
		while (m.find()) {
		  //System.out.println(m.group(1));
		  phrase.add(m.group(1));
		}
		
//		for(int i=0; i<phrase.size();i++){
//			System.out.println(i+": "+phrase.get(i));
//		}
		//------------extract word------------
		Vector<String> singleWord = new Vector<String>();
		String[] sw = input.split("\"([^\"]*)\"");
		for(int i=0; i<sw.length; i++)
		{
			String[] sw2 = sw[i].split(" ");
			for(int j=0; j<sw2.length;j++)
				if(!sw2[j].equals(" ")&&!sw2[j].equals(""))
					singleWord.add(sw2[j]);
		}
		
//		for(String temp: singleWord)
//			System.out.println(temp);
		
		//--------stop and stem------------------
		String[] temp = null;
		String temp_stopstem = "";
		for(int i=0; i<phrase.size();i++)
		{
			temp = phrase.get(i).split(" ");
			if(temp!=null && temp.length!=0)
			{
				for(int j=0; j<temp.length; j++)
				{
					if(ss.isStopWord(temp[j]))
						continue;
					else
					{
						temp_stopstem += ss.stem(temp[j]);
						if(j!=temp.length-1)
							temp_stopstem += " ";
					}
				}
				if(!phrase.get(i).equals(temp_stopstem))
					phrase.set(i, temp_stopstem);
			}
			
			temp_stopstem = "";
						
		}
		
//		for(int i=0; i<phrase.size();i++){
//			System.out.println(i+": "+phrase.get(i));
//		}
		
		for(int i=0; i<singleWord.size();i++)
		{
			if(ss.isStopWord(singleWord.get(i)))
				{
					singleWord.remove(i);
				}
			else
				singleWord.set(i, ss.stem(singleWord.get(i)));
		}
		
//		for(String temp2: singleWord)
//			System.out.println(temp2);
		
//		System.out.println(singleWord.size());
		//---------------combine result---------------
		for(String phrase_word: phrase)
			query.add(phrase_word);
		for(String single_word: singleWord)
			query.add(single_word);
		
		for(String q: query)
			System.out.println(q);
		
		
		
		
		return new Vector<String>();
	}
	
//	public String checkPhrase(String input){
//		
//		for(int i=0; i < input.length(); i++)
//		{
//			
//		}
//	}
	
	public static void main (String[] args){
		SearchEngine se = new SearchEngine();
		se.search("eating bb \"the Hong Kong\" cb \"Hong Kong3 eating\" db e");
	}
}
