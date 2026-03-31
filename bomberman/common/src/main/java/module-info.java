module common {
    // Exportation des packages pour que les autres modules (ClientSide et ServerSide) y aient accès
    exports iut.gon.bomberman.common.model.Mess;
    exports iut.gon.bomberman.common.model.labyrinthe;
    exports iut.gon.bomberman.common.model.player;
    exports iut.gon.bomberman.common.model.player.Effects;

    // Un module de base comme "common" ne doit pas dépendre des modules qui l'utilisent (dépendance circulaire)
}
