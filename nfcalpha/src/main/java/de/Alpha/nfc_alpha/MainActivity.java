package de.Alpha.nfc_alpha;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.FileInputStream;

public class MainActivity extends Activity {

    /// static, in case is is used nearly everywhere
    public static NFCFramework framework;
    private static WebView wv;
    private WebAppInterface wai;

    public static WebView getWV() {
        return wv;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
// set all null to handle rotate config
        this.framework = null;
        this.wv = null;
        this.wai = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (framework != null) {
            if (framework.checkNFC()) {
                framework.uninstallService();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (framework != null) {
            framework.resolveIntent(getIntent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setIntent(new Intent());
        if (framework != null) {
            if (framework.checkNFC()) {
                framework.installService();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv = (WebView) findViewById(R.id.webview);
        wai = new WebAppInterface(this);
        wv.addJavascriptInterface(wai, "Android");
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String url = "http://nfc.net16.net/";
        wv.loadUrl(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();

                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();
                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                AssetFileDescriptor fd;
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                try {
                    fd = getContentResolver().openAssetFileDescriptor(uri, "r");
                    FileInputStream fis = fd.createInputStream();
                    byte[] buf = new byte[(int) fd.getDeclaredLength()];
                    fis.read(buf);
                    framework.setPayload(new String(buf));
                    Toast.makeText(this, "Contact: " + name + " selected to write on NFC-Tag!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load Contact: " + name, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        }
        if (!framework.getPayload().equals("")) {
            framework.createWriteNdef(NdefCreator.vCard(framework.getPayload()));
            framework.enableWrite();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }


}
