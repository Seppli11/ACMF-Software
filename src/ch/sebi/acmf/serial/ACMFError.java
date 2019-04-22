package ch.sebi.acmf.serial;

/**
 * Created by Sebastian on 20.07.2017.
 */
public class ACMFError {
    public final static Byte UNKNOWN_STATE_ERROR = 0x1;
    public final static Byte UNKNOWN_CMD_ERROR = 0x2;
    public final static Byte VALUE_REVEIVING_TIMEOUT_ERROR = 0x3;
    public final static Byte DISPLAY_WRITE_BUFFER_ERROR = 0x4;
}
