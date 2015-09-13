package sample.Automate;

import sample.AutomateUI.StateUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by amine_err on 30/07/2015.
 */
public class State {
    private boolean Final;
    private boolean Initial;
    private String name;
    private boolean searched;
    private AF af;
    private ArrayList<Trans> transitions;
    private StateUI stateUI;

    public StateUI getStateUI() {
        return stateUI;
    }

    public void setStateUI(StateUI stateUI) {
        this.stateUI = stateUI;
    }

    public AF getAf() {
        return af;
    }
    public void setAf(AF af) {
        this.af = af;
    }

    public State(){ transitions = new ArrayList<Trans>(); }

    public State f(String str){
        for(Trans t : transitions){
            if( t.getName().equals(str) )
                return t.getTo();
        }
        return null;
    }
    public Classe f2(String str){
        Classe res = new Classe();
        for(Trans t : transitions){
            if( t.getName().equals(str) )
                res.addSet(t.getTo());
        }
        return res;
    }

    public Classe epsilonFermeture(){
        if( isSearched() )
            return new Classe();
        Classe c = new Classe();
        c.addSet(this); this.setSearched(true);
        Classe c2 = f2(Trans.Epsilon);
        if( !c2.isEmpty() ){
            for(State e : c2.getAle()){
                c.addSet(e.epsilonFermeture());
            }
        }
        return c;
    }

    public ArrayList<State> getNexts(){
        ArrayList<State> ale = new ArrayList<State>();
        ArrayList<String> als = this.getAf().getAlphabet();
        for(String str : als){
            ale.add(this.f(str));
        }
        return ale;
    }
    public boolean isReachedFrom(State e){
        if( e == null )
            return false;
        if( e.isSearched() )
            return false;
        if( equals(e) )
            return true;
        e.setSearched(true);
        ArrayList<State> nexts = e.getNexts();
        for(State ee : nexts){
            if( isReachedFrom(ee) )
                return true;
        }
        return false;
    }

    public Classe toClasse(){
        Classe c = new Classe();
        c.add(this);
        return c;
    }

    public boolean isFinal() {
        return Final;
    }
    public void setFinal(boolean aFinal) {
        Final = aFinal;
    }

    public boolean isInitial() {
        return Initial;
    }
    public void setInitial(boolean initial) {
        Initial = initial;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSearched() {
        return searched;
    }
    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public ArrayList<Trans> getTransitions() {
        return this.transitions;
    }
    public void setTransitions(ArrayList<Trans> transitions) {
        this.transitions = transitions;
        Collections.sort(this.transitions, new Comparator<Trans>() {
            @Override
            public int compare(Trans o1, Trans o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    public void addTansition(Trans t){
        transitions.add(t);
        Collections.sort(transitions, new Comparator<Trans>() {
            @Override
            public int compare(Trans o1, Trans o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    public void removeTransition(Trans t){
        this.transitions.remove(t);
    }

    @Override
    public String toString() {
        return getName();
    }
}
