package com.objectdynamics.deckbuttons.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.objectdynamics.deckbuttons.R;

public class CustomButton extends RelativeLayout {

    TextView title;
    ImageView icon;

    public CustomButton(Context context, String title) {
        this(context,title,null);
    }
    public CustomButton(Context context, String title, Bitmap icon) {
        super(context);
        inflate(context, R.layout.custom_button, this);

        /*int[] sets = {R.attr.cButton_title, R.attr.cButton_button};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        CharSequence title = typedArray.getText(0);
        CharSequence buyButton = typedArray.getText(1);
        typedArray.recycle();*/

        this.title = (TextView) findViewById(R.id.cButton_title);
        this.icon = (ImageView) findViewById(R.id.cButton_icon);
        setTitle(title);
        if(icon!=null) setIcon(icon);
    }

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
}
