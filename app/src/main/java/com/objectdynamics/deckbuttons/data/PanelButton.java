package com.objectdynamics.deckbuttons.data;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.objectdynamics.deckbuttons.ui_components.CustomButton;
import com.objectdynamics.deckbuttons.util.comms.SocketSendBroadcast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PanelButton {

    private Context ctx;
    private JSONObject button_data;
    private CustomButton view;
    private String name;
    private String bg;
    private Action action;
    private int[] position = new int[2];
    public PanelButton(Context ctx, JSONObject button_data, SocketSendBroadcast<String> sendBuffer) throws JSONException{
        this.ctx = ctx;
        this.button_data = button_data;
        if(button_data!= null){
            this.action = new Action(ctx,this,button_data.getJSONObject("action"),sendBuffer);
            this.name = button_data.getString("name");
            JSONArray positions = button_data.getJSONArray("position");
            for(int iic=0;iic < positions.length();iic++)
                position[iic]=positions.getInt(iic);
        }else{
            this.name="Unused Button";
            this.position=new int[]{-1,-1};
        }

        this.view = new CustomButton(ctx,this.action,this.name);
        if(button_data==null) {
            this.view.getTitle().setTextColor(Color.DKGRAY);
            this.view.setAllClickable(false);
        }
        this.view.setLayoutParams(new RelativeLayout.LayoutParams(80, 80));
    }

    public CustomButton getView() { return view; }
    public String getName() { return name; }
    public int[] getPosition() { return position; }
}
