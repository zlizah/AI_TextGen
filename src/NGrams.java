import java.util.*;

/**
 * Collection of n-grams
 */
class NGrams {
    //Fields
    private final HashMap<String, NGram> map;

    //Constructor
    public NGrams(HashMap<String, NGram> map) {
        this.map = map;
    }

    //Get getWordChoices
    public NGram getWordChoices(String oldWord) {
        NGram words = map.get(oldWord);
        if (words == null) {
        	//Return an empty NGram
            return new NGram();
        }
        return words;
    }

    /**
     *	Compute how much the given word appear in the corpus in comparison to all other words.
     */
    private static double getUniOcc(String w) {
        int occs = CorpusParser.allWords.get(w); //Assumed to work correctly for now
        return (double) occs / (double) CorpusParser.numberOfWords;
    }

    /**
     * Interpolates quadgrams into a hashmap containing probabilities
     */
    public static HashMap<String, Double> quadPolarWord(NGram biWord, NGram triWord, NGram quadWord) {
        HashMap<String, Double> probabilities = new HashMap<>();
        ArrayList<String> possibleWords = new ArrayList<>(CorpusParser.allWords.keySet());

        // Make sure to keep bi-gram probability above 0 as a fallback
        double[] lambdas = {0.03, 0.12, 0.4, 0.45};

        for (String word : possibleWords) {
            double uniOcc = getUniOcc(word); //P(w_n)
            double biOcc = biWord.getNOcc(word); //P(w_n | w_n-1)
            double triOcc = triWord.getNOcc(word); //P(w_n | w_n-1 w_n-2)
            double quadOcc = quadWord.getNOcc(word); //P(w_n | w_n-1 w_n-2 w_n-3)

            double interpolatedProbability = lambdas[3] * quadOcc + lambdas[2] * triOcc + lambdas[1] * biOcc + lambdas[0] * uniOcc; //Simple linear interpolation

            probabilities.put(word, interpolatedProbability);
        }

        return probabilities;
    }

    //Fetch the keys
    public ArrayList<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }
}