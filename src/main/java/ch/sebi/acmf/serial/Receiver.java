package ch.sebi.acmf.serial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.sebi.acmf.serial.cmd.Command;
import ch.sebi.acmf.serial.cmd.CommandHandler;
import ch.sebi.acmf.serial.cmd.Comment;
import ch.sebi.acmf.serial.cmd.Ping;
import ch.sebi.acmf.serial.cmd.SendSongsOk;
import ch.sebi.acmf.serial.cmd.error.DisplayWriteBufferError;
import ch.sebi.acmf.serial.cmd.error.UnknownCmdError;
import ch.sebi.acmf.serial.cmd.error.UnknownStateError;
import ch.sebi.acmf.serial.cmd.error.ValueReceivingTimoutError;
import ch.sebi.acmf.serial.cmd.error.WrongArgumentsError;

/**
 * Created by Sebastian on 07.08.2017.
 */
public class Receiver {
	private Logger logger = LogManager.getLogger(getClass().getName());

	private List<CommandHandler> cmdHandlers = new ArrayList<>();

	private LinkedList<Byte> receivedBytes = new LinkedList<>();
	private ReentrantLock receivedBytesLock = new ReentrantLock();


	private ACMF acmf;
	public Receiver(ACMF acmf) {
		this.acmf = acmf;
		addReceiver(this);
	}

	public void receivedBytes(Byte[] bytes) {
		receivedBytesLock.lock();
		receivedBytes.addAll(Arrays.asList(bytes));
		receivedBytesLock.unlock();
		logger.info(acmf.getPort().getDescriptivePortName() + ": in> ");
		for(Byte b : bytes) {
			logger.info(String.format("%02X ", b));
		}
		checkReceivedBytes();
	}

	public void addOneTimeCmdListener(Command cmd, CommandHandler.OneTimeCmdListener l) {
		CommandHandler h = getCommandHandler(cmd);
		if(h == null) {
			throw new IllegalArgumentException("No CommandHandlers for cmd " + cmd.getId() + " avaible.");
		}
		h.addListener(l);
	}

	public void addCmdListener(Command cmd, CommandHandler.NormalCmdListener l) {
		CommandHandler h = getCommandHandler(cmd);
		if(h == null) {
			throw new IllegalArgumentException("No CommandHandlers for cmd " + cmd.getId() + " avaible.");
		}
		h.addListener(l);
	}

	private void checkReceivedBytes() {
		receivedBytesLock.lock();

		if(receivedBytes.size() < 2) {
			return;
		}
		Byte id = receivedBytes.get(0);
		Byte packageCounter = receivedBytes.get(1);

		if(receivedBytes.size() < 2 + packageCounter) {
			return;
		}

		Byte[] bytes = new Byte[packageCounter];
		for(int i= 0; i < packageCounter; i++) {
			bytes[i] = receivedBytes.get(2 + i); //+2 because the first two bytes are the id and the packageCounter.
		}

		for(int i = 0; i < 2 + packageCounter; i++) {
			receivedBytes.pop();
		}
		receivedBytesLock.unlock();

		CommandHandler handler = null;
		Command cmd = null;
		for(CommandHandler h : cmdHandlers) {
			cmd = h.commandUsable(id, bytes);
			if(cmd != null) {
				handler = h;
				break;
			}
		}
		if(handler == null) {
			throw new IllegalStateException("No CommandHandler found for command " + id + " with " + packageCounter + " package(s).");
		}
		logger.info(acmf.getPort().getDescriptivePortName() + ": in> " + handler.getCmdInstance().getClass().getName() + " (id: " + handler.getCmdId() + " pc: " + handler.getCmdPackageCounter() + ")");
		handler.received(cmd, acmf);
	}

	/**
	 * Adds the command handler of the command. It will call the function {@link Command#getCommandHandler()}. If it returns null, it won't add it to the list.
	 * @param cmd The command to which the {@link CommandHandler} belongs.
	 */
	public void addCommand(Command cmd) {
		CommandHandler handler = cmd.getCommandHandler();
		if(handler != null) {
			cmdHandlers.add(handler);
		}
	}

	public <T extends Command> CommandHandler<T> getCommandHandler(T cmd) {
		for (CommandHandler h : cmdHandlers) {
			if(h.isCommandUsable(cmd)) {
				return h;
			}
		}
		return null;
	}

	private static List<Receiver> receivers = new ArrayList<>();
	private static List<Command> cmds = new ArrayList<>();
	public static void addCommandToReceivers(Command cmd) {
		cmds.add(cmd);
		for (Receiver r : receivers) {
			r.addCommand(cmd);
		}
	}

	private static void addReceiver(Receiver r) {
		for(Command c : cmds) {
			r.addCommand(c);
		}
		receivers.add(r);
	}
	static {
		addCommandToReceivers(new Ping());
		addCommandToReceivers(new SendSongsOk());
		addCommandToReceivers(new UnknownStateError());
		addCommandToReceivers(new UnknownCmdError());
		addCommandToReceivers(new ValueReceivingTimoutError());
		addCommandToReceivers(new DisplayWriteBufferError());
		addCommandToReceivers(new WrongArgumentsError());
		addCommandToReceivers(new Comment());
	}
}
