package com.objectdynamics.deckbuttons.util;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.io.*;

public class HandleCacheFile {

    public static JSONObject getFile(File cachedir, String filename){
        File config_file = new File(cachedir,filename);
        String str;
        try{
            FileInputStream fiS = new FileInputStream(config_file);
            byte[] dat = new byte[(int) config_file.length()];
            fiS.read(dat);
            fiS.close();
            str=new String(dat,"UTF-8");
        }catch (Exception e){
            return null;
        }

        try{
            return new JSONObject(str);
        }catch (Exception e){
            return null;
        }
    }

    public static void saveConfig(File cachedir,JSONObject json,String filename) throws IOException {
        File conf_file = new File(cachedir,filename);
        FileWriter fileWriter = new FileWriter(conf_file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        bw.write(json.toString());
        fileWriter.flush();
        bw.close();
        fileWriter.close();
    }
}
