module iut.gon.serverside {
    // Dépendance vers le module commun pour accéder aux Messages
    requires common;

    // Exportation des packages du serveur
    exports iut.gon.serverside;
    exports iut.gon.serverside.Threads;
    exports iut.gon.serverside.Threads.PlayerInputHandling;
    exports iut.gon.serverside.Logger;
    exports iut.gon.serverside.Player.DTO;
    exports iut.gon.serverside.Lob;
}
