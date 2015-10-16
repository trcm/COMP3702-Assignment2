package solver;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import problem.Fridge;
import problem.Matrix;
import problem.ProblemSpec;


public class MySolver implements OrderingAgent {
	private final Double FAILURE = -1.0;
	private final Double SUCCESS = 0.0;

	private ProblemSpec spec = new ProblemSpec();
	private Fridge fridge;
    private List<Matrix> probabilities;
	private ArrayList<int[]> combs;
	private ArrayList<int[]> eatCombs;
	private FridgeGraph stateGraph;
	private FridgeGraph eatGraph;
	
	public MySolver(ProblemSpec spec) throws IOException {
	    this.spec = spec;
		fridge = spec.getFridge();
        probabilities = spec.getProbabilities();
		combs = new ArrayList<>();
		eatCombs = new ArrayList<>();
		generateFridgeStates();
		generateEatStates();
		stateGraph = new FridgeGraph();
		eatGraph = new FridgeGraph();
		generateFridgeGraph();
		generateEatGraph();

		for (int i = 0; i < 2; i++) {
			doOfflineComputation();
		}
		FridgeState bestNext = getBestNext(stateGraph.getNode(combs.get(0)));
//		System.out.println(transition(combs.get(7), combs.get(1)));
//		System.out.println(Arrays.toString(combs.get(7)) + " " + Arrays.toString(combs.get(1)));
	}
	
