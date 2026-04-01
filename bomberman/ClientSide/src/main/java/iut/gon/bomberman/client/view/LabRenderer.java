package iut.gon.bomberman.client.view;

import iut.gon.bomberman.client.GameSettings;
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
    private final Image explosionImg    = load("/iut/gon/bomberman/client/assets/explosion_0_4.png");
    private Image bombImg;

    private final Map<String, Image[]> spriteCache = new HashMap<>();
    private int animationCounter = 0;
    private int deathAnimationCounter = 0;
    private boolean isDeathAnimationPlaying = false;

    public LabRenderer() {
        updateAssets();
    }

    /**
     * Recharge les images dynamiques (bombe et skin joueur)
     */
    public void updateAssets() {
        // Recharge la bombe depuis les paramètres globaux
        String bombPath = GameSettings.getSelectedBombPath();
        this.bombImg = load(bombPath);

        // Vide le cache pour forcer le rechargement du nouveau skin choisi
        spriteCache.clear();
        System.out.println("Renderer : Assets mis à jour (Bombe: " + bombPath + ")");
    }

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
                    gc.setFill(javafx.scene.paint.Color.YELLOW);
                    gc.fillOval(x * TILE_SIZE + 4, y * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 18));
                    gc.fillText("S", x * TILE_SIZE + 10, y * TILE_SIZE + 22);
                } else if (type == CellType.FIRE_BONUS) {
                    gc.setFill(javafx.scene.paint.Color.RED);
                    gc.fillOval(x * TILE_SIZE + 4, y * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                    gc.setFill(javafx.scene.paint.Color.WHITE);
                    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 18));
                    gc.fillText("F", x * TILE_SIZE + 10, y * TILE_SIZE + 22);
                }
            }
        }
    }

    /**
     * Dessine les bombes sur le canvas
     * @param gc le contexte graphique du canvas
     * @param bombs la liste des bombes à dessiner
     */
    public void drawBombs(GraphicsContext gc, List<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            double sx = bomb.getX() * TILE_SIZE;
            double sy = bomb.getY() * TILE_SIZE;
            gc.drawImage(bombImg, sx, sy, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Dessine les explosions sur le canvas
     * @param gc le contexte graphique du canvas
     * @param cells la liste des cellules d'explosion à dessiner
     */
    public void drawExplosions(GraphicsContext gc, List<int[]> cells) {
        for (int[] cell : cells) {
            gc.drawImage(explosionImg, cell[0] * TILE_SIZE, cell[1] * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Dessine un Joueur sur le canvas
     * @param gc le contexte graphique du canvas
     * @param joueur le joueur à dessiner
     */
    public void drawPlayer(GraphicsContext gc, Joueur joueur) {
        if (joueur == null) return;

        double screenX = joueur.getX() * TILE_SIZE;
        double screenY = joueur.getY() * TILE_SIZE;

        // Déterminer la direction et l'état
        String dirSuffix = updateDirection(joueur.getDirection());
        boolean isIdle = joueur.getDirection() == Direction.IDLE;

        // Gérer le compteur d'animation
        if (!isIdle) {
            animationCounter++;
        } else {
            animationCounter = 0;
        }

        double visualOffsetY = -0.25;

        // Calculer l'index du frame (0, 1, 2)
        int frameIndex = isIdle ? 0 : (animationCounter / ANIMATION_SPEED) % 3;

        // Charger/Récupérer les sprites depuis le cache
        if (!spriteCache.containsKey(dirSuffix)) {
            loadSpritesIntoCache(dirSuffix, joueur);
        }

        Image currentSprite = spriteCache.get(dirSuffix)[frameIndex];


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

    /**
     * Affiche l'animation de mort avec les sprites "D"
     * L'animation se joue une seule fois puis reste sur le dernier frame
     * @param gc le contexe graphique du canvas
     * @param joueur le joueur dont la mort va être animé
     * @param screenX position X sur l'écran
     * @param screenY position Y sur l'écran
     */
    private void drawDeathAnimation(GraphicsContext gc, Joueur joueur, double screenX, double screenY) {
        if (!isDeathAnimationPlaying) {
            isDeathAnimationPlaying = true;
            deathAnimationCounter = 0;
        }

        if (deathAnimationCounter < 30) {
            deathAnimationCounter++;
        }

        String deathDirection = "D";
        if (!spriteCache.containsKey(deathDirection)) {
            loadSpritesIntoCache(deathDirection, joueur);
        }

        Image[] deathSprites = spriteCache.get(deathDirection);

        int frameIndex = Math.min((deathAnimationCounter / ANIMATION_SPEED), 2);
        Image currentDeathSprite = deathSprites[frameIndex];

        gc.drawImage(currentDeathSprite, screenX, screenY, TILE_SIZE, TILE_SIZE);
    }

    private void loadSpritesIntoCache(String direction, Joueur joueur) {
        // On récupère le chemin du skin (ex: /.../assets/32/S_0.png)
        String fullPath = joueur.getSkinPath();

        // On extrait le dossier (on enlève "S_0.png" à la fin pour avoir le dossier "/.../assets/32/")
        String baseFolder = fullPath.substring(0, fullPath.lastIndexOf("/") + 1);

        // Directions avec un seul frame (pas d'animation)
        boolean isSingleFrame = direction.equals("R") || direction.equals("D");
        int frameCount = isSingleFrame ? 1 : 3;

        Image[] frames = new Image[3];

        for (int i = 0; i < frameCount; i++) {
            // On construit le chemin : dossier + direction (N, S, E, W) + index
            String path = baseFolder + direction + "_" + i + ".png";
            frames[i] = load(path);
        }

        // Si un seul frame, on le répète sur les 3 slots
        if (isSingleFrame) {
            frames[1] = frames[0];
            frames[2] = frames[0];
        }

        spriteCache.put(direction, frames);
    }

    public String updateDirection(Direction direction) {
        return switch (direction) {
            case UP -> "N";//N_0
            case DOWN -> "S";//S_0
            case LEFT -> "W";//W_0
            case RIGHT -> "E";//E_0
            case IDLE -> "R";//R_0
        };
    }

    public void stopAnimation() {
        animationCounter = 0;
    }
}