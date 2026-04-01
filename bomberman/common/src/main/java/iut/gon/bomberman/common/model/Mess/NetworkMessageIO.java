package iut.gon.bomberman.common.model.Mess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Utilitaire pour lire/écrire les messages réseau au format "type (int) + payload".
 * Les codes types utilisés ici doivent correspondre aux TYPE envoyés par les DTO côté serveur :
 *  - 1 : InitGame
 *  - 2 : GameUpdate
 */
public final class NetworkMessageIO {

    public static final int TYPE_INIT_GAME = 1;
    public static final int TYPE_GAME_UPDATE = 2;

    private NetworkMessageIO() {}

    /**
     * Lit un message depuis le flux en lisant d'abord l'entier type, puis le payload correspondant.
     * Retourne une instance de Message (InitGame ou GameUpdate), ou lance IOException si type inconnu.
     */
    public static Message readMessage(DataInputStream in) throws IOException {
        int type = in.readInt();
        switch (type) {
            case TYPE_INIT_GAME:
                return InitGame.read(in);
            case TYPE_GAME_UPDATE:
                return GameUpdate.read(in);
            default:
                throw new IOException("Type de message inconnu lu depuis le réseau : " + type);
        }
    }

    /**
     * Écrit un message avec son type en tête puis le payload. Les types supportés sont InitGame et GameUpdate.
     */
    public static void writeMessage(DataOutputStream out, Message message) throws IOException {
        if (message instanceof InitGame) {
            out.writeInt(TYPE_INIT_GAME);
            ((InitGame) message).write(out);
        } else if (message instanceof GameUpdate) {
            out.writeInt(TYPE_GAME_UPDATE);
            ((GameUpdate) message).write(out);
        } else {
            throw new IOException("Tentative d'écrire un type de message non supporté: " + message.getClass().getName());
        }
    }
}

