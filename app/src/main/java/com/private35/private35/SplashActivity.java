package com.private35.private35;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
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
 * Created by sunxipeng on 2016/10/13.
 */
public class SplashActivity extends Activity {

    private WebView webView;
    private static String url = "http://app.pk555.com/";
    android.os.Handler handler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 3) {

                full(false);
                tv_splash.setVisibility(View.GONE);

                //startActivity(new Intent(SplashActivity.this,MainActivity.class));
                webView.setVisibility(View.VISIBLE);
            }

        }
    };
    private TextView tv_splash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        full(true);
        setContentView(R.layout.activity_splash);

        tv_splash = (TextView) findViewById(R.id.tv_splash);
        webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.addJavascriptInterface(this, "webtest");
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {


            public void onPageFinished(WebView paramWebView, String paramString) {
                super.onPageFinished(paramWebView, paramString);
                paramWebView.loadUrl("javascript:(function(){  var objs = document.getElementsByTagName(\"img\");   for(var i=0;i<objs.length;i++){     objs[i].onclick=function(){          window.webtest.jsInvokeJava(this.src);       }  }})()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView paramWebView, String paramString, Bitmap paramBitmap) {
                if (paramString.startsWith("mqqwpa")) {
                    paramWebView.stopLoading();
                    Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse(paramString));
                    SplashActivity.this.startActivity(localIntent);
                    return;
                }
                super.onPageStarted(paramWebView, paramString, paramBitmap);
            }
        });

        Message message = new Message();
        message.what = 3;
        handler.sendEmptyMessageDelayed(3, 3000);

    }

    Bitmap mBitmap;

    @JavascriptInterface
    public void jsInvokeJava(String paramString)
            throws Exception {


        if ((paramString.equals("http://app.pk555.com/Public/home/images/bar1.png"))) {
            Log.i("songe", "被点击的图片地址为：" + paramString);

            startActivity(new Intent(SplashActivity.this,ImageActivity.class));
            /*byte[] arrayOfByte = getImage("http://app.pk555.com/Public/home/images/57063619676c7.jpg");
            if (arrayOfByte != null) {
                this.mBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
                savebitmap(this.mBitmap);
            }*/
        }
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

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            } else {
                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyWebViewDownLoadListener implements DownloadListener {

        private String downloadurl;

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {

            Log.d("SplashActivity", url);
            String string[] = url.split("/");
            for (String str : string) {

                if (str.contains(".apk")) {

                    downloadurl = str;
                }
            }
            Toast.makeText(SplashActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir("download", downloadurl);
            request.setDestinationInExternalPublicDir(getPackageName() + "/myDownLoad", downloadurl);
            long appid = downloadManager.enqueue(request);

        }

    }

    private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
