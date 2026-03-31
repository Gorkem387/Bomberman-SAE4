package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.ai.AISTRATEGIES;
import iut.gon.bomberman.client.ai.Ai;
import iut.gon.bomberman.client.ai.HeatMap;
import iut.gon.bomberman.client.view.LabRenderer;

import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;


import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;
    private Joueur joueur;
    private final Set<KeyCode> input = new HashSet<>();
    private HeatMap heatMap;
    private Ai ai;
    private Joueur[] players = new Joueur[2];

    @FXML
    public void initialize() {
        // Génération du labyrinthe ( 2 méthodes différentes )
        // Exploration exhaustive (DFSGenerator) :
        DFSGenerator generator = new DFSGenerator();

        // Fusion aléatoire du chemin ( Algorithme de Kruskal ) :
        // KruskalGenerator generator = new KruskalGenerator();

        this.heatMap = new HeatMap(21, 21);
        this.labyrinthe = generator.createLabyrinthe(21, 21);
        this.gc = gameCanvas.getGraphicsContext2D();

        this.ai = new Ai(new Joueur(2, "IA"), this.labyrinthe, AISTRATEGIES.AGGRESSIVE, this, this.heatMap);

        this.joueur = new Joueur(1, "Gorke");
        this.joueur.setX(1);
        this.joueur.setY(1);

        this.players[0] = this.joueur;
        this.players[1] = this.ai.getPlayer();

        gameCanvas.setWidth(labyrinthe.getWidth() * 32);
        gameCanvas.setHeight(labyrinthe.getHeight() * 32);

        gameCanvas.setFocusTraversable(true);

        gameCanvas.setOnKeyPressed(e -> input.add(e.getCode()));
        gameCanvas.setOnKeyReleased(e -> input.remove(e.getCode()));

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
        // Les déplacements
        double dx = 0;
        double dy = 0;

        if (input.contains(KeyCode.Z) || input.contains(KeyCode.UP)) {
            dy--;
        }
        if (input.contains(KeyCode.S) || input.contains(KeyCode.DOWN)) {
            dy++;
        }
        if (input.contains(KeyCode.Q) || input.contains(KeyCode.LEFT)) {
            dx--;
        }
        if (input.contains(KeyCode.D) || input.contains(KeyCode.RIGHT)) {
            dx++;
        }

        if (dx != 0 || dy != 0) {
            joueur.move(dx, dy, labyrinthe);
        } else {
            // Pas de mouvement : mettre la direction à IDLE
            joueur.setDirection(iut.gon.bomberman.common.model.player.Direction.IDLE);
        }

        this.ai.play(players);
    }

    private void render() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        renderer.draw(gc, labyrinthe);
        renderer.drawPlayer(gc, this.joueur);
    }
}
