package iut.gon.bomberman;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;

public class AttenteLobbyController implements Initializable{

    @FXML private ListView listeLobby;
    @FXML private MenuBar menu;
    @FXML private Pane pane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menu.prefWidthProperty().bind(pane.widthProperty());
        listeLobby.prefWidthProperty().bind(pane.widthProperty());
        listeLobby.prefHeightProperty().bind(pane.heightProperty());
        
		
	}
	
	public void ajoutLobby(Lobby lobby) {
		listeLobby.getItems().add(lobby.getNom() + "         " + lobby.getJoueurs().size()+ " / " + lobby.getNBJMax());
	}


}