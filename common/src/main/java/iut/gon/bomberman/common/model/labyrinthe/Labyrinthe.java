package iut.gon.bomberman.common.model.labyrinthe;

import java.io.Serializable;
import java.io.TimeUnit;

public class Labyrinthe implements Serializable {
    private final int width;
    private final int height;
    private final CellType[][] grid;
    private HeatMap heatMap;

    public Labyrinthe(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new CellType[width][height];
        this.heatMap = new HeatMap(width, height);


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = CellType.WALL;
            }
        }
    }

    public void updateCellCross(int x, int y, int range, CellType type){
        for(int i = x - 1; i < range; i++){
            for (int j = y -1; j < player.getRadius(); j++){
                this.setCell(i,j,type);
                this.heatMap.updateMap(x,y,1);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public CellType getCell(int x, int y) {
        return isInside(x, y) ? grid[x][y] : CellType.WALL;
    }

    public void setBomb(int x, int y, Joueur player, int time){
        this.setCell(x,y,Celltype.BOMB);
        player.setNb_Bombes(player.getNb_bombes() - 1);
        for(int i = 0; i < 3; i++){
            TimeUnit.SECONDS.sleep(time / 3);
            UpdateCellCross(x,y,player.getRadius(),CellType.EXPLOSION);
        }
        TimeUnit.SECONDS.sleep(1);
        for(int i = x - 1; i < player.getRadius(); i++){
            for (int j = y -1; j < player.getRadius(); j++){
                this.setCell(i,j,Celltype.EMPTY);
                this.heatMap.resetRisk(i,j);
            }
        }
    }

    public void setCell(int x, int y, CellType type) {
        if (isInside(x, y)) {
            grid[x][y] = type;
        }
    }

    public HeatMap getHeatMap() {
        return heatMap;
    }

    public int getRisk(int x, int y){
        return this.heatMap.getRisk(x,y);
    }

    public boolean isWalkable(int x, int y) {
        return isInside(x, y) && getCell(x, y) == CellType.EMPTY;
    }
}
