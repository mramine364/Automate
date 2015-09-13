package sample.AutomateUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import sample.Automate.Trans;
import sample.Controller;
import sample.SF.SF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by don on 8/16/2015.
 */
public class transLine extends Path {

    private Polygon polygon;
    private TextField text;
    private MoveTo start;
    private CubicCurveTo curve;
    private CubicCurveTo reverseCurve;
    private StateUI to;
    private StateUI from;
    private double arrowHeadTransLength = 14;

    public ArrayList<Trans> getTrans() {
        //if( trans == null ){
        ArrayList<Trans> trans = new ArrayList<Trans>();
        //}
        //trans.clear();
        String[] s = getText().getText().split(",");
        for( int i=0; i<s.length; i++ ){
            s[i] = s[i].replace(" ","");
        }
        HashSet<String> hs = new HashSet<String>(Arrays.asList(s));
        hs.remove("");

        for( String s1 : hs ){
            Trans trans1 = new Trans();
            trans1.setName(s1);
            trans1.setFrom( from.getState() );
            trans1.setTo( to.getState() );
            trans.add(trans1);
            //System.out.println("ArrayList<Trans> getTrans(): "+trans1);
        }

        return trans;
    }

    public StateUI getTo() {
        return to;
    }

    public void setTo(StateUI to) {
        this.to = to;
    }

    public StateUI getFrom() {
        return from;
    }

    public void setFrom(StateUI from) {
        this.from = from;
    }

    public transLine() {

        polygon = new Polygon();
        start = new MoveTo();
        curve = new CubicCurveTo();
        reverseCurve = new CubicCurveTo();
        getElements().addAll(start,curve,reverseCurve);
        setStrokeType(StrokeType.CENTERED);
        setStrokeWidth(2);

        text = new TextField();
        text.setText("a");
        text.setEditable(false);
        text.setAlignment(Pos.CENTER);
        text.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);");
        text.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                textResize();
            }
        });
        textResize();

        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (Controller.paletteOn.equals("")) {
                    fixControl(2 * event.getSceneX() - startX(), 2 * event.getSceneY() - startY());
                    fixPolygon();
                    fixText();
                }
            }
        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (Controller.paletteOn.equals("")) {
                    hovered();
                } else if (Controller.paletteOn.equalsIgnoreCase("Delete")) {
                    hoveredToDelete();
                }
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (Controller.paletteOn.equals("") || Controller.paletteOn.equalsIgnoreCase("Delete")) {
                    unHovered();
                }
            }
        });
        text.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (Controller.paletteOn.equals("")) {
                    hovered();
                } else if (Controller.paletteOn.equalsIgnoreCase("Delete")) {
                    hoveredToDelete();
                }
            }
        });
        text.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (Controller.paletteOn.equals("") || Controller.paletteOn.equalsIgnoreCase("Delete")) {
                    unHovered();
                }
            }
        });

    }

    public void textResize(){
        text.setPrefWidth(textLength()); // why 7? Totally trial number.
    }

    public double textLength(){
        return text.getText().length() * 7 + 20;
    }

    public void addTo(Pane p){
        p.getChildren().addAll(this, polygon, text);
    }

    public void removeFrom(Pane p){
        p.getChildren().removeAll(this, polygon, text);
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public TextField getText() {
        return text;
    }

    public void setText(TextField text) {
        this.text = text;
    }

    public void fixPolygon(){
        Point2D A = new Point2D(curve.getX(),curve.getY());
        Point2D Ap = new Point2D(curve.getControlX2(),curve.getControlY2());
        Point2D Ms = SF.getMSecond(A, Ap, arrowHeadTransLength);

        Point2D P1 = SF.getRotatePoint(Ms, A, Math.PI / 7);
        Point2D P2 = SF.getRotatePoint(Ms, A, -Math.PI / 7);
        getPolygon().getPoints().setAll(new Double[]{A.getX(),A.getY(),P1.getX(),P1.getY(),P2.getX(),P2.getY()});
    }

    public void fixText(){
        getText().setLayoutX(curve.getControlX1()-textLength()/2);
        getText().setLayoutY(curve.getControlY1());
    }

    public void fixControl(){
        curve.setControlX1( startX()/2 + endX()/2 );
        curve.setControlY1( startY()/2 + endY()/2 );
        curve.setControlX2( curve.getControlX1() );
        curve.setControlY2( curve.getControlY1() );

        reverseCurve.setControlX1( curve.getControlX2() );
        reverseCurve.setControlY1( curve.getControlY2() );
        reverseCurve.setControlX2( curve.getControlX1() );
        reverseCurve.setControlY2( curve.getControlY1() );
    }

    public void fixControl(double x1, double y1){
        curve.setControlX1( x1 );
        curve.setControlY1( y1 );
        curve.setControlX2( curve.getControlX1() );
        curve.setControlY2( curve.getControlY1() );

        reverseCurve.setControlX1( curve.getControlX2() );
        reverseCurve.setControlY1( curve.getControlY2() );
        reverseCurve.setControlX2( curve.getControlX1() );
        reverseCurve.setControlY2( curve.getControlY1() );
    }

    public void startX(double x){
        start.setX(x);
        reverseCurve.setX(x);
    }

    public void startY(double y){
        start.setY(y);
        reverseCurve.setY(y);
    }

    public void endX(double x){
        curve.setX(x);
    }

    public void endY(double y){
        curve.setY(y);
    }

    public double startX(){
        return start.getX();
    }

    public double startY(){
        return start.getY();
    }

    public double endX(){
        return curve.getX();
    }

    public double endY(){
        return curve.getY();
    }

    public void hovered(){
        setStroke(Color.BLUE);
        polygon.setStroke(Color.BLUE);
        setOpacity(0.6);
        polygon.setOpacity(0.6);
        text.setStyle("-fx-background-color: rgba(255, 255, 255, 0.45);");
    }

    public void hoveredToDelete(){
        setStroke(Color.RED);
        polygon.setStroke(Color.RED);
        setOpacity(0.6);
        polygon.setOpacity(0.6);
        text.setStyle("-fx-background-color: rgba(255, 25, 28, 0.67);");
    }

    public void unHovered(){
        setStroke(Color.BLACK);
        polygon.setStroke(Color.BLACK);
        setOpacity(1);
        polygon.setOpacity(1);
        text.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);");
    }
}
