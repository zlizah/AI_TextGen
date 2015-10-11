import java.util.*;
import java.io.*;

/**
 * Reads corpus into hashmap
 */
class CorpusParser {
    private static HashSet<String> startWords;
    public static int numberOfWords = 0;
	
		
	//Read unigram hashmap from file
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
	
	public static HashSet<String> getStartWords() {
	    return startWords;
	}
	
	//Read the corpus file into n-grams
    public static ArrayList<NGrams> readCorpus() throws IOException {
		//Grams
        HashMap<String, NGram> uni_grams = new HashMap<>();
		HashMap<String, NGram> bi_grams = new HashMap<>();
		HashMap<String, NGram> tri_grams = new HashMap<>();
		HashMap<String, NGram> quad_grams = new HashMap<>();
		startWords = new HashSet<>();
		
		//Reader
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("../corpus/corpus_TED_speeches.txt"));
        } catch (FileNotFoundException e) {
            br = new BufferedReader(new FileReader("corpus/corpus_TED_speeches.txt"));
        }
        //List of previous words, index 0 contains most recent, size <= 3
        LinkedList<String> wordQueue = new LinkedList<>();

        //Go through the corpus
        String line = br.readLine();
        while (line != null) {
            //Remove timestamp lines
            if (line.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
                line = br.readLine();
                continue;
            }
            
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

                if(word.equals("") || word.equals(" ")) {
                    continue;
                }
                
                // (Applause and laughter)
                if(word.startsWith("(") && word.endsWith(")")) continue;
                
//                //Count number of occurrences in the entire corpus (Used for Interpolation)
//                if(allWords.containsKey(word)) {
//                    allWords.put(word, 1 + (allWords.get(word)));
//                } else {
//                    allWords.put(word, 1);
//                }
//                numberOfWords += 1;
                getDefaultGram(uni_grams, "UNI").addObservation(word);


                //BI-GRAMS
                if (wordQueue.size() >= 1) {
                    //Add this word as an observation in the bi-gram list
                    NGram tuple = getDefaultGram(bi_grams, wordQueue.get(0));
                    tuple.addObservation(word);

                }
                                    
                //TRI-GRAMS //TODO Fix exceptions from empty linked list
                if (wordQueue.size() >= 2) {
                    //Hash the multiple old words into one single string
                    String hash = String.format("%s %s", wordQueue.get(0), wordQueue.get(1));
                    
                    //Add this word as an observation in the tri-gram list
                    NGram triple = getDefaultGram(tri_grams, hash);
                    triple.addObservation(word);
                }
                
                //QUAD-GRAMS
                if (wordQueue.size() >= 3) {
                    //Hash the multiple old words into one single string
                    String hash = String.format("%s %s %s", wordQueue.get(0), wordQueue.get(1), wordQueue.get(2));
                    
                    //Add this word as an observation in the quad-gram list
                    NGram quadruple = getDefaultGram(quad_grams, hash);
                    quadruple.addObservation(word);
                }

                //Append to list of start words (possibly)
                if (word.length() > 1 && !word.equals("And") 
                        && word.substring(0, 1).matches("[A-Z]")
                        && !NGrams.isTerminal(word)) {
                    startWords.add(word);
                }

                //Update old word
                if (wordQueue.size() >= 3) wordQueue.removeLast();
                wordQueue.addFirst(word);
            }
            
            //Read next line
            line = br.readLine();
        }
		br.close();
        NGrams uni_gram = new NGrams(uni_grams);
		NGrams bi_gram = new NGrams(bi_grams);
		NGrams tri_gram = new NGrams(tri_grams);
		NGrams quad_gram = new NGrams(quad_grams);
		
		ArrayList<NGrams> ngrams = new ArrayList<>();
        ngrams.add(uni_gram);
		ngrams.add(bi_gram);
		ngrams.add(tri_gram);
		ngrams.add(quad_gram);


		return ngrams;
    }

    private static NGram getDefaultGram(HashMap<String, NGram> bi_grams, String key) {
        NGram tuple = bi_grams.get(key);
        if (tuple == null) {
            tuple = new NGram();
            bi_grams.put(key, tuple);
        }
        return tuple;
    }
}
