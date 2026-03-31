package iut.gon.bomberman.client.view;

import iut.gon.bomberman.common.model.labyrinthe.Bomb;
import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Direction;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;

public class LabRenderer {

    private static final int TILE_SIZE = 32;
    private static final int ANIMATION_SPEED = 10;

    private final Image wallImg         = load("/iut/gon/bomberman/client/assets/block_04.png");
    private final Image destructibleImg = load("/iut/gon/bomberman/client/assets/block_06.png");
    private final Image groundImg       = load("/iut/gon/bomberman/client/assets/ground_01.png");
    private final Image bombImg         = load("/iut/gon/bomberman/client/assets/B_0.png");
    private final Image explosionImg    = load("/iut/gon/bomberman/client/assets/explosion_0_4.png");

    private final Map<String, Image[]> spriteCache = new HashMap<>();
    private int animationCounter = 0;

    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
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
                else if (type == CellType.SPEED_BONUS) {
                    // Un petit socle jaune
                    gc.setFill(javafx.scene.paint.Color.YELLOW);
                    gc.fillOval(x * TILE_SIZE + 4, y * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 18));
                    gc.fillText("S", x * TILE_SIZE + 10, y * TILE_SIZE + 22);
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
        if (joueur == null || !joueur.isAlive()) return;

        String dirSuffix = updateDirection(joueur.getDirection());
        boolean isIdle = joueur.getDirection() == Direction.IDLE;

        if (!isIdle) animationCounter++;
        else animationCounter = 0;

        int frameIndex = isIdle ? 0 : (animationCounter / ANIMATION_SPEED) % 3;

        if (!spriteCache.containsKey(dirSuffix)) loadSpritesIntoCache(dirSuffix);
        Image currentSprite = spriteCache.get(dirSuffix)[frameIndex];
        double visualOffsetY = -0.25;

        gc.drawImage(currentSprite,
                joueur.getX() * TILE_SIZE,
                (joueur.getY() + visualOffsetY) * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);
        gc.setStroke(javafx.scene.paint.Color.RED);
        double hSize = 0.7;
        double off = 0.15;
        // Hitbox
        gc.strokeRect((joueur.getX() + off) * TILE_SIZE,
                (joueur.getY() + off) * TILE_SIZE,
                hSize * TILE_SIZE, hSize * TILE_SIZE);
    }

    private void loadSpritesIntoCache(String direction) {
        Image[] frames = new Image[3];
        for (int i = 0; i < 3; i++) {
            String path = "/iut/gon/bomberman/client/assets/8/" + direction + "_" + i + ".png";
            frames[i] = load(path);
        }
        spriteCache.put(direction, frames);
    }

    public String updateDirection(Direction direction) {
        return switch (direction) {
            case UP -> "N";
            case DOWN -> "S";
            case LEFT -> "W";
            case RIGHT -> "E";
            case IDLE -> "S";
        };
    }

    public void stopAnimation() {
        animationCounter = 0;
    }
}