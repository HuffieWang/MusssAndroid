package com.musss.core.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.musss.core.R;
import com.musss.core.config.MusApi;
import com.musss.core.entity.MusEntity;
import com.musss.core.retrofit.MusCallback;
import com.musss.core.retrofit.MusRetrofit;
import com.musss.core.util.MusCrashHandler;
import com.musss.core.util.MusUtil;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class MusLogcatActivity extends AppCompatActivity {

    Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat);

        uploadButton = findViewById(R.id.btnUpload);


        uploadButton.setOnClickListener(view -> {
            setButtonEnable(false);
            upload();
        });

        List<String> strings = MusUtil.fileReadLines(MusCrashHandler.getInstance().getTraceFile());
        StringBuilder builder = new StringBuilder();
        for(String s : strings){
            builder.append(s).append("\n");
        }
        ((TextView)(findViewById(R.id.tv_log))).setText(builder);
    }

    private void upload(){
        File file = MusCrashHandler.getInstance().getTraceFile();
        if(file == null || !file.exists()){
            onCancel();
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), body)
                .setType(MultipartBody.FORM)
                .build();
        MusRetrofit.getApi(MusApi.class).upload(MusUtil.getAndroidID(this), multipartBody.parts()).enqueue(new MusCallback<MusEntity>() {
            @Override
            public void onResponse(Response<MusEntity> response) {
                onSuccess();
            }

            @Override
            public void onFailure(Throwable t) {
                onFail(t.getMessage());
            }
        });
    }

    private void onCancel(){
        toast("日志为空");
        setButtonEnable(true);
    }

    private void onSuccess(){
        toast("上传成功");
        setButtonEnable(true);
    }

    private void onFail(String msg){
        if(!TextUtils.isEmpty(msg)){
            toast(msg);
            if(msg.contains("复制")){
                MusUtil.copyToClipboard(this, MusUtil.getAndroidID(this));
            }
        }
        setButtonEnable(true);
    }

    private void setButtonEnable(boolean isEnable){
        if(isEnable){
            uploadButton.setText("上传日志");
            uploadButton.setEnabled(true);
        } else {
            uploadButton.setText("上传中");
            uploadButton.setEnabled(false);
        }
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}