
import java.util.*;
import java.io.*;

/**
 * Reads corpus into hashmap
 */
public class CorpusParser {
    private final static int N = 1; // Size of n-grams
    private static ArrayList<String> startWords;
    public static HashMap<String, Integer> allWords; //Needed for interpolation
    public static int numberOfWords = 0;
	
		
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
	
	public static ArrayList<String> getStartWords() {
	    return startWords;
	}
	
	//Read the corpus file into n-grams
    public static ArrayList<NGrams> readCorpus(String path) throws IOException {
		//Grams
		HashMap<String, NGram> bi_grams = new HashMap<>();
		HashMap<String, NGram> tri_grams = new HashMap<>();
		HashMap<String, NGram> quad_grams = new HashMap<>();
		startWords = new ArrayList<String>();
        allWords = new HashMap<String, Integer>();
		
		//Reader
        BufferedReader br = new BufferedReader(new FileReader(path));
        
        //List of previous words, index 0 contains most recent, size <= 3
        LinkedList<String> wordQueue = new LinkedList<String>();
        
        //Treat header separately
        String header = br.readLine();
        
        //Go through the corpus
        String line = br.readLine();
        while (line != null) {
            //Remove unnecesary whitespace
            line = line.trim().replaceAll("\\s+", " ");
            
            
            //Replace -- with ,
            line = line.replaceAll(" --", ",");
            line = line.replaceAll(" –-", ",");
               
            //Split line into words    
            String[] words = line.split(" ");
            
            //Iterate through all the words on this line
            for (String word : words) {
                //Avoid extra whitespace
                word = word.trim();
            
                //Caps check except I and A
                if(word.matches("([A-Z])+(:?)")&& !word.equals("I") 
                        && !word.equals("A")) {
                    continue;
                }
                
                // (Apllause and laughter)
                if(word.startsWith("(") && word.endsWith(")")) continue;
                
                //Count number of occurances in the entire corpus (Used for Interpolation)
                if(allWords.containsKey(word)) {
                    allWords.put(word, 1 + (allWords.get(word)));
                } else {
                    allWords.put(word, 1);
                }
                numberOfWords += 1;

                //BI-GRAMS
            	String oldWord_one = null;
            	if (wordQueue.size() >= 1) oldWord_one = wordQueue.get(0);
                if (oldWord_one != null) {
                    //Add this word as an observation in the bi-gram list
                    NGram tuple = bi_grams.get(oldWord_one);
                    if (tuple == null) {
                        tuple = new NGram(2);
                        bi_grams.put(oldWord_one, tuple);
                    }
                    tuple.addObservation(word);

                }
                                    
                //TRI-GRAMS //TODO Fixa exceptions from empty linkedlist
                String oldWord_two = null;
                if (wordQueue.size() >= 2) oldWord_two = wordQueue.get(1);
                if (oldWord_two != null) {
                    //Hash the multiple old words into one single string
                    String[] tri_words = new String[2];
                    tri_words[0] = oldWord_one;
                    tri_words[1] = oldWord_two;
                    String hash = StringHasher.hashWords(tri_words);
                    
                    //Add this word as an observation in the tri-gram list
                    NGram triple = tri_grams.get(hash);
                    if (triple == null) {
                        triple = new NGram(3);
                        tri_grams.put(hash, triple);
                    }
                    triple.addObservation(word);
                }
                
                //QUAD-GRAMS
                String oldWord_three = null;
                if (wordQueue.size() >= 3) oldWord_three = wordQueue.get(2);
                if (oldWord_three != null) {
                    //Hash the multiple old words into one single string
                    String[] quad_words = new String[3];
                    quad_words[0] = oldWord_one;
                    quad_words[1] = oldWord_two;
                    quad_words[2] = oldWord_three;
                    String hash = StringHasher.hashWords(quad_words);
                    
                    //Add this word as an observation in the quad-gram list
                    NGram quadruple = quad_grams.get(hash);
                    if (quadruple == null) {
                        quadruple = new NGram(4);
                        quad_grams.put(hash, quadruple);
                    }
                    quadruple.addObservation(word);
                }

                //Append to list of start words (possibly)
                if (word.length() > 1 && !word.equals("And") 
                        && word.substring(0, 1).matches("[A-Z]")) {
                    startWords.add(word);
                }

                //Update old word
                if (oldWord_three != null) wordQueue.removeLast();
                wordQueue.addFirst(word);
            }
            
            //Read next line
            line = br.readLine();
        }
		br.close();
		NGrams bi_gram = new NGrams(bi_grams);
		NGrams tri_gram = new NGrams(tri_grams);
		NGrams quad_gram = new NGrams(quad_grams);
		
		ArrayList<NGrams> ngrams = new ArrayList<NGrams>();
		ngrams.add(bi_gram);
		ngrams.add(tri_gram);
		ngrams.add(quad_gram);


		return ngrams;
    }
}
