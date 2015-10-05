
import java.util.HashMap;
import java.util.Random;

public class NGram {

    // The amount of words this n-gram depends on
    public final int n;
    public int sumOccurances;
    public final HashMap<String, Integer> occurences;

    public NGram(int n) {
        this.n = n;
        this.occurences = new HashMap<>();
        sumOccurances = 0;
    }

    /**
     * Adds statistics to this n-gram in regard to which word come after it,
     * by adding to the amount of times that word has occurred
     * @param word the observed word
     */
    public void addObservation(String word) {
        int next = 1;
        if (occurences.containsKey(word)) {
            next = occurences.get(word) + 1;
        }
        occurences.put(word, next);
        sumOccurances += 1;
    }
    
    /** 
     * Get a randomly distributed word over this set of words.
     */
    public String getDistributedWord() {
        Random rng = new Random();
        int index = rng.nextInt(sumOccurances);
        for (String word : occurences.keySet()) {
            index -= occurences.get(word);
            if (index <= 0) {
                return word;
            }
        }
        return null;
    }
    
    public int getOccurances() {
    	return sumOccurances;
    }
    
    public boolean isEmpty() {
    	return occurences.isEmpty();
    }
}
