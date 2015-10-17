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
    private List<FridgeState> eatChildren;
    private List<FridgeState> parent;
    private HashMap<FridgeState, Double> probs;
    public Double v0;
    public Double vi;
    public int visited = 1;

    public FridgeState(int[] currentInventory) {
        fridgeInventory = currentInventory;
        parent = new ArrayList<FridgeState>();
        children = new ArrayList<FridgeState>();
        eatChildren = new ArrayList<>();
        probs = new HashMap<>();
    }

    public void incrementVisit() {
        visited++;
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

    public void addChildren(FridgeState child) {
        children.add(child);
    }

    public List<FridgeState> getEatChildren() {
        return eatChildren;}

    public void addEatChildren(FridgeState x, Double transProb){
        probs.put(x, transProb);
        eatChildren.add(x);
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
