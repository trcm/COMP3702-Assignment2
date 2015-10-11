package solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import problem.Fridge;
import problem.Matrix;
import problem.ProblemSpec;

public class MySolver implements OrderingAgent {
	
	private ProblemSpec spec = new ProblemSpec();
	private Fridge fridge;
    private List<Matrix> probabilities;
	
	public MySolver(ProblemSpec spec) throws IOException {
	    this.spec = spec;
		fridge = spec.getFridge();
        probabilities = spec.getProbabilities();
	}
	
	public void doOfflineComputation() {
	    // TODO Write your own code here.
	}
	
	public List<Integer> generateShoppingList(List<Integer> inventory,
	        int numWeeksLeft) {
		// Example code that buys one of each item type.
        // TODO Replace this with your own code.
		List<Integer> shopping = new ArrayList<Integer>();
		int totalItems = 0;
		for (int i : inventory) {
			totalItems += i;
		}
		
		int totalShopping = 0;
		for (int i = 0; i < fridge.getMaxTypes(); i++) {
			if (totalItems >= fridge.getCapacity() || 
			        totalShopping >= fridge.getMaxPurchase()) {
				shopping.add(0);
			} else {
				shopping.add(1);
				totalShopping ++;
				totalItems ++;
			}
		}
		return shopping;
	}

}
