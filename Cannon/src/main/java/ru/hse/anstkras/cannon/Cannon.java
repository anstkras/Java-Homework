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
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Cannon extends Application {

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private Pane pane;
    private CannonRepresentation cannon = new CannonRepresentation();
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
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(900, HEIGHT - 150, 50, 50);

        cannon.draw();

        pane = new BorderPane(canvas);
        pane.setMinWidth(WIDTH);
        pane.setMinHeight(HEIGHT);
        pane.setMaxSize(WIDTH, HEIGHT);

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode code = key.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) {
                cannon.increaseAngle();
            }
            if (code == KeyCode.S ||code == KeyCode.DOWN) {
                cannon.decreaseAngle();
            }
        });

        Scene scene = new Scene(pane);
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
        private final static int CIRCLE_HEIGHT = 70;
        private final static int CIRCLE_WIDTH = 70;
        private final static int RECTANGLE_WIDTH = 200;
        private final static int RECTANGLE_HEIGHT = 40;

        private void draw() {
            graphicsContext.setFill(Color.BLUE);
            graphicsContext.fillOval(circle_x - CIRCLE_WIDTH / 2.0, circle_y - CIRCLE_HEIGHT / 2.0, CIRCLE_WIDTH, CIRCLE_HEIGHT);
            graphicsContext.save();
            graphicsContext.transform(new Affine(new Rotate(angle, circle_x, circle_y)));
            graphicsContext.fillRect(circle_x, circle_y - RECTANGLE_HEIGHT / 2.0, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            graphicsContext.restore();
        }

        private void increaseAngle() {
            graphicsContext.save();
            graphicsContext.transform(new Affine(new Rotate(angle, circle_x, circle_y)));
            graphicsContext.clearRect(circle_x, circle_y - RECTANGLE_HEIGHT / 2.0, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            graphicsContext.restore();
            angle -= 10;
            draw();
        }

        private void decreaseAngle() {
            graphicsContext.save();
            graphicsContext.transform(new Affine(new Rotate(angle, circle_x, circle_y)));
            graphicsContext.clearRect(circle_x, circle_y - RECTANGLE_HEIGHT / 2.0, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            graphicsContext.restore();
            angle += 10;
            draw();
        }
    }
}
