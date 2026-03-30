package iut.gon.bomberman.common.model.entity;

import java.io.Serializable;

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
