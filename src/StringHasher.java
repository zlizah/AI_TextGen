
/**
 * Provides static methods for hashing the string used in the N-grams
 */
class StringHasher {
    /**
     * Hashes the given lis of strings into one strings using hashtags (#)
     */
    public static String hashWords(String[] words) {
        String retString = words[0];
        for (int i = words.length-1; i >= 0; i--) {
            retString += "#" + words[i];
        }
        return retString;
    }
}
