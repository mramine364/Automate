package sample.Automate;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import sample.AutomateUI.StateUI;
import sample.AutomateUI.transLine;
import sample.Controller;
import sample.SF.SF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by amine_err on 30/07/2015.
 */
public class AF {
    private ArrayList<String> alphabet;
    private ArrayList<State> Q;
    private ArrayList<Trans> ts;

    public AF(){
        Q = new ArrayList<State>();
        ts = new ArrayList<Trans>();
        alphabet = new ArrayList<String>();
    }
    public ArrayList<Trans> getTs() {
        return ts;
    }
    public void setTs(ArrayList<Trans> ts) {
        this.ts = ts;
    }

    public void prepareStates(){
        for( Trans t : ts ){
            State e = t.getFrom();
            e.addTansition(t);
        }
    }

    public boolean isAFD(){return false;} // TODO
    public boolean isAFDComplete(){return false;} // TODO

    public ArrayList<String> getAlphabet() {
        return alphabet;
    }
    public void setAlphabet(ArrayList<String> alphabet) {
        Collections.sort(alphabet);
        this.alphabet = alphabet;
    }

    public ArrayList<State> getQ() {
        return Q;
    }
    public void setQ(ArrayList<State> q) {
        Q = new ArrayList<State>();
        for(State state : q ){
            this.add(state);
        }
    }
    public void addE(ArrayList<State> q) {
        for(State state : q ){
            this.add(state);
        }
    }

    public State f(State e, String str){
        return e.f(str);
    }
    public State intiale(){
        for( State e : Q ){
            if( e.isInitial() )
                return e;
        }
        return null;
    }
    public ArrayList<State> Finaux(){
        ArrayList<State> ale = new ArrayList<State>();
        for( State e : Q ){
            if( e.isFinal() )
                ale.add(e);
        }
        return ale;
    }
    public ArrayList<State> NonFinaux(){
        ArrayList<State> ale = new ArrayList<State>();
        for( State e : Q ){
            if( !e.isFinal() )
                ale.add(e);
        }
        return ale;
    }
    public ArrayList<State> ReachedFromIntiale(){
        ArrayList<State> ale = new ArrayList<State>();
        State E = this.intiale();
        for( State e : Q ){
            if( e.isReachedFrom(E) )
                ale.add(e);
            initializeSearch();
        }
        return ale;
    }
    public ArrayList<State> ReachFinal(){
        ArrayList<State> ale = new ArrayList<State>();
        ArrayList<State> Ef = Finaux();
        for( State e : Q ){
            if( e.isFinal() )
                ale.add(e);
            else{
                for( State e2 : Ef ){

                    if( e2.isReachedFrom(e) ){
                        ale.add(e);
                        initializeSearch();
                        break;
                    }else{
                        initializeSearch();
                    }
                }
            }
        }
        return ale;
    }
    public void initializeSearch(){
        for( State e : Q )
            e.setSearched(false);
    }

    public AF getOptimizedAFD(){

        AF afd = new AF();
        afd.setAlphabet( alphabet );
        ArrayList<State> i1 = ReachedFromIntiale();
        ArrayList<State> i2 = ReachFinal();
        afd.setQ(AF.Intersection(i1, i2));
        Classe c1 = new Classe(); c1.setAle(afd.NonFinaux());
        Classe c2 = new Classe(); c2.setAle(afd.Finaux());
        Classes cs = new Classes(); cs.add(c1);cs.add(c2);
        Classes cs2 = new Classes(); cs2.setCs(cs);
        while( true ){
            for( Classe c : cs.getAlc() ){
                for( State e : c.getAle() ){
                    cs2.add(e);
                }
            }
            if( cs.nbrClasses() == cs2.nbrClasses() )
                break;
            else{
                cs = cs2;
                cs2 = new Classes(); cs2.setCs(cs);
            }
        }
        return cs2.toAFD();
    }



    public AF getAFD(){
        Classes cs = new Classes();
        cs.add(intiale().toClasse());
        ArrayList<String> Alphabet = this.getAlphabet();
        for(int i=0; i<cs.getAlc().size(); i++){
            Classe c = cs.getAlc().get(i);
            for(String s : Alphabet){
                Classe c2 = c.f(s);
                if( !c2.isEmpty() && !cs.contain(c2) ){
                    cs.add(c2);
                }
            }
        }
        return cs.toAFD2();
    }

    public AF getAFND(){
        AF afnd = new AF();
        afnd.setAlphabet(getAlphabet());
        ArrayList<String> Alphabet = getAlphabet();
        for(State state : getQ()){
            State e = new State();
            e.setName(state.getName());
            afnd.add(e);
            if(state.isInitial()){
                e.setInitial(true);
                Classe c = e.toClasse().epsilonFermeture();
                ArrayList<State> f = Finaux();
                for(State ee : f){
                    if( c.contain(ee) ){
                        e.setFinal(true);
                        break;
                    }
                }
            }
            if(state.isFinal())
                e.setFinal(true);
        }
        for(State state : getQ()){
            for(String str : Alphabet){
                //System.out.println("state.toClasse: "+state.getAf());
                //Classe c0 = state.toClasse().epsilonFermeture();
                Classe c = state.toClasse().epsilonFermeture().f(str).epsilonFermeture();
                ArrayList<Trans> alt = new ArrayList<Trans>();
                for(State e : c.getAle()){
                    Trans t = new Trans();
                    t.setName(str);
                    State ef = afnd.getQ().get(getQ().indexOf(state));
                    t.setFrom(ef);
                    t.setTo(afnd.getQ().get(getQ().indexOf(e)));
                    ef.addTansition(t);
                }
            }
        }
        return afnd;
    }

