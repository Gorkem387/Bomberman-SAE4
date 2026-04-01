package iut.gon.serverside.Lob;

import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Threads.Thread_Jeu;

import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.bomberman.common.model.Mess.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {

        private Thread_Jeu thread = null;
        private int id;
        private String nomLobby;
        private Joueur proprietaire;
        private final List<Joueur> joueursInvites = new ArrayList<>();
        private final Map<Integer, ClientHandler> handlers = new HashMap<>(); // Lien JoueurId -> Connection
        private final Labyrinthe labyrinthe;
        private int nbJMax;
        private TypeLab typeLab;
        private EtatLobby etatLobby = EtatLobby.EN_ATTENTE;
        private Logger logger = Logger.getInstance();

        public Lobby(int id, String nom, Joueur owner, int nbJMax, TypeLab typeLab, int lab_size_x, int lab_size_y) {
            this.id = id;
            this.nomLobby = nom;
            this.proprietaire = owner;
            this.nbJMax = nbJMax;
            this.typeLab = typeLab;
            this.labyrinthe = new Labyrinthe(lab_size_x, lab_size_y);
        }

        public void addJoueur(Joueur joueur, ClientHandler handler) {
            joueursInvites.add(joueur);
            handlers.put(joueur.getId(), handler);
            broadcastUpdate();
        }

        public void removeJoueur(Joueur joueur) {
            joueursInvites.remove(joueur);
            handlers.remove(joueur.getId());
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

        /**
         * Déclenche une mise à jour visuelle pour tous les membres du lobby.
         */
        public void broadcastUpdate() {
            // Dans cette architecture, la mise à jour est déclenchée lors d'une action
            // ou via un message périodique si nécessaire.
        }
        
        /**
         * Envoie un message à TOUS les joueurs du lobby de manière synchrone.
         */
        public void broadcast(Message message) {
            // Synchronisation pour éviter les erreurs lors d'un départ de joueur simultané
            synchronized (handlers) {
                for (ClientHandler h : handlers.values()) {
                    h.send(message);
                }
            }
        }

        public void startGame() {
            if (!joueursInvites.isEmpty()) {
                logger.log(LogTypes.SUCCESS,"Démarrage de la partie avec " + joueursInvites.size() + " joueurs.");
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

        public void setReadyStatus(ClientHandler client, Boolean isReady){
            Joueur j = client.getJoueur();
            if (j != null) {
                j.setEtat(isReady ? EtatJoueur.PRET : EtatJoueur.PAS_PRET);
                // Notification automatique de tous les membres
            }
        }

        public void setStatus(EtatLobby newEtat){
            this.etatLobby = newEtat;
        }

        public EtatLobby getStatus(){
            return etatLobby;
        }
}
