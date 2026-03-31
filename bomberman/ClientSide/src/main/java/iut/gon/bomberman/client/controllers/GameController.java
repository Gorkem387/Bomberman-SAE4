package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.ai.AISTRATEGIES;
import iut.gon.bomberman.client.ai.Ai;
import iut.gon.bomberman.client.ai.HeatMap;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameController {

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;

    // AI ===================
    private HeatMap heatMap;
    private Ai ia;
    private Joueur iaPlayer;
    // Fin AI ===============

    private Joueur joueur;
    private BombManager bombManager;
    private AnimationTimer gameLoop;
    private boolean isGameOver = false;

    private final Set<KeyCode> input = new HashSet<>();
    private long lastNanoTime = -1;
    private boolean spaceWasPressed = false;

    @FXML
    public void initialize() {
        // Choix du générateur (DFS par défaut)
        DFSGenerator generator = new DFSGenerator();

        this.labyrinthe = generator.createLabyrinthe(21, 21);

        this.gc = gameCanvas.getGraphicsContext2D();
        this.bombManager = new BombManager();

        this.joueur = new Joueur(1, "Gorke");
        this.joueur.setX(1);
        this.joueur.setY(1);

        //=========Ai ===========
        this.iaPlayer = new Joueur(2, "IA");
        this.heatMap = new HeatMap(21, 21);
        this.ia = new Ai(iaPlayer, this.labyrinthe, AISTRATEGIES.AGGRESSIVE, this, heatMap, bombManager);
        this.iaPlayer.setX(19);
        this.iaPlayer.setY(19);
        // Fin AI ==============

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

            this.ia.play(new Joueur[]{this.ia.getPlayer(), this.joueur});
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
            renderer.drawPlayer(gc, this.iaPlayer);
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