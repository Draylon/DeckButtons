package com.objectdynamics.deckbuttons.util.comms;

public interface SocketSendBroadcast<T> {
    public void send(T header,T content);
    public void send(T content);
}
