package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.view.LabRenderer;
import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

import javax.swing.*;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;

    @FXML
    public void initialize() {
        DFSGenerator generator = new DFSGenerator();
        this.labyrinthe = generator.createLabyrinthe(21, 21);

        gameCanvas.setWidth(labyrinthe.getWidth() * 32);
        gameCanvas.setHeight(labyrinthe.getHeight() * 32);

        draw();
    }
    
    private void draw() {
        renderer.draw(gameCanvas.getGraphicsContext2D(), labyrinthe);
    }
}
