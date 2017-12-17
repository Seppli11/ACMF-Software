package ch.sebi.acmf.serial.cmd;

import ch.sebi.acmf.serial.ACMF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 07.08.2017.
 */
public abstract class CommandHandler<T extends Command> {
    private T cmdInstance;

    private List<CmdListener<T>> listeners = new ArrayList<>();

    /**
     * Creates a new CommandHandler.
     * Add it to the ACMF/Reveiver with TODO
     * @param cmdInstance An instance of the type T for getting id and package counter.
     */
    public CommandHandler(T cmdInstance) {
        this.cmdInstance = cmdInstance;
    }

    /**
     * Returns the instance from the constructor. This instance exists because it's the only way how you can get the id and the package counter of the command of this handler.
     */
    public T getCmdInstance() {
        return cmdInstance;
    }

    public Byte getCmdId() {
        return cmdInstance.getId();
    }

    public Byte getCmdPackageCounter() {
        return cmdInstance.getPackageCounter();
    }

    public boolean isCommandUsable(Command cmd){
        return getCmdId() == cmd.getId() && getCmdPackageCounter() == cmd.getPackageCounter();
    }

    public void addListener(CmdListener<T> l) {
        listeners.add(l);
    }
    private void fireListeners(T cmd, ACMF acmf) {
        List<CmdListener> listenersToDelete = new ArrayList<>();
        for(CmdListener<T> l : listeners) {
            l.apply(cmd, acmf);
            if(l instanceof OneTimeCmdListener) {
                listenersToDelete.add(l);
            }
        }

        for(CmdListener l : listenersToDelete) {
            listeners.remove(l);
        }
    }

    /**
     * Tries to onverts received bytes in to a {@link Command} object.
     * If this function is able to convert id and bytes into an cmdInstance of T, the {@link #received(Command, ACMF)} function will be called
     * with the returned T. <br>
     * If The function isn't able to convert the input bytes into T, the function should return <strong>null</strong>.
     * @param id The id of a command
     * @param bytes The bytes of the command.
     * @return T if it's possible to convert id and bytes into T or <strong>null</strong> if it's not possible.
     */
    public abstract T commandUsable(byte id, Byte[] bytes);

    /**
     * Handels the command cmd.
     * @param cmd The command which was received.
     * @param acmf The {@link ACMF} which received the command.
     */
    public void received(T cmd, ACMF acmf) {
        fireListeners(cmd, acmf);
    }

    @FunctionalInterface
    private static interface CmdListener<T extends Command> {
        void apply(T cmd, ACMF acmf);
    }

    @FunctionalInterface
    public static interface OneTimeCmdListener<T extends Command> extends CmdListener<T> {

    }

    @FunctionalInterface
    public static interface NormalCmdListener<T extends Command> extends CmdListener<T> {

    }

    public @interface CmdHandler {
        Class<? extends Command> value();
    }
}
