package com.objectdynamics.deckbuttons.ui_components;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.objectdynamics.deckbuttons.R;
import com.objectdynamics.deckbuttons.data.Panel;
import com.objectdynamics.deckbuttons.data.PanelButton;
import com.objectdynamics.deckbuttons.data.PanelController;
import com.objectdynamics.deckbuttons.util.HandleCacheFile;
import com.objectdynamics.deckbuttons.util.ImageBlur;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PanelsView extends AppCompatActivity {

    private Semaphore responseClock = new Semaphore(1,true);
    private Activity instance;
    private int default_lock_time=4000;
    ServerSocket usb_server;
    Socket usb_cli=null;
    InputStream dataIn=null;
    PrintStream dataOut=null;
    String mode;
    String ip;
    Integer port_i;
    private final char sigmsg = '┼';
    private final char sigend = '¤';
    private final char sighead = '¦';
    private final char sigresp = '─';
    //==============
    private JSONObject panels_data;
    private PanelController panels;

    private ArrayList<String> latest_response_h=new ArrayList<String>();
    private ArrayList<String> latest_response_c=new ArrayList<String>();

    private void send_broadcast(String data){
        if(dataOut!=null) {
            dataOut.print(sigmsg+data+sigmsg);
            //client1_send.flush();
        }
    }

    private int[] fetchFromList(String responseHeader){
        for(int ii=0;ii < latest_response_h.size();ii++){
            if(latest_response_h.get(ii).equals(responseHeader)) return new int[]{ii,latest_response_h.size()};
        }
        return new int[]{-1, latest_response_h.size()};
    }

    private String create_request(String header){return create_request(header,header);}
    private String create_request(String header,String responseHeader){
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

    private void readBroadCast(String buffer){
        System.out.println("Processing broadcast\n"+buffer);
        try{
            if(buffer == null) throw new NullPointerException("Buffer returned null!!");
            String[] spl = buffer.split(String.valueOf(sighead));
            String header=spl[0];
            String content="";
            if(spl.length > 1) content=spl[1];
            switch (header){
                default:
                    System.out.println("Unknown message:\nHeader:"+header+"\nContent: "+content);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Thread removeLoading = new Thread(new Runnable() {
        @Override
        public void run() {
            final ImageView animbg = findViewById(R.id.animate_transparent_bg);
            if(animbg == null)
                return;
            final LinearLayout limg = findViewById(R.id.loading_img);
            final AlphaAnimation ap1 = new AlphaAnimation(1.0f,0.0f);
            ap1.setDuration(750);
            ap1.setFillAfter(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    limg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                    animbg.startAnimation(ap1);
                }
            });
            try {
                Thread.sleep(750);
                runOnUiThread(new Runnable() {@Override public void run() { ((RelativeLayout)findViewById(R.id.gridContainer)).removeView(findViewById(R.id.loadingSignContainer)); }});
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    });

    //================================================
    //================================================
    //================================================
    //================================================


    Thread initialize = new Thread(new Runnable(){
        @Override
        public void run() {
            if(mode.equals("usb")){
                usb_conn_th.start();
                try { usb_conn_th.join(); } catch (InterruptedException e) { e.printStackTrace(); }
            }else if(mode.equals("wifi")){
                wifi_conn_th.start();
                try { wifi_conn_th.join(); } catch (InterruptedException e) { e.printStackTrace(); }
            }
            try{
                JSONObject panels_json = HandleCacheFile.getFile(getApplicationContext().getCacheDir(),"panels.json");
                boolean forceUpdate = true;
                if(panels_json == null || forceUpdate) {
                    String panels_json_txt = create_request("request_panels", "latest_panels");
                    panels_json = new JSONObject(panels_json_txt);
                    HandleCacheFile.saveConfig(getApplicationContext().getCacheDir(), panels_json, "panels.json");
                }
                panels = new PanelController(getApplicationContext(),panels_json);
                removeLoading.start();
                panels.mount(instance,(RelativeLayout)findViewById(R.id.panels_container),(RelativeLayout)findViewById(R.id.panels_bg_anims));
                panels.setPanelButtonSizes(instance);

            }catch (JSONException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
                System.err.println("Failed saving file");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    private byte[] rr_buffer = new byte[1024];
    private boolean rr_sigstart=false;
    private boolean rr_isResponse=false;
    private String rr_msg="";
    private boolean inputBufferThrottle=false;
    Thread receiveBroadCasts = new Thread(new Runnable(){
        @Override
        public void run() {
            try{
                int rr_read;
                while((rr_read = dataIn.read(rr_buffer)) != -1) {
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    });
    Thread usb_conn_th = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                usb_server = new ServerSocket(port_i);
                System.out.println("Servidor iniciado na porta "+port_i);
                usb_cli = usb_server.accept();
                System.out.println("Cliente conectado local "+usb_cli.getInetAddress().getHostAddress());
                dataIn = new BufferedInputStream(usb_cli.getInputStream());
                dataOut=new PrintStream(usb_cli.getOutputStream());
                send_broadcast(sigmsg +"usb_conn_stb"+ sigmsg);
                receiveBroadCasts.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    });
    Thread wifi_conn_th = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Toast.makeText(getApplicationContext(),"Feature not implemented (yet)",Toast.LENGTH_LONG).show();
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
    });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.panels_main);

        instance=this;

        //ImageBlur.blurView(getApplicationContext(),((ImageView)findViewById(R.id.animate_transparent_bg)),10);
        ImageBlur.blurView(getApplicationContext(),((ImageView)findViewById(R.id.blurTesting)),((ImageView)findViewById(R.id.animate_transparent_bg)),10);

        Intent retr_values = getIntent();
        mode = retr_values.getStringExtra("mode");
        ip = retr_values.getStringExtra("ip");
        if(ip==null) ip="";
        String port = retr_values.getStringExtra("port");
        if(port==null)port="0";
        port_i = Integer.parseInt(port);

        initialize.start();


        /*GridFillAdapter customAdapter = new GridFillAdapter(getApplicationContext(),conn_index);
        maingrid.setAdapter(customAdapter);
        maingrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity
                Intent intent = new Intent(Panels.this, PerformAction.class);
                intent.putExtra("image", logos[position]); // put image data in Intent
                startActivity(intent); // start Intent
            }
        });*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}

/*
https://stackoverflow.com/questions/9520911/java-sending-and-receiving-file-byte-over-sockets
https://abhiandroid.com/ui/gridview
https://stackoverflow.com/questions/14782901/android-how-to-handle-button-click
https://stackoverflow.com/questions/33431314/how-to-read-and-write-files-to-my-cache
https://www.devmedia.com.br/java-sockets-criando-comunicacoes-em-java/9465
https://developer.android.com/training/system-ui/navigation#java

JSON MODEL:
{
    "current":0,
    "panel_array":[
        {
            "name":"",
            "bg":"",
            "button_list":[
                {
                    "name":"button",
                    "action":{
                        ... nao sei
                    },
                    "stage":PRESS,SWITCH,STAGE,ETC.
            ],
            button_stages_colors:[]
        }
}
 */