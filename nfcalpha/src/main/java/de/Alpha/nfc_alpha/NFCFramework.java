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


    NFCFramework(Context caller) {
        try {
            System.out.println("Initialzing NFC Framework");
            mNfcAdapter = NfcAdapter.getDefaultAdapter(caller);
            if (mNfcAdapter != null) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}