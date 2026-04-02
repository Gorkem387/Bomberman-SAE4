package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.LobbyDetailsRequest;
import iut.gon.bomberman.common.model.Mess.LobbyDetailsResponse;
import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Threads.ClientHandler;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyDetailsHandler implements MessageHandler<LobbyDetailsRequest> {

    /**
     * Permet de notifier les joueurs sur le lobby
     * @param message
     * @param client
     */
    @Override
    public void handle(LobbyDetailsRequest message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(message.getLobbyId());
        
        if (lobby != null) {
            // Création de la réponse (DTO)
            LobbyDetailsResponse response = createResponse(lobby);
            
            // On renvoie à CELUI qui a demandé
            client.send(response);
            
            // --- MISE À JOUR TEMPS RÉEL ---
            // On profite de cette demande (ou lors de Join/Leave) pour notifier TOUS les autres joueurs
            // Ainsi, si un joueur change d'état (Prêt) ou rejoint, tout le monde est au courant sans rien faire
            lobby.broadcast(response);
        }
    }

    /**
     * Méthode utilitaire pour générer la réponse à partir de l'état actuel du lobby.
     */
    public LobbyDetailsResponse createResponse(Lobby lobby) {
        LobbyDetailsResponse.PlayerDTO ownerDTO = new LobbyDetailsResponse.PlayerDTO(
                lobby.getProprietaire().getId(), 
                lobby.getProprietaire().getNom(), 
                lobby.getProprietaire().getEtat() == EtatJoueur.PRET, 
                true
        );
        
        List<LobbyDetailsResponse.PlayerDTO> playerDTOs = lobby.getJoueurs().stream()
                .map(j -> new LobbyDetailsResponse.PlayerDTO(
                        j.getId(), 
                        j.getNom(), 
                        j.getEtat() == EtatJoueur.PRET, 
                        j.getId() == lobby.getProprietaire().getId()
                ))
                .collect(Collectors.toList());
        
        return new LobbyDetailsResponse(lobby.getId(), lobby.getNom(), lobby.getNbJMax(), ownerDTO, playerDTOs);
    }
}
