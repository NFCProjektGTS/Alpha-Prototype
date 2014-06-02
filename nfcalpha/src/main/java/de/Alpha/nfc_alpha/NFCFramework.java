package de.Alpha.nfc_alpha;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Noli on 27.05.2014.
 */
public class NFCFramework {

    protected NfcAdapter mNfcAdapter;
    protected Activity caller;
    protected Tag wTAG;
    protected boolean WriteMode = false;
    protected boolean used;
    protected WebAppInterface wai;
    protected boolean enabled = false;
    protected IntentFilter[] mTagFilters;
    protected NdefMessage[] mCurrentNdef;
    protected NdefMessage[] mWriteNdef;
    protected OnTagWriteListener tagListener = null;

    NFCFramework(Activity caller, WebAppInterface wai) {
        this.caller = caller;
        this.wai = wai;
        this.wai.printDebugInfo("Initialzing NFC Framework");
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(caller);
        this.enabled = checkNFC();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        this.mTagFilters = new IntentFilter[]{tagDetected};


    }

    public OnTagWriteListener getTagListener() {
        return tagListener;
    }

    public void setTagListener(OnTagWriteListener tagListener) {
        this.tagListener = tagListener;
    }

    public void installService() {
        if (enabled) {
            Intent activityIntent = new Intent(caller, caller.getClass());
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent intent = PendingIntent.getActivity(caller, 0,
                    activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mNfcAdapter.enableForegroundDispatch(caller, intent, mTagFilters, null);
        }
    }

    public void uninstallService() {
        if (enabled) {
            mNfcAdapter.disableForegroundDispatch(caller);
        }
    }

    public NfcAdapter getmNfcAdapter() {
        return mNfcAdapter;
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
            if (!WriteMode) {
                if (rawMsgs != null) {
                    msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                } else {
                    msgs = RawNDEFContent(intent);

                }
                mCurrentNdef = msgs;
                printTag(mCurrentNdef);
            } else {
                wTAG = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (wTAG != null && mWriteNdef[0] != null) {
                    OnTagWriteListener writelisten = tagListener;
                    writelisten.onTagWrite(writeTag(wTAG, mWriteNdef[0]));
                }
            }
        }

    }


    public byte[] rawTagData(Parcelable parc) {
        StringBuilder s = new StringBuilder();
        Tag tag = (Tag) parc;
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
        for (NdefMessage msg : msgs) {
            for (NdefRecord rec : msg.getRecords()) {
                byte[] payload = rec.getPayload();
                String content = new String(payload);
                this.wai.printDebugInfo("Message: " + msg.toString() + " Record: " + rec.toString() + content);
            }
        }


        //wai.printDebugInfo(msgs.toString());
        //System.out.println(msgs);
    }

    private int writeTag(Tag tag, NdefMessage message) {
        try {
            int size = message.toByteArray().length;
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    wai.printDebugInfo("Tag is read-only.");
		    disableWrite();
                    return OnTagWriteListener.WRITE_ERROR_READ_ONLY;
                }
                if (ndef.getMaxSize() < size) {
                    wai.printDebugInfo("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " +
                            size + " bytes.");
		    disableWrite(); 
                    return OnTagWriteListener.WRITE_ERROR_CAPACITY;
                }

                ndef.writeNdefMessage(message);
		disableWrite();
                return OnTagWriteListener.WRITE_OK;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        disableWrite();
                        this.mWriteNdef = null;
                        return OnTagWriteListener.WRITE_OK;
                    } catch (IOException e) {
                        disableWrite(); 
                        return OnTagWriteListener.WRITE_ERROR_IO_EXCEPTION;
                    }
                } else {
		    disableWrite(); 
                    return OnTagWriteListener.WRITE_ERROR_BAD_FORMAT;
                }
            }
        } catch (Exception e) {
	    disableWrite(); 
            wai.printDebugInfo("Failed to write tag: " + e);
        }	
	disableWrite(); 
        return OnTagWriteListener.WRITE_ERROR_IO_EXCEPTION;
    }

    public void enableWrite() {
        //allow write for next NFC intent
        if (enabled) {
            if (this.wTAG != null && this.mWriteNdef != null) {
                this.WriteMode = true;
                installService();
                Toast.makeText(caller, "Writemode enabled", Toast.LENGTH_LONG).show();
            } else {
                wai.printDebugInfo("Please scan a NFC Tag to write on");
            }
        }
    }

    public void createWriteNdef(NdefMessage message) {
        this.mWriteNdef[0] = message;
    }

    public void disableWrite(){
    if(enabled){
        this.wTAG = null;
    this.mWriteNdef = null;
    this.WriteMode = false;
    uninstallService();
    Toast.makeText(caller, "Writemode disabled", Toast.LENGTH_LONG).show();
    }
    }

    public interface OnTagWriteListener {
        public static final int WRITE_OK = 0;
        public static final int WRITE_ERROR_READ_ONLY = 1;
        public static final int WRITE_ERROR_CAPACITY = 2;
        public static final int WRITE_ERROR_BAD_FORMAT = 3;
        public static final int WRITE_ERROR_IO_EXCEPTION = 4;

        public void onTagWrite(int status);
    }

}
