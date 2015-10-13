
import java.util.*;
import java.io.*;

//TODO skiljetecken
//TODO fixa så att vissa saker är parametrar/nånting istället för hårdkodat
//TODO konstiga tecken som uppkommer

class Main {
    //Print n-grams to file
    private static void printToFile(HashMap<String, NGram> n_gram) {
        //Print one gram map to a file
        try {
            FileOutputStream fileOut = new FileOutputStream("../ngrams/onegram.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(n_gram);
            out.close();
            fileOut.close();
        } catch(IOException i) {
            System.out.println("Exception occurred");
            i.printStackTrace();
        }
    }

    //Prints hashmap contents to syso
    private static void printMapContents(HashMap<String, ArrayList<String>> n_gram) {
        //View map contents
        Set<String> keyset = n_gram.keySet();
        for (String key : keyset) {
            if (key != null) {
                //System.out.println("Key element was: " + key);
                //System.out.println("Key array has size: " + key.size());
                System.out.print("Key was: " + key);
                ArrayList<String> words = n_gram.get(key);
                System.out.print(" with length " + words.size() + " and words: ");

                //Print the contents of this word
                for (String word : words) {
                    System.out.print(word + ", ");
                }
                System.out.println();
            }
        }
    }

    //Main
    public static void main(String[] args) throws IOException {
        int words = 300;
        ArrayList<NGrams> ngrams = new ArrayList<>();

        //Check arg exists
        if (args.length == 0) {
            System.out.println("No argument provided");
            System.exit(0);
        }

        //Check arg to determine if read from file or korpus
        switch (args[0]) {
            case "file":
                //Read map from file
                //ngrams = CorpusParser.readNgramsFromFile("../ngrams/onegram.ser");

                //Error check
                //		if (ngrams == null) {
                //	System.out.println("Could not read ngrams from file");
                //}
                break;
            case "corpus":
                //Read map from korpus
                ngrams = CorpusParser.readCorpus();
                //printToFile(oneGrams); Doesnt work atm

                //Error check
                if (ngrams == null) {
                    System.out.println("Could not read ngrams from corpus");
                }
                break;
            default:
                System.out.println("Invalid argument");
                System.exit(0);
        }

        //Send n-grams to TextGenerator
        TextGenerator textGen = new TextGenerator(ngrams);

        //Generate text
        String text = textGen.generateText(words);
        text = text.replaceAll("\\s+", " ");
        
        //Print text
        System.out.println("\n***************************\n");
        System.out.println(text);
        System.out.println("\n***************************\n");
    }
}

















