package de.Alpha.nfc_alpha;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.provider.Settings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Noli on 27.05.2014.
 */
public class NFCFramework {
    protected NfcAdapter mNfcAdapter;
    protected PendingIntent mNfcPendingIntent;
    protected Tag TAG;
    protected boolean WriteMode;
    protected WebAppInterface wai;



    NFCFramework(Context caller, WebAppInterface wai) {
        this.wai = wai;
        wai.printDebugInfo("Initialzing NFC Framework");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(caller);
        if (mNfcAdapter != null) {
            wai.printDebugInfo("Success!: " + mNfcAdapter.toString());
            if (!mNfcAdapter.isEnabled()){
                wai.printDebugInfo("NFC is disabled!");
                wai.printDebugInfo("Turning On NFC Service");
                //enableNFC(caller);
                caller.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));


            }
        } else {
            wai.printDebugError("NFC Hardware not detected");
        }

    }

    private boolean enableNFC(Context context) {
        final boolean desiredState = true;

        new Thread("toggleNFC") {
            public void run() {
                wai.printDebugInfo("Setting NFC enabled state to: " + desiredState);
                boolean success = false;
                Class<?> NfcManagerClass;
                Method setNfcEnabled, setNfcDisabled;
                boolean Nfc;
                try {
                    NfcManagerClass = Class.forName(mNfcAdapter.getClass().getName());
                    setNfcEnabled = NfcManagerClass.getDeclaredMethod("enable");
                    setNfcEnabled.setAccessible(true);
                    Nfc = (Boolean) setNfcEnabled.invoke(mNfcAdapter);
                    success = Nfc;
                } catch (ClassNotFoundException e) {
                } catch (NoSuchMethodException e) {
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
                if (success) {
                    wai.printDebugInfo("Successfully changed NFC enabled state to " + desiredState);
                } else {
                    wai.printDebugError("Error setting NFC enabled state to " + desiredState);
                }
            }
        }.start();
        return false;
    }
}
