package solver;

import java.util.ArrayList;

/**
 * Created by tom on 7/10/15.
 */
public class FridgeGraph {

    private FridgeState root;
    private ArrayList<FridgeState> nodes;

    public FridgeGraph(FridgeState root) {
        this.root = root;
        nodes = new ArrayList<FridgeState>();
    }

    public void addFridge(FridgeState n) {
        if (!contains(n)) {
            nodes.add(n);
        }
    }

    public boolean contains(FridgeState comp) {
        return nodes.contains(comp);
    }
}
