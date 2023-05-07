import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("Show Modal Window");
        button.setOnAction(event -> {
            Stage modalStage = new Stage();
            modalStage.setTitle("Modal Window");
            modalStage.initOwner(primaryStage);
            modalStage.initModality(Modality.APPLICATION_MODAL);

            StackPane modalRoot = new StackPane();
            Scene modalScene = new Scene(modalRoot, 200, 150);
            modalStage.setScene(modalScene);

            modalStage.showAndWait();

            System.out.println("Modal window closed.");
        });

        StackPane root = new StackPane();
        root.getChildren().add(button);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("ShowAndWait Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
