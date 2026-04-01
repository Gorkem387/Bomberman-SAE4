package iut.gon.bomberman.common.model.DTO;

import iut.gon.bomberman.common.model.labyrinthe.CellType;

public class MinimEDTO {

    private int id;
    private CellType cellType;
    private int x;
    private int y;

    public MinimEDTO(int id, CellType cellType, int x, int y) {
        this.id = id;
        this.cellType = cellType;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
