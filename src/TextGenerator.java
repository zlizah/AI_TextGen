import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Text generator
 */
class TextGenerator {
    //Fields
    private final NGrams biGrams;
    private final NGrams triGrams;
    private final NGrams quadGrams;

    //Constructor
    public TextGenerator(ArrayList<NGrams> nGrams) {
        biGrams = nGrams.get(0);
        triGrams = nGrams.get(1);
        quadGrams = nGrams.get(2);

    }

    //Generator
    public String generateText(int words) {
        //Generate a random first word
        ArrayList<String> startWords = CorpusParser.getStartWords();
        Random rng = new Random();
        int randFirstInt = rng.nextInt(startWords.size()); //0 -> size -1
        String startWord = startWords.get(randFirstInt);
        return generateText(words, startWord);
    }


    /**
     * Interpolates given ngrams given probabilities of given words. Can create "new" ngrams.
     */
    private String getInterpolatedWord(NGram biWord, NGram triWord, NGram quadWord, int sentenceLength) {

        HashMap<String, Double> probabilities = quadPolarWord(biWord, triWord, quadWord);

        String chosenWord = chooseWord(probabilities);


        if(sentenceLength == 0) {
            // Capitalize first word of sentence and handle null case
            if (chosenWord != null && chosenWord.length() > 0) {
                chosenWord = chosenWord.substring(0, 1).toUpperCase() + chosenWord.substring(1);
            } else {
                chosenWord = "";
            }
        }

        return chosenWord;
    }


    /**
     * Interpolates quadgrams into a hashmap containing probabilities
     */
    private HashMap<String, Double> quadPolarWord(NGram biWord, NGram triWord, NGram quadWord) {
        HashMap<String, Double> probabilities = new HashMap<>();
        ArrayList<String> possibleWords = new ArrayList<>(CorpusParser.allWords.keySet());

        // Make sure to keep bi-gram probability above 0 as a fallback
        double[] lambdas = {0.03, 0.12, 0.4, 0.45};

        for (String word : possibleWords) {
            double uniOcc = getUniOcc(word); //P(w_n)
            double biOcc = biWord.getNOcc(word); //P(w_n | w_n-1)
            double triOcc = triWord.getNOcc(word); //P(w_n | w_n-1 w_n-2)
            double quadOcc = quadWord.getNOcc(word); //P(w_n | w_n-1 w_n-2 w_n-3)

            double interpolatedProbability = lambdas[3] * quadOcc + lambdas[2] * triOcc + lambdas[1] * biOcc + lambdas[0] * uniOcc; //Simple linear interpolation

            probabilities.put(word, interpolatedProbability);
        }

        return probabilities;
    }

    /**
     *	Compute how much the given word appear in the corpus in comparison to all other words.
     */
    private double getUniOcc(String w) {
        int occs = CorpusParser.allWords.get(w); //Assumed to work correctly for now
        return (double) occs / (double) CorpusParser.numberOfWords;
    }

    /**
     * Choose a random word from a set of words with a certain probabilistic value.
     */
    private String chooseWord(HashMap<String, Double> probabilities) {
        double interpolSum = 0.0;
        ArrayList<String> possibleWords = new ArrayList<>(CorpusParser.allWords.keySet());
        for (String word : possibleWords) {
            interpolSum += probabilities.get(word);
        }

        Random rng = new Random();
        double index = rng.nextDouble() * interpolSum; //Between 0 and interpolSum

        double sum = 0;
        for (String word : possibleWords) {
            index -= probabilities.get(word);
            if (index <= 0) {
                return word;
            }
            sum += probabilities.get(word);
        }
        System.out.println("ERROR: " + index);
        System.out.println("SUM: " + sum);
        return null; //Error
    }

    /**
     * Generate a bunch of text
     * @param words the amount of words in the text
     * @param firstWord the initial start word. Might be several words.
     * @return the text
     */
    private String generateText(int words, String firstWord) {
        StringBuilder text = new StringBuilder();
        text.append(firstWord);
        LinkedList<String> wordQueue = new LinkedList<>();
        wordQueue.addFirst(firstWord);
        Random rng = new Random();

        //Loop until enough words, make sure to end text with a period (.)
        int sentenceLength = 1;
        for (int index = 0; index < words || !wordQueue.getFirst().contains("."); ++index) {
            //Fetch old words
            String oldWord_one = wordQueue.getFirst();
            String oldWord_two = wordQueue.size() >= 2 ? wordQueue.get(1) : "";
            String oldWord_three = wordQueue.size() >= 3 ? wordQueue.get(2) : "";

            String triHash = String.format("%s %s", oldWord_one, oldWord_two);
            String quadHash = String.format("%s %s %s", oldWord_one, oldWord_two, oldWord_three);

            //Generate next word, depending on the amount of available grams
            String nextWord = getInterpolatedWord(biGrams.getWordChoices(oldWord_one),
                    triGrams.getWordChoices(triHash),
                    quadGrams.getWordChoices(quadHash),
                    sentenceLength);

            // End sentence
            if (nextWord.endsWith(".")) {
                sentenceLength = 0;
            } else {
                ++sentenceLength;
            }

            text.append(" ");
            text.append(nextWord);

            //Update queue of old words
            if (oldWord_three != null) wordQueue.removeLast();
            wordQueue.addFirst(nextWord);
        }

        return text.toString();
    }
}
