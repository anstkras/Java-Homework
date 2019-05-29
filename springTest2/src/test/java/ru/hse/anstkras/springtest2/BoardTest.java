package ru.hse.anstkras.springtest2;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void testSmallBoard() {
        var board = new Board(2);
        ru.hse.anstkras.springtest2.Board.MoveState moveState = board.makeMove(0,0,0,0);
        if (moveState != Board.MoveState.SUCCESS) {
            moveState = board.makeMove(0, 0, 0, 1);
        }
        if (moveState != Board.MoveState.SUCCESS) {
            moveState = board.makeMove(0, 0, 1 , 0);
        }
        if (moveState != Board.MoveState.SUCCESS) {
            moveState = board.makeMove(0,0,1,1);
        }
        assertSame(moveState, Board.MoveState.SUCCESS);
        assertFalse(board.isActive(0,0));
    }

    @Test
    void testAllNumbersPresents() {
        var board = new Board(4);
        int []numbers = new int[8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                numbers[board.getCell(i, j)]++;
            }
        }
        for (int i = 0; i < 8; i++) {
            assertEquals(2, numbers[i]);
        }
    }
}