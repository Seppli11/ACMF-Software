package ch.sebi.acmf.serial.cmd;

import ch.sebi.acmf.data.Song;

import static ch.sebi.acmf.utils.ByteUtils.B;

/**
 * Created by Sebastian on 07.08.2017.
 */
public class SendSong extends Command {
    public static final byte SEND_SONG = 0x4;
    public static final byte SEND_SONG_PACKAGE_COUNTER = 21;

    private static final byte NAME_POS = 2;
    private static final byte TEMPLATE_COUNTER = 22;

    private Song song;

    public SendSong(Song song) {
        this.song = song;
    }

    @Override
    public Byte getId() {
        return SEND_SONG;
    }

    @Override
    public Byte getPackageCounter() {
        return SEND_SONG_PACKAGE_COUNTER;
    }

    /**
     * SendSong command format: id (1b), packageCounter (1b), name (20b), templateCounter (1b)
     * @return
     */
    @Override
    public Byte[] toBytes() {
        Byte[] bytes = getTemplate();
        Byte[] nameBytes = B(song.getName().getBytes());
        for(int i = 0; i < nameBytes.length && i < 20; i++) {
            bytes[i+NAME_POS] = nameBytes[i]; //write name to the third byte.
        }

        bytes[TEMPLATE_COUNTER] = (byte) song.getTemplates().size();

        return bytes;
    }
}
