package com.objectdynamics.deckbuttons.data;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import com.objectdynamics.deckbuttons.R;
import com.objectdynamics.deckbuttons.ui_adapters.GridFillAdapter;
import com.objectdynamics.deckbuttons.ui_components.CustomPanel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Panel {

    private String name;
    private Image bg;

    private Context ctx;
    private CustomPanel view;
    private GridFillAdapter gridFillAdapter;
    private JSONObject panel_data;
    private PanelButton[] buttons;
    private JSONArray buttons_json;
    private boolean fillGrid;
    private int rows,cols;
    private JSONArray buttons_staged_colors;

    public Panel(Context ctx, JSONObject panel_data) throws JSONException {
        this.ctx=ctx;
        this.panel_data=panel_data;
        this.buttons_json = panel_data.getJSONArray("button_list");

        this.fillGrid=panel_data.getBoolean("fillGrid");
        this.rows=panel_data.getInt("rows");
        this.cols=panel_data.getInt("cols");

        if(this.fillGrid){
            PanelButton[] temp_buttons = new PanelButton[this.buttons_json.length()];
            for (int iic = 0; iic < this.buttons_json.length(); iic++)
                temp_buttons[iic] = new PanelButton(ctx, this.buttons_json.getJSONObject(iic));

            this.buttons = new PanelButton[this.rows*this.cols];
            int iic=0;
            for(int iicx=0;iicx < this.rows;iicx++)
                for(int iicy=0;iicy < this.cols;iicy++) {
                    int current_index = ((iicx*this.cols)+iicy);
                    if(iic < temp_buttons.length){
                        if(iicx == temp_buttons[iic].getPosition()[0] && iicy == temp_buttons[iic].getPosition()[1]) {
                            this.buttons[current_index] = temp_buttons[iic];
                            iic++;
                        }else
                            this.buttons[current_index] = new PanelButton(ctx, null);
                    }else
                        this.buttons[current_index] = new PanelButton(ctx, null);
                }
        }else {
            this.buttons = new PanelButton[this.buttons_json.length()];
            for (int iic = 0; iic < this.buttons_json.length(); iic++)
                this.buttons[iic] = new PanelButton(ctx, this.buttons_json.getJSONObject(iic));
        }

        gridFillAdapter = new GridFillAdapter(ctx,this.buttons);
        this.view = new CustomPanel(ctx,panel_data.getString("name"),gridFillAdapter);

        this.view.setColumns(this.cols);
    }

    public void setButtonSizes(Activity thread) throws InterruptedException {
        int gwidth,gheight;
        final GridView v1 = this.view.getGrid();
        final Semaphore vtree_lock = new Semaphore(1,true);
        try { vtree_lock.acquire(); } catch (InterruptedException e) { e.printStackTrace(); }
        v1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //System.out.println(v1.getX()+" | "+v1.getWidth()+" | "+v1.getY()+" | "+v1.getHeight()+" | "+v1.getId()+" | "+v1.getWindowVisibility()+" | "+v1.getColumnWidth());
                vtree_lock.release();
            }
        });
        vtree_lock.acquire();
        vtree_lock.release();
        vtree_lock.release();
        //===   Grid dimensions ready
        //========================
        gwidth=v1.getWidth() - v1.getPaddingLeft() - v1.getPaddingRight();
        gheight=v1.getHeight() - v1.getPaddingTop() - v1.getPaddingBottom();

        if(gwidth <= 0 || gheight <= 0) return;
        final int half_spacing = (int)((v1.getHorizontalSpacing() + v1.getVerticalSpacing())/4);
        final int med = (int)Math.min( (gwidth/this.cols),(gheight/this.rows) );
        final PanelButton[] bts = this.buttons;
        thread.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PanelButton bt:bts)
                    bt.getView().setLayoutParams(new AbsListView.LayoutParams(med-2*half_spacing,med-2*half_spacing));
            }
        });
    }

    public CustomPanel getView() {return view;}

    public PanelButton[] getButtons() { return buttons; }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
}
