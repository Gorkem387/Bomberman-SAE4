package iut.gon.serverside.Lob;

import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.serverside.Player.DTO.InitGameDTO;
import iut.gon.serverside.Player.EtatJoueur;
import iut.gon.serverside.Player.Joueur;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Threads.Thread_Jeu;

import java.util.ArrayList;

public class Lobby {

        private Thread_Jeu thread = null;
        private int id;
        private String nomLobby;
        private Joueur proprietaire;
        private final ArrayList<Joueur> joueursInvites = new ArrayList<>();
        private final Labyrinthe labyrinthe = new Labyrinthe();
        private int nbJMax;
        private TypeLab typeLab;
        private EtatLobby etatLobby = EtatLobby.EN_ATTENTE;
        private Logger logger = Logger.getInstance();

        public Lobby(Thread_Jeu thread, int id, String nom, Joueur owner, int nbJMax, TypeLab typeLab) {
            this.thread = thread;
            this.id = id;
            this.nomLobby = nom;
            this.proprietaire = owner;
            this.nbJMax = nbJMax;
            this.typeLab = typeLab;
        }

    public Lobby(int id, String nom, Joueur owner, int nbJMax, TypeLab typeLab) {
        this.id = id;
        this.nomLobby = nom;
        this.proprietaire = owner;
        this.nbJMax = nbJMax;
        this.typeLab = typeLab;
    }

        public void addJoueur(Joueur joueur) {
            joueursInvites.add(joueur);
        }

        public void removeJoueur(Joueur joueur) {
            joueursInvites.remove(joueur);
        }

        public ArrayList<Joueur> getJoueurs() {
            return joueursInvites;
        }

        public String getNom() {
            return nomLobby;
        }

        public void setNom(String nom) {
            this.nomLobby = nom;
        }

        public Labyrinthe getLabyrinthe() {
            return labyrinthe;
        }

        public int getNbJMax() {
            return nbJMax;
        }

        public void setNbJMax(int nbJMax) {
            this.nbJMax = nbJMax;
        }

        public TypeLab getTypeLab() {
            return typeLab;
        }

        public int getId(){
            return id;
        }

        public void broadcastInit(InitGameDTO dto) {
            for (Joueur tj : joueursInvites) {
                tj.sendInitDTO(dto);
            }
        }

        private boolean peuCommencer(){
            for(Joueur j : joueursInvites){
                if(j.getEtat() == EtatJoueur.NOT_CONNECTED || j.getEtat() == EtatJoueur.PAS_PRET) return false;
            }
            return true;
        }

        public void startGame() {
            if (!joueursInvites.isEmpty()) {
                logger.log(LogTypes.SUCCESS,"Démarrage de la partie avec " + joueursInvites.size() + " joueurs.");
                etatLobby = EtatLobby.COMPLET;
                this.thread = new Thread_Jeu(null, this);

            } else {
                logger.log(LogTypes.WARNING, "Pas assez de joueurs pour démarrer la partie.");
            }
        }

        public void setStatus(EtatLobby newEtat){
            this.etatLobby = newEtat;
        }

        public void setReadyStatus(ClientHandler client, Boolean isReady){
            EtatJoueur etat = isReady ? EtatJoueur.PRET : EtatJoueur.PAS_PRET;
            joueursInvites.get(client.playerId).setEtat(etat);
        }

        public Joueur getJoueur(int id){
            return joueursInvites.get(id);
        }

        public boolean rejoindreLobby(ClientHandler client){
            //todo : faire logique , check si trpo de joueurs etc
            joueursInvites.add());
        }

    public void setThread(Thread_Jeu threadJeu) {
            this.thread = threadJeu;
    }
}
