
import java.util.*;

/**
 *
 */
public class N_gram {
    //Fields
    public final int n;
    public final HashMap<String, ArrayList<String>> map;

    //Constructor
    public N_gram(int n, HashMap<String, ArrayList<String>> map) {
        this.n = n;
        this.map = map;
    }

    //Get getWordChoices
    public ArrayList<String> getWordChoices(String oldWord) {
        ArrayList<String> words = map.get(oldWord);
        if (words != null) {
            return words;
        } else {
            words = map.get(oldWord.toLowerCase());
            if (words != null) {
                return words;
            } else {
                return null;
            }
        }
    }

    //Fetch the keys
    public ArrayList<String> getKeys() {
        ArrayList<String> keyList = new ArrayList<String>();

        //Append all keys to the list
        Set<String> keyset = map.keySet();
        Iterator<String> keyiter = keyset.iterator();
        while(keyiter.hasNext()) {
            String key = keyiter.next();
            keyList.add(key);
        }

        return keyList;
    }
}