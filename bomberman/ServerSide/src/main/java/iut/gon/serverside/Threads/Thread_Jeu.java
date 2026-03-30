package iut.gon.serverside.Threads;

import iut.gon.serverside.Lob.Lobby;

public class Thread_Jeu extends Thread {

    Lobby lobby;

        public Thread_Jeu(Lobby lobby) {
            this.lobby = lobby;
        }

}
