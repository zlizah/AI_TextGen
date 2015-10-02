import java.util.HashMap;

public class NGram {

    // The amount of words this n-gram depends on
    public final int n;
    public final HashMap<String, Integer> occurences;

    public NGram(int n) {
        this.n = n;
        this.occurences = new HashMap<>();
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
    }


    /**
     * Consider what words usually follow after this n-gram and return the most likely.
     * @return
     */
    public String getNextWord() {
        // Get the most common word
        String maxWord = "";
        for (String w : occurences.keySet()) {
            if (maxWord.equals("") || occurences.get(maxWord) < occurences.get(w)) {
                maxWord = w;
            }
        }
        return maxWord;
    }
}
