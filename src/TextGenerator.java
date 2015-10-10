import java.util.*;

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


    /*
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

    /*
    * Intepolates bigrams into a hashmap containing probabilities
    */
    private HashMap<String, Double> biPolarWord(NGram biWord) {
        HashMap<String, Double> probabilities = new HashMap<>();
        ArrayList<String> possibleWords = new ArrayList<>(CorpusParser.allWords.keySet());

        double[] lambdas = new double[2];
        lambdas[0] = 0.05;
        lambdas[1] = 0.95;

        for (String word : possibleWords) {
            double uniOcc = getUniOcc(word); //P(w_n)

            double biOcc = getNOcc(biWord, word); //P(w_n |w_n-1)

            double interpolatedProbability = lambdas[1] * biOcc + lambdas[0] * uniOcc; //Simple linear interpolation

            probabilities.put(word, interpolatedProbability);
        }

        return probabilities;
    }

    /*
    * Intepolates trigrams into a hashmap containing probabilities
    */
    private HashMap<String, Double> triPolarWord(NGram triWord, NGram biWord) {
        HashMap<String, Double> probabilities = new HashMap<>();
        ArrayList<String> possibleWords = new ArrayList<>(CorpusParser.allWords.keySet());

        double[] lambdas = new double[3];
        lambdas[0] = 0.05;
        lambdas[1] = 0.25;
        lambdas[2] = 0.70;

        for (String word : possibleWords) {
            double uniOcc = getUniOcc(word); //P(w_n)

            double biOcc = getNOcc(biWord, word); //P(w_n | w_n-1)

            double triOcc = getNOcc(triWord, word); //P(w_n | w_n-1 w_n-2)

            double interpolatedProbability = lambdas[2] * triOcc + lambdas[1] * biOcc + lambdas[0] * uniOcc; //Simple linear interpolation

            probabilities.put(word, interpolatedProbability);
        }

        return probabilities;
    }


    /*
    * Intepolates quadgrams into a hashmap containing probabilities
    */
    private HashMap<String, Double> quadPolarWord(NGram triWord, NGram biWord, NGram quadWord) {
        HashMap<String, Double> probabilities = new HashMap<>();
        ArrayList<String> possibleWords = new ArrayList<>(CorpusParser.allWords.keySet());

        double[] lambdas = new double[4];
        lambdas[0] = 0.03;
        lambdas[1] = 0.12;
        lambdas[2] = 0.4;
        lambdas[3] = 0.45;

        for (String word : possibleWords) {
            double uniOcc = getUniOcc(word); //P(w_n)

            double biOcc = getNOcc(biWord, word); //P(w_n | w_n-1)

            double triOcc = getNOcc(triWord, word); //P(w_n | w_n-1 w_n-2)

            double quadOcc = getNOcc(quadWord, word); //P(w_n | w_n-1 w_n-2 w_n-3)

            double interpolatedProbability = lambdas[3] * quadOcc + lambdas[2] * triOcc + lambdas[1] * biOcc + lambdas[0] * uniOcc; //Simple linear interpolation

            probabilities.put(word, interpolatedProbability);
        }

        return probabilities;
    }

    /*
    *	Compute how much the given word appear in the corpus in comparison to all other words.
    */
    private double getUniOcc(String w) {
        int occs = CorpusParser.allWords.get(w); //Assumed to work correctly for now
        return (double) occs/ (double) CorpusParser.numberOfWords;
    }

    /*
	*	Compute the probability that the given word can be followed by the given ngram.
	*/
    private double getNOcc(NGram ngram, String word) {
        double ret;

        if(ngram.occurrences.containsKey(word)) {
            ret = (double) ngram.occurrences.get(word) / (double) ngram.getOccurrences();
        } else {
            ret = 0;
        }
        return ret;
    }

    /*
    *	Choose a random word from a set of words with a certain probabilistic value.
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

    private String getDistributedWord(NGram biWord, NGram triWord, NGram quadWord, int sentenceLength) {
        //TODO Empty lists?
        return getInterpolatedWord(biWord, triWord, quadWord, sentenceLength); //Use interpolation instead
		/*
		Random rng = new Random(65);
		
		int temp = 100;
		if (quadWord.isEmpty()) temp = 2;
		if (triWord.isEmpty()) temp = 1;
		if (biWord.isEmpty()) return null;
		int index = rng.nextInt(temp);
		
		//Very good distribution, DO NOT TOUCH! 20/60/20
		if (index < 20) {
			return biWord.getNextWord(sentenceLength);
		} else if (index < 60) {
			return triWord.getNextWord(sentenceLength);
		} else {
			return quadWord.getNextWord(sentenceLength);
		} */
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
        for (int index = 0; index < words || !wordQueue.getFirst().contains(".");
             index ++) {
            //Fetch old words
            String oldWord_one = wordQueue.getFirst();
            String oldWord_two = null;
            String oldWord_three = null;
            if (wordQueue.size() >= 2) {
                oldWord_two = wordQueue.get(1);
            }
            if (wordQueue.size() >= 3) {
                oldWord_three = wordQueue.get(2);
            }

            //Hash multiples
            String[] triList = {oldWord_one, oldWord_two};
            String[] quadList = {oldWord_one, oldWord_two, oldWord_three};
            String triHash = StringHasher.hashWords(triList);
            String quadHash = StringHasher.hashWords(quadList);

            //Generate next word, depending on the amount of available grams
            String nextWord;
            NGram empty = new NGram();
            if (oldWord_one == null) {
                nextWord = getDistributedWord(empty, empty, empty, sentenceLength);
            } else if (oldWord_two == null) {
                nextWord = getDistributedWord(biGrams.getWordChoices(oldWord_one), empty, empty, sentenceLength);
            } else if (oldWord_three == null) {
                nextWord = getDistributedWord(biGrams.getWordChoices(oldWord_one),
                        triGrams.getWordChoices(triHash), empty, sentenceLength);
            } else {
                nextWord = getDistributedWord(biGrams.getWordChoices(oldWord_one),
                        triGrams.getWordChoices(triHash), quadGrams.getWordChoices(quadHash), sentenceLength);
            }

            //Pick random word from biGrams if no choices found
            if (nextWord == null) {
                ArrayList<String> wordChoices = biGrams.getKeys();
                int randInt = rng.nextInt(wordChoices.size());
                nextWord = wordChoices.get(randInt);
            }

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
