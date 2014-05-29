package de.Alpha.nfc_alpha;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by Kern on 27.05.2014.
 */
public class WebAppInterface {
    Context mContext;
     WebView wv = MainActivity.getWV();


    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        NFCFramework framework = new NFCFramework(mContext, this);
        /*
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        */
    }
    @JavascriptInterface
    public void activateNFC() {

        //final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        //new MainActivity().openSettings(intent);
    }
    @JavascriptInterface
    public final void closeApp(){
        System.out.println("App geschlossen, da kein NFC aktiviert wird.");
    }

    @JavascriptInterface
    public void initNFC() {
        //placeholder
    }

    //run("<JavascriptMethod()>;"); zum auführen einer Javascript Methode

    public void run(final String script) {
        wv.post(new Runnable() {
            @Override
            public void run() {
                wv.loadUrl("javascript:"+script);
            }
        });
    }

    public void printdebug(String s) {
        run("debug('" + s + "');");
    }


}