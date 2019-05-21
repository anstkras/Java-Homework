package ru.hse.anstkras.cannon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Cannon extends Application {

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private Pane pane;
    private final static int WIDTH = 1000;
    private final static int HEIGHT = 500;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        canvas.setWidth(WIDTH);
        canvas.setHeight(HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.setFill(Color.GREEN);
        graphicsContext.fillPolygon(new double[]{-100, 300, 500},
                                    new double[]{HEIGHT, HEIGHT - 200, HEIGHT},
                                    3);

        graphicsContext.fillPolygon(new double[]{350, 650, 800},
                                    new double[]{HEIGHT, HEIGHT - 300, HEIGHT},
                                    3);
        graphicsContext.fillPolygon(new double[]{700, 950, 1100},
                                    new double[]{HEIGHT, HEIGHT - 150, HEIGHT},
                                    3);
        pane = new BorderPane(canvas);
        pane.setMinWidth(WIDTH);
        pane.setMinHeight(HEIGHT);
        pane.setMaxSize(WIDTH, HEIGHT);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cannon");
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMaxHeight(primaryStage.getHeight());
        primaryStage.setMaxWidth(primaryStage.getWidth());
        primaryStage.show();
    }
}
