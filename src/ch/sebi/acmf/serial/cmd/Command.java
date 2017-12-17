package ch.sebi.acmf.serial.cmd;

import ch.sebi.acmf.serial.Receiver;

/**
 * Created by Sebastian on 07.08.2017.
 */
public abstract class Command {

    public Command() {
        //Receiver.addCommandToReceivers(this);
    }

    public abstract Byte getId();
    public abstract Byte getPackageCounter();

    /**
     * @return Returns this command in byte form for sending it to the arduino.
     */
    public abstract Byte[] toBytes();

    /**
     * @return Returns a Byte array with the needed length for the id, the packageCounter and the package.
     * Thus the length of the array is <code>2 + the packageCounter</code> ( <code>2 +</code> because of the id and the packageCounterByte).
     */
    public Byte[] getTemplate() {
        Byte[] bytes = new Byte[2 + getPackageCounter()];
        bytes[0] = getId();
        bytes[1] = getPackageCounter();
        for(int i = 2; i < bytes.length; i++) {
            bytes[i] = 0;
        }
        return bytes;
    }

    /**
     * If not overridden, this function returns null. If it returns null, the {@link ch.sebi.acmf.serial.Receiver} won't register an {@link CommandHandler}
     * and if this commend would be received from the Arduino, the receiver would complain about not able to handle this command.
     * @return null if not overridden.
     */
    public CommandHandler getCommandHandler() {
        return null;
    }
}
