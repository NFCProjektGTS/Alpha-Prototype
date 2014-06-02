package de.Alpha.nfc_alpha;

import android.content.Context;
import android.media.AudioManager;

import java.nio.ByteBuffer;

/**
 * Created by Noli on 30.05.2014.
 */
public class Utils {

    public static String convertByteArrayToHexString(byte[] array) {
        StringBuilder hex = new StringBuilder(array.length * 2);
        for (byte b : array) {
            hex.append(String.format("%02X ", b));
        }
        return hex.toString();
    }

    public static long convertByteArrayToDecimal(byte[] array) {
        ByteBuffer bb = ByteBuffer.wrap(array);
        return bb.getLong();
    }

    public static void toggleSilent(Context ctx) {
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        if (am.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

    }
}