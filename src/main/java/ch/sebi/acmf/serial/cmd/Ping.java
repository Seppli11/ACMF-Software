package ch.sebi.acmf.serial.cmd;

import ch.sebi.acmf.serial.ACMF;

/**
 * Created by Sebastian on 07.08.2017.
 */
public class Ping extends Command {
    public static final byte PING = 0x1;

    public Ping() {
    }

    @Override
    public Byte getId() {
        return PING;
    }

    @Override
    public Byte getPackageCounter() {
        return 0;
    }

    @Override
    public Byte[] toBytes() {
        return getTemplate();
    }

    @Override
    public CommandHandler getCommandHandler() {
        /*return new CommandHandler(this) {
            @Override
            public Command commandUsable(byte id, Byte[] bytes) {
                if(id == getCmdId() && bytes.length == getCmdPackageCounter()) {
                    return new Ping();
                }
                return null;
            }
        };*/
      return new PingHandler(this);
    }

    @CommandHandler.CmdHandler(Ping.class)
    public class PingHandler extends CommandHandler<Ping> {

        /**
         * Creates a new CommandHandler.
         * Add it to the ACMF/Reveiver with TODO
         *
         * @param cmdInstance An instance of the type T for getting id and package counter.
         */
        public PingHandler(Ping cmdInstance) {
            super(cmdInstance);
        }

        @Override
        public Ping commandUsable(byte id, Byte[] bytes) {
            if(id == getCmdId()) {
                return new Ping();
            }
            return null;
        }

        @Override
        public void received(Ping cmd, ACMF acmf) {
            acmf.send(new Ping());
            super.received(cmd, acmf);
        }
    }
}
