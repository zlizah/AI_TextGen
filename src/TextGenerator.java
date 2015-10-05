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

        firstWords = biGrams.getKeys();

        int randFirstInt = rng.nextInt(firstWords.size()); //0 -> size -1
        String firstWord = firstWords.get(randFirstInt);
        return generateText(words, firstWord);
    }
	
	private String getDistributedWord(NGram biWord, NGram triWord, NGram quadWord) {
		//TODO Empty lists?
		Random rng = new Random(65);
		
		int temp = 3;
		if (quadWord.isEmpty()) temp = 2;
		if (triWord.isEmpty()) temp = 1;
		if (biWord.isEmpty()) return null;
		int index = rng.nextInt(temp);
		if (index == 0) {
			return biWord.getDistributedWord();
		} else if (index == 1) {
			return triWord.getDistributedWord();
		} else {
			return quadWord.getDistributedWord();
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
		LinkedList<String> wordQueue = new LinkedList<String>();
		wordQueue.addFirst(firstWord);
		
		//Loop until enough words
		for (int index = 0; index < words; index ++) {
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
			String nextWord = "";
			NGram empty = new NGram(1);
			if (oldWord_one == null) {
				nextWord = getDistributedWord(empty, empty, empty);
			} else if (oldWord_two == null) {
				nextWord = getDistributedWord(biGrams.getWordChoices(oldWord_one), empty, empty);
			} else if (oldWord_three == null) {
				nextWord = getDistributedWord(biGrams.getWordChoices(oldWord_one), 
						triGrams.getWordChoices(triHash), empty);
			} else {
				nextWord = getDistributedWord(biGrams.getWordChoices(oldWord_one), 
						triGrams.getWordChoices(triHash), quadGrams.getWordChoices(quadHash));
			}
			
			//Pick random word from biGrams if no choices found
			if (nextWord == null) {
                ArrayList<String> wordChoices = biGrams.getKeys();
                int randInt = rng.nextInt(wordChoices.size());
                nextWord = wordChoices.get(randInt);
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
