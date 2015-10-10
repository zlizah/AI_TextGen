import java.util.HashMap;
import java.io.Serializable;

public class NGram implements Serializable {


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

    /**
     * Compute the probability that the given word can be followed by the given ngram.
     */
    public double getNOcc(String word, int sentenceLength) {
        double ret;

        if(occurrences.containsKey(word)) {
            ret = NGrams.sentenceEvaluation(word, occurrences.get(word), sentenceLength) / (double) sumOccurrences;
        } else {
            ret = 0;
        }
        return ret;
    }

}
