package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.ai.AISTRATEGIES;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;


public class ConfigPartieEnLigneController {

    @FXML private CheckBox cartePetite;
    @FXML private CheckBox carteMoyenne;
    @FXML private CheckBox carteGrande;
    @FXML private Slider nombreJoueurs;
    @FXML private Button creerLobby;

    @FXML
    public void initialize() {
        carteMoyenne.setSelected(true);
        cartePetite.selectedProperty().addListener((obs, o, selected) -> {
            if (selected) { carteMoyenne.setSelected(false); carteGrande.setSelected(false); }
        });
        carteMoyenne.selectedProperty().addListener((obs, o, selected) -> {
            if (selected) { cartePetite.setSelected(false); carteGrande.setSelected(false); }
        });
        carteGrande.selectedProperty().addListener((obs, o, selected) -> {
            if (selected) { cartePetite.setSelected(false); carteMoyenne.setSelected(false); }
        });

        creerLobby.setOnAction(e -> {});

    }

    public int getNombreJoueurs() {
        return (int) Math.round(nombreJoueurs.getValue());
    }

    public int getTailleMap() {
        if (cartePetite.isSelected()) return 18;
        if (carteGrande.isSelected()) return 35;
        return 29;
    }
}