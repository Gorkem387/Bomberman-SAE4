package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Message.MoveRequest;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.LobbyManager;

/**
 * Gère les demandes de déplacement des joueurs pendant la partie.
 */
public class MoveHandler implements MessageHandler<MoveRequest> {

    @Override
    public void handle(MoveRequest message, ClientHandler client) {
        // Le mouvement se fait dans un lobby spécifique.
        // Un clientHandler devrait stocker l'ID du lobby dans lequel il se trouve (par exemple via client.lobbyId)
        // Ou bien, on peut chercher le lobby qui contient le joueur (moins performant)
        
        // Simuler la recherche du lobby (à adapter dans votre LobbyManager pour obtenir le lobby par ID)
        //todo : donner l'id du lobby au clientHandler
        Lobby lobby = LobbyManager.getInstance().getLobby(client.getLobbyId());


        if (client.joueur != null) {
            // Mise à jour de la position dans le modèle métier Joueur
            // Le serveur fait ici de la "Vérification de mouvement"

            //todo : double check si les mouvement sont ok, check pour les collisions
            client.joueur.setX(message.getX());
            client.joueur.setY(message.getY());
            
            // Si le mouvement est valide, le serveur diffusera la position 
            // via le GameUpdate du Thread_Jeu (60 itérations/sec)
            System.out.println("Déplacement du joueur " + client.joueur.getNom() + 
                " vers (" + message.getX() + "," + message.getY() + ")");
        }
    }
}
