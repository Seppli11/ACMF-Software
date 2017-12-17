package ch.sebi.acmf.serial.cmd.error;

import ch.sebi.acmf.serial.cmd.Command;
import ch.sebi.acmf.serial.cmd.CommandHandler;

/**
 * Created by Sebastian on 08.08.2017.
 */
public class WrongArgumentsError extends Command {
    public static final byte ERROR_OCCURED = 0xD;
    public static final byte UNKNOWN_STATE_ERROR_PACKAGE_COUNTER = 4;

    public static final byte UNKNOWN_STATE_ERROR_CODE = 0x5;

    private byte cmd;
    private byte expectedArgumentLength;
    private byte actualArgumentLength;

    public WrongArgumentsError(byte cmd, byte expectedArgumentLength, byte actualArgumentLength) {
        this.cmd = cmd;
        this.expectedArgumentLength = expectedArgumentLength;
        this.actualArgumentLength = actualArgumentLength;
    }

    public WrongArgumentsError() {
        this.cmd = 0;
        this.expectedArgumentLength = 0;
        this.actualArgumentLength = 0;
    }

    @Override
    public Byte getId() {
        return ERROR_OCCURED;
    }

    @Override
    public Byte getPackageCounter() {
        return UNKNOWN_STATE_ERROR_PACKAGE_COUNTER;
    }

    @Override
    public Byte[] toBytes() {
        Byte[] bytes = getTemplate();
        bytes[3] = UNKNOWN_STATE_ERROR_CODE;
        bytes[4] = cmd;
        bytes[5] = expectedArgumentLength;
        bytes[6] = actualArgumentLength;
        return bytes;
    }

    @Override
    public CommandHandler getCommandHandler() {
        return new CommandHandler(this) {
            @Override
            public Command commandUsable(byte id, Byte[] bytes) {
                if(id == getId()) {
                    if(bytes.length >= 4 && bytes[0] == UNKNOWN_STATE_ERROR_CODE) {
                        System.out.println(WrongArgumentsError.class.getSimpleName() + ": cmd: " + cmd + " expectedArgumentLength: " + expectedArgumentLength
                                + " actualArgumentLength: " + actualArgumentLength);
                        return new WrongArgumentsError(bytes[1], bytes[2], bytes[3]);
                    }
                }
                return null;
            }
        };
    }
}
