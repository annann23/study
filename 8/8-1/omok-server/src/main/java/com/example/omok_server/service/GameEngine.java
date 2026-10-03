package com.example.omok_server.service;

public class GameEngine {
    public static final int BOARD_SIZE = 15;
    private final int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private int currentTurn = 1;
    private boolean finished = false;
    private final int[][] directions = { {0, 1}, {1, 0}, {1, 1}, {-1, 1} };

    private boolean isInBoard(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    //가로세로 대각선 방향 체크
    private boolean isWon(int x, int y, int stone) {
        for(int[] d : directions){
            if (countStones(x, y, d[0], d[1], stone) >= 5) return true;
        }

        return false;
    }

    private int countStones(int x, int y, int dx, int dy, int stone){
        int count = 1;
        int currentX = x + dx;
        int currentY = y + dy;

        while(isInBoard(currentX, currentY) && board[currentX][currentY] == stone) {
            count++;
            currentX += dx;
            currentY += dy;
        }

        currentX = x - dx;
        currentY = y - dy;

        while(isInBoard(currentX, currentY) && board[currentX][currentY] == stone) {
            count++;
            currentX -= dx;
            currentY -= dy;
        }

        return count;
    }

    public boolean canPlace(int x, int y, int stone) {
        return !finished
                && stone == currentTurn
                && isInBoard(x, y)
                && board[x][y] == 0;
    }

    public boolean placeStone(int x, int y, int stone) {
        board[x][y] = stone;
        if (isWon(x, y, stone)) {
            finished = true;
            return true;
        }
        currentTurn = (stone == 1) ? 2 : 1;
        return false;
    }
}

