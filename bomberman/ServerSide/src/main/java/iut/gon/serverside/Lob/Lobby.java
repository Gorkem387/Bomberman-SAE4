package iut.gon.serverside.Lob;

import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import iut.gon.serverside.Player.Joueur;

import java.util.ArrayList;

public class Lobby {

        private int id;
        private String nomLobby;
        private Joueur proprietaire;
        private final ArrayList<Joueur> joueursInvites = new ArrayList<>();
        private final Labyrinthe labyrinthe = new Labyrinthe(21,21);
        private int nbJMax;
        private TypeLab typeLab;

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

}
