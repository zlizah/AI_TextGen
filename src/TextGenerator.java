
import java.util.*;

/**
 * Text generator
 */
public class TextGenerator {
	//Fields
	ArrayList<N_gram> n_grams;
	Random rng;
	
	//Constructor
	public TextGenerator(ArrayList<N_gram> n_grams) {
		this.n_grams = n_grams;
		rng = new Random();
	}
	
	//Generator
	public String generateText(int words) {
		StringBuilder text = new StringBuilder();
		String oldWord = "The";
		text.append(oldWord);
		
		//Loop until enough words
		for (int index = 0; index < words; index ++) {
			//Fetch word chocies
			ArrayList<String> wordChoices = null;
			for (int j = n_grams.size() - 1; j >= 0; j--) {
				wordChoices = n_grams.get(j).getWordChoices(oldWord);
				if (wordChoices != null) {
					break;
				}
			}
			
			//Pick random word from oneGrams if no choices found
			if (wordChoices == null) {
				for (int i = 0; i < n_grams.size(); i++) {
					if (n_grams.get(i).n == 1) {
						wordChoices = n_grams.get(i).getKeys();
					}
				}
			}
			
			//Pick a random word from the list
			int randInt = rng.nextInt(wordChoices.size()); //0 -> size -1
			String word = wordChoices.get(randInt);
			text.append(" ");
			text.append(word);
			oldWord = word;
		}
		
		return text.toString();
	}
}