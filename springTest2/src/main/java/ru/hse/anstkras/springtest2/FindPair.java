package ru.hse.anstkras.springtest2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/** Represents activity for getting the size of the board */
public class FindPair extends Application {
    private final static int DEFAULT_SIZE = 4;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var pane = new GridPane();
        final TextField sizeTextField = new TextField();
        sizeTextField.setPromptText("Enter size of the board.");
        GridPane.setConstraints(sizeTextField, 0, 1);
        pane.getChildren().add(sizeTextField);
        Button button = new Button("Start game");
        GridPane.setConstraints(button, 0, 2);
        pane.getChildren().add(button);

        button.setOnAction((e) -> {
            int size = DEFAULT_SIZE;
            try {
                size = Integer.valueOf(sizeTextField.getText());
                if (size < 2 || size > 100) {
                    size = DEFAULT_SIZE;
                }
            } catch (NumberFormatException ignored) {
                // just use default size
            }
            FindPairBoard findPairBoard = new FindPairBoard(primaryStage, size);
        });
        Scene scene = new Scene(pane);
        primaryStage.setTitle("FindPair");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(200);
        primaryStage.show();
    }
}
