package de.Alpha.nfc_alpha;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {
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

    public NFCFramework getFramework() {
        return framework;
    }

    public void setFramework(NFCFramework framework) {
        this.framework = framework;
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
        final Activity test = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    public void finish() {
        super.finish();
    }


}
