package iut.gon.serverside.Threads;

import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Logger.Logger;
import iut.gon.serverside.Player.DTO.IDTO;
import iut.gon.serverside.Player.DTO.InitGameDTO;
import iut.gon.serverside.Player.DTO.JoueurMisAJourDTO;

public class Thread_Jeu extends Thread {

    private boolean running = true;
    private Lobby lobby;
    private Logger logger = Logger.getInstance();
    private InitGameDTO initGameDTO;
    private JoueurMisAJourDTO joueurMisAJourDTO;

    public Thread_Jeu(Lobby lobby) {
        this.lobby = lobby;
        lobby.setThread(this);
    }

    @Override
    public void run() {
        initGameDTO = new InitGameDTO();
        initGameDTO.id = lobby.getId();
        initGameDTO.pseudo = lobby.getNom();
        initGameDTO.skin = 0;
        initGameDTO.x = 0;
        initGameDTO.y = 0;

        broadcastUpdate(initGameDTO);
        while (running) {
            joueurMisAJourDTO = new JoueurMisAJourDTO(
                    lobby.getJoueur(0).getId(),
                    lobby.getJoueur(0).getX(),
                    lobby.getJoueur(0).getY()
            );

            broadcastUpdate(joueurMisAJourDTO);
            try { Thread.sleep(16); }
            catch (InterruptedException e) {} // ~60 FPS
        }
    }

    private void broadcastUpdate(IDTO DTO) {
        // Pour chaque Joueur dans le lobby, on récupère son ClientHandler pour envoyer
        for (int i = 0; i < lobby.getJoueurs().size(); i++) {
            lobby.getJoueur(i).getClientHandler().send(DTO);
        }
    }
}
