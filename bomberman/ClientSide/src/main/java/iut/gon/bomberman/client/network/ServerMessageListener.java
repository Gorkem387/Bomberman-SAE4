package iut.gon.bomberman.client.network;

import iut.gon.bomberman.common.model.Mess.Message;

public interface ServerMessageListener {

    void onServerMessage(Message message);

}
