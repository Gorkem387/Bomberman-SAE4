package iut.gon.bomberman.client.ai;

public class HeatMap {
    private int[][] map;
    private int width;
    private int height;

    public HeatMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new int[width][height];
    }

    public void updateMap(int x, int y, int value) {
        if (isInside(x, y)) {
            this.map[x][y] += value;
        }
    }

    public int readRisk(int x, int y) {
        return isInside(x, y) ? this.map[x][y] : 999;
    }

    public void resetRisk(int x, int y) {
        if (isInside(x, y)) {
            this.map[x][y] = 0;
        }
    }

    private boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}