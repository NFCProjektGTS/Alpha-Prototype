package de.Alpha.nfc_alpha;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by Kern on 27.05.2014.
 */
public class WebAppInterface {
    Context mContext;
     WebView wv = MainActivity.getWV();

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
         run("test();");
    }

    @JavascriptInterface
    public void initNFC() {

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


}