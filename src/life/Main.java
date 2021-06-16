package life;

import java.io.IOException;
import java.util.Random;

// Generation interface contains the main operations related to Game of Life.
interface Generation {

    static char[][] generator(char[][] grid, int length) {
        int numOfNeighbors;
        char[][] nextState = new char[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                numOfNeighbors = countNeighbors(grid, length, i, j);
                // If cell is dead
                if (grid[i][j] == ' ') {
                    // If cell has 3 neighbors, resurrect the cell to life.
                    if (numOfNeighbors == 3) {
                        nextState[i][j] = 'O';
                    } else {
                        nextState[i][j] = grid[i][j];
                    }
                } else {
                    // If cell has less than 2 neighbor or greater than 3,
                    // kill the cell.
                    if (numOfNeighbors < 2 || numOfNeighbors > 3) {
                        nextState[i][j] = ' ';
                    } else {
                        nextState[i][j] = grid[i][j];
                    }
                }
            }
        }
        return nextState;
    }

    // Count Neighbors across the cell of a Matrix.
    private static int countNeighbors(char[][] grid, int size, int row, int col) {
        int countNeighbor = 0;
        // isCol - keeps track either row/column is to be changed for traversing
        // neighbor.
        // isInc - keeps tracks when to increment or decrement the row/col to traverse
        // neighbor.
        boolean isCol = true, isInc = true;
        int[] index;
        for (int k = 0; k < 8; k++) {
            if (isInc) {
                if (isCol) {
                    index = getIndexes(size, row, ++col);
                    // System.out.println("Row: " + index[0] + " Col: " + index[1] + "First");
                    if (grid[index[0]][index[1]] == 'O') {
                        countNeighbor++;
                    }
                    if (k == 0) {
                        isCol = false;
                    }
                } else {
                    index = getIndexes(size, ++row, col);
                    // System.out.println("Row: " + index[0] + " Col: " + index[1] + "Second");
                    if (grid[index[0]][index[1]] == 'O') {
                        countNeighbor++;
                    }
                    isCol = true;
                    isInc = false;
                }
            } else {
                if (isCol) {
                    index = getIndexes(size, row, --col);
                    // System.out.println("Row: " + index[0] + " Col: " + index[1] + "Third");
                    if (grid[index[0]][index[1]] == 'O') {
                        countNeighbor++;
                    }
                    if (k == 3)
                        isCol = false;
                } else {
                    index = getIndexes(size, --row, col);
                    // System.out.println("Row: " + index[0] + " Col: " + index[1] + "Fourth");
                    if (grid[index[0]][index[1]] == 'O') {
                        countNeighbor++;
                    }
                    if (k == 5) {
                        isCol = true;
                        isInc = true;
                    }
                }
            }
        }
        return countNeighbor;
    }

    // Sends the indexes for the neighbors after checking for negative index
    // or greater than size of matrix.
    private static int[] getIndexes(int size, int i, int j) {
        int[] index = new int[2];
        if (i < 0 || j < 0) {
            i = i < 0 ? size + i : i;
            j = j < 0 ? size + j : j;
        }
        if (i >= size || j >= size) {
            i = i >= size ? size - i : i;
            j = j >= size ? size - j : j;
        }
        // i = i < 0 ? size + i : i == size ? 0 : i;
        // j = j < 0 ? size + j : j == size ? 0 : i;
        index[0] = i;
        index[1] = j;
        return index;
    }

}

class GameOfLifeSetup implements Generation {
    private char[][] grid;
    final private int length; // Number of rows and columns in grid
    final private Random rand;
    final private int cellSize; // Width and Height of each cell in GUI

    // Change Grid Size or Change cell size here
    // For grid -> length
    // For cell size -> cellSize
    GameOfLifeSetup() {
        this.length = 30;
        if (this.length == 0) {
            try {
                throw new Exception("Length of Matrix is 0");
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
        this.cellSize = 20;
        this.grid = new char[this.length][this.length];
        this.rand = new Random();
        setGrid();
    }

    // Randomly initialising the grid for the 0th generation.
    private void setGrid() {
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.length; j++) { 
                if (this.rand.nextBoolean()) {
                    this.grid[i][j] = 'O';
                } else {
                    this.grid[i][j] = ' ';
                }
            }
        }
    }

    public void displayGrid() {
        for (char[] row: this.grid) {
            for (char col: row) {
                System.out.print(col);
            }
            System.out.println();
        }
    }

    // Updating grid for next generation
    public void generateGenerations(char[][] updatedGrid) {
        this.grid = updatedGrid;
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        }
        catch (IOException | InterruptedException ignored) {}
        this.grid = Generation.generator(this.grid, this.length);
    }

    // Count of number of alive cells in the matrix
    public int getAlive() {
        int count = 0;
        for (char[] row : this.grid) {
            for (char col : row) {
                if (col == 'O') {
                    count++;
                }
            }
        }
        return count;
    }

    public char[][] getGrid() {
        return this.grid;
    }
    
    public int getLength() {
        return this.length;
    }

    public int getCellSize() {
        return this.cellSize;
    }
}

public class Main {
    public static void main(String[] args) {
        // Displaying JFrame by setting the class to true.
        new GameOfLife().setVisible(true);
    }
}
