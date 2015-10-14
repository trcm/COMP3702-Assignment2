package solver;

import problem.Fridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Team Galaktikon on 4/10/15.
 */
public class FridgeState {

    private List<Integer> fridgeInventory;
    private List<FridgeState> children;
    private List<FridgeState> parent;
    private HashMap<FridgeState, Double> probs;

    public FridgeState(List<Integer> currentInventory) {
        fridgeInventory = currentInventory;
        parent = new ArrayList<FridgeState>();
        children = new ArrayList<FridgeState>();
        probs = new HashMap<>();
    }

    public List<Integer> getInventory() {
        return this.fridgeInventory;
    }

    public FridgeState getChild(List<Integer> inventory) {
        for (FridgeState c : children) {
            if (c.getInventory().equals(inventory)) {
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

}
