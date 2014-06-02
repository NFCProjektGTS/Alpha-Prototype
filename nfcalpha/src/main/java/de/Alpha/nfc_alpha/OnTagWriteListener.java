package de.Alpha.nfc_alpha;

public class OnTagWriteListener {
    public static final int WRITE_OK = 0;
    public static final int WRITE_ERROR_READ_ONLY = 1;
    public static final int WRITE_ERROR_CAPACITY = 2;
    public static final int WRITE_ERROR_BAD_FORMAT = 3;
    public static final int WRITE_ERROR_IO_EXCEPTION = 4;

    public static String onTagWrite(int status) {
        switch (status) {
            case 0:
                return "WRITE_OK";
            case 1:
                return "WRITE_ERROR_READ_ONLY";
            case 2:
                return "WRITE_ERROR_CAPACITY";
            case 3:
                return "WRITE_ERROR_BAD_FORMAT";
            case 4:
                return "WRITE_ERROR_IO_EXCEPTION";
        }
        return null;
    }
}