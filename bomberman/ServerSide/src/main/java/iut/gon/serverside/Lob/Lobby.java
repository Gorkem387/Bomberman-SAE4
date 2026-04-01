package iut.gon.serverside.Lob;

import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Threads.Thread_Jeu;

import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.bomberman.common.model.Mess.*;

import java.util.*;
import java.util.stream.Collectors;

public class Lobby {

        private Thread_Jeu thread = null;
        private int id;
        private String nomLobby;
        private Joueur proprietaire;
        private final List<Joueur> joueursInvites = new ArrayList<>();
        private final Map<Integer, ClientHandler> handlers = new HashMap<>();
        private final Labyrinthe labyrinthe;
        private int nbJMax;
        private TypeLab typeLab;
        private EtatLobby etatLobby = EtatLobby.EN_ATTENTE;
        private Logger logger = Logger.getInstance();

        // --- GESTION DU DÉCOMPTE ---
        private Timer countdownTimer;
        private int countdownSecondsRemaining = 5;
        private boolean isCountdownRunning = false;

        public Lobby(int id, String nom, Joueur owner, int nbJMax, TypeLab typeLab, int lab_size_x, int lab_size_y) {
            this.id = id;
            this.nomLobby = nom;
            this.proprietaire = owner;
            this.nbJMax = nbJMax;
            this.typeLab = typeLab;
            this.labyrinthe = new Labyrinthe(lab_size_x, lab_size_y);
        }

        public synchronized void addJoueur(Joueur joueur, ClientHandler handler) {
            joueursInvites.add(joueur);
            handlers.put(joueur.getId(), handler);
            broadcastUpdate();
        }

        public synchronized void removeJoueur(Joueur joueur) {
            joueursInvites.remove(joueur);
            handlers.remove(joueur.getId());
            if (isCountdownRunning) {
                cancelCountdown("Un joueur a quitté le lobby.");
            }
            broadcastUpdate();
        }

        public List<Joueur> getJoueurs() {
            return joueursInvites;
        }

        public String getNom() {
            return nomLobby;
        }

        public int getId(){
            return id;
        }

        public Joueur getProprietaire() {
            return proprietaire;
        }

        public int getNbJMax() {
            return nbJMax;
        }

        public void broadcastUpdate() {
            // Envoyer LobbyDetailsResponse à tout le monde
            // (Implémenté via LobbyDetailsHandler)
        }
        
        public void broadcast(Message message) {
            synchronized (handlers) {
                for (ClientHandler h : handlers.values()) {
                    h.send(message);
                }
            }
        }

        public synchronized void startCountdown() {
            if (isCountdownRunning) return;

            // Vérifier que tout le monde est prêt
            for (Joueur j : joueursInvites) {
                if (j.getEtat() != EtatJoueur.PRET) {
                    logger.log(LogTypes.WARNING, "Lancement annulé : tout le monde n'est pas prêt.");
                    return;
                }
            }

            logger.log(LogTypes.INFO, "Début du décompte de 5 secondes pour le lobby " + id);
            isCountdownRunning = true;
            countdownSecondsRemaining = 5;
            
            countdownTimer = new Timer();
            countdownTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (countdownSecondsRemaining > 0) {
                        broadcast(new CountdownUpdate(countdownSecondsRemaining));
                        countdownSecondsRemaining--;
                    } else {
                        cancel(); // Arrêter le timer
                        initGame();
                    }
                }
            }, 0, 1000);
        }

        public synchronized void cancelCountdown(String reason) {
            if (!isCountdownRunning) return;
            
            isCountdownRunning = false;
            if (countdownTimer != null) {
                countdownTimer.cancel();
            }
            logger.log(LogTypes.WARNING, "Décompte annulé dans le lobby " + id + " : " + reason);
            broadcast(new CountdownUpdate(-1)); // -1 signale l'annulation au client
            broadcast(new ChatMessage("SERVEUR", "Décompte annulé : " + reason, id));
        }

        private void initGame() {
            logger.log(LogTypes.SUCCESS, "Initialisation de la partie pour le lobby " + id);
            
            // Création de la liste des joueurs DTO pour InitGameMessage
            List<InitGameMessage.PlayerInitDTO> players = joueursInvites.stream()
                .map(j -> new InitGameMessage.PlayerInitDTO(j.getId(), j.getNom(), j.getX(), j.getY()))
                .collect(Collectors.toList());

            // Envoi du message INIT_GAME à tout le monde
            broadcast(new InitGameMessage(id, "map_data", players));
            
            // Démarrage réel du thread de jeu
            startGame();
        }

        public void startGame() {
            if (!joueursInvites.isEmpty()) {
                logger.log(LogTypes.SUCCESS,"Lancement réel de la boucle de jeu pour le lobby " + id);
                etatLobby = EtatLobby.COMPLET;
                this.thread = new Thread_Jeu(this);
                this.thread.start();
            }
        }

        public boolean rejoindreLobby(ClientHandler client){
            Joueur j = client.getJoueur();
            if (joueursInvites.contains(j)) return true;
            
            if (joueursInvites.size() >= nbJMax) {
                return false;
            }
            
            addJoueur(j, client);
            return true;
        }

        public synchronized void setReadyStatus(ClientHandler client, Boolean isReady){
            Joueur j = client.getJoueur();
            if (j != null) {
                j.setEtat(isReady ? EtatJoueur.PRET : EtatJoueur.PAS_PRET);
                logger.log(LogTypes.INFO, "Joueur " + j.getNom() + " : " + j.getEtat());
                
                // Si un joueur passe en "Pas Prêt" pendant le décompte, on l'annule
                if (!isReady && isCountdownRunning) {
                    cancelCountdown(j.getNom() + " n'est plus prêt.");
                }
            }
        }

        public void setStatus(EtatLobby newEtat){
            this.etatLobby = newEtat;
        }

        public EtatLobby getStatus(){
            return etatLobby;
        }

        public void setThread(Thread_Jeu threadJeu) {
            this.thread = threadJeu;
        }
}
