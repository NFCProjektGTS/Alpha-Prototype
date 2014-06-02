package de.Alpha.nfc_alpha;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import static de.Alpha.nfc_alpha.MainActivity.framework;
import static de.Alpha.nfc_alpha.MainActivity.getWV;

/**
 * Created by Kern on 27.05.2014.
 */
public class WebAppInterface {
    Activity mContext;
    WebView wv = getWV();


    WebAppInterface(Activity c) {
        mContext = c;
    }


    //run("<JavascriptMethod()>;"); zum auführen einer Javascript Methode

    public void run(final String script) {
        wv.post(new Runnable() {
            @Override
            public void run() {
                wv.loadUrl("javascript:" + script);
            }
        });
    }

    @JavascriptInterface
    public void firstload() {
        MainActivity.framework = new NFCFramework(mContext, this);
        framework.installService();
        //new NFCFramework(mContext,this);
    }

    public void activeNFC() {
        framework.setEnabled(framework.checkNFC());
    }


    @JavascriptInterface
    public void writeStummschalten() {
        framework.setPayload(Operations.OPC_SILENT);
        framework.createWriteNdef(NdefCreator.muteMessage());
            framework.enableWrite();
        printDebugInfo("Schreibe Stummschalten");
    }

    @JavascriptInterface
    public void writeKontakt() {
        mContext.startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE), 1);
        //protected void onActivityResult(int requestCode, int resultCode, Intent data)
        printDebugInfo("Schreibe Kontakt");
    }


    public void showNotification(String s1, String s2) {
        run("notify(" + s1 + "," + s2 + ");");
        printDebugWarning("Notification:" + s1);
    } //S1 Überschrift / S2 Details

    public void hideNotification() {
        run("hidenotify();");
    }

    public void printDebugInfo(String s) {
        run("debug(0,'I: " + s + "');");
    }

    public void printDebugWarning(String s) {
        run("debug(1,'W: " + s + "');");
    }

    public void printDebugError(String s) {
        run("debug(2,'E: " + s + "');");
    }


}