package solver;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tom on 7/10/15.
 */
public class FridgeGraph {

    private FridgeState root;
    private ArrayList<FridgeState> nodes;

    public FridgeGraph() {
//        this.root = root;
        nodes = new ArrayList<FridgeState>();
    }

    public void addFridge(FridgeState n) {
        if (!contains(n)) {
            nodes.add(n);
        }
    }

    public FridgeState getNode(int[] inventory) {
        for (FridgeState f: nodes) {
            if (Arrays.equals(f.getInventory(), inventory)) {
                return f;
            }
        }
        return null;
    }

    public boolean contains(FridgeState comp) {
        return nodes.contains(comp);
    }

    public ArrayList<FridgeState> getStates() {
        return nodes;
    }
}
