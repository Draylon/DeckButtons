package com.objectdynamics.deckbuttons.ui_components;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.objectdynamics.deckbuttons.R;

public class SplashScreensAnimation {

    private static class AnimationRunnable implements Runnable {
        protected Activity fixedActivity;
        public AnimationRunnable(Activity activity){
            this.fixedActivity=activity;
        }
        @Override
        public void run() {}
    }

    public static void hideConnection(Activity activity){
        Thread animation=new Thread(new AnimationRunnable(activity) {
            @Override
            public void run() {
                final ImageView animbg = this.fixedActivity.findViewById(R.id.animate_transparent_bg);
                if(animbg == null) return;
                final LinearLayout limg = this.fixedActivity.findViewById(R.id.loading_img);
                final AlphaAnimation ap1 = new AlphaAnimation(1.0f,0.0f);
                ap1.setDuration(750);
                ap1.setFillAfter(true);
                this.fixedActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        limg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        animbg.startAnimation(ap1);
                    }
                });
            }
        });
        animation.start();
    }
    public static void showConnection(Activity activity){
        Thread animation=new Thread(new AnimationRunnable(activity){
            @Override
            public void run() {
                final ImageView animbg = this.fixedActivity.findViewById(R.id.animate_transparent_bg);
                if(animbg == null) return;
                final LinearLayout limg = this.fixedActivity.findViewById(R.id.loading_img);
                final LinearLayout rimg = this.fixedActivity.findViewById(R.id.reconnecting_img);
                final LinearLayout eimg = this.fixedActivity.findViewById(R.id.editor_img);
                final AlphaAnimation ap1 = new AlphaAnimation(0.0f,1.0f);
                ap1.setDuration(750);
                ap1.setFillAfter(true);
                this.fixedActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rimg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        eimg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        limg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(750).start();
                        animbg.startAnimation(ap1);
                    }
                });
            }
        });
        animation.start();
    }

    public static void showReconnecting(Activity activity){
        Thread animation=new Thread(new AnimationRunnable(activity){
            @Override
            public void run() {
                final ImageView animbg = this.fixedActivity.findViewById(R.id.animate_transparent_bg);
                if(animbg == null)
                    return;
                final LinearLayout limg = this.fixedActivity.findViewById(R.id.loading_img);
                final LinearLayout rimg = this.fixedActivity.findViewById(R.id.reconnecting_img);
                final LinearLayout eimg = this.fixedActivity.findViewById(R.id.editor_img);
                final AlphaAnimation ap1 = new AlphaAnimation(0.0f,1.0f);
                ap1.setDuration(750);
                ap1.setFillAfter(true);
                this.fixedActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        limg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        eimg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        rimg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(750).start();
                        animbg.startAnimation(ap1);
                    }
                });
            }
        });
        animation.start();
    }
    public static void hideReconnecting(Activity activity){
        Thread animation=new Thread(new AnimationRunnable(activity){
            @Override
            public void run() {
                final ImageView animbg = this.fixedActivity.findViewById(R.id.animate_transparent_bg);
                if(animbg == null)
                    return;
                final LinearLayout limg = this.fixedActivity.findViewById(R.id.reconnecting_img);
                final AlphaAnimation ap1 = new AlphaAnimation(1.0f,0.0f);
                ap1.setDuration(750);
                ap1.setFillAfter(true);
                this.fixedActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        limg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        animbg.startAnimation(ap1);
                    }
                });
            }
        });
        animation.start();
    }

    public static void showEditing(Activity activity){
        Thread animation=new Thread(new AnimationRunnable(activity){
            @Override
            public void run() {
                final ImageView animbg = this.fixedActivity.findViewById(R.id.animate_transparent_bg);
                if(animbg == null)
                    return;
                final LinearLayout limg = this.fixedActivity.findViewById(R.id.loading_img);
                final LinearLayout rimg = this.fixedActivity.findViewById(R.id.reconnecting_img);
                final LinearLayout eimg = this.fixedActivity.findViewById(R.id.editor_img);
                final AlphaAnimation ap1 = new AlphaAnimation(0.0f,1.0f);
                ap1.setDuration(750);
                ap1.setFillAfter(true);
                this.fixedActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rimg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        limg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        eimg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(750).start();
                        animbg.startAnimation(ap1);
                    }
                });
            }
        });
        animation.start();
    }
    public static void hideEditing(Activity activity){
        Thread animation=new Thread(new AnimationRunnable(activity){
            @Override
            public void run() {
                final ImageView animbg = this.fixedActivity.findViewById(R.id.animate_transparent_bg);
                if(animbg == null) return;
                final LinearLayout limg = this.fixedActivity.findViewById(R.id.editor_img);
                final AlphaAnimation ap1 = new AlphaAnimation(1.0f,0.0f);
                ap1.setDuration(750);
                ap1.setFillAfter(true);
                this.fixedActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        limg.animate().scaleX(0.0f).scaleY(0.0f).setDuration(750).start();
                        animbg.startAnimation(ap1);
                    }
                });
            }
        });
        animation.start();
    }
}


/*try {
    Thread.sleep(750);
    activity.runOnUiThread(new Runnable() {
        @Override public void run() {
            ((RelativeLayout)activity.findViewById(R.id.gridContainer)).removeView(activity.findViewById(R.id.loadingSignContainer));
        }
    });
} catch (InterruptedException e) { e.printStackTrace(); }*/