import java.util.*;

/**
 * Text generator
 */
public class TextGenerator {
	//Fields
	NGrams n_grams;
	Random rng;
	
	//Constructor
	public TextGenerator(NGrams n_grams) {
		this.n_grams = n_grams;
		rng = new Random();
	}
	
	//Generator
	public String generateText(int words) {

        //Generate a random first word
        ArrayList<String> firstWords = null;

        firstWords = n_grams.getKeys();

        int randFirstInt = rng.nextInt(firstWords.size()); //0 -> size -1
        String firstWord = firstWords.get(randFirstInt);
        return generateText(words, firstWord);
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
		
		//Loop until enough words
        int sentenceLength = 1;
		for (int index = 0; index < words || !firstWord.endsWith("."); index ++) {
			//Fetch word choices
			String nextWord = n_grams.getWordChoices(firstWord).getNextWord(sentenceLength);

			
			//Pick random word from oneGrams if no choices found
			if (nextWord == null) {
                ArrayList<String> wordChoices = n_grams.getKeys();
                //Pick a random word from the list
                int randInt = rng.nextInt(wordChoices.size()); //0 -> size -1
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
            firstWord = nextWord;
		}
		
		return text.toString();
	}
}