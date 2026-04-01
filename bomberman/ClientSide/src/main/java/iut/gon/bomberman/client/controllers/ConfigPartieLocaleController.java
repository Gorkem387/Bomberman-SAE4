package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.ai.AISTRATEGIES;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigPartieLocaleController {

    @FXML private ToggleButton cartePetite;
    @FXML private ToggleButton carteMoyenne;
    @FXML private ToggleButton carteGrande;
    @FXML private Slider nombreIA;
    @FXML private VBox listeIA;
    @FXML private Button startGame;
    @FXML private Text valeurIA;

    @FXML
    public void initialize() {
        final int[] lastNb = {1};

        nombreIA.valueProperty().addListener((obs, oldVal, newVal) -> {
            int nb = (int) Math.round(newVal.doubleValue());

            valeurIA.setText(String.valueOf(nb));

            if (nb != lastNb[0]) {
                lastNb[0] = nb;
                updateListeIA(nb);
            }
        });

        int depart = (int) Math.round(nombreIA.getValue());
        valeurIA.setText(String.valueOf(depart));
        updateListeIA(depart);

        startGame.setOnAction(this::loadGameView);
    }

    private void updateListeIA(int nombre) {
        listeIA.getChildren().clear();

        for (int i = 0; i < nombre; i++) {
            HBox ligne = new HBox(10);
            ligne.setPadding(new Insets(5, 0, 5, 0));

            Text label = new Text("Adversaire " + (i + 1) + " : ");

            ToggleButton agressif = new ToggleButton("Agressif");
            ToggleButton survivant = new ToggleButton("Survivant");
            ToggleButton chaos = new ToggleButton("Chaos");

            ToggleGroup group = new ToggleGroup();
            agressif.setToggleGroup(group);
            survivant.setToggleGroup(group);
            chaos.setToggleGroup(group);

            agressif.setSelected(true);

            ligne.getChildren().addAll(label, agressif, survivant, chaos);
            listeIA.getChildren().add(ligne);
        }
    }

    public List<AISTRATEGIES> getStrategiesChoisies() {
        List<AISTRATEGIES> strategies = new ArrayList<>();
        for (Node node : listeIA.getChildren()) {
            if (node instanceof HBox ligne) {
                ToggleButton agressif = (ToggleButton) ligne.getChildren().get(1);
                ToggleButton survivant = (ToggleButton) ligne.getChildren().get(2);
                ToggleButton chaos = (ToggleButton) ligne.getChildren().get(3);

                if (survivant.isSelected()) {
                    strategies.add(AISTRATEGIES.SURVIVOR);
                } else if (chaos.isSelected()) {
                    strategies.add(AISTRATEGIES.CHAOS);
                } else {
                    strategies.add(AISTRATEGIES.AGGRESSIVE);
                }
            }
        }
        return strategies;
    }

    public int getTailleMap() {
        if (cartePetite.isSelected()) return 15;
        if (carteGrande.isSelected()) return 23;
        return 19;
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