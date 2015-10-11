package solver;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import problem.Fridge;
import problem.Matrix;
import problem.ProblemSpec;

public class ShoppingGenerator {
	
	private ProblemSpec spec = new ProblemSpec();
	private Fridge fridge;
	private List<Matrix> probabilities;
	private ArrayList<FridgeState> possibleStates;
	private FridgeGraph global;
	public ArrayList<int[]> combs;
	int count = 0;

	public ShoppingGenerator(String problemSpecFilename) throws IOException {
		spec.loadInputFile(problemSpecFilename);
		fridge = spec.getFridge();
		probabilities = spec.getProbabilities();
		possibleStates = new ArrayList<FridgeState>();
		combs = new ArrayList<int[]>();
		generateFridgeStates();
	}
	
	public List<Integer> generateShopping(List<Integer> inventory) {
		
		// Example code that buys one of each item type.
		// Replace this with your code.
		List<Integer> shopping = new ArrayList<Integer>();


		generateFridgeStates();
		int totalItems = 0;
		for (int i : inventory) {
			totalItems += i;
		}
		int totalShopping = 0;
		for (int i = 0; i < fridge.getMaxTypes(); i++) {
			if (totalItems >= fridge.getCapacity()) {
				shopping.add(0);
			} else {
				shopping.add(1);
				totalItems ++;
			}
		}
		return shopping;
	}

	/**
	 * Generate the list of states for policy generation. Yo.
	 */
	public void generateFridgeStates() {
		// initial state, fridge is empty
//		FridgeState cState = null;
//
//		List<Integer> emptyFridge = new ArrayList<Integer>();
//		for (int i = 0; i < fridge.getCapacity(); i++) {
//			emptyFridge.add(0);
//		}
//
//		cState = new FridgeState(emptyFridge, null);
//
//		// initialize the global fridge graph and add the initial empty fridge tot he graph
//		global = new FridgeGraph(cState);
//
//		// populate the graph with all possible states
//
//		Queue<FridgeState> queFridge = new ArrayDeque<FridgeState>();
//		queFridge.add(cState);
//		List<FridgeState> children = new ArrayList<FridgeState>();

		int[] n = new int[fridge.getMaxTypes()];
//		int[] s = {fridge.getCapacity(), fridge.getCapacity(), fridge.getCapacity()};
		int[] s = {2,2,2};
		genStates(n, s, 0);

		System.out.println(combs.size());
//		while (queFridge.size() > 0) {
//			cState = queFridge.poll();
////			children = genStates(cState);
//
//			for (FridgeState f: children) {
//				if (!queFridge.contains(f)) {
//					queFridge.add(f);
//				}
//			}
//		}
	}

	public int[] genStates(int[] n, int[] Nr, int idx) {

		if (idx == n.length) {
			if (IntStream.of(n).sum() <= fridge.getCapacity()) {
					int[] t = n.clone();
					if (!arrContains(t)) {
						combs.add(t);
					}
			}
			return null;
		}
		for (int i = 0; i <= Nr[idx]; i++) {
			n[idx] = i;
			genStates(n, Nr, idx + 1);
		}

		return null;
	}

	public int fac(int n) {
		int result = 1;
		for (int i = n; i > 0; i--) {
			result = result * i;
		}
		return result;
	}

	public boolean arrContains(int[] x) {
		for (int[] i : combs) {
			for (int j = 0; j <= i.length; j++) {
				if (j == i.length) {
					return true;
				} else if (i[j] != x[j]) {
					break;
				}
			}
		}
		return false;
	}

}
