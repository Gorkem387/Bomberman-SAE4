package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.ai.AISTRATEGIES;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigPartieLocaleController {

    @FXML private CheckBox cartePetite;
    @FXML private CheckBox carteMoyenne;
    @FXML private CheckBox carteGrande;
    @FXML private Slider nombreIA;
    @FXML private VBox listeIA;
    @FXML private Button startGame;

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

        nombreIA.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateListeIA((int) Math.round(newVal.doubleValue()));
        });

        updateListeIA(1);

        startGame.setOnAction(this::loadGameView);
    }

    private void updateListeIA(int nombre) {
        listeIA.getChildren().clear();

        for (int i = 0; i < nombre; i++) {
            HBox ligne = new HBox(10);
            ligne.setPadding(new Insets(5, 0, 5, 0));

            Text label = new Text("Adversaire " + (i + 1) + " : ");

            CheckBox agressif = new CheckBox("Agressif");
            CheckBox survivant = new CheckBox("Survivant");
            CheckBox chaos = new CheckBox("Chaos");

            agressif.setSelected(true);
            agressif.selectedProperty().addListener((obs, o, selected) -> {
                if (selected) { survivant.setSelected(false); chaos.setSelected(false); }
            });
            survivant.selectedProperty().addListener((obs, o, selected) -> {
                if (selected) { agressif.setSelected(false); chaos.setSelected(false); }
            });
            chaos.selectedProperty().addListener((obs, o, selected) -> {
                if (selected) { agressif.setSelected(false); survivant.setSelected(false); }
            });

            ligne.getChildren().addAll(label, agressif, survivant, chaos);
            listeIA.getChildren().add(ligne);
        }
    }

    public List<AISTRATEGIES> getStrategiesChoisies() {
        List<AISTRATEGIES> strategies = new ArrayList<>();
        for (int i = 0; i < listeIA.getChildren().size(); i++) {
            HBox ligne = (HBox) listeIA.getChildren().get(i);
            CheckBox agressif = (CheckBox) ligne.getChildren().get(1);
            CheckBox survivant = (CheckBox) ligne.getChildren().get(2);
            CheckBox chaos = (CheckBox) ligne.getChildren().get(3);

            if (survivant.isSelected()) strategies.add(AISTRATEGIES.SURVIVOR);
            else if (chaos.isSelected()) strategies.add(AISTRATEGIES.CHAOS);
            else strategies.add(AISTRATEGIES.AGGRESSIVE);
        }
        return strategies;
    }

    public int getTailleMap() {
        if (cartePetite.isSelected()) return 15;
        if (carteGrande.isSelected()) return 30;
        return 21;
    }

    private void loadGameView(ActionEvent event) {
        try {
            String fxmlPath = "/iut/gon/bomberman/client/game-view.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent gameRoot = loader.load();

            GameController gameController = loader.getController();
            gameController.setConfig(getTailleMap(), getStrategiesChoisies());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(gameRoot));
            stage.setTitle("Bomberman - Partie en cours");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}