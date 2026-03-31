package iut.gon.bomberman.client.ai;

public class HeatMap{
    int[][] map;
    int x;
    int y;

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
        }
    }
}