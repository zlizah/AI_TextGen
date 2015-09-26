
import java.util.*;
import java.io.*;

//TODO skiljetecken
//TODO generera text 
//TODO change hashset to list and randomize index

public class Main {
	//Print n-grams to file
	private static void printToFile(HashMap<String, ArrayList<String>> n_gram) {
		//Print one gram map to a file
		try {
			FileOutputStream fileOut = new FileOutputStream("../ngrams/onegram.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(n_gram);
			out.close();
			fileOut.close();
		} catch(IOException i) {
			System.out.println("Exception occured");
			i.printStackTrace();
		}
	}
	
	//Prints hashmap contents to syso
	private static void printMapContents(HashMap<String, ArrayList<String>> n_gram) {
		//View map contents
        Set<String> keyset = n_gram.keySet();
        Iterator<String> keyiter = keyset.iterator();
        while(keyiter.hasNext()) {
            String key = keyiter.next();
            if (key != null) {
                //System.out.println("Key element was: " + key);
                //System.out.println("Key array has size: " + key.size());
                System.out.print("Key was: " + key);
                ArrayList<String> words = n_gram.get(key);
                System.out.print(" with length " + words.size() + " and words: ");
                
                //Print the contents of this word
                Iterator<String> wordsiter = words.iterator();
                while (wordsiter.hasNext()) {
                    String word = wordsiter.next();
                    System.out.print(word + ", ");
                }
                System.out.println();   
            }
        }
	}
    
    //Main
    public static void main(String[] args) throws IOException, FileNotFoundException {
		int words = 100;
		ArrayList<N_gram> n_grams = new ArrayList<N_gram>();
        
		//Check arg exists
		if (args.length == 0) {
			System.out.println("No argument provided");
			System.exit(0);
		}
		
		//Check arg to determine if read from file or korpus
		HashMap<String, ArrayList<String>> oneGrams = null;
		if (args[0].equals("file")) {
			//Read map from file
			oneGrams = CorpusParser.readNgramsFromFile("../ngrams/onegram.ser");
			//printMapContents(oneGrams);
			
			//Error check
			if (oneGrams == null) {
				System.out.println("Could not read ngrams from file");
			}
		} else if (args[0].equals("corpus")) {
			//Read map from kropus
			oneGrams = CorpusParser.readFromKorpus("../corpus/corpus.txt");
			//printMapContents(oneGrams);
			printToFile(oneGrams);
			
			//Error check
			if (oneGrams == null) {
				System.out.println("Could not read ngrams from corpus");
			}
		} else {
			System.out.println("Invalid argument");
		}
		n_grams.add(new N_gram(1, oneGrams));
		
		//Send n-grams to TextGenerator
		TextGenerator textGen = new TextGenerator(n_grams);	
		
		//Generate text
		String text = textGen.generateText(words);
		System.out.println(text);
    }
}

















