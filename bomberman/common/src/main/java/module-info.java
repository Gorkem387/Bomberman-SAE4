module common {
    // Exportation des packages pour que les autres modules (ClientSide et ServerSide) y aient accès
    exports iut.gon.bomberman.common.model.Message;
    exports iut.gon.bomberman.common.model.labyrinthe;
    exports iut.gon.bomberman.common.model.player;

    // Un module de base comme "common" ne doit pas dépendre des modules qui l'utilisent (dépendance circulaire)
}
