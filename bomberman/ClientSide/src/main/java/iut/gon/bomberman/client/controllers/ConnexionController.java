package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.MainApp;
import iut.gon.bomberman.client.network.NetworkManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Contrôleur pour la vue de connexion (connexion.fxml).
 * Gère l'établissement de la connexion réseau et l'enregistrement du pseudo local.
 */
public class ConnexionController {

    @FXML
    private TextField pseudo;

    @FXML
    private Button boutonConnexion;

    /**
     * Méthode appelée automatiquement par JavaFX au chargement de la vue
     */
    @FXML
    public void initialize() {
        // Action lors du clic sur le bouton de connexion
        boutonConnexion.setOnAction(event -> handleConnexion());

        // Permettre de valider avec la touche Entrée dans le champ pseudo
        pseudo.setOnAction(event -> handleConnexion());
    }

    /**
     * Algoritme qui permet de gérer la connexion du joueur, d'ajouter le nom du joueur,
     * établir la connexion HTTP au serveur et mettre à jour la liste des lobby.
     */

    @FXML
    public void handleConnexion() {
        String playerName = pseudo.getText().trim();

        if (playerName.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un pseudo.");
            return;
        }

        // 1. Enregistrer le pseudo dans le NetworkManager (le "Qui clique" dont on a parlé)
        NetworkManager nm = NetworkManager.getInstance();
        nm.setLocalPlayerName(playerName);

        // 2. Tenter la connexion au serveur (IP et Port à adapter ou rendre paramétrables)
        try {
            // Si déjà connecté, on ne reconnecte pas
            if (!nm.isConnected()) {
                nm.connectToServer("localhost", 3001);
            }

            if (nm.isConnected()) {
                System.out.println("Connecté au serveur en tant que : " + playerName);
                
                // 3. Changer de vue vers la liste des lobbies
                switchToLobbyList();
            } else {
                showAlert("Erreur de connexion", "Impossible de joindre le serveur.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la connexion : " + e.getMessage());
        }
    }

    /**
     * Fonction permettant de passer de la page de connexion à la page d'attente des lobby.
     */

    private void switchToLobbyList() {
        try {
            // On utilise la méthode de navigation de votre MainApp
            MainApp.setRoot("fxml/attenteLobby");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue des salons.");
        }
    }

    /**
     * Algorithme permettant d'afficher une alerte en cas d'erreur de connexion.
     * @param title
     * @param content
     */

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
