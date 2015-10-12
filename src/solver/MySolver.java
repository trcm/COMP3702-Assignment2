package solver;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import problem.Fridge;
import problem.Matrix;
import problem.ProblemSpec;


public class MySolver implements OrderingAgent {

	private final int FAILURE = -1;
	private final int SUCCESS = 0;

	private ProblemSpec spec = new ProblemSpec();
	private Fridge fridge;
    private List<Matrix> probabilities;
	private ArrayList<int[]> combs;
	private ArrayList<int[]> eatCombs;
	
	public MySolver(ProblemSpec spec) throws IOException {
	    this.spec = spec;
		fridge = spec.getFridge();
        probabilities = spec.getProbabilities();
		combs = new ArrayList<>();
		eatCombs = new ArrayList<>();
		generateFridgeStates();
		generateEatStates();
		System.out.println(transition(combs.get(1), combs.get(0)));
		System.out.println(Arrays.toString(combs.get(1)) + " " + Arrays.toString(combs.get(0)));
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

	/**
	 * Generate the list of states for policy generation. Yo.
	 */
	public void generateFridgeStates() {

		int[] n = new int[fridge.getMaxTypes()];
		ArrayList<Integer> s = new ArrayList<>();
		for (int i = 0; i < fridge.getCapacity(); i++) {
			s.add(fridge.getCapacity());
		}
		genStates(n, s, 0);

		System.out.println(combs.size() + "\n\n\n");
		for (int[] x : combs) {
			System.out.println(Arrays.toString(x));
//			System.out.println("State score " + getStateProb(x));
		}
	}

	public void generateEatStates() {
		int[] n = new int[fridge.getMaxTypes()];
		ArrayList<Integer> s = new ArrayList<>();
		for (int i = 0; i < fridge.getCapacity(); i++) {
			s.add(fridge.getMaxItemsPerType());
		}
		genEatStates(n, s, 0);

		System.out.println("Eat combinations " + eatCombs.size());
		for (int[] x : eatCombs) {
			System.out.println(Arrays.toString(x));
			System.out.println("State score " + getStateProb(x));
		}

	}

	public int[] genStates(int[] n, ArrayList<Integer> Nr, int idx) {

		if (idx == n.length) {
			if (IntStream.of(n).sum() <= fridge.getCapacity()) {
				int[] t = n.clone();
				if (!arrContains(combs, t)) {
					combs.add(t);
				}
			}
			return null;
		}
		for (int i = 0; i <= Nr.get(idx); i++) {
			n[idx] = i;
			genStates(n, Nr, idx + 1);
		}

		return null;
	}

	public int[] genEatStates(int[] n, ArrayList<Integer> Nr, int idx) {

		if (idx == n.length) {
			if (IntStream.of(n).sum() <= fridge.getCapacity() * fridge.getMaxItemsPerType()) {
				int[] t = n.clone();
				if (!arrContains(eatCombs, t)) {
					eatCombs.add(t);
				}
			}
			return null;
		}
		for (int i = 0; i <= Nr.get(idx); i++) {
			n[idx] = i;
			genEatStates(n, Nr, idx + 1);
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

	public boolean arrContains(ArrayList<int[]> arr, int[] x) {
		for (int[] i : arr) {
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

	public Double getStateProb(int[] state) {
		Double sum = 0.0;
		for (int i = 0; i < state.length; i++) {
			int j = state[i], k = state[i];
			if (j > 2)
				j = 2;
			if (k > fridge.getCapacity())
				k = fridge.getCapacity();

			List<Double> m = probabilities.get(i).getRow(k);

			ArrayList<Double> prodF = new ArrayList<>();
			ArrayList<Double> prodS = new ArrayList<>();

			for (int l = 0; l <= fridge.getMaxItemsPerType(); l++) {
				if (l <= state[i]) {
					prodS.add(m.get(l));
				} else {
					prodF.add(m.get(l));
				}
			}
			if (prodF.size() > 0) {
				Double pF = 1.0;
				for (Double p: prodF) {
					pF *= p;
				}
				sum += pF * FAILURE;
			}
			if (prodS.size() > 0) {
				Double pS = 1.0;
				for (Double p : prodS) {
					pS *= p;
				}
				sum += pS * SUCCESS;
			}
		}
		return sum;
	}

	public Double transition(int[] state, int[] finalState) {
		Double transistionProb = 0.0;

		ArrayList<Double> probTable = new ArrayList<>();
		for (int i = 0; i < state.length; i++) {
			probTable.add(0.0);
		}

		for (int i = 0; i < state.length; i++) {
			System.out.println(i);
			int stateDiff = state[i] - finalState[i];

			List<Double> pr = probabilities.get(i).getRow(state[i]);

			for (int l = 0; l <= fridge.getMaxItemsPerType(); l++) {
				System.out.printf("state i %d final state i %d i %d l %d\n", state[i], finalState[i], i, l);
				if (state[i] - l <= finalState[i] && state[i] != 0) {
					System.out.println("prod");
					// this probability will take us to at least the final state inventory
					if (probTable.get(i) == 0) {
						probTable.set(i, pr.get(l));
					} else {
						probTable.set(i, probTable.get(i) * pr.get(l));
					}
				}

			}

		}

		for (Double b: probTable) {
			transistionProb += b;
		}


		return transistionProb;
	}

}