	public void doOfflineComputation() {
	    // TODO Write your own code here.

		// generate vi for all states
		ArrayList<FridgeState> states = eatGraph.getStates();

		for (FridgeState f : states) {
			if (f.vi != 1000) {
				f.v0 = f.vi;
			}

			Double vi = 0.0;
			ArrayList<Double> bellman = new ArrayList<>();

			List<FridgeState> children = f.getChildren();

			for (FridgeState c: children) {
				Double temp = transition(f.getInventory(), c.getInventory());
				bellman.add(transition(f.getInventory(), c.getInventory()) * c.v0);
			}

			Double sum = 0.0;
			for (Double b : bellman) {
				sum += b;
			}
			sum = spec.getDiscountFactor() * sum;

			vi = f.v0 + sum;
			f.vi = vi;
		}

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
		int[] inventoryArray = new int[inventory.size()];
		for (int x = 0; x < inventory.size(); x++) inventoryArray[x] = inventory.get(x);
		FridgeState current = stateGraph.getSpecific(inventoryArray);
		System.out.println(Arrays.toString(current.getInventory()));
		FridgeState bestNext = getBestNext(current);
		for(int i = 0; i < inventory.size(); i++) {
			shopping.add(bestNext.getInventory()[i] - current.getInventory()[i]);
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

//		System.out.println(combs.size() + "\n\n\n");
//		for (int[] x : combs) {
//			System.out.println(Arrays.toString(x));
////			System.out.println("State score " + getStateProb(x));
//		}
	}

	public void generateEatStates() {
		int[] n = new int[fridge.getMaxTypes()];
		ArrayList<Integer> s = new ArrayList<>();
		for (int i = 0; i < fridge.getCapacity(); i++) {
			s.add(fridge.getMaxItemsPerType());
		}
		genEatStates(n, s, 0);

//		System.out.println("Eat combinations " + eatCombs.size());
//		for (int[] x : eatCombs) {
//			System.out.println(Arrays.toString(x));
//			System.out.println("State score " + getStateProb(x));
//		}

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
		ArrayList<Double> sum = new ArrayList<Double>();
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
				Double pF = 0.0;
				for (Double p: prodF) {
					pF += p;
				}
				sum.add(pF);
			}
		}
		for (int x = 0; x < sum.size() - 1; x++) {
			sum.set(x+1,sum.get(x+1) * sum.get(x));
		}
		if(sum.size() == 0) {
			return 0.0;
		}
		return sum.get((sum.size()-1)) * FAILURE;
	}

	public Double transition(int[] state, int[] finalState) {

		ArrayList<Double> probTable = new ArrayList<>();
		for (int i = 0; i < state.length; i++) {
			if(state[i] == 0)
				probTable.add(1.0);
			else
				probTable.add(0.0);
		}
		for (int i = 0; i < state.length; i++) {
			if(state[i] == 0)
				continue;
			int stateDiff = state[i] - finalState[i];

			List<Double> pr = probabilities.get(i).getRow(state[i]);

			for (int l = 0; l <= fridge.getMaxItemsPerType(); l++) {
				if (state[i] - l <= finalState[i] && state[i] != 0) {
					// this probability will take us to at least the final state inventory
					probTable.set(i, probTable.get(i) + pr.get(l));
				}

			}

		}
		double multi = 1;
		for(double x: probTable)
		multi *= x;

		return multi;
	}

	public void generateFridgeGraph() {
		for (int i = 0; i < combs.size(); i++) {
			stateGraph.addFridge(new FridgeState(combs.get(i)));
		}

		for (int i = 0; i < combs.size(); i++) {
			FridgeState current = stateGraph.getNode(combs.get(i));
			current.v0 = getStateProb(current.getInventory());
			current.vi = 1000.0;
			if (current.capacity() == fridge.getCapacity()) {
				continue;
			}

			for (int j = 0; j < combs.size(); j++) {
				FridgeState inner = stateGraph.getNode(combs.get(j));
				if (current.equals(inner))
					continue;
				int maxDiff = 0;
				boolean t = false;
				for (int k = 0; k < fridge.getMaxTypes(); k++) {
					int diff = inner.getInventory()[k] - current.getInventory()[k];
					if (diff < 0) {
						t = true;
						break;
					}
					maxDiff += diff;
				}

				if (maxDiff > fridge.getMaxPurchase() || maxDiff < 0 || t) {
					continue;
				}

				Double p = transition(current.getInventory(), inner.getInventory());
				// add the inner as a child of the parent
				current.addChildren(inner, 0.0);
				//add the current as a parent of the inner
				inner.addParent(current);
			}

		}

	}

	public void generateEatGraph() {


		for (int i = 0; i < eatCombs.size(); i++) {
			eatGraph.addFridge(new FridgeState(eatCombs.get(i)));
		}

		for (int i = 0; i < eatCombs.size(); i++) {
			FridgeState current = eatGraph.getNode(eatCombs.get(i));
			current.v0 = getStateProb(current.getInventory());
			current.vi = 1000.0;
			if (current.capacity() == 0) {
				continue;
			}

			for (int j = 0; j < eatCombs.size(); j++) {
				FridgeState inner = eatGraph.getNode(eatCombs.get(j));
				if (current.equals(inner) || inner.capacity() > current.capacity())
					continue;


				boolean t = false;
				for (int k = 0; k < fridge.getMaxTypes(); k++) {
					int diff = current.getInventory()[k] - inner.getInventory()[k];
					if (diff < 0) {
						t = true;
						break;
					}
				}

				if (t) {
					continue;
				}

				Double p = transition(current.getInventory(), inner.getInventory());
				// add the inner as a child of the parent
				current.addChildren(inner, p);
				//add the current as a parent of the inner
				inner.addParent(current);
			}
		}
	}

	public FridgeState getBestNext(FridgeState current) {
		System.out.println("I am at");
		System.out.println(Arrays.toString(current.getInventory()));
		// get all the nodes that could be eaten to this node
		System.out.println("Current has " + current.getChildren().size() + " children");
		ArrayList<FridgeState> choices= new ArrayList<FridgeState>();
		//For all children get them from the eat graph
		for (FridgeState f: current.getChildren()) {
			FridgeState choice = eatGraph.getSpecific(f.getInventory());
			//add them as choices
			choices.add(choice);
		}
		choices = viSort(choices);
		//Return the best choice i.e one with lowest score
		System.out.println("This is my future according to scores!");
		System.out.println(Arrays.toString(choices.get(choices.size() -1).getInventory()));
		System.out.println("And my Score is: " + choices.get(choices.size() - 1).vi);
		return choices.get(choices.size() - 1);
	}

	public ArrayList<FridgeState> viSort(ArrayList<FridgeState> toSort) {

		if(toSort.size() == 0)
			return toSort;
		for (int i = 0; i < toSort.size(); i++) {
			if (i == toSort.size()-1)  {
				return toSort;
			} else {
				Double viA = toSort.get(i).vi;
				Double viB = toSort.get(i+1).vi;
				if (viA > viB) {
					// swap
					FridgeState temp = toSort.get(i);
					toSort.set(i, toSort.get(i + 1));
					toSort.set(i + 1, temp);
					viSort(toSort);
				} else if (viA == viB) {
					continue;
				}
			}
		}
		return viSort(toSort);


	}

}
