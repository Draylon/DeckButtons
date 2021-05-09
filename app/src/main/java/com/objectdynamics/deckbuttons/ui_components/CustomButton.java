package com.objectdynamics.deckbuttons.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import com.objectdynamics.deckbuttons.R;
import com.objectdynamics.deckbuttons.data.Action;
import com.objectdynamics.deckbuttons.data.Panel;
import com.objectdynamics.deckbuttons.data.PanelButton;

public class CustomButton extends RelativeLayout {

    private TextView title;
    private ImageView icon;
    private Context ctx;
    private Action action;

    public CustomButton(Context context,Action action, String title) {
        this(context,action,title,null);
    }
    public CustomButton(Context context, final Action action, String title, Bitmap icon) {
        super(context);
        inflate(context, R.layout.custom_button, this);

        this.ctx=context;
        this.action = action;
        this.title = (TextView) findViewById(R.id.cButton_title);
        this.icon = (ImageView) findViewById(R.id.cButton_icon);
        setTitle(title);
        if(icon!=null) setIcon(icon);
        this.setOnClickListener(clickListener);
        this.title.setOnClickListener(clickListener);
        this.icon.setOnClickListener(clickListener);
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) { CustomButton.this.action.run(); }
    };

    public TextView getTitle() {
        return title;
    }

    public CustomButton setTitle(String title) {
        String nstring="";
        for(int ii=0;ii < title.length();ii++)
            if(ii%2==0) nstring+=title.charAt(ii);
            else nstring+=" ";
        this.title.setText(nstring);
        return this;
    }

    public ImageView getIcon() {
        return icon;
    }

    public CustomButton setIcon(Bitmap icon) {
        if(icon != null) this.icon.setImageBitmap(icon);
        return this;
    }

    public void setOnClickListener() {
    }

    public void setAllClickable(boolean b) {
        this.title.setClickable(b);
        this.icon.setClickable(b);
        this.setClickable(b);
    }
}
