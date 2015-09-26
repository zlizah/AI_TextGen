
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
}