package ch.sebi.acmf.serial.cmd;

import ch.sebi.acmf.data.Value;

import java.util.List;

/**
 * Created by Sebastian on 07.08.2017.
 */
public class SendValue extends Command {
    public static final byte SEND_VALUE = 0x6;

    private static final byte PILL_SIZE = 4;

    private static final byte VAL1_EXISTS_POS = 2;
    private static final byte CC_POS = 3;
	private static final byte VAL1_POS = 4;
	private static final byte VAL_POS = 5;

    public static final byte MAX_VALUES = 100;

    private Value[] values;

    public SendValue(List<Value> values) {
        if(values.size() >= MAX_VALUES)
            throw new IllegalArgumentException("Values is too big. The max size is " + MAX_VALUES + "!");

        this.values = values.stream().filter(value -> !value.isUndefined()).filter(value -> value.getValue() >= 0 && value.getValue() < 128).toArray(Value[]::new);
    }

    @Override
    public Byte getId() {
        return SEND_VALUE;
    }

    @Override
    public Byte getPackageCounter() {
        return (byte) (values.length*PILL_SIZE);
    }

    @Override
    public Byte[] toBytes() {
        Byte[] bytes = getTemplate();
        int currentByteIndex = 0;
        for (int i = 0; i < values.length; i++) {
            /*bytes[i*PILL_SIZE + CC_POS] = values[i].getTValue().getMsg();
            bytes[i*PILL_SIZE + VAL_POS] = values[i].getValue();*/
            bytes[currentByteIndex + VAL1_EXISTS_POS] = (byte) (values[i].getTValue().getValue1() >= 0? 1: 0);
            bytes[currentByteIndex + CC_POS] = values[i].getTValue().getMsg();
            bytes[currentByteIndex + VAL1_POS] = (byte) values[i].getTValue().getValue1();
            bytes[currentByteIndex + VAL_POS] = values[i].getValue();
            currentByteIndex += 4; //after bytes array modification, because they releay on the fact, that currentByteIndex is set to the start byte
        }
        return bytes;
    }
}
