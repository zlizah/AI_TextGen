import java.util.*;

/**
 * Collection of n-grams
 */
public class NGrams {
    //Fields
    public final HashMap<String, NGram> map;

    //Constructor
    public NGrams(HashMap<String, NGram> map) {
        this.map = map;
    }

    //Get getWordChoices
    public NGram getWordChoices(String oldWord) {
        NGram words = map.get(oldWord);
        if (words == null) {
        	//Return an empty NGram
            return new NGram(2);
        }
        return words;
    }

    //Fetch the keys
    public ArrayList<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }
}