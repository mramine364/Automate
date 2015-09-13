package sample.AutomateUI;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import sample.Automate.State;
import sample.Controller;
import sample.SF.SF;

import java.util.ArrayList;

/**
 * Created by don on 8/23/2015.
 */
public class StateUI extends Pane {

    public static int ecount = 0;
    private double etatRadius = 24;
    private StateUI This = this;
    private Circle circle;
    private Circle circle2;
    private Line line;
    private Line line2;
    private Line line3;
    private TextField text;
    //private Paint circlePaint = Paint.valueOf("#d84aff1a");
    //private Paint circle2Paint = Paint.valueOf("#d84dff14");
    private State state;
    private boolean positioned = false;
    private ArrayList<transLine> transLines;

    public ArrayList<transLine> getTransLines() {
        return transLines;
    }

    public void setTransLines(ArrayList<transLine> transLines) {
        this.transLines = transLines;
    }

    public boolean isPositioned() {
        return positioned;
    }

    public void setPositioned(boolean positioned) {
        this.positioned = positioned;
    }

    public State getState() {
        if( state == null ){
            state = new State();
        }
        state.setName(text.getText());
        state.setInitial( isTnitiale() );
        state.setFinal( isFinale() );
        return state;
    }

    public TextField getText() {
        return text;
    }

    public void setText(TextField text) {
        this.text = text;
    }

    public void setState(State state) {

        this.state = state;
    }

    public boolean isTnitiale(){
        return line != null;
    }

    public boolean isFinale(){
        return circle2!=null;
    }

    public Circle getCircle() {
        return circle;
    }

    public StateUI(final Pane mainPane) {
        this(mainPane, "State");
    }

