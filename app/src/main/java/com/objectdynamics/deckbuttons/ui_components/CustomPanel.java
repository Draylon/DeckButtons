package com.objectdynamics.deckbuttons.ui_components;

import android.content.Context;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.objectdynamics.deckbuttons.R;
import com.objectdynamics.deckbuttons.data.Panel;
import com.objectdynamics.deckbuttons.ui_adapters.GridFillAdapter;

public class CustomPanel extends RelativeLayout {

    private TextView title;
    private GridView grid;
    public CustomPanel(Context context, String title, GridFillAdapter adapter) {
        super(context);
        inflate(context, R.layout.panel,this);
        this.title = ((TextView)findViewById(R.id.panel_title));
        this.title.setText(title);

        this.grid = this.findViewById(R.id.panel_grid);
        this.grid.setAdapter(adapter);
    }

    public GridView getGrid() { return this.grid; }

    public void setColumns(int cols) {
        this.grid.setNumColumns(cols);
    }
}
