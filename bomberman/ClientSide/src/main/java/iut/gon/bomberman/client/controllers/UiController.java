package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.common.model.player.Joueur;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.Objects;

public class UiController {
    
    @FXML
    private Pane heartImageBox;

    @FXML

    private javafx.scene.control.Label rangeLabel;

    @FXML

    private javafx.scene.control.Label speedLabel;
    
    private Image heartImage;
    
    @FXML
    public void initialize() {
        // Charge l'image du cœur
        try {
            String resourcePath = Objects.requireNonNull(
                getClass().getResource("/iut/gon/bomberman/client/assets/heart.png")
            ).toExternalForm();
            heartImage = new Image(resourcePath, 40, 40, true, true);
        } catch (NullPointerException e) {
            System.err.println("Impossible de charger l'image du cœur : " + e.getMessage());
        }
    }

    public void updatePlayerStats(Joueur j) {
        if (j == null) return;
        displayHearts(j.getPv());
        if (rangeLabel != null) {
            rangeLabel.setText("PORTÉE : " + j.getExplosionRange());
        }
        if (speedLabel != null) {
            speedLabel.setText(String.format("VITESSE : x%.1f", j.getSpeed_multiplier()));
        }
    }
    
    /**
     * Affiche les cœurs basés sur le nombre de points de vie du joueur
     * @param hearts Nombre de cœurs à afficher
     */
    public void displayHearts(int hearts) {
        if (heartImageBox == null) return;
        
        // Efface les anciens cœurs
        heartImageBox.getChildren().clear();
        
        // Ajoute les nouveaux cœurs
        for (int i = 0; i < hearts; i++) {
            ImageView heartView = new ImageView(heartImage);
            heartView.setLayoutX(i * 45);
            heartView.setLayoutY(0);
            heartImageBox.getChildren().add(heartView);
        }
    }
}
