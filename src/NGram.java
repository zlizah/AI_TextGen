import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NGram implements Serializable {

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
        ArrayList<String> commonWords = new ArrayList<>();
        int maxOccurences = 0;
        for (String w : occurences.keySet()) {
            if (maxOccurences < occurences.get(w)) {
                maxOccurences = occurences.get(w);
                commonWords.clear();
                commonWords.add(w);
            } else if (maxOccurences == occurences.get(w)) {
                commonWords.add(w);
            }
        }

        String nextWord = getRandomWord(commonWords);
        occurences.put(nextWord, Math.max(1, occurences.get(nextWord) - 1));
        return nextWord;
    }

    public String getRandomWord(ArrayList<String> list) {
        Random rng = new Random();

        int randInt = rng.nextInt(list.size()); //0 -> size -1
        return list.get(randInt);
    }
}
