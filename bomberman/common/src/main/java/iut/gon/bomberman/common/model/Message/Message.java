package iut.gon.bomberman.common.model.message;

import java.io.Serializable;

/**
 * Interface de base pour tous les messages transitant sur le réseau.
 * Doit être présente dans le module Common dans une architecture multi-module.
 */
public interface Message extends Serializable {
    MessageType getType();
}
