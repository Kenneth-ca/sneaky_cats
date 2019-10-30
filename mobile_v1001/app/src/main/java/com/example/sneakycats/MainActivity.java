package com.example.sneakycats;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;


import java.util.HashMap;
import java.util.Map;

import android.provider.ContactsContract.Contacts;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.sneakycats.MESSAGE";
    public static final String EXTRA_URL_API = "http://fesusrocuts.tech/cat/";
    public static final String EXTRA_MESSAGE_NOWHATSAPP = "DON'T SEND MESSAGES BECAUSE YOU NOT INSTALLED WS";
    public static final String EXTRA_MESSAGE_YESWHATSAPP = "SEND MESSAGES INTO DB WS";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void sendMessage2(View view) {
        // Do something in response to button
        EditText et1 = (EditText) findViewById(R.id.editText);
        //et1.setText("Test 1");
        Toast.makeText(this, et1.getText().toString(), Toast.LENGTH_LONG).show();
    }
    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    /** Called when the user taps the Send button */
    public void sendMessage2(String message) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void openNavigationCats(View view) {
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            Toast.makeText(this, EXTRA_MESSAGE_YESWHATSAPP, Toast.LENGTH_LONG).show();
            openWebViewCat();
        } else sendMessage2(EXTRA_MESSAGE_NOWHATSAPP);
    }
    public void openNavigationCatsToast() {
        // Do something in response to button
        EditText et1 = (EditText) findViewById(R.id.editText);
        //et1.setText("Test 1");
        Toast.makeText(this, et1.getText().toString(), Toast.LENGTH_LONG).show();
    }
    /** Called when the user taps the Send button */
    public void openWhatsAppToMsg(View view) {
        /*Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);*/
        Log.v(TAG, "+++ openWhatsAppToMsg +++");
        //Uri uri = Uri.parse("https://wa.me/+573227309677");
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.putExtra("jid","3227309677");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);

        //Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=573227309677&text=Hello,%20from%20my%20Apps");
        //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //startActivity(intent);
    }
    
    /** Called when the user taps the Send button */
    public void openWebViewCat() {
        Intent intent = new Intent(this, NavigationCats.class);
        intent.putExtra(EXTRA_URL_API, MainActivity.EXTRA_URL_API);
        startActivity(intent);
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
