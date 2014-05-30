package de.Alpha.nfc_alpha;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    static private WebView wv;
    protected PendingIntent mNfcPendingIntent;
    private WebAppInterface wai;
    private NFCFramework framework;

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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        if (framework != null && framework.checkNFC()) {
            framework.getmNfcAdapter().enableForegroundDispatch(this, mNfcPendingIntent, null, null);
            framework.resolveIntent(getIntent());
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
        mNfcPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        wv = (WebView) findViewById(R.id.webview);
        wai = new WebAppInterface(this);
        wv.addJavascriptInterface(wai, "Android");
        wv.setWebViewClient(new WebViewClient());
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.loadUrl("http://nfc.net16.net/");

        framework = new NFCFramework(this, wai);
    }





    @Override
    public void finish() {
        super.finish();
    }


}
