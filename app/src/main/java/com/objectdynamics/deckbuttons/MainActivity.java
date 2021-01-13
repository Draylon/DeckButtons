package com.objectdynamics.deckbuttons;

import android.content.Intent;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.objectdynamics.deckbuttons.ui_components.PanelsView;
import com.objectdynamics.deckbuttons.util.HandleCacheFile;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN); // Hide nav bar
        setContentView(R.layout.activity_main);

        //============ READING ADDR FROM CACHE =================

        String ip,port;
        JSONObject config_array = HandleCacheFile.getFile(getApplicationContext().getCacheDir(),"connection_config.json");
        if(config_array == null)
            try {config_array = new JSONObject("{}"); } catch (JSONException e) { e.printStackTrace(); }
        try{ ip = config_array.getString("ip");}
        catch (Exception e){ip="127.0.0.1";}
        try{ port = config_array.getString("port");}
        catch (Exception e){port="5818";}

        if(ip.length() >= 8)
            ((TextView)findViewById(R.id.ip_addr_box)).setText(ip);
        if(port != null)
            ((TextView)findViewById(R.id.port_addr_box)).setText(port);

        config_array.remove("port");
        config_array.remove("ip");
        try {
            config_array.put("ip",ip);
            config_array.put("port",port);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            HandleCacheFile.saveConfig(getApplicationContext().getCacheDir(),config_array,"connection_config.json");
        } catch (IOException e) {
            System.err.println("Failed Saving conn_config!!!");
            e.printStackTrace();
        }

        //=================================

        Button usb_sel_bt = (Button) findViewById(R.id.usb_sel);
        Button wifi_vis = (Button) findViewById(R.id.wifi_sel);
        Button wifi_sel_bt = (Button) findViewById(R.id.w_log_button);

        final Intent open_panels = new Intent(MainActivity.this, PanelsView.class);

        usb_sel_bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),"Selected USB (defaulting)",Toast.LENGTH_LONG).show();
                open_panels.putExtra("mode","usb");
                open_panels.putExtra("ip","127.0.0.1");
                open_panels.putExtra("port","5818");
                MainActivity.this.startActivity(open_panels);
                MainActivity.this.finish();
            }
        });
        wifi_vis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lmod = findViewById(R.id.mod_expand);
                if(lmod.getVisibility() == View.VISIBLE) lmod.setVisibility(View.GONE);
                else  lmod.setVisibility(View.VISIBLE);
            }
        });
        wifi_sel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.ip_addr_box);
                TextView tp = (TextView) findViewById(R.id.port_addr_box);
                //Toast.makeText(v.getContext(),"Selected WIFI with "+tv.getText()+":"+tp.getText(),Toast.LENGTH_LONG).show();
                open_panels.putExtra("mode","wifi");
                open_panels.putExtra("ip",tv.getText());
                open_panels.putExtra("port",tp.getText());
                MainActivity.this.startActivity(open_panels);
                MainActivity.this.finish();
            }
        });

        usb_sel_bt.performClick();
    }


}