    public static ArrayList<State> Intersection(ArrayList<State> ale, ArrayList<State> ale2){
        ArrayList<State> res = new ArrayList<State>();
        for( State e : ale ){
            if( ale2.contains(e) ){
                res.add(e);
            }
        }
        return res;
    }

    public void add(State state){
        state.setAf(this);
        Q.add(state);
    }
    public void add(Trans trans){
        this.ts.add(trans);
    }
    public void addT(ArrayList<Trans> trans){
        this.ts.addAll(trans);
    }
    public void add(AF af){
        for (State state : af.getQ()){
            add(state);
        }
    }

    public AF unify(AF af){
        State e = new State();
        e.setInitial(true);
        Trans t1 = new Trans(Trans.Epsilon);
        t1.setFrom(e);
        t1.setTo(intiale());
        e.addTansition(t1);
        Trans t2 = new Trans(Trans.Epsilon);
        t2.setFrom(e);
        t2.setTo(af.intiale());
        e.addTansition(t2);
        intiale().setInitial(false);
        af.intiale().setInitial(false);
        add(af);
        getQ().add(0,e);
        return this;
    }
    public AF merge(AF af){
        ArrayList<State> ale = Finaux();
        State ei = af.intiale();
        ei.setInitial(false);
        for(State e : ale){
            Trans t = new Trans(Trans.Epsilon);
            t.setFrom(e);
            t.setTo(ei);
            e.setFinal(false);
            e.addTansition(t);
        }
        add(af);
        return this;
    }
    public AF loop(){

        State state = new State();
        state.setFinal(true);
        state.setInitial(true);

        ArrayList<State> ale = Finaux();

        State ei = intiale();
        ei.setInitial(false);

        for(State e : ale){
            Trans t1 = new Trans(Trans.Epsilon);
            t1.setFrom(e);
            t1.setTo(state);
            e.addTansition(t1);

            Trans t2 = new Trans(Trans.Epsilon);
            t2.setFrom(state);
            t2.setTo(ei);
            state.addTansition(t2);
        }
        add(state);
        return this;
    }

    public boolean hasInitiale(){
        for( State state : getQ() ){
            if( state.isInitial() ){
                return true;
            }
        }
        return false;
    }
    public boolean hasFinale(){
        for( State state : getQ() ){
            if( state.isFinal() ){
                return true;
            }
        }
        return false;
    }
    public boolean hasTwoInitiale() {
        int count = 0;
        for( State state : getQ() ){
            if( state.isInitial() ){
                count++;
                if( count>1 ){
                    return true;
                }
            }
        }
        return count>1;
    }

    public void compile(boolean alphabetTo) throws Exception {
        // not empty
        if( getQ().isEmpty() ){
            throw new Exception("No State Exception");
        }
        // has initiale etat
        if( !hasInitiale() ){
            throw new Exception("No Initial State Exception");
        }
        // has finale etat
        if( !hasFinale() ){
            throw new Exception("No Final State Exception");
        }
        // does not have 2 initials
        if( hasTwoInitiale() ){
            throw new Exception("Two Initials State Exception");
        }

        if( hasNoTransition() ){
            throw new Exception("No Transition Exception");
        }
        // alphabet check
        // checked alphabet in alphabet
        if( alphabetTo ){
            //System.out.println(alphabet);
            for( Trans s1 : getTs() ){
                //System.out.println(s1.getName());
                if( !alphabet.contains(s1.getName()) && !s1.getName().equals(Trans.Epsilon) ){
                    throw new Exception("Alphabet Exception: '"+s1.getName()+"' does not exist.");
                }
            }
        }
    }

    public boolean hasNoTransition() {
        return ts.size() == 0;
    }

