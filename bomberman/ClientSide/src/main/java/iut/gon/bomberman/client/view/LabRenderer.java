package iut.gon.bomberman.client.view;

import iut.gon.bomberman.common.model.labyrinthe.Bomb;
import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class LabRenderer {

    private static final int TILE_SIZE = 32;

    private final Image wallImg        = load("/iut/gon/bomberman/client/assets/block_04.png");
    private final Image destructibleImg = load("/iut/gon/bomberman/client/assets/block_06.png");
    private final Image groundImg      = load("/iut/gon/bomberman/client/assets/ground_01.png");
    private final Image playerSprite   = load("/iut/gon/bomberman/client/assets/D_0.png");

    private final Image bombImg      = load("/iut/gon/bomberman/client/assets/B_0.png");
    private final Image explosionImg = load("/iut/gon/bomberman/client/assets/explosion_0_4.png"); // Change le nom si besoin !

    private Image load(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    public void draw(GraphicsContext gc, Labyrinthe lab) {
        for (int x = 0; x < lab.getWidth(); x++) {
            for (int y = 0; y < lab.getHeight(); y++) {
                CellType type = lab.getCell(x, y);
                gc.drawImage(groundImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                if (type == CellType.WALL) {
                    gc.drawImage(wallImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (type == CellType.DESTRUCTIBLE) {
                    gc.drawImage(destructibleImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    public void drawBombs(GraphicsContext gc, List<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            double sx = bomb.getX() * TILE_SIZE;
            double sy = bomb.getY() * TILE_SIZE;
            gc.drawImage(bombImg, sx, sy, TILE_SIZE, TILE_SIZE);
        }
    }

    public void drawExplosions(GraphicsContext gc, List<int[]> cells) {
        for (int[] cell : cells) {
            gc.drawImage(explosionImg, cell[0] * TILE_SIZE, cell[1] * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    public void drawPlayer(GraphicsContext gc, Joueur joueur) {
        if (joueur == null) return;
        double screenX = joueur.getX() * TILE_SIZE;
        double screenY = joueur.getY() * TILE_SIZE;
        gc.drawImage(playerSprite, screenX, screenY, TILE_SIZE, TILE_SIZE);
    }
}