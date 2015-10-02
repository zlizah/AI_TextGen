import java.util.HashMap;

public class NGram {

    // The amount of words this n-gram depends on
    public final int n;
    public final HashMap<String, Integer> occurences;

    public NGram(int n) {
        this.n = n;
        this.occurences = new HashMap<>();
    }

    public void addObservation(String word) {
    }


    public String getNextWord() {
        return null;
    }
}
