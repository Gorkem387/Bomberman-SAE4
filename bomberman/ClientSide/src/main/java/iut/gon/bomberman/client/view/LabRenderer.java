package iut.gon.bomberman.client.view;

import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Direction;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LabRenderer {
    private static final int TILE_SIZE = 32;
    private static final int ANIMATION_SPEED = 10; // frames before changing sprite

    private final Image wallImg = new Image(getClass().getResourceAsStream("/iut/gon/bomberman/client/assets/block_04.png"));
    private final Image destructibleImg = new Image(getClass().getResourceAsStream("/iut/gon/bomberman/client/assets/block_06.png"));
    private final Image groundImg = new Image(getClass().getResourceAsStream("/iut/gon/bomberman/client/assets/ground_01.png"));

    // Cache des sprites pour chaque direction
    private final Map<String, Image[]> spriteCache = new HashMap<>();
    private int animationCounter = 0;

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

    public void drawPlayer(GraphicsContext gc, Joueur joueur) {
        if (joueur == null) {
            return;
        }
        
        // Déterminer la direction à partir de l'objet joueur
        String direction = updateDirection(joueur.getDirection());
        boolean isIdle = joueur.getDirection() == Direction.IDLE;
        
        // Incrémenter le compteur d'animation seulement si en mouvement
        if (!isIdle) {
            animationCounter++;
        } else {
            // Réinitialiser le compteur en mode IDLE
            animationCounter = 0;
        }
        
        // Calculer l'index du frame actuel (0, 1, 2)
        int frameIndex = isIdle ? 0 : (animationCounter / ANIMATION_SPEED) % 3;
        
        // Charger les sprites de cette direction si nécessaire
        if (!spriteCache.containsKey(direction)) {
            if(Objects.equals(direction, "R")) {
                Image[] sprite = new Image[3];
                for (int i = 0; i < 3; i++) {
                    sprite[i] = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                            "/iut/gon/bomberman/client/assets/8/R" + "_" + 0 + ".png"
                    )));
                }
                spriteCache.put(direction, sprite);
            } else {
                Image[] frames = new Image[3];
                for (int i = 0; i < 3; i++) {
                    frames[i] = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                            "/iut/gon/bomberman/client/assets/8/" + direction + "_" + i + ".png"
                    )));
                }
                spriteCache.put(direction, frames);
            }
        }
        
        // Obtenir l'image du frame actuel
        Image currentSprite = spriteCache.get(direction)[frameIndex];
        
        double screenX = joueur.getX() * TILE_SIZE;
        double screenY = joueur.getY() * TILE_SIZE;
        gc.drawImage(currentSprite, screenX, screenY, TILE_SIZE, TILE_SIZE);
    }

    public String updateDirection(Direction direction) {
        return switch (direction) {
            case UP -> "N";
            case DOWN -> "S";
            case LEFT -> "W";
            case RIGHT -> "E";
            case IDLE -> "R"; // Par défaut
        };
    }

    public void stopAnimation() {
        animationCounter = 0;
    }
}
