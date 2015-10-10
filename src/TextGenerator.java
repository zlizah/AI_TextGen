import java.util.*;

/**
 * Text generator
 */
public class TextGenerator {
	//Fields
	NGrams biGrams;
	NGrams triGrams;
	NGrams quadGrams;
	Random rng;
	
	//Constructor
	public TextGenerator(ArrayList<NGrams> nGrams) {
		biGrams = nGrams.get(0);
		triGrams = nGrams.get(1);
		quadGrams = nGrams.get(2);
		rng = new Random();
	}
	
	//Generator
	public String generateText(int words) {
        //Generate a random first word
        ArrayList<String> firstWords = null;
        ArrayList<String> startWords = CorpusParser.getStartWords();
        int randFirstInt = rng.nextInt(startWords.size()); //0 -> size -1
        String startWord = startWords.get(randFirstInt);
        return generateText(words, startWord);
    }
	


    private String getInterpolatedWord(NGram biWord, NGram triWord) {
    	Random rng = new Random(65);
		
		int temp = 100;
		if (triWord.isEmpty()) temp = 10;
		if (biWord.isEmpty()) return null;
		int index = rng.nextInt(temp);

		if(index < 10) {
			return biPolarWord(biWord);
		} 
		return triPolarWord(triWord, biWord);
	}

	private String biPolarWord(NGram biWord) {
		HashMap<String, Double> probabilities = new HashMap<String, Double>();
		ArrayList<String> possibleWords = new ArrayList<String>(CorpusParser.allWords.keySet());

		double[] lambdas = new double[2];
		Random rng = new Random();
		lambdas[0] = rng.nextDouble();
		lambdas[1] = 1 - lambdas[0];

		for(int i = 0; i < possibleWords.size(); i++) {
			String word = possibleWords.get(i);

			double uniOcc = getUniOcc(word); //P(w_n)

			double biOcc = getNOcc(biWord, word);

			double interpolatedProbability = lambdas[1] * biOcc + lambdas[0] * uniOcc;
			probabilities.put(word, interpolatedProbability);
		}

		return chooseWord(probabilities);
	}

	private String triPolarWord(NGram triWord, NGram biWord) {
		HashMap<String, Double> probabilities = new HashMap<String, Double>();
		ArrayList<String> possibleWords = new ArrayList<String>(CorpusParser.allWords.keySet());

		double[] lambdas = new double[3];
		Random rng = new Random();
		lambdas[0] = rng.nextDouble();
		lambdas[1] = (1-lambdas[0]) / (rng.nextInt(10) + 1);
		lambdas[2] = 1 - (lambdas[0] + lambdas[1]);

		for(int i = 0; i < possibleWords.size(); i++) {
			String word = possibleWords.get(i);

			double uniOcc = getUniOcc(word); //P(w_n)

			double biOcc = getNOcc(biWord, word);

			double triOcc = getNOcc(triWord, word);

			double interpolatedProbability = lambdas[2] * triOcc + lambdas[1] * biOcc + lambdas[0] * uniOcc;

			probabilities.put(word, interpolatedProbability);
		}

		return chooseWord(probabilities);
	}


    private double getUniOcc(String w) {
    	int occs = CorpusParser.allWords.get(w); //Assumed to work correctly for now
    	return (double) occs/CorpusParser.numberOfWords;
    }


    private double getNOcc(NGram ngram, String word) {
    	double ret;

		if(ngram.occurrences.containsKey(word)) {
			ret = ngram.occurrences.get(word) / ngram.getOccurrences(); 
		} else {
			ret = 0;
		}
		return ret;
    }

    private String chooseWord(HashMap<String, Double> probabilities) {
    	Random rng = new Random();
    	double index = rng.nextDouble();
    	ArrayList<String> possibleWords = new ArrayList<String>(CorpusParser.allWords.keySet());

    	for(int i = 0; i < possibleWords.size(); i++) {
    		String word = possibleWords.get(i);
    		index -= probabilities.get(word);
    		if(index <= 0) {
    			return word;
    		}
    	}
    	return null; //Error
    }

	private String getDistributedWord(NGram biWord, NGram triWord, NGram quadWord, int sentenceLength) {
		//TODO Empty lists?
		//return getInterpolatedWord(biWord, triWord); //Use interpolation instead
		
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
		} 
	}

    /**
     * Generate a bunch of text
     * @param words the amount of words in the text
     * @param firstWord the initial start word. Might be several words.
     * @return the text
     */
    public String generateText(int words, String firstWord) {
        StringBuilder text = new StringBuilder();
		text.append(firstWord);
		LinkedList<String> wordQueue = new LinkedList<>();
		wordQueue.addFirst(firstWord);
		
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
			NGram empty = new NGram(1);
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
