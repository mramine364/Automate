package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import sample.Automate.AF;
import sample.Automate.State;
import sample.Automate.RE;
import sample.Automate.Trans;
import sample.AutomateUI.StateUI;
import sample.AutomateUI.transLine;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ToggleButton statei;
    @FXML
    private ToggleButton state;
    @FXML
    private ToggleButton statef;
    @FXML
    private ToggleButton stateif;
    @FXML
    private ToggleButton trans;
    @FXML
    private ToggleButton delete;

    @FXML
    private GridPane gridPane;
    @FXML
    private Pane mainPane;
    @FXML
    private GridPane palette;
    @FXML
    private Pane rightPane;
    @FXML
    private Pane middlePane;
    @FXML
    private Slider zoom;

    @FXML
    private RadioButton afne;
    @FXML
    private RadioButton afnd;
    @FXML
    private RadioButton afd;
    @FXML
    private RadioButton afdo;
    @FXML
    private Button convertBtn;

    @FXML
    private CheckBox autoCheck;
    @FXML
    private TextField alphabet;

    @FXML
    private RadioButton regExp;
    @FXML
    private TextField regExpText;
    @FXML
    private RadioButton automate;

    private Scene scene;
    public static String paletteOn = "";
    private double stateRadius = 24;

    private String stateImgUrl = "IMG/state.png";
    private String stateFImgUrl = "IMG/final.png";
    private String stateIImgUrl = "IMG/init_state.png";
    private String stateIFImgUrl = "IMG/init_final_state.png";
    private String transImgUrl = "IMG/trans.png";
    private String deleteImgUrl = "IMG/DeleteRed.png";

    public static Pane cursor;
    private double cursorWidth = 22;
    private double cursorHeight = 22;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public AF getAF(){
        AF res = new AF();
        //set Alphabet
        res.setAlphabet( getAlphabet() );

        //set Q : ArrayList<Etat>
        setQ(res);

        //set T : ArrayList<trans>
        setTs(res);

        //prepare etat
        res.prepareStates();

        //return AF
        return res;
    }

    public ArrayList<String> getAlphabet(){
        HashSet<String> hs = new HashSet<String>();

        if( autoCheck.isSelected() ){ // get alphabet automatically
            if( automate.isSelected() ){ // alphabet from transitions
                for( Object obj : mainPane.getChildren() ){
                    if( obj.getClass().getSimpleName().equals("transLine") ){
                        transLine t = (transLine) obj;
                        String[] ss = t.getText().getText().split(",");
                        // remove spaces
                        String[] s = new String[ss.length];
                        for( int i=0; i<ss.length; i++ ){
                            s[i] = ss[i].replace(" ","");
                        }
                        hs.addAll( Arrays.asList(s) );
                    }
                }
            }else{ // alphabet from regExp Text
                StringBuffer s = new StringBuffer(regExpText.getText());
                for( int i=0; i<s.length(); i++ ){
                    if( s.charAt(i)=='*' || s.charAt(i)=='(' || s.charAt(i)==')' || s.charAt(i)==' ' ){
                        s.replace(i,i+1,"");
                        i--;
                    }else if( s.charAt(i)=='|' || s.charAt(i)=='.' ){
                        s.replace(i,i+1,",");
                        i--;
                    }
                }
                String st[] = s.toString().split(",");
                //System.out.println(st);
                hs.addAll( Arrays.asList(st) );
            }
        }else{
            String[] s = alphabet.getText().split(",");
            hs.addAll(Arrays.asList(s));
        }
        hs.remove(Trans.Epsilon);
        hs.remove("");
        return new ArrayList<String>(hs);
    }

    public void setQ(AF af){

        for( Object obj : mainPane.getChildren() ){
            if( obj.getClass().getSimpleName().equals("StateUI") ){
                StateUI stateUI = (StateUI) obj;
                State state = stateUI.getState();
                state.setAf(af);
                af.add(state);
                state.getTransitions().clear();
                //System.out.println("form setQ: "+etat);
            }
        }
    }

    public void setTs(AF af){
        for( Object obj : mainPane.getChildren() ){
            if( obj.getClass().getSimpleName().equals("transLine") ){
                transLine t = (transLine) obj;
                af.addT(t.getTrans());
            }
        }
    }

    private double mainPaneWidth;
    private double mainPaneHeight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainPaneWidth = 460;
        mainPaneHeight = 440;

        middlePane.setStyle("-fx-background-color: #fff0f9");
        zoom.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                double newScale = Double.parseDouble(newValue+"")/50;
                newScale = (newScale==0)?0.1:newScale;

                mainPane.setScaleX(newScale);
                mainPane.setScaleY(newScale);

                mainPane.setPrefWidth(mainPaneWidth / newScale);
                mainPane.setPrefHeight(mainPaneHeight / newScale);
            }
        });

        convertBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                AF af = null;
                try {
                    if( regExp.isSelected() ){
                        RE re = new RE(regExpText.getText(),getAlphabet());
                        re.compile(!autoCheck.isSelected());
                        af = re.toAF();
                    }else// if( automate.isSelected() )
                    {
                        af = getAF();
                        af.compile(!autoCheck.isSelected());
                    }

                    if( afne.isSelected() ){
                        System.out.println("------START AFNE-------");
                        ArrayList<State> states = af.getQ();
                        for (State state : states ){
                            System.out.println(state.isInitial()+" "+ state.isFinal()+" "+ state.getTransitions());
                        }
                        System.out.println("------FIN-------");
                        Result.display( af.toAFUI() );

                    }else if( afnd.isSelected() ){
                        System.out.println("------START AFND-------");
                        ArrayList<State> states = af.getAFND().getQ();
                        for (State state : states ){
                            System.out.println(state.isInitial()+" "+ state.isFinal()+" "+ state.getTransitions());
                        }
                        System.out.println("------FIN-------");
                        Result.display(af.getAFND().toAFUI());
                    }else if( afd.isSelected() ){
                        System.out.println("------START AFD-------");
                        ArrayList<State> states = af.getAFND().getAFD().getQ();
                        for (State state : states ){
                            System.out.println(state.isInitial()+" "+ state.isFinal()+" "+ state.getTransitions());
                        }
                        System.out.println("------FIN-------");
                        Result.display(af.getAFND().getAFD().toAFUI());
                    }else if( afdo.isSelected() ){
                        System.out.println("------START AFDO-------");
                        ArrayList<State> states = af.getAFND().getAFD().getOptimizedAFD().getQ();
                        for (State state : states ){
                            System.out.println(state.isInitial()+" "+ state.isFinal()+" "+ state.getTransitions());
                        }
                        System.out.println("------FIN-------");
                        Result.display( af.getAFND().getAFD().getOptimizedAFD().toAFUI() );
                    }
                }catch (Exception e){
                    //System.out.println(e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Automate Exception");
                    alert.setHeaderText(e.getMessage());
                    e.printStackTrace();
                    alert.showAndWait();
                }

            }
        });

        alphabet.setText("");
        autoCheck.setSelected(true);
        if( autoCheck.isSelected() ){
            alphabet.setEditable(false);
        }else{
            alphabet.setEditable(true);
        }
        autoCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if( autoCheck.isSelected() ){
                    alphabet.setEditable(false);
                }else{
                    alphabet.setEditable(true);
                }
            }
        });

        palette.setStyle("-fx-border-width: 2px; -fx-border-color: teal; -fx-background-color: snow;");
        rightPane.setStyle("/*-fx-border-width: 1px; -fx-border-color: black;*/ -fx-background-color: snow;");

        cursor = new Pane();
        cursor.setPrefHeight(cursorHeight); cursor.setPrefWidth(cursorWidth);
        cursor.setMouseTransparent(true);

        mainPane.setStyle("-fx-background-color: white;");
        mainPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cursor.setLayoutX(event.getSceneX()-cursorWidth/2);
                cursor.setLayoutY(event.getSceneY()-cursorHeight/2);
            }
        });
        mainPane.getChildren().add(cursor);

        /** main pane */
        mainPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("stateI") ){
                    final StateUI p = new StateUI(mainPane,"stateI");
                    p.setLayoutX(event.getSceneX() - p.getPrefWidth()/2);
                    p.setLayoutY(event.getSceneY() - p.getPrefHeight() / 2);

                    final ArrayList<transLine> alt = new ArrayList<transLine>();
                    final ArrayList<transLine> alt2 = new ArrayList<transLine>();
                    p.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            alt.clear();
                            alt2.clear();
                            alt.addAll(getTransRelatedTo(mainPane,p));
                            alt2.addAll(getTransRelatedTo2(mainPane,p));
                        }
                    });
                    p.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (paletteOn.equalsIgnoreCase("")) {
                                double pmouseX = p.getLayoutX();
                                double pmouseY = p.getLayoutY();
                                p.setLayoutX( event.getSceneX() - p.getPrefWidth()/2 );
                                p.setLayoutY( event.getSceneY() - p.getPrefHeight()/2 );
                                double diffX = p.getLayoutX() - pmouseX;
                                double diffY = p.getLayoutY() - pmouseY;

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
                        }
                    });
                    mainPane.getChildren().add(p);
                }else if( paletteOn.equalsIgnoreCase("state") ){
                    final StateUI p = new StateUI(mainPane);
                    p.setLayoutX(event.getSceneX()-p.getPrefWidth()/2);
                    p.setLayoutY(event.getSceneY() - p.getPrefHeight() / 2);
                    final ArrayList<transLine> alt = new ArrayList<transLine>();
                    final ArrayList<transLine> alt2 = new ArrayList<transLine>();
                    p.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            alt.clear();
                            alt2.clear();
                            alt.addAll(getTransRelatedTo(mainPane,p));
                            alt2.addAll(getTransRelatedTo2(mainPane,p));
                        }
                    });
                    p.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if( paletteOn.equalsIgnoreCase("") ){

                                double pmouseX = p.getLayoutX();
                                double pmouseY = p.getLayoutY();
                                p.setLayoutX( event.getSceneX() - p.getPrefWidth()/2 );
                                p.setLayoutY( event.getSceneY() - p.getPrefHeight()/2 );
                                double diffX = p.getLayoutX() - pmouseX;
                                double diffY = p.getLayoutY() - pmouseY;

                                for( transLine transline : alt ){
                                    // search line and move it & search polygone and move it
                                    if( transline!=null && transline.getPolygon()!=null ){
                                        transline.endX( transline.endX()+diffX );
                                        transline.endY(transline.endY() + diffY);
                                        transline.fixPolygon();

                                    }
                                }

                                for( transLine transline : alt2 ){
                                    // search line and move it
                                    if( transline!=null && transline.getPolygon()!=null ){
                                        transline.startX(transline.startX() + diffX );
                                        transline.startY(transline.startY() + diffY );

                                    }
                                }
                            }
                        }
                    });
                    mainPane.getChildren().add(p);
                } else if( paletteOn.equalsIgnoreCase("stateIF") ){
                    final StateUI p = new StateUI(mainPane, "stateIF");

                    p.setLayoutX(event.getSceneX()-p.getPrefWidth()/2);
                    p.setLayoutY(event.getSceneY()-p.getPrefHeight()/2);
                    final ArrayList<transLine> alt = new ArrayList<transLine>();
                    final ArrayList<transLine> alt2 = new ArrayList<transLine>();

                    p.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            alt.clear();
                            alt2.clear();
                            alt.addAll(getTransRelatedTo(mainPane,p));
                            alt2.addAll(getTransRelatedTo2(mainPane,p));
                        }
                    });
                    p.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if( paletteOn.equalsIgnoreCase("") ){

                                double pmouseX = p.getLayoutX();
                                double pmouseY = p.getLayoutY();
                                p.setLayoutX( event.getSceneX() - p.getPrefWidth()/2 );
                                p.setLayoutY( event.getSceneY() - p.getPrefHeight()/2 );
                                double diffX = p.getLayoutX() - pmouseX;
                                double diffY = p.getLayoutY() - pmouseY;

                                for( transLine transline : alt ){
                                    if( transline!=null && transline.getPolygon()!=null ){
                                        transline.endX(transline.endX() + diffX);
                                        transline.endY(transline.endY() + diffY);
                                        transline.fixPolygon();
                                    }
                                }
                                for( transLine transline : alt2 ){
                                    if( transline!=null && transline.getPolygon()!=null ){
                                        transline.startX(transline.startX() + diffX);
                                        transline.startY(transline.startY() + diffY);
                                    }
                                }
                            }
                        }
                    });
                    mainPane.getChildren().add(p);
                } else if( paletteOn.equalsIgnoreCase("stateF") ){
                    final StateUI p = new StateUI(mainPane, "stateF");
                    p.setLayoutX(event.getSceneX()-p.getPrefWidth()/2);
                    p.setLayoutY(event.getSceneY()-p.getPrefHeight()/2);
                    final ArrayList<transLine> alt = new ArrayList<transLine>();
                    final ArrayList<transLine> alt2 = new ArrayList<transLine>();

                    p.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            alt.clear();
                            alt2.clear();
                            alt.addAll(getTransRelatedTo(mainPane,p));
                            alt2.addAll(getTransRelatedTo2(mainPane,p));
                        }
                    });
                    p.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if( paletteOn.equalsIgnoreCase("") ){

                                double pmouseX = p.getLayoutX();
                                double pmouseY = p.getLayoutY();
                                p.setLayoutX( event.getSceneX() - p.getPrefWidth()/2 );
                                p.setLayoutY( event.getSceneY() - p.getPrefHeight()/2 );
                                double diffX = p.getLayoutX() - pmouseX;
                                double diffY = p.getLayoutY() - pmouseY;

                                for( transLine transline : alt ){
                                    if( transline!=null && transline.getPolygon()!=null ){
                                        transline.endX( transline.endX()+ diffX );
                                        transline.endY( transline.endY() + diffY );
                                        transline.fixPolygon();
                                    }
                                }
                                for( transLine transline : alt2 ){
                                    if( transline!=null && transline.getPolygon()!=null ){
                                        transline.startX( transline.startX() + diffX );
                                        transline.startY( transline.startY() + diffY );
                                    }
                                }
                            }
                        }
                    });
                    mainPane.getChildren().add(p);
                }
            }
        });
        /***/
        /** Etat initiale */
        final Image img = new Image(getClass().getResourceAsStream(stateIImgUrl));
        statei.setText("");  statei.setGraphic(new ImageView(img));

        statei.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("stateI") ){
                    cursor.getChildren().clear();
                    getScene().setCursor(Cursor.DEFAULT);
                    paletteOn = "";
                    statei.setSelected(false);
                }else{
                    cursor.getChildren().setAll(new ImageView(img));
                    getScene().setCursor(Cursor.NONE);
                    paletteOn = "stateI";
                    state.setSelected(false);
                    stateif.setSelected(false);
                    statef.setSelected(false);
                    trans.setSelected(false);
                    delete.setSelected(false);
                }
            }
        });
        /***/
        /** Etat  */
        final Image img1 = new Image(getClass().getResourceAsStream(stateImgUrl));
        state.setText("");  state.setGraphic(new ImageView(img1));

        state.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("state") ){
                    cursor.getChildren().clear();
                    getScene().setCursor(Cursor.DEFAULT);
                    paletteOn = "";
                    state.setSelected(false);
                }else{
                    cursor.getChildren().setAll(new ImageView(img1));
                    getScene().setCursor(Cursor.NONE);
                    paletteOn = "state";
                    statei.setSelected(false);
                    stateif.setSelected(false);
                    statef.setSelected(false);
                    trans.setSelected(false);
                    delete.setSelected(false);
                }
            }
        });
        /***/
        /** Etat initiale final */
        final Image img2 = new Image(getClass().getResourceAsStream(stateIFImgUrl));
        stateif.setText("");  stateif.setGraphic(new ImageView(img2));
        stateif.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("stateIF") ){
                    cursor.getChildren().clear();
                    getScene().setCursor(Cursor.DEFAULT);
                    paletteOn = "";
                    stateif.setSelected(false);
                }else{
                    cursor.getChildren().setAll(new ImageView(img2));
                    getScene().setCursor(Cursor.NONE);
                    paletteOn = "stateIF";
                    statei.setSelected(false);
                    state.setSelected(false);
                    statef.setSelected(false);
                    trans.setSelected(false);
                    delete.setSelected(false);
                }
            }
        });
        /***/
        /** Etat final */
        final Image img3 = new Image(getClass().getResourceAsStream(stateFImgUrl));
        statef.setText("");  statef.setGraphic(new ImageView(img3));
        statef.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("stateF") ){
                    cursor.getChildren().clear();
                    getScene().setCursor(Cursor.DEFAULT);
                    paletteOn = "";
                    statef.setSelected(false);
                }else{
                    cursor.getChildren().setAll(new ImageView(img3));
                    getScene().setCursor(Cursor.NONE);
                    paletteOn = "stateF";
                    statei.setSelected(false);
                    state.setSelected(false);
                    stateif.setSelected(false);
                    trans.setSelected(false);
                    delete.setSelected(false);
                }
            }
        });
        /***/
        /** Etat final */
        final Image img4 = new Image(getClass().getResourceAsStream(transImgUrl));
        trans.setText("");  trans.setGraphic(new ImageView(img4));
        trans.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("Trans") ){
                    cursor.getChildren().clear();
                    getScene().setCursor(Cursor.DEFAULT);
                    paletteOn = "";
                    trans.setSelected(false);
                }else{
                    cursor.getChildren().setAll(new ImageView(img4));
                    getScene().setCursor(Cursor.NONE);
                    paletteOn = "Trans";
                    statei.setSelected(false);
                    state.setSelected(false);
                    stateif.setSelected(false);
                    statef.setSelected(false);
                    delete.setSelected(false);
                }
            }
        });
        /***/
        /** Etat initiale */
        final Image img5 = new Image(getClass().getResourceAsStream(deleteImgUrl));
        delete.setText("");  delete.setGraphic(new ImageView(img5));
        delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("Delete") ){
                    cursor.getChildren().clear();
                    getScene().setCursor(Cursor.DEFAULT);
                    paletteOn = "";
                    delete.setSelected(false);
                }else{
                    cursor.getChildren().setAll(new ImageView(img5));
                    getScene().setCursor(Cursor.NONE);
                    paletteOn = "Delete";
                    statei.setSelected(false);
                    state.setSelected(false);
                    stateif.setSelected(false);
                    statef.setSelected(false);
                    trans.setSelected(false);
                }
            }
        });
        /***/

        /** Initilaze palette */
        rightPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cursor.getChildren().clear();
                getScene().setCursor(Cursor.DEFAULT);
            }
        });
        rightPane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( paletteOn.equalsIgnoreCase("stateI") ){
                    cursor.getChildren().setAll(new ImageView(img));
                    getScene().setCursor(Cursor.NONE);
                }else if( paletteOn.equalsIgnoreCase("state") ){
                    cursor.getChildren().setAll(new ImageView(img1));
                    getScene().setCursor(Cursor.NONE);
                }
                else if( paletteOn.equalsIgnoreCase("stateIF") ){
                    cursor.getChildren().setAll(new ImageView(img2));
                    getScene().setCursor(Cursor.NONE);
                }
                else if( paletteOn.equalsIgnoreCase("stateF") ){
                    cursor.getChildren().setAll(new ImageView(img3));
                    getScene().setCursor(Cursor.NONE);
                }
                else if( paletteOn.equalsIgnoreCase("Trans") ){
                    cursor.getChildren().setAll(new ImageView(img4));
                    getScene().setCursor(Cursor.NONE);
                }
                else if( paletteOn.equalsIgnoreCase("Delete") ){
                    cursor.getChildren().setAll(new ImageView(img5));
                    getScene().setCursor(Cursor.NONE);
                }
            }
        });
    }

    public static ArrayList<transLine> getTransRelatedTo(Pane mainPane, Pane pc){ // end
        ArrayList<transLine> alp = new ArrayList<transLine>();
        for( Object ob : mainPane.getChildren() ){
            if( ob.getClass().getSimpleName().equals("transLine") ){
                transLine line = (transLine)ob;
                if( line.getTo()!=null && line.getTo().equals(pc) ){
                    alp.add(line);
                }
            }
        }
        return alp;
    }

    public static ArrayList<transLine> getTransRelatedTo2(Pane mainPane, Pane pc){ // start
        ArrayList<transLine> alp = new ArrayList<transLine>();
        for( Object ob : mainPane.getChildren() ){
            if( ob.getClass().getSimpleName().equals("transLine") ){
                transLine line = (transLine)ob;

                if(  line.getFrom() !=null && line.getFrom().equals(pc) ){
                    alp.add(line);
                }
            }
        }
        return alp;
    }


}