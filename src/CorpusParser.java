
import java.util.*;
import java.io.*;

/**
 * Reads corpus into hashmap
 */
public class CorpusParser {
    private final static int N = 1;
//	//Reads n-grams from the provided korpus
//    public static HashMap<String, ArrayList<String>> readFromKorpus(String path) {
//		HashMap<String, NGram> n_grams = null;
//        try {
//            n_grams = readCorpus(path);
//        } catch (Exception e) {
//			System.out.println(e);
//            System.out.println("Exception occured in read");
//            System.exit(1);
//        }
//
//		return n_grams;
//    }
	
		
	//Read onegram hashmap from file
	@SuppressWarnings("unchecked")
	public static HashMap<String, NGram> readNgramsFromFile(String path) {
		//Read the mapobject from the file
		HashMap<String, NGram> n_grams;
		Object mapobj;
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
			n_grams = (HashMap<String, NGram>) mapobj;
		} else {
			System.out.println("Could not map file to hashmap");
			System.exit(2);
			n_grams = new HashMap<>();
		}
		
		//Print map contents
		return n_grams;
	}
	
	//Read the corpus file into n-grams
    public static HashMap<String, NGram> readCorpus(String path) throws IOException {
		//Variables
		HashMap<String, NGram> n_grams = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
       
        //Treat header separately
        String header = br.readLine();
        
        //Regular line
        String line = br.readLine();
        String oldWord = null;
        while (line != null) {
            String[] words = line.split(" ");
            
            //Iterate through all the words on this line
            for (String word : words) {
                //Find the index word
                NGram currentNGram = n_grams.get(oldWord);

                //If no mapping exists for the previous word, create a new list
                if (currentNGram == null) {
                    currentNGram = new NGram(N);
                    n_grams.put(oldWord, currentNGram);
                }
                currentNGram.addObservation(word);

                //Update old word
                oldWord = word;
            }
            
            //Read next line
            line = br.readLine();
        }
		
		return n_grams;
    }
}