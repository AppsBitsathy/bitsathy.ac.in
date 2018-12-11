package com.example.admin.bitsathy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    WebView webView;
    NavigationView navigationView;
    LinearLayout v;
    ImageView splash;
    boolean swipe_state = false;
    SwipeRefreshLayout mySwipeRefreshLayout;
    String re_url = "";
    Boolean error_set = false;

    @SuppressLint("setJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        v = findViewById(R.id.include);
        splash= findViewById(R.id.splash_img);
        webView = findViewById(R.id.WebView);

        load_url("https://bitsathy.ac.in");

        webView.setDownloadListener(new DownloadListener() {
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.setVisibility(View.GONE);
                v.setVisibility(View.VISIBLE);
            }
        }, 3000);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        mySwipeRefreshLayout = this.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (error_set){
                    error_set = false;
                    load_url(re_url);

                }
                else{
                    load_url(re_url);
                    swipe_state = true;
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }else if (webView.canGoBack()){
            webView.goBack();
            return;
        }
        else if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2300);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            Toast.makeText(MainActivity.this, "Thank you", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String s = item.getTitle().toString();


        if (id == R.id.nav_home) {
            load_url("https://bitsathy.ac.in");
        } else if (id == R.id.nav_camps) {
           // load_url("https://camps.bitsathy.ac.in");
            Intent camps = new Intent(MainActivity.this,CampsActivity.class);
            startActivity(camps);

        }else if (id == R.id.bit_glance) {
            load_url("https://bitsathy.ac.in/bitglance.php");
        } else if (id == R.id.history) {
            load_url("https://bitsathy.ac.in/our-history-and-tradition.php");
        } else if (id == R.id.office_chairman) {
            load_url("https://bitsathy.ac.in/chairmans-message.php");
        } else if (id == R.id.achievements) {
            load_url("https://bitsathy.ac.in/achievements.php");
        } else if (id == R.id.initiatives) {
            load_url("https://bitsathy.ac.in/sustainability-and-green-initiatives.php");
        } else if (id == R.id.departments) {
            load_url("https://bitsathy.ac.in/departments.php");
        } else if (id == R.id.programmes) {
            load_url("https://bitsathy.ac.in/overview.php");
        } else if (id == R.id.exam) {
            load_url("https://bitsathy.ac.in/examinations.php");
        } else if (id == R.id.coe) {
            load_url("https://bitsathy.ac.in/centerofexcellence.php");
        } else if (id == R.id.admission_overview) {
            load_url("https://bitsathy.ac.in/overview.php");
        } else if (id == R.id.india_be) {
            load_url("https://bitsathy.ac.in/ind/ug/");
        } else if (id == R.id.india_me) {
            load_url("https://bitsathy.ac.in/ind/pg/");
        } else if (id == R.id.india_phd) {
            load_url("https://bitsathy.ac.in/ind/phd/");
        } else if (id == R.id.india_mba) {
            load_url("https://bitsathy.ac.in/ind/mba/");
        } else if (id == R.id.others_be) {
            load_url("https://bitsathy.ac.in/fgn/ug/");
        } else if (id == R.id.others_me) {
            load_url("https://bitsathy.ac.in/fgn/pg/");
        } else if (id == R.id.others_phd) {
            load_url("https://bitsathy.ac.in/fgn/phd/");
        } else if (id == R.id.others_mba) {
            load_url("https://bitsathy.ac.in/fgn/mba/");
        } else if (id == R.id.direct_me) {
            load_url("https://bitsathy.ac.in/ind/pg/index.php");
        } else if (id == R.id.apply) {
            load_url("https://bitsathy.ac.in/admission-contact.php");
        } else if (id == R.id.clubs) {
            load_url("https://bitsathy.ac.in/clubs.php");
        } else if (id == R.id.infra) {
            load_url("https://bitsathy.ac.in/infrastructures.php");
        } else if (id == R.id.features) {
            load_url("https://bitsathy.ac.in/unique-feature.php");
        } else if (id == R.id.entrepreneur) {
            load_url("https://bitsathy.ac.in/startups.php");
        } else if (id == R.id.placements) {
            load_url("https://bitsathy.ac.in/placement/");
        } else if (id == R.id.gal_camps) {
            load_url("https://bitsathy.ac.in/gallery.php");
        } else if (id == R.id.gal_event) {
            load_url("https://bitsathy.ac.in/eventgallery/");
        } else if (id == R.id.gal_video) {
            load_url("https://bitsathy.ac.in/video-gallery.php");
        } else if (id == R.id.learning_centre) {
            load_url("https://bitsathy.ac.in/learning-centre.php");
        } else if (id == R.id.facilities_campus) {
            load_url("https://bitsathy.ac.in/camfac/");
        } else if (id == R.id.facilities_sports) {
            load_url("https://bitsathy.ac.in/physical_edu/lab/labdetails.php");
        } else if (id == R.id.bit_cloud) {
            load_url("https://bitsathy.ac.in/bitcloud/");
        } else if (id == R.id.bus) {
            load_url("https://bitsathy.ac.in/bus.php");
        } else if (id == R.id.facilities_hostel) {
            load_url("https://bitsathy.ac.in/hostel.php");
        } else if (id == R.id.stud_counselling) {
            load_url("https://bitsathy.ac.in/students-counseling.php");
        } else if (id == R.id.workplace) {
            load_url("https://bitsathy.facebook.com/");
        } else if (id == R.id.twitter) {
            load_url("https://twitter.com/Bitsathyindia?lang=en");
        } else if (id == R.id.insta) {
            load_url("https://www.instagram.com/lifeatbit/?hl=en");
        } else if (id == R.id.fb) {
            load_url("https://www.facebook.com/bitsathyindia");
        } else if (id == R.id.youtube) {
            load_url("https://www.youtube.com/bitsathyindia?sub_confirmation=1");
        } else if (id == R.id.gplus) {
            load_url("https://plus.google.com/u/0/101833663993365987716");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("setJavaScriptEnabled")
    void load_url(String url){

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        if ( isNetworkStatusAvialable(getApplicationContext())){
            re_url = url;
            webView.loadUrl(Uri.parse(url).toString());
        }
        else {
            error_set = true;
            re_url = url;
            webView.loadUrl("file:///android_asset/index.html");

        }
    }

    //************Network State
    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                return netInfos.isConnected();
        }
        return false;
    }

    public class WebViewClient extends android.webkit.WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mySwipeRefreshLayout.setRefreshing(true);
            String host = Uri.parse(url).getHost();
            if (host.equals("camps.bitsathy.ac.in")){
                webView.stopLoading();
                Intent camps = new Intent(getApplicationContext(),CampsActivity.class);
                startActivity(camps);
            }
            else if (!host.equals("bitsathy.ac.in") && !url.contains("file:///android_asset/")){
                webView.stopLoading();
                Intent intent;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mySwipeRefreshLayout.setRefreshing(false);
                startActivity(intent);
            }

            super.onPageStarted(view, url, favicon);
            setTitle(webView.getTitle());
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        /*@Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error){
            Log.d("CHECK", "onReceivedSslError");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
        }*/

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setTitle(webView.getTitle());
            super.onPageFinished(view, url);
            mySwipeRefreshLayout.setRefreshing(false);
            swipe_state = false;


        }
    }
}
