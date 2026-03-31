package iut.gon.bomberman.client.ai;

public class HeatMap {
    private int[][] map;
    private int width;
    private int height;

<<<<<<< HEAD
    public void updateMap(int x, int y, int value){
        this.map[x][y] += value;
    }

    public int readRisk(int x, int y){
        return this.map[x][y];
    }

    public void resetRisk(int x, int y){
        this.map[x][y] = 0;
    }

    public HeatMap(int x, int y){
        this.x = x;
        this.y = y;
        this.map = new int[this.x][this.y];
        for(int i = 0; i < this.x; i++){
            for(int j = 0; j < this.y; j++){
                this.map[i][j] = 0;
            }
=======
    public HeatMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new int[width][height];
    }

    public void updateMap(int x, int y, int value) {
        if (isInside(x, y)) {
            this.map[x][y] += value;
>>>>>>> dev
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