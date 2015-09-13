package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Created by don on 8/25/2015.
 */
public class Result {


    public static void display(final Pane resPane){

        HBox resContainer = new HBox();

        final double resPaneWidth = resPane.getPrefWidth();
        final double resPaneHeight = resPane.getPrefHeight();

        Slider slider = new Slider();
        slider.setOrientation(Orientation.VERTICAL);
        slider.setMax(100);
        slider.setMin(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setValue(50);
        slider.setMajorTickUnit(10);
        slider.setMinorTickCount(1);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double scale = Double.parseDouble(newValue+"") / 50;
                scale = (scale==0)?0.1:scale;
                resPane.setScaleX(scale);
                resPane.setScaleY(scale);
            }
        });
        slider.setPrefHeight( resPaneHeight );
        slider.setPrefWidth(40);

        resContainer.getChildren().add(resPane);
        resContainer.getChildren().add(slider);

        HBox.setHgrow(resPane, Priority.ALWAYS);

        Stage stage = new Stage();
        Scene scene = new Scene(resContainer, resContainer.getPrefWidth(), resContainer.getPrefHeight());
        stage.setTitle("Automate Result");
        stage.setScene(scene);
        stage.show();
    }
}
