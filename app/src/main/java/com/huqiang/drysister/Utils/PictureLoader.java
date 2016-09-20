package com.huqiang.drysister.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by huqiang on 2016/9/21.
 */
public class PictureLoader {
    private  ImageView LoadImg;
    private String imgUrl;
    private byte[] picByte;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0x123){
                if (picByte!=null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte,0,picByte.length);
                    LoadImg.setImageBitmap(bitmap);
                }
            }
        }
    };

    public void load(ImageView imageView, String imgUrl){
        this.LoadImg = imageView;
        this.imgUrl = imgUrl;
        Drawable drawable = imageView.getDrawable();
        if (drawable!=null && drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap!=null &&!bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(imgUrl);
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000);
                    if (connection.getResponseCode() == 200){
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream  baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int length = -1;
                        while ((length= inputStream.read(bytes))!=-1){
                            baos.write(bytes,0,length);
                        }
                        picByte = baos.toByteArray();
                        inputStream.close();
                        baos.close();
                        handler.sendEmptyMessage(0x123);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    };
}
