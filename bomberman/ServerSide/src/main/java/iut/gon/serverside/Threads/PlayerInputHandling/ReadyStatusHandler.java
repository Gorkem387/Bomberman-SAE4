package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.Lob.EtatLobby;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.bomberman.common.model.message.ReadyStatus;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.LobbyManager;

public class ReadyStatusHandler implements MessageHandler<ReadyStatus> {

    @Override
    public void handle(ReadyStatus message, ClientHandler client) {
        System.out.println("Le joueur dans le lobby " + message.getLobbyId() + " est prêt: " + message.isReady());

        Lobby lobby = LobbyManager.getInstance().getLobby(message.getLobbyId());

        //logging
        Logger logger = Logger.getInstance();
        logger.log(LogTypes.INFO, "\"Le joueur dans le lobby \" + message.getLobbyId() + \" est prêt: \" + message.isReady()");


        // Logique de début de partie si tout le monde est prêt
        lobby.setReadyStatus(client, message.isReady());

        boolean isReadyToLaunch = true;

        for(Joueur j : lobby.getJoueurs()){
            if(j.getEtat() != EtatJoueur.PRET) isReadyToLaunch = false;
        }

        if(isReadyToLaunch) lobby.setStatus(EtatLobby.PRET);
    }
}
