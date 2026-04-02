package iut.gon.bomberman.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomizeController {

    // Elements de l'interface FXML
    @FXML private Button btnSkin;
    @FXML private Button btnBombe;
    @FXML private Label categoryLabel;
    @FXML private FlowPane skinGrid;

    @FXML private ImageView equippedSkinView;
    @FXML private Label equippedSkinLabel;
    @FXML private ImageView equippedBombeView;
    @FXML private Label equippedBombeLabel;

    // On utilise LinkedHashMap pour garder l'ordre d'insertion des éléments
    private final Map<String, SkinData> skinRegistry  = new LinkedHashMap<>();
    private final Map<String, SkinData> bombeRegistry = new LinkedHashMap<>();

    // Variables d'état pour savoir ce qui est sélectionné par défaut
    private String equippedSkin  = "Classique";
    private String equippedBombe = "Classique";
    private String currentCategory = "skin";

    /**
     * Méthode appelée automatiquement par JavaFX au chargement de la vue
     */
    @FXML
    public void initialize() {
        // Initialisation des données ( Apparence du personnage )
        skinRegistry.put("Classique", new SkinData("/iut/gon/bomberman/client/assets/8/S_0.png", "Skin classique"));
        skinRegistry.put("Chien", new SkinData("/iut/gon/bomberman/client/assets/4/S_0.png", "Skin chien"));
        skinRegistry.put("Style animé", new SkinData("/iut/gon/bomberman/client/assets/32/S_0.png", "Skin style animé"));
        skinRegistry.put("Variante marron", new SkinData("/iut/gon/bomberman/client/assets/3/S_0.png", "Skin marron"));
        skinRegistry.put("Variante verte", new SkinData("/iut/gon/bomberman/client/assets/33/S_0.png", "Skin vert"));
        skinRegistry.put("Variante jaune", new SkinData("/iut/gon/bomberman/client/assets/34/S_0.png", "Skin jaune"));

        // ( Apparence de la bombe )
        bombeRegistry.put("Classique", new SkinData("/iut/gon/bomberman/client/assets/B_0.png", "Bombe d'origine"));
        bombeRegistry.put("Retro", new SkinData("/iut/gon/bomberman/client/assets/B2_0.png", "Bombe retro"));

        // Affichage par défaut
        showCategory("skin");
        updateEquippedDisplay();
    }

    /**
     * Gère le clic sur les boutons d'onglets (Personnage ou Bombe)
     */
    @FXML
    public void handleSelectCategory(ActionEvent event) {
        if (event.getSource() == btnSkin) {
            showCategory("skin");
        } else {
            showCategory("bombe");
        }
    }

    /**
     * Construit dynamiquement la grille d'objets selon la catégorie choisie
     */
    private void showCategory(String category) {
        currentCategory = category;
        skinGrid.getChildren().clear();

        // Sélection du bon registre selon la catégorie
        Map<String, SkinData> registry = category.equals("skin") ? skinRegistry : bombeRegistry;
        categoryLabel.setText(category.equals("skin") ? "Skins disponibles" : "Bombes disponibles");

        String activeStyle = "-fx-background-color: #2980b9; -fx-text-fill: white;";
        String inactiveStyle = "-fx-background-color: #7f8c8d; -fx-text-fill: white;";
        btnSkin.setStyle(category.equals("skin") ? activeStyle : inactiveStyle);
        btnBombe.setStyle(category.equals("bombe") ? activeStyle : inactiveStyle);

        // Création d'une "carte" pour chaque élément du registre
        for (Map.Entry<String, SkinData> entry : registry.entrySet()) {
            skinGrid.getChildren().add(buildCard(entry.getKey(), entry.getValue(), category));
        }
    }

    /**
     * Crée graphiquement une carte (VBox) représentant un skin ou une bombe
     */
    private VBox buildCard(String name, SkinData data, String category) {
        ImageView iv = new ImageView();
        iv.setFitWidth(80); iv.setFitHeight(80); iv.setPreserveRatio(true);
        try {
            iv.setImage(new Image(getClass().getResourceAsStream(data.imagePath)));
        } catch (Exception e) { System.err.println("Image introuvable : " + data.imagePath); }

        Label lbl = new Label(name);
        lbl.setStyle("-fx-text-fill: white;");

        VBox card = new VBox(8, iv, lbl);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(110, 120);

        // Vérifie si cet objet est celui actuellement équipé pour changer le style de bordure
        boolean isEquipped = name.equals(category.equals("skin") ? equippedSkin : equippedBombe);
        String baseStyle = "-fx-border-radius: 8; -fx-padding: 8; -fx-background-radius: 8;";
        card.setStyle(baseStyle + (isEquipped ? "-fx-background-color: #2c3e50; -fx-border-color: #27ae60; -fx-border-width: 3;"
                : "-fx-background-color: #34495e; -fx-border-color: #7f8c8d; -fx-border-width: 1;"));

        // Événement : Si on clique sur la carte, on équipe l'objet
        card.setOnMouseClicked(e -> equipItem(name, data, category));
        return card;
    }

    /**
     * Met à jour la variable de sélection selon l'élément cliqué
     */
    private void equipItem(String name, SkinData data, String category) {
        if (category.equals("skin")) {
            equippedSkin = name;
        } else {
            equippedBombe = name;
        }
        updateEquippedDisplay();
        showCategory(category);
    }

    /**
     * Met à jour les images et labels de la zone "Equipement actuel"
     */
    private void updateEquippedDisplay() {
        try {
            // Mise à jour de l'aperçu du personnage
            SkinData s = skinRegistry.get(equippedSkin);
            if (s != null) {
                equippedSkinView.setImage(new Image(getClass().getResourceAsStream(s.imagePath)));
                equippedSkinLabel.setText(equippedSkin);
            }
            // Mise à jour de l'aperçu de la bombe
            SkinData b = bombeRegistry.get(equippedBombe);
            if (b != null) {
                equippedBombeView.setImage(new Image(getClass().getResourceAsStream(b.imagePath)));
                equippedBombeLabel.setText(equippedBombe);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Méthode qui permet de retourner au menu
     * @param event L'événement (clic bouton) qui déclenche le retour
     */
    private void returnToMenu(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // On appelle la méthode de démarrage de MainApp qui réinitialise tout proprement
            new iut.gon.bomberman.client.MainApp().start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enregistre les choix de l'utilisateur dans la classe globale GameSettings
     * puis retourne au menu principal
     */
    @FXML
    public void handleSave(ActionEvent event) {
        // Récupère les données correspondant aux noms sélectionnés
        SkinData selectedSkinData = skinRegistry.get(equippedSkin);
        SkinData selectedBombeData = bombeRegistry.get(equippedBombe);

        if (selectedSkinData != null) {
            iut.gon.bomberman.client.GameSettings.setSelectedSkinPath(selectedSkinData.imagePath);
        }
        if (selectedBombeData != null) {
            iut.gon.bomberman.client.GameSettings.setSelectedBombPath(selectedBombeData.imagePath);
        }

        System.out.println("Sauvegarde effectuée dans GameSettings !");
        System.out.println("Skin : " + equippedSkin + " -> " + selectedSkinData.imagePath);

        returnToMenu(event);
    }

    /**
     * Retourne au menu sans sauvegarder les modifications dans GameSettings
     */
    @FXML
    public void handleBackToMenu(ActionEvent event) {
        returnToMenu(event);
    }

    /**
     * Petite classe interne pour structurer les données d'un skin ou d'une bombe
     */
    private static class SkinData {
        String imagePath, description;
        SkinData(String p, String d) { this.imagePath = p; this.description = d; }
    }
}