package com.musss.core.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.DeviceIdentifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MusUtil {

    public static String getAndroidID(Context context){
        String id;
        if(DeviceID.supportedOAID(context)){
            id = DeviceIdentifier.getOAID(context);
            if(isIdEmpty(id)){
                id = DeviceID.getAndroidID(context);
            }
        } else {
            id = DeviceID.getAndroidID(context);
        }
        return id;
    }

    public static String getFormatAndroidID(Context context){
        String androidID = getAndroidID(context);
        if(!TextUtils.isEmpty(androidID)){
            androidID = androidID.replaceAll("[^a-zA-Z0-9]", "");
        }
        return androidID;
    }

    private static boolean isIdEmpty(String id){
        return !TextUtils.isEmpty(id) && TextUtils.isEmpty(id
                .replace("-", "")
                .replace("_", "")
                .replace("0", "")
        );

    }


    public static boolean copyToClipboard(Context context, String copyStr) {
        try {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<String> fileReadLines(File file){
        List<String> list = new ArrayList<>();
        try {
            if(file != null && file.exists()){
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while (true){
                    line = reader.readLine();
                    if(line == null){
                        break;
                    }
                    list.add(line);
                }
            }
        } catch (Exception ignored){
        }
        return list;
    }

    public static void writeLines(File file, List<String> lines){
        try {
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            for(String line : lines){
                fileWriter.write(line+"\n");
            }
            fileWriter.close();
        } catch (Exception ignored){
        }
    }
}
