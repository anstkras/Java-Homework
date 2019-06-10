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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Represents a simplified version of Scorched Earth
 */
public class Cannon extends Application {
    private final static int WIDTH = 1000;
    private final static int HEIGHT = 500;
    private final ArrayList<Segment> segmentsList = new ArrayList<>();
    private Pane pane;
    private CannonRepresentation cannon;
    private Circle target;
    private Polygon mountain1;
    private Polygon mountain2;
    private Polygon mountain3;
    private Stage stage;
    private javafx.event.EventHandler<KeyEvent> keyEventHandler;
    private Text instruction;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        BorderPane root = new BorderPane();
        pane = new Pane();
        root.setMinWidth(WIDTH);
        root.setMinHeight(HEIGHT);
        root.setMaxSize(WIDTH, HEIGHT);
        root.setCenter(pane);
        instruction = new Text(0, 30, "1 - small cannonball\n2 - medium cannonball \n3 - big cannonball");

        mountain1 = new Polygon(-100, HEIGHT,
                                300, HEIGHT - 200,
                                500, HEIGHT);
        mountain1.setFill(Color.GREEN);
        segmentsList.add(new Segment(-0.5, 0, HEIGHT, 300, HEIGHT - 200));
        segmentsList.add(new Segment(1, 300, HEIGHT - 200, 425, HEIGHT - 75));

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
        pane.getChildren().addAll(mountain1, mountain2, mountain3, target, instruction);
        cannon = new CannonRepresentation();

