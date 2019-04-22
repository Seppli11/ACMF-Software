package ch.sebi.acmf.serial.cmd;

import ch.sebi.acmf.data.SongSet;

/**
 * Created by Sebastian on 08.08.2017.
 */
public class SendSongsRequest extends Command{
    public static final byte SEND_SONGS_REQUEST = 0x2;
    public static final byte SEND_SONGS_REQUEST_PACKAGE_COUNTER = 1;

    public static final byte SONG_COUNTER_POS = 2;

    private SongSet songSet;


    public SendSongsRequest(SongSet songSet) {
        this.songSet = songSet;
    }

    @Override
    public Byte getId() {
        return SEND_SONGS_REQUEST;
    }

    @Override
    public Byte getPackageCounter() {
        return SEND_SONGS_REQUEST_PACKAGE_COUNTER;
    }

    @Override
    public Byte[] toBytes() {
        Byte[] bytes = getTemplate();
        bytes[SONG_COUNTER_POS] = (byte) songSet.songs.size();
        return bytes;
    }
}
