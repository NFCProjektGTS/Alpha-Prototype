package de.Alpha.nfc_alpha;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;

/**
 * Created by Noli on 27.05.2014.
 */
public class NFCFramework {

    protected NfcAdapter mNfcAdapter;
    protected Context caller;
    protected Tag TAG;
    protected boolean WriteMode;
    protected boolean used;
    protected WebAppInterface wai;
    protected boolean enabled = false;
    protected IntentFilter[] mTagFilters;

    NFCFramework(Context caller, WebAppInterface wai) {
        this.caller = caller;
        this.wai = wai;
        this.wai.printDebugInfo("Initialzing NFC Framework");
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(caller);
        this.enabled = checkNFC();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        this.mTagFilters = new IntentFilter[]{tagDetected};



    }

    public NfcAdapter getmNfcAdapter() {
        return mNfcAdapter;
    }

    public void setmNfcAdapter(NfcAdapter mNfcAdapter) {
        this.mNfcAdapter = mNfcAdapter;
    }

    public boolean checkNFC() {
        if (mNfcAdapter != null) {
            wai.printDebugInfo("Adapter found: " + mNfcAdapter.toString());

            if (!mNfcAdapter.isEnabled()) {
                wai.printDebugInfo("NFC is disabled");
                wai.printDebugInfo("Opening NFC Activation Dialog");
                wai.run("NFCDialog();");
                if (mNfcAdapter.isEnabled()) {
                    return true;
                }
            }
            return true;
        } else {
            wai.showNotification("NFC Hardware nicht gefunden", "Bitte stellen sie sicher das ihr Gerät NFC unterstützt.");
            //wai.printDebugError("NFC Hardware not detected");
        }
        return false;
    }

    public void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            this.wai.printDebugInfo("Tag Discovered");
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                msgs = RawNDEFContent(intent);

            }
            printTag(msgs);
        }
    }


    public byte[] rawTagData(Parcelable parc) {
        StringBuilder s = new StringBuilder();
        Tag tag = (Tag) parc;
        TAG = tag;
        byte[] id = tag.getId();
        s.append("UID In Hex: ").append(Utils.convertByteArrayToHexString(id)).append("\n");
        s.append("UID In Dec: ").append(Utils.convertByteArrayToDecimal(id)).append("\n\n");

        String prefix = "android.nfc.tech.";
        s.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            s.append(tech.substring(prefix.length()));
            s.append(", ");
        }
        s.delete(s.length() - 2, s.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                s.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                s.append("Mifare Classic type: ").append(type).append('\n');
                s.append("Mifare size: ").append(mifareTag.getSize() + " bytes").append('\n');
                s.append("Mifare sectors: ").append(mifareTag.getSectorCount()).append('\n');
                s.append("Mifare blocks: ").append(mifareTag.getBlockCount());
                }

            if (tech.equals(MifareUltralight.class.getName())) {
                s.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                s.append("Mifare Ultralight type: ").append(type);
                }
            }

        return s.toString().getBytes();
    }

    public NdefMessage[] RawNDEFContent(Intent intent) {
        byte[] empty = new byte[0];
        byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] payload = rawTagData(tag);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
        NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
        return new NdefMessage[]{msg};
    }

    public void printTag(NdefMessage[] msgs) {

        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String content = new String(payload);
        this.wai.printDebugInfo(content);
        //wai.printDebugInfo(msgs.toString());
        //System.out.println(msgs);
    }


}
