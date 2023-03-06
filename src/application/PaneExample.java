package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class PaneExample extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(300, 200);
        pane.getChildren().add(new Circle(30, 50, 10));

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}