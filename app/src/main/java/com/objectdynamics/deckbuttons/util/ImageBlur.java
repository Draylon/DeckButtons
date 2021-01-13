package com.objectdynamics.deckbuttons.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.Vector;

public class ImageBlur {


    public static void blurView(Context appCtx,ImageView targetView, Integer radius) {
        blurView(appCtx,targetView,targetView,radius);
    }
    public static void blurView(Context appCtx, Bitmap source, ImageView targetView,Integer radius){
        Bitmap blurred = blurRenderScript(appCtx,source, radius); // radius 25
        targetView.setImageBitmap(blurred);
    }
    public static void blurView(Context appCtx,ImageView sourceView, ImageView targetView, Integer radius){
        System.out.println(targetView.getDrawable());
        try{
            Bitmap bmp = null;
            if(sourceView.getDrawable() instanceof ColorDrawable)
                bmp = getCDBitmap(((ColorDrawable)sourceView.getDrawable()),sourceView.getWidth(),sourceView.getHeight());
            else if(sourceView.getDrawable() instanceof VectorDrawableCompat)
                bmp = getVDCBitmap((VectorDrawableCompat)sourceView.getDrawable());
            else
                bmp = ((BitmapDrawable)sourceView.getDrawable()).getBitmap();
            //Bitmap bmp = ((BitmapDrawable)targetView.getDrawable()).getBitmap();
            //Bitmap bmp = ((BitmapDrawable)((LayerDrawable)targetView.getDrawable()).getDrawable(0)).getBitmap();
            if(bmp == null) throw new Exception("Cannot cast to BitmapDrawable");
            blurView(appCtx,bmp,targetView,radius);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Bitmap getVDCBitmap(VectorDrawableCompat vectorDrawable){
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
    private static Bitmap getCDBitmap(ColorDrawable colorDrawable,int width,int height){
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        colorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        colorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];
        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
