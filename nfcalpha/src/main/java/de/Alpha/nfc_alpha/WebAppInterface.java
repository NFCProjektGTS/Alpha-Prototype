package de.Alpha.nfc_alpha;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import static de.Alpha.nfc_alpha.MainActivity.*;

/**
 * Created by Kern on 27.05.2014.
 */
public class WebAppInterface {
    Context mContext;
     WebView wv = getWV();


    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        NFCFramework framework = new NFCFramework(mContext, this);


        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

    }
    @JavascriptInterface
    public void activateNFC() {
        printDebugInfo("NFC Einstllungen geöffnet zum aktivieren");
        final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mContext.startActivity(intent);

    }
    @JavascriptInterface
    public final void closeApp(){
        printDebugWarning("App geschlossen, da kein NFC aktiviert wird.");
        //wv.destroy();
        showNotification("NFC nicht aktiviert!","Wenn NFC nicht aktiviert ist können Teile der App nicht genutzt werden."); //TODO FIXEN [INFO:CONSOLE(1)] "Uncaught SyntaxError: Unexpected identifier", source: http://nfc.net16.net/ (1)

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
                wv.loadUrl("javascript:"+script);
            }
        });
    }
    @JavascriptInterface
    public void firstload() {
        new NFCFramework(mContext,this);
    }


    public void showNotification(String s1,String s2){     //S1 Überschrift / S2 Details
        run("notify('"+s1+"','"+s2+"');");
        printDebugWarning("Notification:"+s1);
    }
    public void hideNotification(){run("hidenotify();");}
    public void printDebugInfo(String s) {run("debug(0,'I: " + s + "');");}
    public void printDebugWarning(String s) {
        run("debug(1,'W: " + s + "');");
    }
    public void printDebugError(String s) {
        run("debug(2,'E: " + s + "');");
    }

    public void setNFCinfo(String type,String manufacture,float size,String data, boolean readonly,String rest1, String rest2){
     run("TagInfo('"+type+"','"+manufacture+"','"+size+"','"+data+"','"+readonly+"','"+rest1+"','"+rest2+"');");
    }

}