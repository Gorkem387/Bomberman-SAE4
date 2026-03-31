package iut.gon.serverside.Lob;

public enum EtatLobby {
    EN_ATTENTE, // Le lobby est en attente de joueurs
    COMPLET, // Le lobby est complet et prêt à démarrer le jeu
    PRET,//tt le monde a l'interieur est pret, pas forcement plein
    EN_JEU, // Le jeu a commencé dans le lobby
    TERMINE // Le jeu dans le lobby est terminé
}
