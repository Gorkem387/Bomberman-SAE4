package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.view.LabRenderer;
import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import javax.swing.*;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;

    @FXML
    public void initialize() {
        DFSGenerator generator = new DFSGenerator();
        this.labyrinthe = generator.createLabyrinthe(21, 21);
        this.gc = gameCanvas.getGraphicsContext2D();

        gameCanvas.setWidth(labyrinthe.getWidth() * 32);
        gameCanvas.setHeight(labyrinthe.getHeight() * 32);

        // Game Loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Cette méthode tourne à 60 FPS
                update();
                render();
            }
        };
        gameLoop.start();
    }

    private void update() {
        // player update ici
    }

    private void render() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        renderer.draw(gc, labyrinthe);
        // TODO render player
    }
}
