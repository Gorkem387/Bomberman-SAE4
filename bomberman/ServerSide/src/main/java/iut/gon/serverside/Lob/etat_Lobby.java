package iut.gon.serverside.Lob;

public enum etat_Lobby {

    EN_ATTENTE, // Le lobby est en attente de joueurs
    COMPLET, // Le lobby est complet et prêt à démarrer le jeu
    EN_JEU, // Le jeu a commencé dans le lobby
    TERMINE // Le jeu dans le lobby est terminé
}
