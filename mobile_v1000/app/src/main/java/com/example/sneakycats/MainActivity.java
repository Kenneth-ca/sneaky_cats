package com.example.sneakycats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = new WebView(activityContext);
        WebSettings webSettings = myWebViewCat.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setContentView(myWebView);
        myWebView.loadUrl("http://fesusrocuts.tech:5001/");
    }
}