        addKeyHandler();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cannon");
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setMaxHeight(HEIGHT);
        primaryStage.setMaxWidth(WIDTH);
        primaryStage.show();
    }

    private void addKeyHandler() {
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
                cannon.setCannonBallType(CannonBallType.SMALL);
            }
            if (code == KeyCode.DIGIT2) {
                cannon.setCannonBallType(CannonBallType.MEDIUM);
            }
            if (code == KeyCode.DIGIT3) {
                cannon.setCannonBallType(CannonBallType.BIG);
            }
        };
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    private enum CannonBallType {
        SMALL(10, 100, 0.5, 30),
        MEDIUM(13, 150, 0.8, 50),
        BIG(15, 200, 1, 70);

        private final int radius;
        private final int length;
        private final double curve;
        private final double space;

        CannonBallType(int radius, int length, double curve, double space) {
            this.radius = radius;
            this.length = length;
            this.curve = curve;
            this.space = space;
        }
    }

    // represents a line segment
    private static class Segment {
        private final double tangent;
        private final double startX;
        private final double startY;
        private final double endX;
        private final double endY;

        private Segment(double tangent, double startX, double startY, double endX, double endY) {
            this.tangent = tangent;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    private final class CannonRepresentation {
        private final static int CIRCLE_RADIUS = 35;
        private final static int RECTANGLE_WIDTH = 180;
        private final static int RECTANGLE_HEIGHT = 40;
        private final static int STEP = 10; // step for x coordinate for each frame
        private final static int FRAME_SPEED = 4; // on each FRAME_SPEED frame cannonball is redrawn
        private final Rotate rectangleRotate = new Rotate();
        private int rectangleAngle = 320;
        private Segment currentSegment; // on which line segment cannon is now
        private int segmentIndex = 0;
        private Circle circle = new Circle(125, HEIGHT - 125, CIRCLE_RADIUS);
        private Rectangle rectangle;
        private CannonBallType cannonBallType = CannonBallType.SMALL;

        private CannonRepresentation() {
            currentSegment = segmentsList.get(segmentIndex);
            draw();
        }

        private void setCannonBallType(@NotNull CannonBallType newCannonBallType) {
            cannonBallType = newCannonBallType;
        }

        // draws the start position of the cannon
        private void draw() {
            circle.setFill(Color.BLUE);

            rectangle = new Rectangle(circle.getCenterX(), circle.getCenterY() - RECTANGLE_HEIGHT / 2.0,
                                      RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            rectangle.setFill(Color.BLUE);
            rectangleRotate.setAngle(rectangleAngle);
            rectangleRotate.setPivotX(rectangle.getX());
            rectangleRotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
            rectangle.getTransforms().add(rectangleRotate);

            pane.getChildren().addAll(circle, rectangle);
        }

        private void increaseAngle() {
            if (rectangleAngle == 150) {
                return;
            }
            rectangleAngle -= 10;
            if (rectangleAngle <= 0) {
                rectangleAngle += 360;
            }
            rectangleRotate.setAngle(rectangleAngle);

        }

        private void decreaseAngle() {
            if (rectangleAngle == 30) {
                return;
            }
            rectangleAngle += 10;
            if (rectangleAngle >= 360) {
                rectangleAngle -= 360;
            }
            rectangleRotate.setAngle(rectangleAngle);
        }

        private void moveRight() {
            if (circle.getCenterX() >= currentSegment.endX - 10) {
                if (segmentIndex < segmentsList.size() - 1) {
                    currentSegment = segmentsList.get(++segmentIndex);
                    circle.setCenterX(currentSegment.startX);
                    circle.setCenterY(currentSegment.startY);

                    rectangleRotate.setAngle(0);
                    rectangle.setX(currentSegment.startX);
                    rectangle.setY(currentSegment.startY - RECTANGLE_HEIGHT / 2.0);
                    updateRotate();
                }
                return;
            }

            circle.setCenterX(circle.getCenterX() + 10);
            circle.setCenterY(circle.getCenterY() + 10 * currentSegment.tangent);

            rectangleRotate.setAngle(0);
            rectangle.setX(rectangle.getX() + 10);
            rectangle.setY(rectangle.getY() + 10 * currentSegment.tangent);
            rectangleRotate.setPivotX(rectangle.getX());
            rectangleRotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
            rectangleRotate.setAngle(rectangleAngle);
        }

        private void moveLeft() {
            if (circle.getCenterX() <= currentSegment.startX + 10) {
                if (segmentIndex > 0) {
                    currentSegment = segmentsList.get(--segmentIndex);
                    circle.setCenterX(currentSegment.endX);
                    circle.setCenterY(currentSegment.endY);

                    rectangleRotate.setAngle(0);
                    rectangle.setX(currentSegment.endX);
                    rectangle.setY(currentSegment.endY - RECTANGLE_HEIGHT / 2.0);
                    updateRotate();
                }
                return;
            }

            circle.setCenterX(circle.getCenterX() - 10);
            circle.setCenterY(circle.getCenterY() - 10 * currentSegment.tangent);

            rectangleRotate.setAngle(0);
            rectangle.setX(rectangle.getX() - 10);
            rectangle.setY(rectangle.getY() - 10 * currentSegment.tangent);
            rectangleRotate.setPivotX(rectangle.getX());
            rectangleRotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
            rectangleRotate.setAngle(rectangleAngle);
        }

        private void updateRotate() {
            rectangleRotate.setPivotX(rectangle.getX());
            rectangleRotate.setPivotY(rectangle.getY() + RECTANGLE_HEIGHT / 2.0);
            rectangleRotate.setAngle(rectangleAngle);
        }

        private void shoot() {
            var bullet = new Circle(rectangle.getX() + Math.cos(Math.toRadians(rectangleAngle)) * RECTANGLE_WIDTH,
                                    rectangle.getY() + RECTANGLE_HEIGHT / 2.0 + Math.sin(Math.toRadians(rectangleAngle)) * RECTANGLE_WIDTH,
                                    cannonBallType.radius);

            pane.getChildren().add(bullet);
            double startX = bullet.getCenterX();
            double startY = bullet.getCenterY();
            // compute coefficient of parabola
            double toDivide;
            toDivide = Math.abs(90 - rectangleAngle % 180);
            if (toDivide == 0) {
                toDivide = 5;
            }
            double coefficient = cannonBallType.curve * Math.abs(0.1 / toDivide);
            double savedAngle = rectangleAngle;
            var timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (now % FRAME_SPEED == 0) {
                        if (savedAngle < 270 && savedAngle >= 90) {
                            bullet.setCenterX(bullet.getCenterX() - STEP);
                        } else {
                            bullet.setCenterX(bullet.getCenterX() + STEP);
                        }
                        int length = cannonBallType.length;
                        double x = Math.abs(bullet.getCenterX() - startX) - length;
                        bullet.setCenterY((coefficient * x * x) + startY - length * length * coefficient);
                    }
                    if (intersectTarget(new Point2D(bullet.getCenterX(), bullet.getCenterY()))) {
                        Timeline timeline = blowTimeline(bullet);
                        timeline.setOnFinished(event -> {
                                                   pane.getChildren().remove(bullet);
                                                   Text text = new Text(200, 100, "You win!");
                                                   text.setFont(Font.font(30));
                                                   pane.getChildren().remove(instruction);
                                                   pane.getChildren().add(text);
                                                   stage.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
                                               }
                        );
                        timeline.play();
                        this.stop();
                    }
                    if (intersectMountains(new Point2D(bullet.getCenterX(), bullet.getCenterY()))) {
                        Timeline timeline = blowTimeline(bullet);
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

        // setup a TimeLine for blow animation
        @NotNull
        private Timeline blowTimeline(@NotNull Circle bullet) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                                                          new KeyValue(bullet.radiusProperty(), bullet.getRadius() * 3)));
            timeline.setCycleCount(1);
            timeline.setAutoReverse(false);
            return timeline;
        }

        private boolean intersectMountains(@NotNull Point2D point) {
            return pointInsideMountain(point, mountain1) ||
                    pointInsideMountain(point, mountain2) ||
                    pointInsideMountain(point, mountain3);

        }

        private boolean intersectTarget(@NotNull Point2D point) {
            double space = cannonBallType.space;
            return distance(point, new Point2D(target.getCenterX(), target.getCenterY())) <= space;
        }

        private double distance(@NotNull Point2D point1, @NotNull Point2D point2) {
            return Math.sqrt((point1.getX() - point2.getX()) * (point1.getX() - point2.getX()) +
                                     (point1.getY() - point2.getY()) * (point1.getY() - point2.getY()));
        }


        private double sign(@NotNull Point2D point1, @NotNull Point2D point2, @NotNull Point2D point3) {
            return (point1.getX() - point3.getX()) * (point2.getY() - point3.getY()) - (point2.getX() - point3.getX()) * (point1.getY() - point3.getY());
        }

        private boolean pointInsideMountain(Point2D point, Polygon mountain) {
            return pointInsideTriangle(point, new Point2D(mountain.getPoints().get(0), mountain.getPoints().get(1)),
                                       new Point2D(mountain.getPoints().get(2), mountain.getPoints().get(3)),
                                       new Point2D(mountain.getPoints().get(4), mountain.getPoints().get(5)));
        }

        private boolean pointInsideTriangle(Point2D point, Point2D vertex1, Point2D vertex2, Point2D vertex3) {
            double sign1 = sign(point, vertex1, vertex2);
            double sign2 = sign(point, vertex2, vertex3);
            double sign3 = sign(point, vertex3, vertex1);

            boolean hasNegative = (sign1 < 0) || (sign2 < 0) || (sign3 < 0);
            boolean hasPositive = (sign1 > 0) || (sign2 > 0) || (sign3 > 0);

            return !(hasNegative && hasPositive);
        }
    }
}
