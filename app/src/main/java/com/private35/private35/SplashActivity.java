package com.private35.private35;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by sunxipeng on 2016/10/13.
 */
public class SplashActivity extends Activity {

    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
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
    private DownloadManager downloadManager;
    private Uri uri;
    private DownloadManager.Request request;

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
    private String downloadurl;
    class MyWebViewDownLoadListener implements DownloadListener {



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
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            uri = Uri.parse(url);
            request = new DownloadManager.Request(uri);

            //手机系统大于或等于23
            if (Build.VERSION.SDK_INT >= 23) {

                if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }else {

                    saveapk();
                }

            }else {

                saveapk();
            }

        }

    }

    private void saveapk(){

        //创建文件的下载路径
        File folder = Environment.getExternalStoragePublicDirectory(getPackageName() + "/myDownLoad");
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
        //request.setDestinationInExternalPublicDir("download", downloadurl);
        request.setDestinationInExternalPublicDir(getPackageName() + "/myDownLoad", downloadurl);

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        long appid = downloadManager.enqueue(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE &&grantResults[0] == PackageManager.PERMISSION_GRANTED){

            saveapk();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);

            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        }

    }
}
