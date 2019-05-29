package ru.hse.anstkras.springtest2;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class FindPairBoard {
    private final int boardSize;
    private final Board board;
    private State state = State.NO_BUTTONS_OPEN;
    private long timeOpen;

    public FindPairBoard(Stage primaryStage, int boardSize) {
        this.boardSize = boardSize;
        board = new Board(boardSize);
        var pane = new GridPane();
        for (int i = 0; i < boardSize; i++) {
            var column = new ColumnConstraints();
            column.setPercentWidth(100.0 / boardSize);
            pane.getColumnConstraints().add(column);
        }

        for (int i = 0; i < boardSize; i++) {
            var row = new RowConstraints();
            row.setPercentHeight(100.0 / boardSize);
            row.setFillHeight(true);
            pane.getRowConstraints().add(row);
        }
        pane.setMinSize(250, 250);

        pane.setHgap(20);
        pane.setVgap(20);
        pane.setPadding(new Insets(10, 10, 10, 10));

        Button[][] buttons = new Button[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new Button();
                final int ii = i;
                final int jj = j;
                buttons[i][j].setMaxHeight(Double.MAX_VALUE);
                buttons[i][j].setMaxWidth(Double.MAX_VALUE);
                buttons[i][j].setOnAction(value -> {
                    if (state == State.NO_BUTTONS_OPEN) {
                        buttons[ii][jj].setText(String.valueOf(board.getCell(ii, jj)));
                        state = State.ONE_BUTTON_OPEN;
                        return;
                    }

                    if (state == State.ONE_BUTTON_OPEN) {
                        buttons[ii][jj].setText(String.valueOf(board.getCell(ii, jj)));
                        state = State.TWO_BUTTONS_OPEN;
                        timeOpen = System.currentTimeMillis();
                        return;
                    }
                    if (state == State.TWO_BUTTONS_OPEN) {
                        if (System.currentTimeMillis() - timeOpen > 3000) {
                            state = State.ONE_BUTTON_OPEN;
                            buttons[ii][jj].setText(String.valueOf(board.getCell(ii, jj)));
                        } else {
                            // TODO
                        }
                        return;
                    }
                });
                pane.add(buttons[i][j], i, j);
            }
        }


        Scene scene = new Scene(pane);
        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(200);
        primaryStage.show();
    }

    private enum State {
        NO_BUTTONS_OPEN,
        ONE_BUTTON_OPEN,
        TWO_BUTTONS_OPEN;
    }
}