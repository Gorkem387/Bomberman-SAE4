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

public class LabRenderer {

    private static final int TILE_SIZE       = 32;
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

    public LabRenderer() {
        updateAssets();
    }

    public void updateAssets() {
        String bombPath = GameSettings.getSelectedBombPath();
        this.bombImg = load(bombPath);
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
                switch (type) {
                    case WALL         -> gc.drawImage(wallImg,         x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    case DESTRUCTIBLE -> gc.drawImage(destructibleImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    case SPEED_BONUS  -> {
                        gc.setFill(javafx.scene.paint.Color.YELLOW);
                        gc.fillOval(x * TILE_SIZE + 4, y * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                        gc.setFill(javafx.scene.paint.Color.BLACK);
                        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 18));
                        gc.fillText("S", x * TILE_SIZE + 10, y * TILE_SIZE + 22);
                    }
                    case FIRE_BONUS -> {
                        gc.setFill(javafx.scene.paint.Color.RED);
                        gc.fillOval(x * TILE_SIZE + 4, y * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                        gc.setFill(javafx.scene.paint.Color.WHITE);
                        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 18));
                        gc.fillText("F", x * TILE_SIZE + 10, y * TILE_SIZE + 22);
                    }
                    default -> {}
                }
            }
        }
    }

    public void drawBombs(GraphicsContext gc, List<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            gc.drawImage(bombImg, bomb.getX() * TILE_SIZE, bomb.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
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

        if (!joueur.isAlive()) {
            drawDeathAnimation(gc, joueur, screenX, screenY);
            return;
        }

        String dirSuffix = updateDirection(joueur.getDirection());
        boolean isIdle   = joueur.getDirection() == Direction.IDLE;

        // Compteur propre à CE joueur
        int counter = animCounters.getOrDefault(joueur.getId(), 0);
        counter = isIdle ? 0 : counter + 1;
        animCounters.put(joueur.getId(), counter);

        int frameIndex = isIdle ? 0 : (counter / ANIMATION_SPEED) % 3;

        // Clé cache = id + direction, chaque joueur a ses propres sprites en cache
        String cacheKey = joueur.getId() + "_" + dirSuffix;
        if (!spriteCache.containsKey(cacheKey)) {
            loadSpritesIntoCache(cacheKey, dirSuffix, joueur);
        }

        Image currentSprite = spriteCache.get(cacheKey)[frameIndex];

        double visualOffsetY = -0.25;
        gc.drawImage(currentSprite,
                joueur.getX() * TILE_SIZE,
                (joueur.getY() + visualOffsetY) * TILE_SIZE,
                TILE_SIZE, TILE_SIZE);

        // Hitbox debug
        gc.setStroke(javafx.scene.paint.Color.RED);
        double hSize = 0.7, off = 0.15;
        gc.strokeRect((joueur.getX() + off) * TILE_SIZE,
                (joueur.getY() + off) * TILE_SIZE,
                hSize * TILE_SIZE, hSize * TILE_SIZE);
    }

    private void drawDeathAnimation(GraphicsContext gc, Joueur joueur, double screenX, double screenY) {
        int id = joueur.getId();

        if (!deathAnimPlaying.getOrDefault(id, false)) {
            deathAnimPlaying.put(id, true);
            deathCounters.put(id, 0);
        }

        int dc = deathCounters.getOrDefault(id, 0);
        if (dc < 30) {
            dc++;
            deathCounters.put(id, dc);
        }

        String cacheKey = id + "_D";
        if (!spriteCache.containsKey(cacheKey)) {
            loadSpritesIntoCache(cacheKey, "D", joueur);
        }

        Image[] deathSprites = spriteCache.get(cacheKey);
        int frameIndex = Math.min((dc / ANIMATION_SPEED), 2);
        gc.drawImage(deathSprites[frameIndex], screenX, screenY, TILE_SIZE, TILE_SIZE);
    }

    private void loadSpritesIntoCache(String cacheKey, String direction, Joueur joueur) {
        String fullPath    = joueur.getSkinPath();
        String baseFolder  = fullPath.substring(0, fullPath.lastIndexOf("/") + 1);

        boolean isSingleFrame = direction.equals("R") || direction.equals("D");
        int frameCount = isSingleFrame ? 1 : 3;

        Image[] frames = new Image[3];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = load(baseFolder + direction + "_" + i + ".png");
        }

        if (isSingleFrame) {
            frames[1] = frames[0];
            frames[2] = frames[0];
        }

        spriteCache.put(cacheKey, frames);
    }

    public String updateDirection(Direction direction) {
        return switch (direction) {
            case UP    -> "N";
            case DOWN  -> "S";
            case LEFT  -> "W";
            case RIGHT -> "E";
            case IDLE  -> "R";
        };
    }

    public void stopAnimation() {
        animCounters.clear();
    }
}