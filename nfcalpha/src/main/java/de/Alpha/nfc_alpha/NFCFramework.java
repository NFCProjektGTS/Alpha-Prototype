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
        wai.printdebug("Initialzing NFC Framework");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(caller);
        if (mNfcAdapter != null) {
            wai.printdebug("Success!: " + mNfcAdapter.toString());
            if (mNfcAdapter.isEnabled()){
                wai.printdebug("NFC is enabled!");
            }else {
                wai.printdebug("NFC is disabled");
                wai.printdebug("opening NFC activation Dialog");
                wai.run("NFCDialog();");
            }
        } else {
            wai.printdebug("Error: NFC Hardware not detected");
        }

    }




}
