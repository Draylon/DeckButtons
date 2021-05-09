package com.objectdynamics.deckbuttons.util.comms;

public class SocketInitializerRunnable implements Runnable{
    protected String mode,ip;
    protected int port;
    protected SocketInitializerRunnable(String mode,String ip,int port){
        this.mode=mode;
        this.ip=ip;
        this.port=port;
    }

    @Override
    public void run() {
        System.out.println("Connecting through "+this.mode+" to "+this.ip+":"+this.port);
    }
}
