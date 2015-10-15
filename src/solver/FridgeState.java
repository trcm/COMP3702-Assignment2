package solver;

import problem.Fridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Team Galaktikon on 4/10/15.
 */
public class FridgeState {

//    private List<Integer> fridgeInventory;
    private int[] fridgeInventory;
    private List<FridgeState> children;
    private List<FridgeState> parent;
    private HashMap<FridgeState, Double> probs;
    public Double v0;
    public Double vi;

    public FridgeState(int[] currentInventory) {
        fridgeInventory = currentInventory;
        parent = new ArrayList<FridgeState>();
        children = new ArrayList<FridgeState>();
        probs = new HashMap<>();
    }

    public int[] getInventory() {
        return this.fridgeInventory;
    }

    public FridgeState getChild(FridgeState f) {
        for (FridgeState c : children) {
            if (c.getInventory().equals(f.getInventory())) {
                return c;
            }
        }
        return null;
    }

    public List<FridgeState> getChildren() {
        return children;
    }

    public void addChildren(FridgeState child, Double transProb) {
        children.add(child);
        probs.put(child, transProb);
    }

    public void addParent(FridgeState parent) {
        this.parent.add(parent);
    }

    public List<FridgeState> getParents() {
        return parent;
    }

    // return the current capactiy of the fridge
    public int capacity() {
        return IntStream.of(fridgeInventory).sum();
    }

    public boolean inChildren(FridgeState p) {
        for (FridgeState c : children) {
            if (Arrays.equals(c.getInventory(), p.getInventory())) {
                return true;
            }
        }
        return false;
    }

}
