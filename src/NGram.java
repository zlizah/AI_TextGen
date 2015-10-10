import java.util.HashMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class NGram implements Serializable {

    private final static int OPTIMAL_SENTENCE_LENGTH = 10;

    public final HashMap<String, Integer> occurrences;
    private int sumOccurrences;


    public NGram() {
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

    private int periodEvaluation(int value, int currentSentenceLength) {
        return (int) (Math.abs(currentSentenceLength - OPTIMAL_SENTENCE_LENGTH) * 0.2 * value);
    }

    private String getRandomWord(ArrayList<String> list) {
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
}
