package ru.hse.anstkras.cannon;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Cannon extends Application {

    private final static int WIDTH = 1000;
    private final static int HEIGHT = 500;
    private final ArrayList<Segment> segmentsList = new ArrayList<>();
    private Pane pane;
    private CannonRepresentation cannon;
    private int cannonballType = 1;
    private Circle target;
    private Stage stage;
    private javafx.event.EventHandler<KeyEvent> keyEventHandler;
    private Polygon mountain1;
    private Polygon mountain2;
    private Polygon mountain3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        stage = primaryStage;
        pane = new Pane();
        pane.setMinWidth(WIDTH);
        pane.setMinHeight(HEIGHT);
        pane.setMaxSize(WIDTH, HEIGHT);
        root.setCenter(pane);


        mountain1 = new Polygon(-100, HEIGHT,
                                300, HEIGHT - 200,
                                500, HEIGHT);
        segmentsList.add(new Segment(-0.5, 0, HEIGHT, 300, HEIGHT - 200));
        segmentsList.add(new Segment(1, 300, HEIGHT - 200, 425, HEIGHT - 75));
        mountain1.setFill(Color.GREEN);

        mountain2 = new Polygon(350, HEIGHT,
                                650, HEIGHT - 300,
                                800, HEIGHT);
        mountain2.setFill(Color.GREEN);

        segmentsList.add(new Segment(-1, 425, HEIGHT - 75, 650, HEIGHT - 300));
        segmentsList.add(new Segment(2, 650, HEIGHT - 300, 776.92, HEIGHT - 46.154));

        mountain3 = new Polygon(700, HEIGHT,
                                950, HEIGHT - 150,
                                1100, HEIGHT);
        mountain3.setFill(Color.GREEN);

        segmentsList.add(new Segment(-0.6, 776.92, HEIGHT - 46.154, 950, HEIGHT - 150));
        segmentsList.add(new Segment(1, 950, HEIGHT - 150, WIDTH, HEIGHT));

        target = new Circle(900, HEIGHT - 125, 25);
        target.setFill(Color.RED);
        pane.getChildren().addAll(mountain1, mountain2, mountain3, target);
        cannon = new CannonRepresentation();
        cannon.draw();

        keyEventHandler = key -> {
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
            if (code == KeyCode.ENTER) {
                cannon.shoot();
            }
            if (code == KeyCode.DIGIT1) {
                cannonballType = 1;
            }
            if (code == KeyCode.DIGIT2) {
                cannonballType = 2;
            }
            if (code == KeyCode.DIGIT3) {
                cannonballType = 3;
            }
        };


        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);

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
        private final static int RECTANGLE_WIDTH = 180;
        private final static int RECTANGLE_HEIGHT = 40;
        private final Rotate rotate = new Rotate();
        private int circle_x = 125;
        private int circle_y = HEIGHT - 125;
        private int angle = 320;
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
            System.out.println(angle);
            if (angle == 150) {
                return;
            }
            angle -= 10;
            if (angle <= 0) {
                angle += 360;
            }
            rotate.setAngle(angle);

        }

        private void decreaseAngle() {
            if (angle == 30) {
                return;
            }
            angle += 10;
            if (angle >= 360) {
                angle -= 360;
            }
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

        private void shootImplement(int radius, int length, double curve, int speed) {
            Circle bullet = new Circle(rectangle.getX() + Math.cos(Math.toRadians(angle)) * RECTANGLE_WIDTH,
                                       rectangle.getY() + RECTANGLE_HEIGHT / 2.0 + Math.sin(Math.toRadians(angle)) * RECTANGLE_WIDTH, radius);

            int tick = 10;
            boolean isBoom = false;
            pane.getChildren().add(bullet);
            double startX = bullet.getCenterX();
            double startY = bullet.getCenterY();
            double toDivide;
            toDivide = Math.abs(90 - angle % 180);
            if (toDivide == 0) {
                toDivide = 5;
            }
            double coefficient = curve * Math.abs(0.1 / toDivide);
            double savedAngle = angle;
            System.out.println(coefficient);
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (now % speed == 0) {
                        if (savedAngle < 270 && savedAngle >= 90) {
                            bullet.setCenterX(bullet.getCenterX() - tick);
                        } else {
                            bullet.setCenterX(bullet.getCenterX() + tick);
                        }
                        double x = Math.abs(bullet.getCenterX() - startX) - length;
                        bullet.setCenterY((coefficient * x * x) + startY - length * length * coefficient);
                    }
                    if (intersectTarget(new Point2D(bullet.getCenterX(), bullet.getCenterY()))) {
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                                                                      new KeyValue(bullet.radiusProperty(), bullet.getRadius() * 3)));
                        timeline.setCycleCount(1);
                        timeline.setAutoReverse(false);
                        timeline.setOnFinished(event -> {
                                                   pane.getChildren().remove(bullet);
                                                   Text text = new Text(200, 100, "You win!");
                                                   text.setFont(Font.font(30));
                                                   pane.getChildren().add(text);
                                                   stage.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
                                               }
                        );
                        timeline.play();
                        this.stop();

                    }
                    if (intersectMountains(new Point2D(bullet.getCenterX(), bullet.getCenterY()))) {
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                                                                      new KeyValue(bullet.radiusProperty(), bullet.getRadius() * 3)));
                        timeline.setCycleCount(1);
                        timeline.setAutoReverse(false);
                        timeline.setOnFinished(event -> pane.getChildren().remove(bullet));
                        timeline.play();
                        this.stop();
                    }

                    if (bullet.getCenterX() >= WIDTH) {
                        pane.getChildren().remove(bullet);
                        this.stop();
                    }
                }
            };
            timer.start();
        }

        private void shoot() {
            if (cannonballType == 1) {
                shootImplement(10, 100, 0.5, 4);
            }
            if (cannonballType == 2) {
                shootImplement(13, 150, 0.8, 4);
            }
            if (cannonballType == 3) {
                shootImplement(15, 200, 1, 4);
            }
        }

        private boolean intersectMountains(Point2D point) {
            //            for (Segment segment : segmentsList) {
            //                Point2D start = new Point2D(segment.start_x, segment.start_y);
            //                Point2D end = new Point2D(segment.end_x, segment.end_y);
            //                if (Math.abs(distance(start, point) + distance(point, end) - distance(start, end)) < 2) {
            //                    return true;
            //                }
            //            }

            if (PointInTriangle(point, new Point2D(mountain1.getPoints().get(0), mountain1.getPoints().get(1)),
                                new Point2D(mountain1.getPoints().get(2), mountain1.getPoints().get(3)),
                                new Point2D(mountain1.getPoints().get(4), mountain1.getPoints().get(5)))) {
                return true;
            }

            if (PointInTriangle(point, new Point2D(mountain2.getPoints().get(0), mountain2.getPoints().get(1)),
                                new Point2D(mountain2.getPoints().get(2), mountain2.getPoints().get(3)),
                                new Point2D(mountain2.getPoints().get(4), mountain2.getPoints().get(5)))) {
                return true;
            }
            if (PointInTriangle(point, new Point2D(mountain3.getPoints().get(0), mountain3.getPoints().get(1)),
                                new Point2D(mountain3.getPoints().get(2), mountain3.getPoints().get(3)),
                                new Point2D(mountain3.getPoints().get(4), mountain3.getPoints().get(5)))) {
                return true;
            }
            return false;
        }

        private boolean intersectTarget(Point2D point) {
            int space = 0;
            if (cannonballType == 1) {
                space = 30;
            }
            if (cannonballType == 2) {
                space = 50;
            }
            if (cannonballType == 3) {
                space = 70;
            }
            return distance(point, new Point2D(target.getCenterX(), target.getCenterY())) <= space;
        }

        private double distance(Point2D a, Point2D b) {
            return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) +
                                     (a.getY() - b.getY()) * (a.getY() - b.getY()));
        }


        private double sign(Point2D p1, Point2D p2, Point2D p3) {
            return (p1.getX() - p3.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p3.getY());
        }

        private boolean PointInTriangle(Point2D pt, Point2D v1, Point2D v2, Point2D v3) {
            double d1, d2, d3;
            boolean has_neg, has_pos;

            d1 = sign(pt, v1, v2);
            d2 = sign(pt, v2, v3);
            d3 = sign(pt, v3, v1);

            has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
            has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

            return !(has_neg && has_pos);
        }

    }
}
