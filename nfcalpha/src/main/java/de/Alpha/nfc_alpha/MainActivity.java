package de.Alpha.nfc_alpha;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.FileInputStream;

public class MainActivity extends Activity {


    /// static in case used nearly everywhere
    public static String payload;
    public static NFCFramework framework;
    static private WebView wv;
    protected PendingIntent mNfcPendingIntent;
    private WebAppInterface wai;
    private IntentFilter[] mWriteTagFilters;

    public static WebView getWV() {
        return wv;
    }

    public PendingIntent getmNfcPendingIntent() {
        return mNfcPendingIntent;
    }

    public void setmNfcPendingIntent(PendingIntent mNfcPendingIntent) {
        this.mNfcPendingIntent = mNfcPendingIntent;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                framework.getmNfcAdapter().disableForegroundDispatch(this);

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

        NdefMessage test = NdefCreator.vCard("TEST");
        wv = (WebView) findViewById(R.id.webview);
        wai = new WebAppInterface(this);
        wv.addJavascriptInterface(wai, "Android");
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String url = "http://nfc.net16.net/";
        wv.loadUrl(url);

        // WENN DIE SEITE FERTIG GELADEN IST WIRD JETZT DAS NFC FRAMEWORK AUFGEBAUT, NICHT HIER
        //>> GIBT SONNST FEHLER BEI DEBUG AUSGABEN WENN DIE DAS INTERFACE SIE NICHT WEITER GEBEN KANN
        //>>                       WebAppInterface.firstload()
        //framework = new NFCFramework(this, wai);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();

                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();
                String lookupKey = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                AssetFileDescriptor fd;
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                try {
                    fd = getContentResolver().openAssetFileDescriptor(uri,
                            "r");
                    FileInputStream fis = fd.createInputStream();
                    byte[] buf = new byte[(int) fd.getDeclaredLength()];
                    fis.read(buf);
                    payload = new String(buf);
                    Toast.makeText(this, "Contact: " + name + " selected to write on NFC-Tag!", Toast.LENGTH_LONG).show();
                    //System.out.println(payload);

                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load Contact: " + name, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        }
        if (payload != null) {
            framework.enableWrite();
            framework.createWriteNdef(NdefCreator.vCard(payload));
        }
    }

    @Override
    public void finish() {
        super.finish();
    }


}
