package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.view.LabRenderer;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameController {

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;
    private Joueur joueur;
    private BombManager bombManager;
    private AnimationTimer gameLoop;
    private boolean isGameOver;

    private final Set<KeyCode> input = new HashSet<>();

    // Pour calculer le deltaTime
    private long lastNanoTime = -1;

    // Pour éviter qu'une bombe soit posée en maintenant la touche
    private boolean spaceWasPressed = false;

    @FXML
    public void initialize() {
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
            // Réinitialise le verrou quand on relâche espace
            if (e.getCode() == KeyCode.SPACE) spaceWasPressed = false;
        });

        this.gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calcul du deltaTime en secondes
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
        // Déplacements
        double dx = 0;
        double dy = 0;

        if (input.contains(KeyCode.Z) || input.contains(KeyCode.UP))    dy--;
        if (input.contains(KeyCode.S) || input.contains(KeyCode.DOWN))  dy++;
        if (input.contains(KeyCode.Q) || input.contains(KeyCode.LEFT))  dx--;
        if (input.contains(KeyCode.D) || input.contains(KeyCode.RIGHT)) dx++;

        if (dx != 0 || dy != 0) {
            joueur.move(dx, dy, labyrinthe);
        }

        // Pose de bombe
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
        bombManager.update(deltaTime, labyrinthe, List.of(joueur));
    }

    private void render() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        renderer.draw(gc, labyrinthe);
        renderer.drawBombs(gc, bombManager.getBombs());
        renderer.drawExplosions(gc, bombManager.getExplosionCells());
        renderer.drawPlayer(gc, joueur);

        if (joueur.isAlive()) {
            renderer.drawPlayer(gc, joueur);
        }

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
}