package ch.sebi.acmf.serial.cmd.error;

import ch.sebi.acmf.serial.cmd.Command;
import ch.sebi.acmf.serial.cmd.CommandHandler;

/**
 * Created by Sebastian on 08.08.2017.
 */
public class UnknownCmdError extends Command {
    public static final byte ERROR_OCCURED = 0xD;
    public static final byte UNKNOWN_CMD_ERROR_PACKAGE_COUNTER = 2;

    public static final byte UNKNOWN_CMD_ERROR_CODE = 0x2;

    private byte cmd;

    public UnknownCmdError(byte cmd) {
        this.cmd = cmd;
    }

    public UnknownCmdError() {
        this.cmd = 0;
    }

    @Override
    public Byte getId() {
        return ERROR_OCCURED;
    }

    @Override
    public Byte getPackageCounter() {
        return UNKNOWN_CMD_ERROR_PACKAGE_COUNTER;
    }

    @Override
    public Byte[] toBytes() {
        Byte[] bytes = getTemplate();
        bytes[3] = UNKNOWN_CMD_ERROR_CODE;
        bytes[4] = cmd;
        return bytes;
    }

    @Override
    public CommandHandler getCommandHandler() {
        return new CommandHandler(this) {
            @Override
            public Command commandUsable(byte id, Byte[] bytes) {
                if(id == getId()) {
                    if(bytes.length >=2 && bytes[0] == UNKNOWN_CMD_ERROR_CODE) {
                        System.out.println(DisplayWriteBufferError.class.getSimpleName() + ": cmd: " + cmd);
                        return new UnknownCmdError(bytes[1]);
                    }
                }
                return null;
            }
        };
    }
}
