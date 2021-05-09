package com.objectdynamics.deckbuttons.ui_components;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.objectdynamics.deckbuttons.R;
import com.objectdynamics.deckbuttons.data.PanelController;
import com.objectdynamics.deckbuttons.util.HandleCacheFile;
import com.objectdynamics.deckbuttons.util.ImageBlur;
import com.objectdynamics.deckbuttons.util.comms.SocketBroadcastEvent;
import com.objectdynamics.deckbuttons.util.comms.SocketComms;
import com.objectdynamics.deckbuttons.util.comms.SocketInitializerRunnable;
import com.objectdynamics.deckbuttons.util.comms.SocketSendBroadcast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PanelsView extends AppCompatActivity {
    private Activity instance;

    SocketComms comms;
    private PanelController panels;

    //================================================
    //================================================

    SocketBroadcastEvent<String> hookfunction = new SocketBroadcastEvent<String>() {
        @Override
        public void call(String buffer) {
            try{
                if(buffer == null) throw new NullPointerException("Buffer returned null!!");
                String[] spl = buffer.split(String.valueOf(SocketComms.sighead));
                String header=spl[0];
                String content="";
                if(spl.length > 1) content=spl[1];
                switch (header){
                    case "connection_reestablished":
                        SplashScreensAnimation.hideReconnecting(PanelsView.this);
                        break;
                    case "attempt_reconnecting":
                        //SplashScreensAnimation.showDisconnected(PanelsView.this);
                        //Trocar esse aqui pelo connection_closed
                        break;
                    case "connection_closed":
                        SplashScreensAnimation.showReconnecting(PanelsView.this);
                        break;
                    default:
                        System.out.println("Unknown message:\nHeader:"+header+"\nContent: "+content);
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
    };

    Thread initialize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.panels_main);
        instance=this;

        //ImageBlur.blurView(getApplicationContext(),((ImageView)findViewById(R.id.animate_transparent_bg)),10);
        ImageBlur.blurView(getApplicationContext(),((ImageView)findViewById(R.id.blurTesting)),((ImageView)findViewById(R.id.animate_transparent_bg)),10);

        Intent retr_values = getIntent();
        String mode = retr_values.getStringExtra("mode");
        String ip = retr_values.getStringExtra("ip");
        if(ip==null) ip="";
        String port = retr_values.getStringExtra("port");
        if(port==null)port="0";
        int port_i = Integer.parseInt(port);

        initialize=new Thread(new SocketInitializerRunnable(mode,ip,port_i){
            @Override
            public void run() {
                SplashScreensAnimation.showConnection(PanelsView.this);
                comms = new SocketComms(getApplicationContext(),this.mode,this.ip,this.port);
                comms.connect();
                comms.hookBroadCast(hookfunction);
                SocketSendBroadcast<String> sendBuffer = new SocketSendBroadcast<String>() {
                    @Override
                    public void send(String header, String content) {
                        comms.send_broadcast(header,content);
                    }
                    @Override
                    public void send(String content) {comms.send_broadcast(content);}
                };
                try{
                    JSONObject panels_json = HandleCacheFile.getFile(getApplicationContext().getCacheDir(),"panels.json");
                    boolean forceUpdate = true;
                    if(panels_json == null || forceUpdate){
                        String panels_json_txt = comms.create_request("request_panels", "latest_panels");
                        panels_json = new JSONObject(panels_json_txt);
                        HandleCacheFile.saveConfig(getApplicationContext().getCacheDir(), panels_json, "panels.json");
                    }
                    panels = new PanelController(getApplicationContext(),panels_json,sendBuffer);
                    panels.mount(instance,(RelativeLayout)findViewById(R.id.panels_container),(RelativeLayout)findViewById(R.id.panels_bg_anims));
                    panels.setPanelButtonSizes(instance);
                    SplashScreensAnimation.hideConnection(PanelsView.this);
                }catch(JSONException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
                    System.err.println("Failed saving file");
                    e.printStackTrace();
                }catch(InterruptedException e){
                    System.err.println("Failed mounting panels");
                    e.printStackTrace();
                }
            }
        });
        initialize.start();
    }
}

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