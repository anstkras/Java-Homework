package ru.hse.anstkras.springtest2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void testSmallBoard() {
        var board = new Board(2);
        ru.hse.anstkras.springtest2.Board.MoveState moveState = board.makeMove(0, 0, 0, 0);
        if (moveState != Board.MoveState.SUCCESS) {
            moveState = board.makeMove(0, 0, 0, 1);
        }
        if (moveState != Board.MoveState.SUCCESS) {
            moveState = board.makeMove(0, 0, 1, 0);
        }
        if (moveState != Board.MoveState.SUCCESS) {
            moveState = board.makeMove(0, 0, 1, 1);
        }
        assertSame(moveState, Board.MoveState.SUCCESS);
        assertFalse(board.isActive(0, 0));
    }

    @Test
    void testAllNumbersPresents() {
        var board = new Board(4);
        int[] numbers = new int[8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                numbers[board.getCell(i, j)]++;
            }
        }
        for (int i = 0; i < 8; i++) {
            assertEquals(2, numbers[i]);
        }
    }

    @Test
    void testWinSmallBoard() {
        var board = new Board(2);
        int i1 = -1;
        int j1 = -1;
        int j2 = -1;
        int i2 = -1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (board.getCell(i, j) == 0) {
                    if (i1 == -1) {
                        i1 = i;
                        j1 = j;
                    } else {
                        i2 = i;
                        j2 = j;
                    }
                }
            }
        }
        Board.MoveState moveState = board.makeMove(i1, j1,i2, j2);
        assertEquals(Board.MoveState.SUCCESS, moveState);
        i1 = -1;
        j1 = -1;
        j2 = -1;
        i2 = -1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (board.getCell(i, j) == 1) {
                    if (i1 == -1) {
                        i1 = i;
                        j1 = j;
                    } else {
                        i2 = i;
                        j2 = j;
                    }
                }
            }
        }
        moveState = board.makeMove(i1, j1, i2, j2);
        assertEquals(Board.MoveState.WIN, moveState);
    }
}