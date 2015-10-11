package solver;

import problem.Fridge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Team Galaktikon on 4/10/15.
 */
public class FridgeState {

    private List<Integer> fridgeInventory;
    private List<FridgeState> children;
    private FridgeState parent;

    public FridgeState(List<Integer> currentInventory, FridgeState parent) {
        fridgeInventory = currentInventory;
        this.parent = parent;
        children = new ArrayList<FridgeState>();
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

}
