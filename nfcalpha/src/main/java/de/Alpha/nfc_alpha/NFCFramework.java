package de.Alpha.nfc_alpha;

import android.app.PendingIntent;
import android.content.Context;
import android.nfc.NfcAdapter;

/**
 * Created by Noli on 27.05.2014.
 */
public class NFCFramework {
    protected NfcAdapter mNfcAdapter;
    protected PendingIntent mNfcPendingIntent;
    protected boolean WriteMode;


    NFCFramework(Context caller, WebAppInterface wai) {
        wai.printDebugInfo("Initialzing NFC Framework");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(caller);
        if (mNfcAdapter != null) {
            wai.printDebugInfo("Success!: " + mNfcAdapter.toString());
            if (mNfcAdapter.isEnabled()){
                wai.printDebugInfo("NFC is enabled!");
            }else {
                wai.printDebugInfo("NFC is disabled");
                wai.printDebugInfo("opening NFC activation Dialog");
                wai.run("NFCDialog();");
            }
        } else {
            wai.printDebugError("NFC Hardware not detected");
        }

    }




}
