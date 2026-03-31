package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.LobbyDetailsRequest;
import iut.gon.bomberman.common.model.Mess.LobbyDetailsResponse;
import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Threads.ClientHandler;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyDetailsHandler implements MessageHandler<LobbyDetailsRequest> {
    @Override
    public void handle(LobbyDetailsRequest message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(message.getLobbyId());
        
        if (lobby != null) {
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
            
            client.send(new LobbyDetailsResponse(lobby.getId(), lobby.getNom(), lobby.getNbJMax(), ownerDTO, playerDTOs));
        }
    }
}
