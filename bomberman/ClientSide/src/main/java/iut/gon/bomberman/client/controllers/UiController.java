package iut.gon.bomberman.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.Objects;

public class UiController {
    
    @FXML
    private Pane heartImageBox;
    
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