    public StateUI(final Pane mainPane, final String type) { // type : [State, Statei, Stateif, Statef]

        transLines = new ArrayList<transLine>();

        circle = new Circle();
        circle.setFill(Paint.valueOf("#d84aff1a")); circle.setLayoutX(29); circle.setLayoutY(27); circle.setRadius(24);
        circle.setStroke(Paint.valueOf("#580882")); circle.setStrokeType(StrokeType.valueOf("INSIDE"));

        text = new TextField();
        text.setText("e"+ecount); ecount++;
        text.setPrefWidth(etatRadius * 1.4);
        text.setPrefHeight(etatRadius * 0.7);
        text.setStyle("-fx-background-color: rgba(246, 241, 255, 0.27);");
        text.setLayoutX(circle.getLayoutX() - text.getPrefWidth() / 2);
        text.setLayoutY(circle.getLayoutY() - text.getPrefHeight() / 2);
        text.setPadding(new Insets(0));
        text.setAlignment(Pos.CENTER);
        text.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                text.setEditable(!text.isEditable());
            }
        });
        text.setMouseTransparent(true);

        if( type.equalsIgnoreCase("StateI") ){
            setPrefHeight(51); setPrefWidth(53);
            line = new Line();
            line.setEndX(20); line.setLayoutX(-6); line.setLayoutY(5.2); line.setRotate(65); line.setStartX(10);
            line2 = new Line();
            line2.setEndX(20); line2.setLayoutX(-9); line2.setLayoutY(8.5); line2.setRotate(19); line2.setStartX(10); line2.setStrokeMiterLimit(6);
            line3 = new Line();
            line3.setEndX(15); line3.setLayoutX(-2); line3.setLayoutY(5); line3.setRotate(43);
            getChildren().addAll(line,line2,line3,circle,text);

        }else if( type.equalsIgnoreCase("StateIF") ){
            setPrefHeight(51); setPrefWidth(53);
            line = new Line();
            line.setEndX(20); line.setLayoutX(-6); line.setLayoutY(5.2); line.setRotate(65); line.setStartX(10);
            line2 = new Line();
            line2.setEndX(20); line2.setLayoutX(-9); line2.setLayoutY(8.5); line2.setRotate(19); line2.setStartX(10); line2.setStrokeMiterLimit(6);
            line3 = new Line();
            line3.setEndX(15); line3.setLayoutX(-2); line3.setLayoutY(5); line3.setRotate(43);
            circle2 = new Circle();
            circle2.setFill(Paint.valueOf("#d84dff14")); circle2.setLayoutX(29); circle2.setLayoutY(27); circle2.setRadius(20);
            circle2.setStroke(Paint.valueOf("#592872d9")); circle2.setStrokeType(StrokeType.valueOf("INSIDE"));
            getChildren().addAll(line,line2,line3,circle2,circle,text);

        }else if( type.equalsIgnoreCase("StateF") ){
            setPrefHeight(48); setPrefWidth(48);
            circle2 = new Circle();
            circle2.setFill(Paint.valueOf("#d84dff14")); circle2.setLayoutX(29); circle2.setLayoutY(27); circle2.setRadius(20);
            circle2.setStroke(Paint.valueOf("#592872d9")); circle2.setStrokeType(StrokeType.valueOf("INSIDE"));
            getChildren().addAll(circle2,circle,text);

        }else{
            setPrefHeight(48); setPrefWidth(48);
            getChildren().addAll(circle,text);
        }

        circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (Controller.paletteOn.equalsIgnoreCase("Delete")) {
                    setOpacity(0.6);
                    circle.setFill(Paint.valueOf("#550000"));
                    if (type.equalsIgnoreCase("StateIF")) {
                        circle2.setFill(Paint.valueOf("#550000"));
                    } else if (type.equalsIgnoreCase("StateF")) {
                        circle2.setFill(Paint.valueOf("#550000"));
                    }
                } else {
                    setOpacity(0.6);
                }
                text.setMouseTransparent(!Controller.paletteOn.equalsIgnoreCase(""));
            }
        });

        circle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setOpacity(1);
                circle.setFill(Paint.valueOf("#d84aff1a"));
                if( type.equalsIgnoreCase("StateIF") ){
                    circle2.setFill(Paint.valueOf("#d84dff14"));
                }else if( type.equalsIgnoreCase("StateF") ){
                    circle2.setFill(Paint.valueOf("#d84dff14"));
                }
            }
        });

        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( Controller.paletteOn.equalsIgnoreCase("Delete") ){
                    // remove trans related to the circle

                    for(int i=0; i<mainPane.getChildren().size(); i++ ){
                        Object obj = mainPane.getChildren().get(i);
                        if( obj.getClass().getSimpleName().equals("transLine") ){
                            transLine tl = (transLine) obj;
                            if( tl.getFrom().equals(This) || tl.getTo().equals(This) ){
                                tl.removeFrom(mainPane);
                                i--;
                            }
                        }
                    }
                    // remove circle
                    mainPane.getChildren().remove(This);
                }
            }
        });

        circle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if( Controller.paletteOn.equalsIgnoreCase("Trans") ){

                    final transLine line = new transLine();
                    line.startX(event.getSceneX());
                    line.startY(event.getSceneY());
                    line.endX(event.getSceneX());
                    line.endY(event.getSceneY());
                    line.addTo(mainPane);
                    line.setFrom(This);

                    mainPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Controller.cursor.setLayoutX(event.getSceneX());
                            Controller.cursor.setLayoutY(event.getSceneY());
                            line.endX(event.getSceneX());
                            line.endY(event.getSceneY());

                            line.fixPolygon();
                            line.fixText();
                            line.fixControl();
                        }
                    });
                    mainPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            mainPane.setOnMouseDragged(null);
                            mainPane.setOnMouseReleased(null);
                            // if released on a circle
                            boolean removeLine = true;
                            for( Object obj : mainPane.getChildren() ){
                                if( obj.getClass().getSimpleName().equals("StateUI") ){
                                    StateUI stateUI = (StateUI) obj;
                                    Point2D O = new Point2D(stateUI.getLayoutX()+ stateUI.getCircle().getLayoutX(), stateUI.getLayoutY()+ stateUI.getCircle().getLayoutY());
                                    Point2D M = new Point2D(line.endX(),line.endY());
                                    if( O.distance(M)<=etatRadius ){
                                        line.setTo(stateUI);
                                        removeLine = false;
                                        Point2D endP = SF.getMSecond(O, M, etatRadius);
                                        line.endX( endP.getX() );
                                        line.endY( endP.getY() );
                                        line.fixPolygon();
                                        Point2D Op = new Point2D(line.getFrom().getLayoutX()+circle.getLayoutX(),line.getFrom().getLayoutY()+circle.getLayoutY());
                                        Point2D Mp = new Point2D(line.startX(),line.startY());
                                        Point2D startP = SF.getMSecond(Op, Mp, etatRadius);
                                        line.startX(startP.getX());
                                        line.startY(startP.getY());
                                        break;
                                    }
                                }
                            }
                            if( removeLine ){
                                line.removeFrom(mainPane);
                                line.setFrom(null);
                            }else{

                                line.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        if( Controller.paletteOn.equalsIgnoreCase("Delete") ){
                                            line.removeFrom(mainPane);
                                        }
                                    }
                                });
                                line.getText().setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        if (Controller.paletteOn.equalsIgnoreCase("Delete")) {
                                            line.removeFrom(mainPane);
                                        }else if( Controller.paletteOn.equalsIgnoreCase("") ){
                                            line.getText().setEditable(!line.getText().isEditable());
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }

    /*public int getNbrForeignTransitions(){
        int count = 0;
        for( transLine t : transLines ){
            if( !t.getTo().equals(this) ){
                count++;
            }
        }
        return ( count == 0 ) ? 1 : count;
    }*/

    private int level = 0;

    public int getLevel() {
        return level;
    }

    public int getViewHeight(int level){ // must initiale state
        if( positioned ){
           return 1;
        }else{
            positioned = true;
            this.level = level;
            int viewHeight = 1;
            for( transLine t : getTransLines() ){
                viewHeight += t.getTo().getViewHeight(level+1);
            }
            return viewHeight;
        }
    }

    public void doPositioning(){ // must be initiale to


            int i = 0, noposlen= 0;

            for( transLine t : getTransLines() ){
                if(  !t.getTo().isPositioned() ){
                    noposlen++;
                }
            }

            for( transLine t : getTransLines() ){
                if(  !t.getTo().isPositioned() ){
                    t.getTo().setCenterX(getCenterX() + 100);
                    t.getTo().setCenterY( getCenterY() - 100 * (noposlen - 1)/2 + 100 * i );
                    i++;
                }
            }

            for( transLine t : getTransLines() ){
                if(  !t.getTo().isPositioned() ){
                    t.getTo().setPositioned(true);
                    t.getTo().doPositioning();
                }
            }

    }

    public void setCenterX(double x){
        setLayoutX(x-getCircle().getLayoutX());
    }
    public void setCenterY(double y){
        setLayoutY(y - getCircle().getLayoutY());
    }

    public double getCenterX(){
        return getLayoutX()+getCircle().getLayoutX();
    }
    public double getCenterY(){
        return getLayoutY()+getCircle().getLayoutY();
    }
}
