package ru.hse.anstkras.cannon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Cannon extends Application {

    private final static int WIDTH = 1000;
    private final static int HEIGHT = 500;
    private final ArrayList<Segment> segmentsList = new ArrayList<>();
    private Pane pane;
    private CannonRepresentation cannon;

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
        segmentsList.add(new Segment(-0.5, 0, HEIGHT, 300, HEIGHT - 200));
        segmentsList.add(new Segment(1, 300, HEIGHT - 200, 425, HEIGHT - 75));
        gora1.setFill(Color.GREEN);

        Polygon gora2 = new Polygon(350, HEIGHT,
                                    650, HEIGHT - 300,
                                    800, HEIGHT);
        gora2.setFill(Color.GREEN);

        segmentsList.add(new Segment(-1, 425, HEIGHT - 75, 650, HEIGHT - 300));
        segmentsList.add(new Segment(2, 650, HEIGHT - 300, 776.92, HEIGHT - 46.154));

        Polygon gora3 = new Polygon(700, HEIGHT,
                                    950, HEIGHT - 150,
                                    1100, HEIGHT);
        gora3.setFill(Color.GREEN);

        segmentsList.add(new Segment(-0.6, 776.92, HEIGHT - 46.154, 950, HEIGHT - 150));
        segmentsList.add(new Segment(1, 950, HEIGHT - 150, WIDTH, HEIGHT));

        Circle target = new Circle(900, HEIGHT - 125, 25);
        target.setFill(Color.RED);
        pane.getChildren().addAll(gora1, gora2, gora3, target);
        cannon = new CannonRepresentation();
        cannon.draw();

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode code = key.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) {
                cannon.increaseAngle();
            }
            if (code == KeyCode.S || code == KeyCode.DOWN) {
                cannon.decreaseAngle();
            }
            if (code == KeyCode.D || code == KeyCode.RIGHT) {
                cannon.moveRight();
            }
            if (code == KeyCode.A || code == KeyCode.LEFT) {
                cannon.moveLeft();
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

    private static class Segment {
        private final double tan;
        private final double angle;
        private final double start_x;
        private final double start_y;
        private final double end_x;
        private final double end_y;

        private Segment(double tan, double start_x, double start_y, double end_x, double end_y) {
            this.tan = tan;
            this.start_x = start_x;
            this.start_y = start_y;
            this.end_x = end_x;
            this.end_y = end_y;

            angle = Math.atan(tan);
        }

        public double getAngle() {
            return angle;
        }

        public double getStart_x() {
            return start_x;
        }

        public double getStart_y() {
            return start_y;
        }

        public double getEnd_x() {
            return end_x;
        }

        public double getEnd_y() {
            return end_y;
        }
    }

    private class CannonRepresentation {
        private final static int CIRCLE_RADIUS = 35;
        private final static int RECTANGLE_WIDTH = 200;
        private final static int RECTANGLE_HEIGHT = 40;
        private final Rotate rotate = new Rotate();
        private int circle_x = 125;
        private int circle_y = HEIGHT - 125;
        private int angle = -40;
        private Segment currentSegment;
        private int segmentIndex = 0;
        private Circle circle;
        private Rectangle rectangle;

        private CannonRepresentation() {
            currentSegment = segmentsList.get(segmentIndex);
        }

        private void draw() {
            circle = new Circle(circle_x, circle_y, CIRCLE_RADIUS);
            circle.setFill(Color.BLUE);

            rectangle = new Rectangle(circle_x, circle_y - RECTANGLE_HEIGHT / 2.0,
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

        private void moveRight() {
            if (circle.getCenterX() >= currentSegment.end_x - 10) {
                if (segmentIndex < segmentsList.size() - 1) {
                    currentSegment = segmentsList.get(++segmentIndex);
                    circle.setCenterX(currentSegment.start_x);
                    circle.setCenterY(currentSegment.start_y);

                    rotate.setAngle(0);
                    rectangle.setX(currentSegment.start_x);
                    rectangle.setY(currentSegment.start_y - RECTANGLE_HEIGHT / 2.0);
                    rotate.setPivotX(rectangle.getX());
                    rotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
                    rotate.setAngle(angle);

                    System.out.println(currentSegment.start_x);
                }
                return;
            }

            circle.setCenterX(circle.getCenterX() + 10);
            circle.setCenterY(circle.getCenterY() + 10 * currentSegment.tan);

            rotate.setAngle(0);
            rectangle.setX(rectangle.getX() + 10);
            rectangle.setY(rectangle.getY() + 10 * currentSegment.tan);
            rotate.setPivotX(rectangle.getX());
            rotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
            rotate.setAngle(angle);
        }

        private void moveLeft() {
            if (circle.getCenterX() <= currentSegment.start_x + 10) {
                if (segmentIndex > 0) {
                    currentSegment = segmentsList.get(--segmentIndex);
                    circle.setCenterX(currentSegment.end_x);
                    circle.setCenterY(currentSegment.end_y);

                    rotate.setAngle(0);
                    rectangle.setX(currentSegment.end_x);
                    rectangle.setY(currentSegment.end_y - RECTANGLE_HEIGHT / 2.0);
                    rotate.setPivotX(rectangle.getX());
                    rotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
                    rotate.setAngle(angle);

                    System.out.println(currentSegment.start_x);

                }
                return;
            }

            circle.setCenterX(circle.getCenterX() - 10);
            circle.setCenterY(circle.getCenterY() - 10 * currentSegment.tan);

            rotate.setAngle(0);
            rectangle.setX(rectangle.getX() - 10);
            rectangle.setY(rectangle.getY() - 10 * currentSegment.tan);
            rotate.setPivotX(rectangle.getX());
            rotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
            rotate.setAngle(angle);
        }
    }
}
