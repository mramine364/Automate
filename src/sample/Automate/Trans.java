package sample.Automate;

import java.util.ArrayList;

/**
 * Created by amine_err on 30/07/2015.
 */
public class Trans {
    private State from;
    private State to;
    private String name;
    public static final String Epsilon = "epsilon";

    public Trans() {
    }

    public Trans(String name) {
        this.name = name;
    }

    public State getFrom() {
        return from;
    }
    public void setFrom(State from) {
        this.from = from;
    }

    public State getTo() {
        return to;
    }
    public void setTo(State to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static ArrayList<Trans> getTransitions(State from, State to){
        ArrayList<Trans> res = new ArrayList<Trans>();
        ArrayList<Trans> alt = from.getTransitions();
        for(Trans trans : alt){
            if( trans.getTo().equals(to) ){
                res.add(trans);
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return name + ", "+from/*.getName()*/+", "+to/*.getName()*/;
    }
}
