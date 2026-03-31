package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.player.Joueur;

public enum AISTRATEGIES {
    AGGRESSIVE {
        @Override
        public void play(Ai ia) {
            // TODO: Rétablir la logique avec le BombManager
            // ia.randomMove();
        }
    },
    SURVIVOR {
        @Override
        public void play(Ai ia) {
            // TODO: Rétablir la logique de survie
        }
    },
    CHAOS {
        @Override
        public void play(Ai ia) {
            // TODO: Rétablir la logique Chaos
        }
    };

    // Cette méthode doit être abstraite pour que les enums l'implémentent
    public abstract void play(Ai ia);
}