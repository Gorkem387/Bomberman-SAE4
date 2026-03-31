package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.common.model.player.Joueur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.Objects;

public class UiController {
    
    @FXML
    private Pane heartImageBox;

    @FXML
    private Label bombCountLabel;

    @FXML
    private Label speedLabel;
    
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

    /**
     * Met à jour toute l'interface d'un coup
     */
    public void updatePlayerStats(Joueur j) {
        if (j == null) return;
        displayHearts(j.getPv());

        if (bombCountLabel != null) {
            bombCountLabel.setText("BOMBES : " + j.getNb_bombes() + " / " + j.getNb_bombes_max());
        }
        if (speedLabel != null) {
            String speedText = String.format("VITESSE : x%.1f", j.getSpeed_multiplier());
            speedLabel.setText(speedText);
            if (j.getSpeed_multiplier() > 1.0) {
                speedLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");
            } else {
                speedLabel.setStyle("-fx-text-fill: white;");
            }
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
