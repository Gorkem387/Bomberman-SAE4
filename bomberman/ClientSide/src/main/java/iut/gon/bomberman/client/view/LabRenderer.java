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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import javafx.scene.paint.Color;

public class LabRenderer {

    private static final int TILE_SIZE = 32;
    private static final int ANIMATION_SPEED = 10;

    private final Image wallImg         = load("/iut/gon/bomberman/client/assets/block_04.png");
    private final Image destructibleImg = load("/iut/gon/bomberman/client/assets/block_06.png");
    private final Image groundImg       = load("/iut/gon/bomberman/client/assets/ground_01.png");
    private final Image explosionImg    = load("/iut/gon/bomberman/client/assets/explosion_0_4.png");
    private Image bombImg;

    // Cache clé = "playerId_direction" pour éviter que deux joueurs s'écrasent
    private final Map<String, Image[]> spriteCache = new HashMap<>();

    // Un compteur d'animation PAR joueur (clé = id)
    private final Map<Integer, Integer> animCounters      = new HashMap<>();
    private final Map<Integer, Integer> deathCounters     = new HashMap<>();
    private final Map<Integer, Boolean> deathAnimPlaying  = new HashMap<>();

    private Image[] bombSprites;
    private int bombAnimationCounter = 0;
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

    /**
     * Fonction qui charge les sprites d'animation de la bombe avec détection dynamique du nombre
     * Détecte le préfixe (B, B2, etc.) et charge tous les sprites disponibles
     * @param bombPath chemin vers la bombe
     */
    private void loadBombSprites(String bombPath) {
        int lastSlash = bombPath.lastIndexOf("/");
        String assetsFolder = bombPath.substring(0, lastSlash + 1);

        String filename = bombPath.substring(lastSlash + 1);  // "B_0.png" ou "B2_0.png"
        String bombPrefix = filename.substring(0, filename.indexOf("_"));  // "B" ou "B2"

        System.out.println("Détection du préfixe de bombe: " + bombPrefix);

        String spriteFolder = assetsFolder + (bombPrefix.equals("B") ? "b" : "B2") + "/";

        int spriteCount = 0;
        for (int i = 0; i < 30; i++) {
            String testPath = spriteFolder + bombPrefix + "_" + i + ".png";
            try (var stream = getClass().getResourceAsStream(testPath)) {
                if (stream != null) {
                    spriteCount++;
                } else {
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }

        if (spriteCount == 0) {
            System.err.println("Aucun sprite de bombe trouvé avec le préfixe: " + bombPrefix);
            bombSprites = new Image[]{bombImg};
            return;
        }

        bombSprites = new Image[spriteCount];
        for (int i = 0; i < spriteCount; i++) {
            String spritePath = spriteFolder + bombPrefix + "_" + i + ".png";
            Image img = load(spritePath);

            bombSprites[i] = img;
        }
    }

    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    /**
     * Dessine le labyrinthe et les bonus sur le canvas
     * @param gc le contexte graphique du canvas
     * @param lab le labyrinthe à dessiner
     */
    public void draw(GraphicsContext gc, Labyrinthe lab) {
        for (int x = 0; x < lab.getWidth(); x++) {
            for (int y = 0; y < lab.getHeight(); y++) {
                CellType type = lab.getCell(x, y);
                gc.drawImage(groundImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (type == CellType.WALL)
                    gc.drawImage(wallImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                else if (type == CellType.DESTRUCTIBLE)
                    gc.drawImage(destructibleImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                else if (type == CellType.SPEED_BONUS)
                    drawBonus(gc, x, y, Color.YELLOW, Color.BLACK, "S");

                else if (type == CellType.FIRE_BONUS)
                    drawBonus(gc, x, y, Color.RED, Color.BLACK, "F");
            }
        }
    }

    /**
     * Fonction pour afficher le bonus
     * @param gc le contexte graphique du canvas
     * @param x coordonées x de la case
     * @param y coordonées y de la case
     * @param color1 première couleur
     * @param color2 deuxième couleur
     * @param text texte à afficher
     */
    public void drawBonus(GraphicsContext gc, int x, int y, Color color1, Color color2, String text){
        gc.setFill(color1);
        gc.fillOval(x * TILE_SIZE + 4, y * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8 );
        gc.setFill(color2);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 18));
        gc.fillText(text, x * TILE_SIZE + 10, y * TILE_SIZE + 22);
    }

    /**
     * Dessine les bombes avec animation de sprites
     * Supporte n'importe quel nombre de sprites
     * @param gc le contexte graphique du canvas
     * @param bombs la liste des bombes à dessiner
     */
    public void drawBombs(GraphicsContext gc, List<Bomb> bombs) {
        bombAnimationCounter++;

        int frameIndex = (bombAnimationCounter / ANIMATION_SPEED) % (bombSprites != null ? bombSprites.length : 1);

        Image bombImageToUse = (bombSprites != null && bombSprites[frameIndex] != null)
            ? bombSprites[frameIndex]
            : bombImg;

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

        if (!joueur.isAlive()) {
            drawDeathAnimation(gc, joueur, screenX, screenY);
            return;
        }

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

        String cacheKey = joueur.getId() + "_" + dirSuffix;

        // Charger/Récupérer les sprites depuis le cache
        if (!spriteCache.containsKey(cacheKey)) {
            loadSpritesIntoCache(cacheKey, dirSuffix, joueur);
        }

        Image currentSprite = spriteCache.get(cacheKey)[frameIndex];


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

        String cacheKey = joueur.getId() + "_D";
        if (!spriteCache.containsKey(cacheKey)) {
            loadSpritesIntoCache(cacheKey, "D",joueur);
        }

        Image[] deathSprites = spriteCache.get(cacheKey);

        int frameIndex = Math.min((deathAnimationCounter / ANIMATION_SPEED), 2);
        Image currentDeathSprite = deathSprites[frameIndex];

        gc.drawImage(currentDeathSprite, screenX, screenY, TILE_SIZE, TILE_SIZE);
    }

    private void loadSpritesIntoCache(String cacheKey, String direction, Joueur joueur) {
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
        spriteCache.put(cacheKey, frames);
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