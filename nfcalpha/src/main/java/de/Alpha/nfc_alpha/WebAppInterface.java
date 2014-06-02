package de.Alpha.nfc_alpha;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.Settings;
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

    @JavascriptInterface
    public void showToast(String toast) {
    }

    @JavascriptInterface
    public void activateNFC() {
        printDebugInfo("NFC Einstllungen geöffnet zum aktivieren");
        final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public final void closeApp() {
        printDebugWarning("App geschlossen, da kein NFC aktiviert wird.");
        //wv.destroy();
        showNotification("NFC nicht aktiviert", "Wenn NFC nicht aktiviert ist können Teile der App nicht genutzt werden.");

        //TODO App schließen hier rein    geht nicht???-> new MainActivity().finish();
    }

    @JavascriptInterface
    public void initNFC() {
        //placeholder
        System.out.println("Test1");
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


    @JavascriptInterface
    public void writeStummschalten() {
        MainActivity.payload = Operations.OPC_SILENT;
        framework.createWriteNdef(NdefCreator.muteMessage());
            framework.enableWrite();
        printDebugInfo("Schreibe Stummschalten");
    }

    @JavascriptInterface
    public void writeKontakt() {
        mContext.startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE), 1);
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