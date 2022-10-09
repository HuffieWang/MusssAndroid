package com.musss.core.util;

import android.content.Context;
import android.os.Build;

import com.musss.core.config.MusEnv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MusCrashHandler implements Thread.UncaughtExceptionHandler {

    private volatile static MusCrashHandler musCrashHandler;

    private Thread.UncaughtExceptionHandler defaultHandler;
    private File traceFile;

    private MusCrashHandler() {
    }

    public static MusCrashHandler getInstance() {
        if (musCrashHandler == null) {
            synchronized (MusCrashHandler.class) {
                if (musCrashHandler == null) {
                    musCrashHandler = new MusCrashHandler();
                }
            }
        }
        return musCrashHandler;
    }

    public void init(Context context) {
        traceFile = new File(context.getCacheDir().getPath(),"trace.txt");
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public File getTraceFile() {
        return traceFile;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if(traceFile != null){
            dumpExceptionToFile(e);
        }
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(t, e);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void dumpExceptionToFile(Throwable e) {
        try {
            File dir = traceFile.getParentFile();
            if(dir == null || (!dir.exists() && !dir.mkdirs())) {
                return;
            }
            if(traceFile.exists() && traceFile.length() > MusEnv.LOG_MAX_SIZE && !traceFile.delete()){
                return;
            }
            if(!traceFile.exists() && !traceFile.createNewFile()){
                return;
            }
            long timeMillis = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeMillis));
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(traceFile, true)));
            pw.print(time + " | " + Build.MODEL + " | Android " + Build.VERSION.RELEASE + "\n");
            e.printStackTrace(pw);
            pw.close();
        } catch (IOException ignored) {
        }
    }
}


