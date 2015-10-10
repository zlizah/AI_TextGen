import java.util.*;

/**
 * Collection of n-grams
 */
class NGrams {
    //Fields
    private final HashMap<String, NGram> map;

    private final static int OPTIMAL_SENTENCE_LENGTH = 10;

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
        return (double) CorpusParser.allWords.get(w) / (double) CorpusParser.numberOfWords;
    }


    public static double sentenceEvaluation(String word, int value, int currentSentenceLength) {
        // We prefer to start with start words
        if (currentSentenceLength == 0 ^ CorpusParser.getStartWords().contains(word)) {
            return ((double)value)/2;
        }

        if (isTerminal(word)) {
            return value + (currentSentenceLength - OPTIMAL_SENTENCE_LENGTH) * 0.5 * value;
        }
        return value;
    }

    public static boolean isTerminal(String word) {
        return (word.endsWith(".") || word.endsWith("?") || word.endsWith("!"));
    }

    /**
     * Interpolates quadgrams into a hashmap containing probabilities
     */
    public static HashMap<String, Double> quadPolarWord(NGram biWord, NGram triWord, NGram quadWord, int sentenceLength) {
        HashMap<String, Double> probabilities = new HashMap<>();
        ArrayList<String> possibleWords = new ArrayList<>(CorpusParser.allWords.keySet());

        // Make sure to keep bi-gram probability above 0 as a fallback
        double[] lambdas = {0.03, 0.12, 0.4, 0.45};

        for (String word : possibleWords) {
            double uniOcc = getUniOcc(word); //P(w_n)
            double biOcc = biWord.getNOcc(word, sentenceLength); //P(w_n | w_n-1)
            double triOcc = triWord.getNOcc(word, sentenceLength); //P(w_n | w_n-1 w_n-2)
            double quadOcc = quadWord.getNOcc(word, sentenceLength); //P(w_n | w_n-1 w_n-2 w_n-3)

            double interpolatedProbability = lambdas[3] * quadOcc + lambdas[2] * triOcc + lambdas[1] * biOcc + lambdas[0] * uniOcc; //Simple linear interpolation

            probabilities.put(word, interpolatedProbability);
        }

        return probabilities;
    }
}