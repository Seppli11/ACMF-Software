package ch.sebi.acmf.serial.cmd;

import ch.sebi.acmf.data.Template;
import javafx.scene.Parent;

import static ch.sebi.acmf.utils.ByteUtils.B;

/**
 * Created by Sebastian on 07.08.2017.
 */
public class SendTemplate extends Command {
    public static final byte SEND_TEMPLATE = 0x5;
    public static final byte SEND_TEMPLATE_PACKAGE_COUNTER = 18;

    private static final byte NAME_POS = 2;
    private static final byte MIDI_OUT_POS = 17;
    private static final byte VALUE_COUNTER_POS = 18;
    private static final byte PARENT_SONG = 19;

    private Template template;
    private boolean midi1Template;
    private byte parentSong;

    public SendTemplate(Template template, boolean midi1Template, byte parentSong) {
        this.template = template;
        this.midi1Template = midi1Template;
        this.parentSong = parentSong;
    }

    @Override
    public Byte getId() {
        return SEND_TEMPLATE;
    }

    @Override
    public Byte getPackageCounter() {
        return SEND_TEMPLATE_PACKAGE_COUNTER;
    }

    @Override
    public Byte[] toBytes() {
        Byte[] bytes = getTemplate();
        Byte[] nameBytes = B(template.getName().getBytes());
        for(int i = 0; i < nameBytes.length && i < 15; i++) {
            bytes[i+NAME_POS] = nameBytes[i]; //write name to the third byte.
    }

        bytes[MIDI_OUT_POS] = (byte) (midi1Template ? 0 : 1);
        System.out.println(template.getDefinedMidi1Values().size());
        if(midi1Template)
            bytes[VALUE_COUNTER_POS] = (byte) template.getDefinedMidi1Values().size();
        else
            bytes[VALUE_COUNTER_POS] = (byte) template.getDefinedMidi2Values().size();


        bytes[PARENT_SONG] = parentSong;
        return bytes;
    }
}
