package iut.gon.bomberman.common.model.labyrinthe;

import java.io.Serializable;
import iut.gon.bomberman.common.model.player.Joueur;

public class Labyrinthe implements Serializable {
    private final int width;
    private final int height;
    private final CellType[][] grid;

    public Labyrinthe(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new CellType[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = CellType.WALL;
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public CellType getCell(int x, int y) {
        return isInside(x, y) ? grid[x][y] : CellType.WALL;
    }

    public void setCell(int x, int y, CellType type) {
        if (isInside(x, y)) {
            grid[x][y] = type;
        }
    }

    public boolean isWalkable(int x, int y) {
        if (!isInside(x, y)) return false;
        CellType type = getCell(x, y);
        return type == CellType.EMPTY || type == CellType.EXPLOSION || type == CellType.SPEED_BONUS || type == CellType.FIRE_BONUS;
    }
}