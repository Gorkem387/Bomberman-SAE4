package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.view.LabRenderer;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Direction;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

public class GameController {

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;
    private Joueur joueur;
    private BombManager bombManager;
    private AnimationTimer gameLoop;
    private boolean isGameOver = false;
    
    private Image heartImage;
    private Image bombImage;

    private final Set<KeyCode> input = new HashSet<>();
    private long lastNanoTime = -1;
    private boolean spaceWasPressed = false;

    @FXML
    public void initialize() {
        // Charge l'image du cœur
        try {
            String resourcePath = Objects.requireNonNull(
                getClass().getResource("/iut/gon/bomberman/client/assets/heart.png")
            ).toExternalForm();
            heartImage = new Image(resourcePath, 30, 30, true, true);
        } catch (NullPointerException e) {
            System.err.println("Impossible de charger l'image du cœur : " + e.getMessage());
        }
        
        // Charge l'image de la bombe
        try {
            String resourcePath = Objects.requireNonNull(
                getClass().getResource("/iut/gon/bomberman/client/assets/B_0.png")
            ).toExternalForm();
            bombImage = new Image(resourcePath, 30, 30, true, true);
        } catch (NullPointerException e) {
            System.err.println("Impossible de charger l'image de la bombe : " + e.getMessage());
        }
        
        // ...existing code...
        DFSGenerator generator = new DFSGenerator();
        this.labyrinthe = generator.createLabyrinthe(21, 21);

        this.gc = gameCanvas.getGraphicsContext2D();
        this.bombManager = new BombManager();

        this.joueur = new Joueur(1, "Gorke");
        this.joueur.setX(1);
        this.joueur.setY(1);

        gameCanvas.setWidth(labyrinthe.getWidth() * 32);
        gameCanvas.setHeight(labyrinthe.getHeight() * 32);
        gameCanvas.setFocusTraversable(true);

        gameCanvas.setOnKeyPressed(e -> input.add(e.getCode()));
        gameCanvas.setOnKeyReleased(e -> {
            input.remove(e.getCode());
            if (e.getCode() == KeyCode.SPACE) spaceWasPressed = false;
        });

        this.gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanoTime < 0) {
                    lastNanoTime = now;
                    return;
                }
                double deltaTime = (now - lastNanoTime) / 1_000_000_000.0;
                lastNanoTime = now;

