package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.ZoomEvent;
import javafx.stage.Stage;
import sample.Automate.AF;
import sample.Automate.State;
import sample.Automate.Trans;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Automate");
        Scene scene = new Scene((Parent)fxmlLoader.load(), 699, 441);

        Controller controller = fxmlLoader.<Controller>getController();
        controller.setScene(scene);
        primaryStage.setScene(scene);

        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
        //test1();
    }

    // TEST AFD OPTIMAZATION
    public static void test1(){
        AF afd = new AF();

        ArrayList<String> als = new ArrayList<String>();
        als.add("a");als.add("b");als.add("c");
        afd.setAlphabet(als);

        State a = new State(); afd.add(a); a.setName("e0"); a.setInitial(true);
        State b = new State(); afd.add(b); b.setName("e1");
        State c = new State(); afd.add(c); c.setName("e2");
        State d = new State(); afd.add(d); d.setName("e3"); d.setFinal(true);

        Trans tab = new Trans(); tab.setName("a"); afd.add(tab);
        tab.setFrom(a); tab.setTo(b);
        Trans tbc = new Trans(); tbc.setName("b"); afd.add(tbc);
        tbc.setFrom(b); tbc.setTo(c);
        Trans tbd = new Trans(); tbd.setName("c"); afd.add(tbd);
        tbd.setFrom(b); tbd.setTo(d);
        Trans tcc = new Trans(); tcc.setName("b"); afd.add(tcc);
        tcc.setFrom(c); tcc.setTo(c);
        Trans tcd = new Trans(); tcd.setName("c"); afd.add(tcd);
        tcd.setFrom(c); tcd.setTo(d);

        afd.prepareStates();

        ArrayList<State> als2 = afd.getOptimizedAFD().getQ();

        System.out.println("start test1");
        for( State e : als2 ){
            System.out.print(e.isInitial()+" ");
            System.out.print(e.isFinal()+" ");
            System.out.println(e.getTransitions() + " ");
        }
        System.out.println("end test1");
    }


}
