import java.io.IOException;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;

public class Prescores {
	private RecordManager recman;
	private HashStruc maxtf;
	private HashStruc sqrtdoclength;
	private HashStruc idf;
	
	Prescores(RecordManager myRecman)  throws IOException
	{
		recman = myRecman;
		maxtf = new HashStruc(myRecman,"maxtf");
		sqrtdoclength = new HashStruc(myRecman,"sqrtdoclength");
		idf = new HashStruc(myRecman,"idf");
	}
	
	public void insertidf(String word_id, double tidf) throws IOException
	{
		idf.addEntry(word_id, tidf);
	}
	
	public void insertmaxtf(String page_id, int tmaxtf) throws IOException
	{
		maxtf.addEntry(page_id, tmaxtf);
	}
	
	public void insertsqrtdoclength(String page_id, double tlength) throws IOException
	{
		sqrtdoclength.addEntry(page_id, tlength);
	}
	
	public double getidf(String ID) throws IOException
	{
		if(ID == null || idf.getEntry(ID) == null)
			return 0;
		
		return  (Double) idf.getEntry(ID);
	}
	
	public int getmaxtf(String ID) throws IOException
	{
		if(ID == null || maxtf.getEntry(ID) == null)
			return 0;
		
		return  (Integer) maxtf.getEntry(ID);
	}
	
	public double getsqrtdoclength(String ID) throws IOException
	{
		if(ID == null || sqrtdoclength.getEntry(ID) == null)
			return 0;
		
		return  (Double) sqrtdoclength.getEntry(ID);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			RecordManager recman = RecordManagerFactory.createRecordManager("searchEngine");
			Prescores prescores = new Prescores(recman);
			Page page_storage = new Page(recman);
			Word word_storage = new Word(recman);
			FastIterator fi = page_storage.getIteratorForPageID();
			FastIterator fi1 = word_storage.getIteratorForWordID();
			FastIterator fi2 = page_storage.getIteratorForPageID();
			String key;
			String key1;
			PageInfoStruct pis;
			
			while((key = (String)fi.next()) != null)
			{
				Vector<InvertPosting> ibw = word_storage.getIBodyWord(key);
				int x = 0;
				double y = 0;
				if(ibw != null && ibw.size() > 0)
				{
					for(int i = 0; i<ibw.size(); i++)
					{
						int freq = ibw.get(i).freq;
						if(freq > x)
						{
							x=freq;
						}
					}
				}
				
				prescores.insertmaxtf(key, x);
				
				if(ibw != null && ibw.size() > 0)
				{
					for(int i = 0; i<ibw.size(); i++)
					{
						int freq = ibw.get(i).freq;
						
						String z=ibw.get(i).word_id;
						String k = word_storage.getWordID(z);
						Vector<Posting> abc = word_storage.getBodyWord(k);
						int df = 0;
						if(abc != null)
							df = abc.size();
						
						if(x!=0 && df!=0)
							y += Math.pow(((double)freq/x)*Math.log((double)300/df)/Math.log(2), 2);
						
					}
				}
				y = Math.sqrt(y);
				prescores.insertsqrtdoclength(key, y);
			}
			
			while((key1 = (String)fi1.next()) != null)
			{
				Vector<Posting> abc = word_storage.getBodyWord(key1);
				int df = 0;
				double idf = 0;
				if(abc != null)
					df = abc.size();
				if(df !=0)
					idf = Math.log((double)300/df)/Math.log(2);
				prescores.insertidf(key1, idf);
			}
			recman.commit();
			recman.close();
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}
