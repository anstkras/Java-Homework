import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private final int boardSize;
    private int[][] board;

    public Board(int boardSize) {
        if (boardSize % 2 != 0) {
            //this.boardSize = boardSize;
            throw new IllegalArgumentException("board size should be not be odd");
        }
        this.boardSize = boardSize;
        Random random = new Random();
        ArrayList<Cell> list = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                list.add(new Cell(i, j));
            }
        }
        for (int i = 0; i < boardSize * boardSize / 2; i++) {
                int first = random.nextInt(list.size());
                Cell firstCell = list.get(first);
                list.remove(first);
                int second = random.nextInt(list.size());
                Cell secondCell = list.get(second);
                list.remove(second);
                board[firstCell.i][firstCell.j] = i;
                board[secondCell.i][secondCell.j] = i;
        }
    }

    public int getCell(int i, int j) {
        checkIndices(i, j);
        return board[i][j];
    }

    private void checkIndices(int i, int j){
        if (i < 0 || j < 0 || i >= boardSize || j >= boardSize) {
            throw new IndexOutOfBoundsException("indices are out of bound");
        }
    }

    private static class Cell{
        private int i;
        private int j;
        private Cell(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }
}
