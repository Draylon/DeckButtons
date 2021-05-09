package com.objectdynamics.deckbuttons.util.comms;

public interface SocketBroadcastEvent<T> {
    void call(T message);
}
