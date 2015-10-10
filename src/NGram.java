import java.util.HashMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class NGram implements Serializable {

    private final static int OPTIMAL_SENTENCE_LENGTH = 10;

    // The amount of words this n-gram depends on
    public final int n;
    public final HashMap<String, Integer> occurrences;
    public int sumOccurrences;


    public NGram(int n) {
        this.n = n;
        this.occurrences = new HashMap<>();
        sumOccurrences = 0;
    }

    /**
     * Adds statistics to this n-gram in regard to which word come after it,
     * by adding to the amount of times that word has occurred
     * @param word the observed word
     */
    public void addObservation(String word) {
        int next = 1;
        if (occurrences.containsKey(word)) {
            next = occurrences.get(word) + 1;
        }
        occurrences.put(word, next);
        sumOccurrences += 1;
    }
    
    /** 
     * Get a randomly distributed word over this set of words.
     */
    public String getNextWord(int currentSentenceLength) {
        // Get the most common word
        ArrayList<String> commonWords = new ArrayList<>();
        int maxOccurrences = 0;
        for (String w : occurrences.keySet()) {
            int wordValue = occurrences.get(w);

            // Treat end-of-sentence differently depending on current sentence length
            if (w.endsWith(".")) {
                wordValue = periodEvaluation(wordValue, currentSentenceLength);
            }

            if (maxOccurrences < wordValue) {
                maxOccurrences = wordValue;
                commonWords.clear();
                commonWords.add(w);
            } else if (maxOccurrences == wordValue) {
                commonWords.add(w);
            }
        }

        String nextWord = getRandomWord(commonWords);
        occurrences.put(nextWord, Math.max(1, occurrences.get(nextWord) - 1));
        return nextWord;
    }

    private int periodEvaluation(int value, int currentSentenceLength) {
        return (int) (Math.abs(currentSentenceLength - OPTIMAL_SENTENCE_LENGTH) * 0.2 * value);
    }

    public String getRandomWord(ArrayList<String> list) {
        Random rng = new Random();

        int randInt = rng.nextInt(list.size()); //0 -> size -1
        return list.get(randInt);

    }
    public String getDistributedWord() {
        Random rng = new Random();
        int index = rng.nextInt(sumOccurrences);
        for (String word : occurrences.keySet()) {
            index -= occurrences.get(word);
            if (index <= 0) {
                return word;
            }
        }
        return null;
    }
    
    public int getOccurrences() {
    	return sumOccurrences;
    }
    
    public boolean isEmpty() {
    	return occurrences.isEmpty();
    }
}
