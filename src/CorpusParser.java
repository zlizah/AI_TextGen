import java.util.*;
import java.io.*;

/**
 * Reads corpus into hashmap
 */
class CorpusParser {
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
    public static ArrayList<NGrams> readCorpus() throws IOException {
		//Grams
		HashMap<String, NGram> bi_grams = new HashMap<>();
		HashMap<String, NGram> tri_grams = new HashMap<>();
		HashMap<String, NGram> quad_grams = new HashMap<>();
		startWords = new ArrayList<>();
        allWords = new HashMap<>();
		
		//Reader
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("../corpus/corpus_speeches.txt"));
        } catch (FileNotFoundException e) {
            br = new BufferedReader(new FileReader("corpus/corpus_speeches.txt"));
        }
        //List of previous words, index 0 contains most recent, size <= 3
        LinkedList<String> wordQueue = new LinkedList<>();

        //Go through the corpus
        String line = br.readLine();
        while (line != null) {
            //Remove unnecessary whitespace
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

                if(word.equals("") ||word.equals(" ")) {
                    continue;
                }
                
                // (Applause and laughter)
                if(word.startsWith("(") && word.endsWith(")")) continue;
                
                //Count number of occurrences in the entire corpus (Used for Interpolation)
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
                        tuple = new NGram();
                        bi_grams.put(oldWord_one, tuple);
                    }
                    tuple.addObservation(word);

                }
                                    
                //TRI-GRAMS //TODO Fix exceptions from empty linked list
                String oldWord_two = null;
                if (wordQueue.size() >= 2) oldWord_two = wordQueue.get(1);
                if (oldWord_two != null) {
                    //Hash the multiple old words into one single string
                    String hash = String.format("%s %s", oldWord_one, oldWord_two);
                    
                    //Add this word as an observation in the tri-gram list
                    NGram triple = tri_grams.get(hash);
                    if (triple == null) {
                        triple = new NGram();
                        tri_grams.put(hash, triple);
                    }
                    triple.addObservation(word);
                }
                
                //QUAD-GRAMS
                String oldWord_three = null;
                if (wordQueue.size() >= 3) oldWord_three = wordQueue.get(2);
                if (oldWord_three != null) {
                    //Hash the multiple old words into one single string
                    String hash = String.format("%s %s %s", oldWord_one, oldWord_two, oldWord_three);
                    
                    //Add this word as an observation in the quad-gram list
                    NGram quadruple = quad_grams.get(hash);
                    if (quadruple == null) {
                        quadruple = new NGram();
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
		
		ArrayList<NGrams> ngrams = new ArrayList<>();
		ngrams.add(bi_gram);
		ngrams.add(tri_gram);
		ngrams.add(quad_gram);


		return ngrams;
    }
}
