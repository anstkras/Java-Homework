package ru.hse.anstkras.cannon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.awt.*;

public class Cannon extends Application {

    private Pane pane;
    private CannonRepresentation cannon = new CannonRepresentation();
    private final static int WIDTH = 1000;
    private final static int HEIGHT = 500;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        pane = new Pane();
        pane.setMinWidth(WIDTH);
        pane.setMinHeight(HEIGHT);
        pane.setMaxSize(WIDTH, HEIGHT);
        root.setCenter(pane);


        Polygon gora1 = new Polygon(-100, HEIGHT,
                                    300, HEIGHT - 200,
                                    500, HEIGHT);
        gora1.setFill(Color.GREEN);

        Polygon gora2 = new Polygon(350, HEIGHT,
                                    650, HEIGHT - 300,
                                    800, HEIGHT);
        gora2.setFill(Color.GREEN);

        Polygon gora3 = new Polygon(700, HEIGHT,
                                    950, HEIGHT - 150,
                                    1100, HEIGHT);
        gora3.setFill(Color.GREEN);


        Circle target = new Circle(900, HEIGHT - 125, 25);
        target.setFill(Color.RED);
        pane.getChildren().addAll(gora1, gora2, gora3, target);
        cannon.draw();



        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode code = key.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) {
                cannon.increaseAngle();
            }
            if (code == KeyCode.S ||code == KeyCode.DOWN) {
                cannon.decreaseAngle();
            }
        });

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cannon");

        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMaxHeight(primaryStage.getHeight());
        primaryStage.setMaxWidth(primaryStage.getWidth());
        primaryStage.show();
    }

    private class CannonRepresentation {
        private int circle_x = 125;
        private int circle_y = HEIGHT - 125;
        private int angle = -40;
        private final static int CIRCLE_RADIUS = 35;
        private final static int RECTANGLE_WIDTH = 200;
        private final static int RECTANGLE_HEIGHT = 40;
        private final Rotate rotate = new Rotate();

        private void draw() {
            Circle circle = new Circle(circle_x, circle_y, CIRCLE_RADIUS);
            circle.setFill(Color.BLUE);

            Rectangle rectangle = new Rectangle(circle_x, circle_y - RECTANGLE_HEIGHT / 2.0,
                                                RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            rectangle.setFill(Color.BLUE);
            rotate.setAngle(angle);
            rotate.setPivotX(rectangle.getX());
            rotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
            rectangle.getTransforms().add(rotate);

            pane.getChildren().addAll(circle, rectangle);
        }

        private void increaseAngle() {
            angle -= 10;
            rotate.setAngle(angle);
        }

        private void decreaseAngle() {
            angle += 10;
            rotate.setAngle(angle);
        }
    }
}
