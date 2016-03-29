import IRUtilities.*;
import java.io.*;

public class StopStem
{
	private Porter porter;
	private java.util.HashSet stopWords;
	public boolean isStopWord(String str)
	{
		return stopWords.contains(str);	
	}
	public StopStem(String str)
	{
		super();
		porter = new Porter();
		stopWords = new java.util.HashSet();
		String abc;
		try{
			File myFile = new File("src/stopwords.txt");
			BufferedReader br = new BufferedReader(new FileReader(myFile));
			while ((abc = br.readLine()) != null) {
				stopWords.add(abc);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		/*
		stopWords.add("is");
		stopWords.add("am");
		stopWords.add("are");
		stopWords.add("was");
		stopWords.add("were");
		*/

		
	}
	public String stem(String str)
	{
		return porter.stripAffixes(str);
	}
	public static void main(String[] arg)
	{
		StopStem stopStem = new StopStem("stopwords.txt");
		String input="";
		try{
			do
			{
				System.out.print("Please enter a single English word: ");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				input = in.readLine();
				if(input.length()>0)
				{	
					if (stopStem.isStopWord(input))
						System.out.println("It should be stopped");
					else
			   			System.out.println("The stem of it is \"" + stopStem.stem(input)+"\"");
				}
			}
			while(input.length()>0);
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
}