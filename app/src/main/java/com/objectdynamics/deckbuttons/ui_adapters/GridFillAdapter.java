package com.objectdynamics.deckbuttons.ui_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.objectdynamics.deckbuttons.R;
import com.objectdynamics.deckbuttons.data.PanelButton;

import java.util.ArrayList;

public class GridFillAdapter extends BaseAdapter {
    Context context;
    PanelButton[] buttons;
    LayoutInflater inflter;
    public GridFillAdapter(Context applicationContext, PanelButton[] buttons) {
        this.context = applicationContext;
        this.buttons = buttons;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() { return buttons.length; }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = buttons[i].getView();
        //view = inflter.inflate(R.layout.custom_button, null); // inflate the layout
        /*ImageView icon = (ImageView) view.findViewById(R.id.cButton_icon); // get the reference of ImageView
        icon.setImageResource(this.buttons.get(i).sel_bg); // set logo images*/
        TextView bt_title = (TextView) view.findViewById(R.id.cButton_title);
        bt_title.setText(buttons[i].getName());
        return view;
    }
}
