package com.musss.core.config;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.google.gson.TypeAdapter;
import com.musss.core.activity.MusLogcatActivity;
import com.musss.core.entity.MusEntity;
import com.musss.core.retrofit.MusCallback;
import com.musss.core.retrofit.MusException;
import com.musss.core.retrofit.MusRetrofit;
import com.musss.core.util.MusCrashHandler;
import com.musss.core.util.MusUtil;
import com.tencent.mmkv.MMKV;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class MusEnv {

    public static final int LOG_MAX_SIZE = 50000;
    public static final int LOG_UPLOAD_INTERVAL = 60000;

    private static Application application;

    private static final String KEY_LOG_LAST_UPLOAD_TIME = "log_last_upload_time";

    public static void init(Application context){
        application = context;
        MMKV.initialize(application);
        initHttp();
        MusCrashHandler.getInstance().init(context);
    }

    public static void startLogcatActivity(Context context){
        context.startActivity(new Intent(context, MusLogcatActivity.class));
    }

    public static void autoUploadLog(boolean isEnable){
        if(isEnable){
            if(application == null){
                toast("自动上传取消:未初始化");
                return;
            }
            File file = MusCrashHandler.getInstance().getTraceFile();
            if(file == null || !file.exists()){
                toast("自动上传取消:文件不存在");
                return;
            }
            long lastUploadTime = MMKV.defaultMMKV().decodeLong(KEY_LOG_LAST_UPLOAD_TIME, 0);
            long currentTime = System.currentTimeMillis();
            if(currentTime < lastUploadTime + LOG_UPLOAD_INTERVAL){
                toast("自动上传取消:定时");
                return;
            }
            MMKV.defaultMMKV().encode(KEY_LOG_LAST_UPLOAD_TIME, currentTime);
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .addFormDataPart("file", file.getName(), body)
                    .setType(MultipartBody.FORM)
                    .build();
            MusRetrofit.getApi(MusApi.class).upload(MusUtil.getAndroidID(application), multipartBody.parts()).enqueue(new MusCallback<MusEntity>() {
                @Override
                public void onResponse(Response<MusEntity> response) {
                    toast("自动上传成功");
                }

                @Override
                public void onFailure(Throwable t) {
                    toast("自动上传失败");
                }
            });
        }
    }

    private static void initHttp(){
        Map<String, String> headers = new HashMap<>();

        MusRetrofit.addRetrofitProvider(MusApi.class, new MusRetrofit.Retrofit2Provider(){

            @Override
            public String getBaseUrl() {
                return MusApi.baseUrl;
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            public Object getResponse(TypeAdapter<?> adapter, String body) throws IOException, JSONException {
                JSONObject json = new JSONObject(body);
                int code = json.optInt("code", 0);
                String msg = json.optString("msg", "unknown");
                JSONObject data = json.optJSONObject("data");
                if (code != 200) {
                    throw new MusException(msg);
                } else {
                    MusEntity baseResult;
                    if(data == null){
                        data = new JSONObject();
                        JSONArray array = json.optJSONArray("data");
                        if(array != null){
                            data = new JSONObject();
                            data.put("list", array);
                        } else {
                            data.put("result", json.optString("data", ""));
                        }
                    }
                    baseResult = (MusEntity) adapter.fromJson(data.toString());
                    baseResult.setCode(code);
                    baseResult.setMsg(msg);
                    return baseResult;
                }
            }
            public long getConnectTimeout(){
                return 5;
            }

            public long getReadTimeout(){
                return 15;
            }

            public long getWriteTimeout(){
                return 15;
            }
        });
    }


    private static void toast(String msg){
//        Toast.makeText(application, msg, Toast.LENGTH_SHORT).show();
    }
}
