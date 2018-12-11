package com.example.admin.bitsathy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Objects;

import static com.example.admin.bitsathy.MainActivity.isNetworkStatusAvialable;

public class CampsActivity extends AppCompatActivity {

    WebView webCamps;
    String re_url;
    boolean error_set = false;
    SwipeRefreshLayout mySwipeRefreshLayout;
    boolean swipe_state = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camps);

        this.setTitle("CAMPS");

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        webCamps = (WebView) findViewById(R.id.web_camps);
        load_url("https://camps.bitsathy.ac.in");

        mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (error_set) {
                    error_set = false;
                    load_url(re_url);

                } else {
                    load_url(re_url);
                    swipe_state = true;
                }
            }
        });

        webCamps.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void load_url(String url) {
        webCamps.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webCamps.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        if (isNetworkStatusAvialable(getApplicationContext())) {
            re_url = url;
            webCamps.loadUrl(Uri.parse(url).toString());
        } else {
            error_set = true;
            re_url = url;
            webCamps.loadUrl("file:///android_asset/index.html");

        }
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setTitle(webCamps.getTitle());
            mySwipeRefreshLayout.setRefreshing(true);
            String host = Uri.parse(url).getHost();
            //Toast.makeText(CampsActivity.this, host, Toast.LENGTH_SHORT).show();
            if (!host.equals("camps.bitsathy.ac.in") && !url.contains("file:///android_asset/")) {
                webCamps.stopLoading();
                Intent intent;
                if (host.equals("accounts.google.com")) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.google.com/o/oauth2/auth?client_id=139304109939-064cs20kcq94a7ln5ebl1p3158fj1l37.apps.googleusercontent.com&redirect_uri=https://camps.bitsathy.ac.in/CAMPS/CommonJSP/glogin.jsp&scope=email &response_type=token"));
                    startActivity(intent);
                } else if (url.contains("bitsathy.ac.in")){
                    finish();
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                finish();
            }
            super.onPageStarted(view, url, favicon);
        }



        @Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
            Log.d("CHECK", "onReceivedSslError");
            AlertDialog.Builder builder = new AlertDialog.Builder(CampsActivity.this);
            AlertDialog alertDialog = builder.create();
            String message = "Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
                case SslError.SSL_DATE_INVALID:
                    message = "The certificate date is invalid";
                    break;
                case SslError.SSL_INVALID:
                    message = "The certificate is invalid";
                    break;
            }
            message += " Do you want to continue anyway?";
            alertDialog.setTitle("SSL Certificate Error");
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            alertDialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setTitle(webCamps.getTitle());
            mySwipeRefreshLayout.setRefreshing(false);
            super.onPageFinished(view, url);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);

    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit CAMPS", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2300);
        }



}
