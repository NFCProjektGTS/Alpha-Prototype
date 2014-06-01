package de.Alpha.nfc_alpha;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Noli on 01.06.2014.
 */
public class NdefCreator {
    public static final byte[] RTD_ANDROID_APP = "android.com:pkg".getBytes(); /* https://github.com/Mobisocial/EasyNFC/blob/master/src/main/java/mobisocial/nfc/NdefFactory.java#L39 */

    private static final String[] URI_PREFIXES = new String[]{
            "",
            "http://www.",
            "https://www.",
            "http://",
            "https://",
            "tel:",
            "mailto:",
            "ftp://anonymous:anonymous@",
            "ftp://ftp.",
            "ftps://",
            "sftp://",
            "smb://",
            "nfs://",
            "ftp://",
            "dav://",
            "news:",
            "telnet://",
            "imap:",
            "rtsp://",
            "urn:",
            "pop:",
            "sip:",
            "sips:",
            "tftp:",
            "btspp://",
            "btl2cap://",
            "btgoep://",
            "tcpobex://",
            "irdaobex://",
            "file://",
            "urn:epc:id:",
            "urn:epc:tag:",
            "urn:epc:pat:",
            "urn:epc:raw:",
            "urn:epc:",
            "urn:nfc:",
    };

    public static NdefMessage fromUrl(URL url) {
        return fromUri(url.toString());
    }

    public static NdefMessage fromUri(String uri) {
        try {
            int prefix = 0;
            for (int i = 1; i < URI_PREFIXES.length; i++) {
                if (uri.startsWith(URI_PREFIXES[i])) {
                    prefix = i;
                    break;
                }
            }
            if (prefix > 0) uri = uri.substring(URI_PREFIXES[prefix].length());
            int len = uri.length();
            byte[] payload = new byte[len + 1];
            payload[0] = (byte) prefix;
            System.arraycopy(uri.getBytes("UTF-8"), 0, payload, 1, len);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI,
                    new byte[0], payload);
            NdefRecord[] records = new NdefRecord[]{record};
            return new NdefMessage(records);
        } catch (NoClassDefFoundError e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            // no UTF-8? really?
            return null;
        }
    }

    public static NdefMessage fromText(String text, String languageCode) {
        try {
            int languageCodeLength = languageCode.length();
            int textLength = text.length();
            byte[] textPayload = new byte[textLength + 1 + languageCodeLength];
            textPayload[0] = (byte) (0x3F & languageCodeLength); // UTF-8 with the given language code length.
            System.arraycopy(languageCode.getBytes(), 0, textPayload, 1, languageCodeLength);
            System.arraycopy(text.getBytes(), 0, textPayload, 1 + languageCodeLength, textLength);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT, new byte[0], textPayload);
            NdefRecord[] records = new NdefRecord[]{record};
            return new NdefMessage(records);
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    public static final NdefMessage getEmptyNdef() {
        byte[] empty = new byte[]{};
        NdefRecord[] records = new NdefRecord[1];
        records[0] = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, empty, empty, empty);
        NdefMessage ndef = new NdefMessage(records);
        return ndef;
    }

    public NdefRecord uriRecord(String uri) {
        try {
            final byte[] uriBytes = uri.getBytes("UTF-8");
            final byte[] recordBytes = new byte[uriBytes.length + 1];
            recordBytes[0] = (byte) 0x0;
            System.arraycopy(uriBytes, 0, recordBytes, 1, uriBytes.length);
            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], recordBytes);
        } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 Unsopported!!!");
            return null;
        }
    }

    public NdefMessage vCard(String vcard) {
        try {
            byte[] uriField = vcard.getBytes(Charset.forName("US-ASCII"));
            byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
            System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload

            NdefRecord nfcRecord = new NdefRecord(
                    NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);
            return new NdefMessage(nfcRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getEmptyNdef();
    }



}
