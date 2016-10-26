package com.private35.private35;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
