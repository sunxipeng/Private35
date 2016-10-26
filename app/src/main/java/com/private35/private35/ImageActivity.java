package com.private35.private35;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/10/26.
 */
public class ImageActivity extends Activity{

    private byte[] arrayOfByte;


    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            savebitmap(mBitmap);


        }
    };
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_iamge);

        findViewById(R.id.iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.iv_save).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            arrayOfByte = getImage("http://app.pk555.com/Public/home/images/57063619676c7.jpg");
                            if (arrayOfByte != null) {
                                mBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
                                handler.sendEmptyMessage(new Message().what = 1);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return false;
            }
        });


        findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            arrayOfByte = getImage("http://app.pk555.com/Public/home/images/57063619676c7.jpg");
                            if (arrayOfByte != null) {
                                mBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);

                                handler.sendEmptyMessage(new Message().what = 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();




            }
        });
    }



    public byte[] getImage(String paramString)
            throws Exception {
        HttpURLConnection localHttpURLConnection = (HttpURLConnection) new URL(paramString).openConnection();
        localHttpURLConnection.setConnectTimeout(5000);
        localHttpURLConnection.setRequestMethod("GET");
        InputStream localInputStream = localHttpURLConnection.getInputStream();
        if (localHttpURLConnection.getResponseCode() == 200)
            return readStream(localInputStream);
        return null;
    }

    public static byte[] readStream(InputStream paramInputStream)
            throws Exception {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte = new byte[1024];
        while (true) {
            int i = paramInputStream.read(arrayOfByte);
            if (i == -1)
                break;
            localByteArrayOutputStream.write(arrayOfByte, 0, i);
        }
        localByteArrayOutputStream.close();
        paramInputStream.close();
        return localByteArrayOutputStream.toByteArray();
    }

    private void savebitmap(Bitmap paramBitmap) {
        File localFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "sunxipeng:::::test" + System.currentTimeMillis() + ".jpg");
        if (localFile.exists())
            localFile.delete();
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            if (paramBitmap.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream)) {
                localFileOutputStream.flush();
                localFileOutputStream.close();
                Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
            }
            if (Build.VERSION.SDK_INT >= 19) {
                Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                localIntent.setData(Uri.fromFile(localFile));
                sendBroadcast(localIntent);
                return;
            }
        } catch (FileNotFoundException localFileNotFoundException) {
            while (true)
                localFileNotFoundException.printStackTrace();
        } catch (IOException localIOException) {
            while (true)
                localIOException.printStackTrace();

        }

        sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

}
