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
            bombManager.placeBomb(joueur, 3);
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
        
        // Dessine les cœurs sur le canvas
        drawHearts(gc, joueur.getPv());
        
        // Dessine les bombes sur le canvas
        drawBombs(gc, joueur.getNb_bombes());
        
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
     * Affiche les cœurs et les bombes côte à côte au centre du canvas
     * @param gc GraphicsContext pour dessiner
     * @param hearts Nombre de cœurs à afficher
     */
    private void drawHearts(GraphicsContext gc, int hearts) {
        if (heartImage == null) return;
        
        // Calcule le centre du canvas
        double centerX = gameCanvas.getWidth() / 2.0;
        double centerY = 0;
        
        // Calcule la largeur totale des cœurs
        int heartsWidth = hearts * 35;
        
        // Position de départ des cœurs (alignés à droite du centre)
        double xStart = centerX - heartsWidth - 10; // -10 pour l'espace entre cœurs et bombes
        
        for (int i = 0; i < hearts; i++) {
            gc.drawImage(heartImage, xStart + (i * 35), centerY);
        }
    }
    
    /**
     * Affiche les bombes disponibles du joueur
     * @param gc GraphicsContext pour dessiner
     * @param bombs Nombre de bombes à afficher
     */
    private void drawBombs(GraphicsContext gc, int bombs) {
        if (bombImage == null) return;
        
        // Calcule le centre du canvas
        double centerX = gameCanvas.getWidth() / 2.0;
        double centerY = 0;
        
        // Position de départ des bombes (alignées à gauche du centre)
        double xStart = centerX + 10; // +10 pour l'espace entre cœurs et bombes
        
        for (int i = 0; i < bombs; i++) {
            gc.drawImage(bombImage, xStart + (i * 35), centerY);
        }
    }
}