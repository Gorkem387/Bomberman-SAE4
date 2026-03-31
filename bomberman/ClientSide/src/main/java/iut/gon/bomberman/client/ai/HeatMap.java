package iut.gon.bomberman.client.ai;

public class HeatMap {
    private int[][] map;
    private int width;
    private int height;

    public void updateMap(int x, int y, int value) {
        this.map[x][y] += value;
    }

    public int readRisk(int x, int y) {
        return this.map[x][y];
    }

    public void resetRisk(int x, int y) {
        this.map[x][y] = 0;
    }

    public HeatMap(int x, int y) {
        this.width = x;
        this.height = y;
        this.map = new int[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.map[i][j] = 0;
            }
        }
    }
}