
import java.util.*;
import java.io.*;

//TODO skiljetecken
//TODO generera text
//TODO Vikta orden för att välja "troligast"
//TODO change hashset to list and randomize index

public class Main {
    //Fields
    private HashMap<String, HashSet<String>> oneGrams;
    
    //Constructor
    public Main() {
        oneGrams = new HashMap<String, HashSet<String>>();
    }
    
    //Read the corpus file into n-grams
    private void read(String path) throws IOException, FileNotFoundException {
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
                HashSet<String> currentWords = oneGrams.get(oldWord);
                
                //If no mapping exists for the previous word, create a new list
                if (currentWords == null) {
                    currentWords = new HashSet<String>();
                    currentWords.add(word);
                    oneGrams.put(oldWord, currentWords);
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
    }
    
	//Reads n-grams from the provided korpus
    public void readFromKorpus(String path) {
        try {
            read(path);
        } catch (Exception e) {
            System.out.println("Exception occured in read");
            System.exit(1);
        }
        System.out.println("Reading successful");
        System.out.println("Map size was: " + oneGrams.size());
        
		//Print hashmap to syso
		printMapContents();
		
		//Print hashmap to a file
		printToFile();
    }

	//Print n-grams to file
	private void printToFile() {
		//Print one gram map to a file
		try {
			FileOutputStream fileOut = new FileOutputStream("ngrams/onegram.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(oneGrams);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in /ngrams/onegram.ser");
		} catch(IOException i) {
			System.out.println("Exception occured");
			i.printStackTrace();
		}
	}
	
	//Prints hashmap contents to syso
	private void printMapContents() {
		//View map contents
        Set<String> keyset = oneGrams.keySet();
        Iterator<String> keyiter = keyset.iterator();
        while(keyiter.hasNext()) {
            String key = keyiter.next();
            if (key != null) {
                //System.out.println("Key element was: " + key);
                //System.out.println("Key array has size: " + key.size());
                System.out.print("Key was: " + key);
                HashSet<String> words = oneGrams.get(key);
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
	
	//Read onegram hashmap from file
	@SuppressWarnings("unchecked")
	public void readNgramsFromFile(String path) {
		//Read the mapobject from the file
		oneGrams = new HashMap<String, HashSet<String>>();
		Object mapobj = null;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			mapobj = in.readObject();
			in.close();
			fileIn.close();
		} catch(IOException i) {
			i.printStackTrace();
			return;
		} catch(ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}
		
		//Cast mapobj to a hashmap 
		if (mapobj instanceof HashMap<?, ?>) {
			oneGrams = (HashMap<String, HashSet<String>>) mapobj; 
		} else {
			System.out.println("Could not map file to hashmap");
			System.exit(2);
			oneGrams = new HashMap<String, HashSet<String>>();
		}
		
		//Print map contents
		printMapContents();
	}
    
    //Main
    public static void main(String[] args) throws IOException, FileNotFoundException {
		Main program = new Main();
        
		//Check arg exists
		if (args.length == 0) {
			System.out.println("No argument provided");
			System.exit(0);
		}
		
		//check arg to determine if read from file or korpus
		if (args[0].equals("file")) {
			//Read map from file
			program.readNgramsFromFile("ngrams/onegram.ser");
		} else if (args[0].equals("korpus")) {
			//Read map from kropus
			program.readFromKorpus("korpus/korpus.txt");
		} else {
			System.out.println("Invalid argument");
		}
    }
}

















