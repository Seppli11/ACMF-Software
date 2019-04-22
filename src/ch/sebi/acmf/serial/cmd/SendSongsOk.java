package ch.sebi.acmf.serial.cmd;

/**
 * Created by Sebastian on 08.08.2017.
 */
public class SendSongsOk extends Command {
    public static final byte SEND_SONGS_OK = 0x3;
    public static final byte SEND_SONGS_OK_PACKAGE_COUNTER = 0;

    @Override
    public Byte getId() {
        return SEND_SONGS_OK;
    }

    @Override
    public Byte getPackageCounter() {
        return SEND_SONGS_OK_PACKAGE_COUNTER;
    }

    @Override
    public Byte[] toBytes() {
        throw new IllegalStateException("SendSongsOk Command isn't intended to be sent!");
    }

    @Override
    public CommandHandler getCommandHandler() {
        return new CommandHandler(this) {
            @Override
            public Command commandUsable(byte id, Byte[] bytes) {
                if(id == getId() && bytes.length == getPackageCounter())
                    return new SendSongsOk();
                return null;
            }
        };
    }
}