    public Pane toAFUI(){
        final Pane resPane = new Pane();
        resPane.setPrefWidth(640);
        resPane.setPrefHeight(480);
        resPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.47)");
        resPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Controller.paletteOn = "";
            }
        });
        // create UI from core
        // create etats
        ArrayList<StateUI> alei = new ArrayList<StateUI>();
        for( State state : getQ() ){
            StateUI stateUI2 = null;
            if( state.isInitial() ){
                if( !state.isFinal() ){
                    stateUI2 = new StateUI(resPane, "StateI");
                }else{
                    stateUI2 = new StateUI(resPane, "StateIF");
                }
            }else{
                if( !state.isFinal() ){
                    stateUI2 = new StateUI(resPane, "State");
                }else{
                    stateUI2 = new StateUI(resPane, "StateF");
                }
            }
            final StateUI stateUI = stateUI2;
            //t.addTo(resPane);
            resPane.getChildren().add(stateUI);
            alei.add(stateUI);
            stateUI.getText().setText(state.getName());
            stateUI.setState(state);
            state.setStateUI(stateUI);

            final ArrayList<transLine> alt = new ArrayList<transLine>();
            final ArrayList<transLine> alt2 = new ArrayList<transLine>();
            stateUI.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    alt.clear();
                    alt2.clear();
                    alt.addAll(Controller.getTransRelatedTo(resPane, stateUI));
                    alt2.addAll(Controller.getTransRelatedTo2(resPane, stateUI));
                }
            });
            stateUI.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                        double pmouseX = stateUI.getLayoutX();
                        double pmouseY = stateUI.getLayoutY();
                        stateUI.setCenterX(event.getSceneX() );
                        stateUI.setCenterY(event.getSceneY() );
                        double diffX = stateUI.getLayoutX() - pmouseX;
                        double diffY = stateUI.getLayoutY() - pmouseY;

                        for( transLine transline : alt ){
                            if( transline!=null && transline.getPolygon()!=null ){
                                transline.endX( transline.endX()+diffX );
                                transline.endY(transline.endY() + diffY);
                                transline.fixPolygon();
                                transline.fixText();
                            }
                        }
                        for( transLine transline : alt2 ){
                            if( transline!=null && transline.getPolygon()!=null ){
                                transline.startX(transline.startX() + diffX );
                                transline.startY(transline.startY() + diffY );
                                transline.fixPolygon();
                                transline.fixText();
                            }
                        }
                }
            });
        }
        ArrayList<transLine> altl = new ArrayList<transLine>();
        for( State state : getQ() ){
            for (State state1 : getQ()){
                transLine t = new transLine();
                t.getText().setText("");
                ArrayList<Trans> alt = Trans.getTransitions(state, state1);
                if( alt.size()>0 ){
                    int k = 0;
                    for( Trans trans : alt ){
                        if( k==0 ){
                            t.getText().setText( trans.getName());
                        }else{
                            if( !Arrays.asList(t.getText().getText().split(",")).contains(trans.getName()) ){
                                t.getText().setText( t.getText().getText()+","+trans.getName());
                            }
                        }
                        k++;
                    }
                    t.setFrom(state.getStateUI());
                    t.setTo(state1.getStateUI());
                    altl.add(t);
                    t.addTo(resPane);
                }
            }
        }

        StateUI initial = intiale().getStateUI();
        // prepare etatui
        for( StateUI stateUI : alei ){
            for( transLine t : altl ){
                if( t.getFrom().equals(stateUI) ){
                    stateUI.getTransLines().add(t);
                }
            }
        }
        // set levels
        //initial.getViewHeight(0);

        // initilaze positioning
        for( StateUI stateUI : alei ){
            stateUI.setPositioned(false);
        }
        // do the positioning
        initial.setCenterX(100);
        initial.setCenterY(resPane.getPrefHeight()/2);
        initial.setPositioned(true);
        initial.doPositioning();

        for( transLine t : altl ){
            if( t.getTo().equals(t.getFrom()) ){
                double sx = t.getTo().getLayoutX()+t.getTo().getCircle().getLayoutX()-t.getTo().getCircle().getRadius()/2;
                double sy = t.getTo().getLayoutY()+t.getTo().getCircle().getLayoutY()-t.getTo().getCircle().getRadius()/2;;
                double ex = t.getTo().getLayoutX()+t.getTo().getCircle().getLayoutX()+t.getTo().getCircle().getRadius()/2;;
                double ey = t.getTo().getLayoutY()+t.getTo().getCircle().getLayoutY()-t.getTo().getCircle().getRadius()/2;
                Point2D s = new Point2D(sx,sy);
                Point2D e = new Point2D(ex,ey);
                t.startX(s.getX());
                t.startY(s.getY());
                t.endX(e.getX());
                t.endY(e.getY());
                t.fixControl(t.endX() / 2 + t.startX() / 2, t.startY() - 40);
            }else{
                double sox = t.getFrom().getLayoutX()+t.getFrom().getCircle().getLayoutX();
                double soy = t.getFrom().getLayoutY()+t.getFrom().getCircle().getLayoutY();
                double eox = t.getTo().getLayoutX()+t.getTo().getCircle().getLayoutX();
                double eoy = t.getTo().getLayoutY()+t.getTo().getCircle().getLayoutY();
                Point2D so = new Point2D(sox,soy);
                Point2D eo = new Point2D(eox,eoy);
                Point2D s = SF.getMSecond(so,eo,t.getFrom().getCircle().getRadius());
                Point2D e = SF.getMSecond(eo,so,t.getTo().getCircle().getRadius());
                t.startX(s.getX());
                t.startY(s.getY());
                t.endX(e.getX());
                t.endY(e.getY());
                t.fixControl();
            }
            t.fixPolygon();
            t.fixText();
        }

        // position the etats
        // create transitions
        // combine transitions
        // position the transitions
        return resPane;
    }


}
