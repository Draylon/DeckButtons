package com.objectdynamics.deckbuttons.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.widget.Toast;
import com.objectdynamics.deckbuttons.R;
import com.objectdynamics.deckbuttons.util.comms.SocketSendBroadcast;
import org.json.JSONException;
import org.json.JSONObject;

public class Action {
    private static int sid=0;
    public int _id;
    public int get_id() {return _id;}

    protected Context ctx;
    protected JSONObject action_data;
    protected PanelButton parent;
    SocketSendBroadcast<String>sendBuffer;
    protected String dllName;

    public Action(Context ctx, PanelButton parent, JSONObject action_data, SocketSendBroadcast<String> sendBuffer) throws JSONException {
        this._id=sid;
        sid++;

        this.ctx=ctx;
        this.action_data=action_data;
        this.parent=parent;
        this.sendBuffer=sendBuffer;
        this.dllName=action_data.getString("dll_name");
    }

    public void run(){
        //Toast.makeText(this.ctx,"Clicked "+parent.getName(),Toast.LENGTH_SHORT).show();
        this.sendBuffer.send("command",this._id+"");
    }
}