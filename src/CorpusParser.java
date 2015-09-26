
import java.util.*;
import java.io.*;

/**
 * Reads corpus into hashmap
 */
public class CorpusParser {   
	//Reads n-grams from the provided korpus
    public static HashMap<String, ArrayList<String>> readFromKorpus(String path) {
		HashMap<String, ArrayList<String>> n_grams = null;
        try {
            n_grams = readCorpus(path);
        } catch (Exception e) {
			System.out.println(e);
            System.out.println("Exception occured in read");
            System.exit(1);
        }
		
		return n_grams;
    }
	
		
	//Read onegram hashmap from file
	@SuppressWarnings("unchecked")
	public static HashMap<String, ArrayList<String>> readNgramsFromFile(String path) {
		//Read the mapobject from the file
		HashMap<String, ArrayList<String>> n_grams = 
				new HashMap<String, ArrayList<String>>();
		Object mapobj = null;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			mapobj = in.readObject();
			in.close();
			fileIn.close();
		} catch(IOException i) {
			i.printStackTrace();
			return null;
		} catch(ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return null;
		}
		
		//Cast mapobj to a hashmap 
		if (mapobj instanceof HashMap<?, ?>) {
			n_grams = (HashMap<String, ArrayList<String>>) mapobj; 
		} else {
			System.out.println("Could not map file to hashmap");
			System.exit(2);
			n_grams = new HashMap<String, ArrayList<String>>();
		}
		
		//Print map contents
		return n_grams;
	}
	
	//Read the corpus file into n-grams
    private static HashMap<String, ArrayList<String>> readCorpus(String path) 
			throws IOException, FileNotFoundException {
		//Variables
		HashMap<String, ArrayList<String>> n_grams = 
				new HashMap<String, ArrayList<String>>();
        BufferedReader br = new BufferedReader(new FileReader(path));
       
        //Treate header separatly
        String header = br.readLine();
        
        //Regular line
        String line = br.readLine();
        String oldWord = null;
        while (line != null) {
            String[] words = line.split(" ");
            
            //Iterate through all the words on this line
            for (int i = 0; i < words.length; i++) {
                //Find the index word
                String word = words[i];
                ArrayList<String> currentWords = n_grams.get(oldWord);
                
                //If no mapping exists for the previous word, create a new list
                if (currentWords == null) {
                    currentWords = new ArrayList<String>();
                    currentWords.add(word);
                    n_grams.put(oldWord, currentWords);
                } 
                //Else just appends this word as one of the possibilityies
                else {
                    currentWords.add(word);
                }
                
                //Update old word
                oldWord = word;     
            }
            
            //Read next line
            line = br.readLine();
        }
		
		return n_grams;
    }
}