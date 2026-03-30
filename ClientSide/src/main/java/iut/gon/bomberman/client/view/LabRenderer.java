package iut.gon.bomberman.client.view;

import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class LabRenderer {
    private static final int TILE_SIZE = 32;

    private final Image wallImg = new Image(getClass().getResourceAsStream("/iut/gon/bomberman/client/assets/block_04.png"));
    private final Image destructibleImg = new Image(getClass().getResourceAsStream("/iut/gon/bomberman/client/assets/block_06.png"));
    private final Image groundImg = new Image(getClass().getResourceAsStream("/iut/gon/bomberman/client/assets/ground_01.png"));

    public void draw(GraphicsContext gc, Labyrinthe lab) {
        for (int x = 0; x < lab.getWidth(); x++) {
            for (int y = 0; y < lab.getHeight(); y++) {
                CellType type = lab.getCell(x,y);

                gc.drawImage(groundImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                if (type == CellType.WALL) {
                    gc.drawImage(wallImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (type == CellType.DESTRUCTIBLE) {
                    gc.drawImage(destructibleImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
}
