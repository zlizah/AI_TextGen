import java.util.*;

/**
 * Text generator
 */
class TextGenerator {
    //Fields
    private final NGrams uniGrams;
    private final NGrams biGrams;
    private final NGrams triGrams;
    private final NGrams quadGrams;

    //Constructor
    public TextGenerator(ArrayList<NGrams> nGrams) {
        uniGrams = nGrams.get(0);
        biGrams = nGrams.get(1);
        triGrams = nGrams.get(2);
        quadGrams = nGrams.get(3);

    }

    //Generator
    public String generateText(int words) {
        //Generate a random first word
        HashSet<String> startWords = CorpusParser.getStartWords();
        Random rng = new Random();
        int randFirstInt = rng.nextInt(startWords.size()); //0 -> size -1
        String startWord = new ArrayList<>(startWords).get(randFirstInt);
        return generateText(words, startWord);
    }


    /**
     * Interpolates given ngrams given probabilities of given words.
     */
    private String getInterpolatedWord(NGram uniWord, NGram biWord, NGram triWord, NGram quadWord, int sentenceLength) {
        HashMap<String, Double> probabilities = NGrams.quadPolarWord(uniWord, biWord, triWord, quadWord, sentenceLength);

        String chosenWord = chooseWord(probabilities, uniWord);


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
     * Choose a random word from a set of words with a certain probabilistic value.
     */
    private String chooseWord(HashMap<String, Double> probabilities, NGram uniGram) {
        double interpolSum = 0.0;
        ArrayList<String> possibleWords = new ArrayList<>(uniGram.occurrences.keySet());
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

        //Loop until enough words, make sure to end text with a period (.)
        int sentenceLength = 1;
        for (int index = 0; index < words || !wordQueue.getFirst().contains("."); ++index) {
            //Fetch old words
            String oldWord1 = wordQueue.getFirst();
            String oldWord2 = wordQueue.size() >= 2 ? wordQueue.get(1) : "";
            String oldWord3 = wordQueue.size() >= 3 ? wordQueue.get(2) : "";

            String triHash = String.format("%s %s", oldWord1, oldWord2);
            String quadHash = String.format("%s %s %s", oldWord1, oldWord2, oldWord3);

            //Generate next word, depending on the amount of available grams
            String nextWord = getInterpolatedWord(uniGrams.getWordChoices("UNI"),
                    biGrams.getWordChoices(oldWord1),
                    triGrams.getWordChoices(triHash),
                    quadGrams.getWordChoices(quadHash),
                    sentenceLength);

            // End sentence
            if (nextWord.endsWith(".")) {
                System.err.println("Sentence length: " + sentenceLength);
                sentenceLength = 0;
            } else {
                ++sentenceLength;
            }
            text.append(" ");
            text.append(nextWord);

            //Update queue of old words
            if (oldWord3 != null) wordQueue.removeLast();
            wordQueue.addFirst(nextWord);
        }

        return text.toString();
    }
}
