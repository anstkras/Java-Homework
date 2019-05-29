package ru.hse.anstkras.springtest2;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

/** Represents internal state of game board */
public class Board {
    private final int boardSize;
    private int[][] board;
    private int count;
    private boolean isWin;

    /**
     * Creates board with the given boardsize
     *
     * @throws IllegalArgumentException if boardsize is odd
     */
    public Board(int boardSize) {
        if (boardSize % 2 != 0) {
            throw new IllegalArgumentException("board size should be not be odd");
        }
        this.boardSize = boardSize;
        count = boardSize * boardSize / 2;
        board = new int[boardSize][boardSize];
        var random = new Random();
        ArrayList<Cell> list = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                list.add(new Cell(i, j));
            }
        }
        for (int i = 0; i < boardSize * boardSize / 2; i++) {
            int first = random.nextInt(list.size());
            Cell firstCell = list.remove(first);
            int second = random.nextInt(list.size());
            Cell secondCell = list.remove(second);
            board[firstCell.i][firstCell.j] = i;
            board[secondCell.i][secondCell.j] = i;
        }
    }

    /**
     * Gets the cell with given indices
     *
     * @throws IndexOutOfBoundsException in case of i and j are out of bound
     * */
    public int getCell(int i, int j) {
        checkIndices(i, j);
        return board[i][j];
    }

    /**
     * Makes move with 2 cells with given coordinates
     * @return how the state change
     */
    @NotNull
    public MoveState makeMove(int firsti, int firstj, int secondi, int secondj) {
        if (isWin) {
            throw new IllegalStateException("Game is already over");
        }
        if (board[firsti][firstj] == -1 ||  board[secondi][secondj] == -1) {
            throw new IllegalStateException("Trying to make a move with inactive cells");
        }
        if (board[firsti][firstj] == board[secondi][secondj]) {
            board[firsti][firstj] = -1;
            board[secondi][secondj] = -1;
            count--;
            if (count == 0) {
                isWin = true;
                return MoveState.WIN;
            } else {
                return MoveState.SUCCESS;
            }
        }
        return MoveState.FAIL;
    }
    /** Checks if the cell is active */
    public boolean isActive(int i, int j) {
        checkIndices(i, j);
        return board[i][j] != -1;
    }

    private void checkIndices(int i, int j) {
        if (i < 0 || j < 0 || i >= boardSize || j >= boardSize) {
            throw new IndexOutOfBoundsException("indices are out of bound");
        }
    }

    private static class Cell {
        private int i;
        private int j;

        private Cell(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    public enum MoveState {
        WIN,
        SUCCESS,
        FAIL
    }
}
