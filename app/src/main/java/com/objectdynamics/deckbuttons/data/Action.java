package com.objectdynamics.deckbuttons.data;

import android.content.Context;
import org.json.JSONObject;

public class Action {

    private Context ctx;
    private JSONObject action_data;
    public Action(Context ctx, JSONObject action_data){
        this.ctx=ctx;
        this.action_data=action_data;
    }
}
