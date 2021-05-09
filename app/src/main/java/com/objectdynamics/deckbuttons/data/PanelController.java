package com.objectdynamics.deckbuttons.data;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;
import com.objectdynamics.deckbuttons.util.comms.SocketSendBroadcast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

public class PanelController  {
    private Context ctx;
    private int current=0;
    private JSONArray panel_array_data;
    private ArrayList<Panel> panel_array = new ArrayList<Panel>();

    public PanelController(Context ctx, JSONObject json_panels, SocketSendBroadcast<String> sendBuffer) throws JSONException{
        this.ctx=ctx;
        this.current = json_panels.getInt("current");
        this.panel_array_data = json_panels.getJSONArray("panel_array");
        for(int ii=0;ii < panel_array_data.length();ii++){
            this.panel_array.add(new Panel(ctx, this.panel_array_data.getJSONObject(ii),sendBuffer));
        }
    }
    public int getCurrentIndex() { return current; }
    public Panel getCurrentPanel(){ return this.panel_array.get(this.current);}
    public ArrayList<Panel> getPanel_array() { return panel_array; }

    public void setPanelButtonSizes(Activity thread) throws InterruptedException {
        for(Panel pnl:this.panel_array)
            pnl.setButtonSizes(thread);
    }

    public void mount(Activity thread, final RelativeLayout panels_container, final RelativeLayout backgrounds_container) throws InterruptedException {
        final Semaphore mounting_panels = new Semaphore(1,true);
            mounting_panels.acquire();
            thread.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(Iterator<Panel> it1 = panel_array.iterator(); it1.hasNext();)
                        panels_container.addView(it1.next().getView());
                    mounting_panels.release();
                }
            });
            mounting_panels.acquire();
            mounting_panels.release();
            mounting_panels.release();
    }
}
