package com.objectdynamics.deckbuttons.util.comms;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SocketComms {

    public static final char sigmsg = '┼';
    public static final char sigend = '¤';
    public static final char sighead = '¦';
    public static final char sigresp = '─';

    private boolean autoReconnect;
    private Semaphore responseClock = new Semaphore(1,true);
    private int default_lock_time;
    private ServerSocket usb_server;
    private Socket usb_cli=null;
    private InputStream dataIn=null;
    private PrintStream dataOut=null;
    private String mode;
    private String ip;
    private Integer port_i;

    private ArrayList<String> latest_response_h=new ArrayList<String>();
    private ArrayList<String> latest_response_c=new ArrayList<String>();

    private Context ctx;
    public SocketComms(Context ctx,String mode,String ip,int port){
        this(ctx,mode,ip,port,true,4000);
    }
    public SocketComms(Context ctx,String mode,String ip,int port,boolean autoReconnect){
        this(ctx,mode,ip,port,autoReconnect,4000);
    }
    public SocketComms(Context ctx,String mode,String ip,int port,boolean autoReconnect,int customTimeouts){
        this.autoReconnect=autoReconnect;
        this.mode=mode;
        this.ip=ip;
        this.port_i=port;
        this.default_lock_time=customTimeouts;
    }

    Thread connectionThread=null;
    Semaphore isConnecting = new Semaphore(1,true);
    public void connect(){
        try { isConnecting.acquire(); } catch (InterruptedException e) { e.printStackTrace(); }
        if(mode.equals("usb"))
            connectionThread=new Thread(usb_conn_th);
        else if(mode.equals("wifi"))
            connectionThread=new Thread(wifi_conn_th);
        try{
            connectionThread.start();
            connectionThread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        isConnecting.release();
    }

    public void isConnected(){
        try { isConnecting.acquire(); } catch (InterruptedException e) { e.printStackTrace(); }
        isConnecting.release();
    }

    public void disconnect() throws IOException {
        this.dataIn.close();
        this.dataOut.close();
        this.usb_cli.close();
        this.usb_server.close();
        this.connectionThread.interrupt();
        this.connectionThread = null;
    }
    public void reconnect(){
        try{ this.disconnect();}
        catch (Exception e){e.printStackTrace();}
        this.connect();
        isConnected();
        readBroadCast("connection_reestablished");
    }

    public void switchConnection(String mode,String ip,int port){
        try{this.disconnect();}catch (Exception e){e.printStackTrace();}
        this.mode=mode;
        this.ip=ip;
        this.port_i=port;
        this.connect();
    }

    public void disconnected(){
        if(this.autoReconnect)
            this.reconnect();
    }

    public void send_broadcast(String content){
        send_broadcast(content,"");
    }
    public void send_broadcast(String header,String content){
        if(dataOut!=null) {
            dataOut.print(sigmsg+header+sighead+content+sigmsg);
            //client1_send.flush();
        }
    }

    private int[] fetchFromList(String responseHeader){
        for(int ii=0;ii < latest_response_h.size();ii++){
            if(latest_response_h.get(ii).equals(responseHeader)) return new int[]{ii,latest_response_h.size()};
        }
        return new int[]{-1, latest_response_h.size()};
    }

    public String create_request(String header){return create_request(header,header);}
    public String create_request(String header,String responseHeader){
        try{responseClock.acquire();}catch (Exception e){e.printStackTrace();}
        String head="";
        String cnt="";
        send_broadcast(header);
        try{responseClock.tryAcquire(default_lock_time, TimeUnit.MILLISECONDS);}catch (InterruptedException e){e.printStackTrace();}
        int[] resp_l = fetchFromList(responseHeader);
        int resp_i=resp_l[0],last_l=resp_l[1];
        while(resp_i == -1){
            try{responseClock.tryAcquire(default_lock_time, TimeUnit.MILLISECONDS);}catch (InterruptedException e){e.printStackTrace();}
            try{
                if(latest_response_h.get(last_l) == responseHeader)
                    resp_i = last_l;
                else
                    last_l=latest_response_c.size();
            }catch (Exception e){}
        }
        head= latest_response_h.get(resp_i);
        cnt = latest_response_c.get(resp_i);
        return cnt;
    }

    private void holdResponse(String message){
        try{
            String[] spl = message.split(String.valueOf(sighead));
            latest_response_h.add(spl[0]);
            latest_response_c.add(spl[1]);
        }catch (Exception e){
            e.printStackTrace();
        }
        responseClock.release();
        responseClock.release();
    }

    ArrayList<SocketBroadcastEvent> broadcastListeners=new ArrayList<SocketBroadcastEvent>();
    public void hookBroadCast(SocketBroadcastEvent function) {
        broadcastListeners.add(function);
    }
    public boolean unkookBroadCast(SocketBroadcastEvent function){
        return broadcastListeners.remove(function);
    }

    private void readBroadCast(String buffer){
        for(SocketBroadcastEvent s:broadcastListeners){
            s.call(buffer);
        }
    }

    private byte[] rr_buffer = new byte[1024];
    private boolean rr_sigstart=false;
    private boolean rr_isResponse=false;
    private String rr_msg="";

    Thread receiveBroadCasts = null;
    Runnable receiveBroadCastsRunnable = new Runnable(){
        @Override
        public void run() {
            try{
                int rr_read;
                while((rr_read = dataIn.read(rr_buffer)) != -1){
                    String output = new String(rr_buffer, 0, rr_read);
                    char[] ch_output = output.toCharArray();
                    for(int iic=0;iic < ch_output.length;iic++) {
                        if (ch_output[iic] == sigmsg) {
                            if (!rr_sigstart) {
                                rr_sigstart = true;
                            }else {
                                if(rr_isResponse)
                                    holdResponse(rr_msg);
                                else
                                    readBroadCast(rr_msg);
                                rr_msg = "";
                                rr_sigstart = false;
                            }
                        }else if (ch_output[iic] == sigresp){
                            rr_isResponse = true;
                        }else if(rr_sigstart) {
                            rr_msg += ch_output[iic];
                        }else{
                            //System.out.print((int)ch_output[iic] + " ");
                        }
                    }
                }
                readBroadCast("connection_closed");
                disconnected();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    Runnable usb_conn_th = new Runnable() {
        @Override
        public void run() {
            try {
                usb_server = new ServerSocket(port_i);
                System.out.println("Servidor iniciado na porta "+port_i);
                usb_cli = usb_server.accept();
                System.out.println("Cliente conectado local "+usb_cli.getInetAddress().getHostAddress());
                dataIn = new BufferedInputStream(usb_cli.getInputStream());
                dataOut=new PrintStream(usb_cli.getOutputStream());
                send_broadcast("usb_conn_stb");
                receiveBroadCasts=new Thread(receiveBroadCastsRunnable);
                receiveBroadCasts.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    };
    Runnable wifi_conn_th = new Runnable() {
        @Override
        public void run() {
            try {
                //Toast.makeText(getApplicationContext(),"Feature not implemented (yet)",Toast.LENGTH_LONG).show();
                /*System.out.println("Aguardando conexão em "+ip+":"+port_i);
                client1 = new Socket(ip,port_i);
                System.out.println("Socket Conectado do IP "+client1.getInetAddress().getHostAddress());
                client1_send = new PrintStream(client1.getOutputStream());
                send_message(sigmsg +"wifi_conn_stb"+ sigmsg);
                hangInputBuffer.start();*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