                update(deltaTime);
                render();
            }
        };
        gameLoop.start();
    }

    private void handleInputs() {
        double dx = 0;
        double dy = 0;

        // Détection des directions
        if (input.contains(KeyCode.Z) || input.contains(KeyCode.UP)) {
            dy--;
            joueur.setDirection(Direction.UP);
        } else if (input.contains(KeyCode.S) || input.contains(KeyCode.DOWN)) {
            dy++;
            joueur.setDirection(Direction.DOWN);
        } else if (input.contains(KeyCode.Q) || input.contains(KeyCode.LEFT)) {
            dx--;
            joueur.setDirection(Direction.LEFT);
        } else if (input.contains(KeyCode.D) || input.contains(KeyCode.RIGHT)) {
            dx++;
            joueur.setDirection(Direction.RIGHT);
        } else {
            // Aucun mouvement
            joueur.setDirection(Direction.IDLE);
        }
        if (dx != 0 || dy != 0) {
            joueur.move(dx, dy, labyrinthe, bombManager);
        }
        // Pose de bombe (Verrouillage par spaceWasPressed pour éviter le spam)
        if (input.contains(KeyCode.SPACE) && !spaceWasPressed) {
            spaceWasPressed = true;
            bombManager.placeBomb(joueur, 3, labyrinthe);
        }
    }

    private void update(double deltaTime) {
        if (joueur.isAlive()) {
            handleInputs();

            if (joueur.getPv() <= 0) {
                joueur.setAlive(false);
                this.isGameOver = true;
            }
        }
        // Mise à jour de la physique (bombes, explosions, dégâts)
        bombManager.update(deltaTime, labyrinthe, List.of(joueur));
    }

    private void render() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        renderer.draw(gc, labyrinthe);
        renderer.drawBombs(gc, bombManager.getBombs());
        renderer.drawExplosions(gc, bombManager.getExplosionCells());
        if (joueur.isAlive()) {
            renderer.drawPlayer(gc, joueur);
        }
        
        // Dessine la barre de statistiques en bas
        drawStatsBar(gc, joueur.getNb_bombes(), joueur.getPv());
        
        if (isGameOver) {
            drawGameOverScreen();
        }
    }

    private void drawGameOverScreen() {
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(javafx.scene.paint.Color.RED);
        gc.setFont(javafx.scene.text.Font.font("Arial", 50));
        gc.fillText("GAME OVER", gameCanvas.getWidth()/2 - 140, gameCanvas.getHeight()/2);
    }
    
    /**
     * Affiche une barre bleu foncé arrondie en bas avec les bombes et les cœurs centrés
     * @param gc GraphicsContext pour dessiner
     * @param bombs Nombre de bombes disponibles
     * @param hearts Nombre de cœurs disponibles
     */
    private void drawStatsBar(GraphicsContext gc, int bombs, int hearts) {
        double barHeight = 35;
        double barY = gameCanvas.getHeight() - barHeight - 5; // Fixée au bas
        
        // Largeur compacte
        double barWidth = 120;
        double barX = (gameCanvas.getWidth() - barWidth) / 2.0; // Centré horizontalement
        
        // Fond bleu foncé avec coins arrondis
        gc.setFill(javafx.scene.paint.Color.rgb(25, 50, 100)); // Bleu foncé
        gc.fillRoundRect(barX, barY, barWidth, barHeight, 15, 15);
        
        // Bordure avec coins arrondis
        gc.setStroke(javafx.scene.paint.Color.rgb(50, 100, 200)); // Bleu plus clair
        gc.setLineWidth(2);
        gc.strokeRoundRect(barX, barY, barWidth, barHeight, 15, 15);
        
        // Positionnement des éléments centrés verticalement
        double elementY = barY + (barHeight - 25) / 2;
        
        // Affiche les bombes (à gauche de la barre)
        double bombX = barX + 8;
        drawStatItem(gc, bombImage, bombs, bombX, elementY, "x");
        
        // Affiche les cœurs (à droite de la barre)
        double heartX = barX + 62;
        drawStatItem(gc, heartImage, hearts, heartX, elementY, "x");
    }
    
    /**
     * Affiche un élément de statistique (image + compteur)
     * @param gc GraphicsContext
     * @param image Image à afficher
     * @param count Nombre à afficher
     * @param x Position X
     * @param y Position Y
     * @param separator Séparateur (par exemple "x")
     */
    private void drawStatItem(GraphicsContext gc, Image image, int count, double x, double y, String separator) {
        if (image == null) return;
        
        // Affiche l'image (réduite à 25x25)
        gc.drawImage(image, x, y, 25, 25);
        
        // Affiche le séparateur et le compteur
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText(separator + " " + count, x + 30, y + 18);
    }
    
    /**
     * Affiche les cœurs et les bombes côte à côte au centre du canvas
     * @param gc GraphicsContext pour dessiner
     * @param hearts Nombre de cœurs à afficher
     */
    private void drawHearts(GraphicsContext gc, int hearts) {
        if (heartImage == null) return;
        
        // Calcule le centre du canvas
        double centerX = gameCanvas.getWidth() / 2.0;
        double centerY = 15;
        
        // Calcule la largeur totale des cœurs
        int heartsWidth = hearts * 35;
        
        // Position de départ des cœurs (alignés à droite du centre)
        double xStart = centerX - heartsWidth - 20; // -20 pour l'espace entre cœurs et bombes
        
        for (int i = 0; i < hearts; i++) {
            gc.drawImage(heartImage, xStart + (i * 35), centerY);
        }
    }
    
    /**
     * Affiche les bombes disponibles du joueur avec un style amélioré
     * @param gc GraphicsContext pour dessiner
     * @param bombs Nombre de bombes à afficher
     */
    private void drawBombs(GraphicsContext gc, int bombs) {
        if (bombImage == null) return;
        
        // Calcule le centre du canvas
        double centerX = gameCanvas.getWidth() / 2.0;
        double centerY = 15;
        
        // Position de départ des bombes (alignées à gauche du centre)
        double xStart = centerX + 20; // +20 pour l'espace entre cœurs et bombes
        
        for (int i = 0; i < bombs; i++) {
            gc.drawImage(bombImage, xStart + (i * 35), centerY);
        }
    }
    
    /**
     * Dessine un fond semi-transparent avec bordure pour les statistiques
     * @param gc GraphicsContext pour dessiner
     * @param hearts Nombre de cœurs
     * @param bombs Nombre de bombes
     */
    private void drawStatsBackground(GraphicsContext gc, int hearts, int bombs) {
        double centerX = gameCanvas.getWidth() / 2.0;
        
        // Calcule les dimensions du conteneur
        int heartsWidth = hearts * 35;
        int bombsWidth = bombs * 35;
        int totalWidth = heartsWidth + bombsWidth + 60; // +60 pour l'espace entre et les padding
        int height = 60;
        
        double bgX = centerX - (totalWidth / 2.0);
        double bgY = 5;
        
        // Fond semi-transparent
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.3));
        gc.fillRoundRect(bgX, bgY, totalWidth, height, 10, 10);
        
        // Bordure
        gc.setStroke(javafx.scene.paint.Color.rgb(255, 255, 255, 0.6));
        gc.setLineWidth(2);
        gc.strokeRoundRect(bgX, bgY, totalWidth, height, 10, 10);
    }
}