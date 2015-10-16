package solver;

/**
 * Created by Kieran on 10/16/2015.
 */
public class visitedMemory {
    public FridgeState a;
    public FridgeState b;
    public  Double visit;
    public Double score;

    public visitedMemory(FridgeState a, FridgeState b, Double score) {
        this.a = a;
        this.b = b;
        this.visit = score;
    }

    public boolean getStates(FridgeState a, FridgeState b) {
       if(this.a.equals(a) && this.b.equals(b))
           return true;
        return false;
    }

    public void incrementScore() {
        visit++;
    }
    public void setScore(double score) {
        this.score = score;
    }
}
