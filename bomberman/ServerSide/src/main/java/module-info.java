module iut.gon.serverside {
<<<<<<< HEAD
=======
    // Dépendance vers le module commun pour accéder aux Messages
    requires common;
    
    // Dépendances Java natives si nécessaire
    requires java.base;

    // Exportation des packages du serveur
>>>>>>> dev
    exports iut.gon.serverside;
    exports iut.gon.serverside.Threads;
    exports iut.gon.serverside.Threads.PlayerInputHandling;
    exports iut.gon.serverside.Logger;
}
