package ch.sebi.acmf.serial;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.SongSet;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.data.Value;
import ch.sebi.acmf.serial.cmd.*;
import ch.sebi.acmf.ui.ACMFMenuItem;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static ch.sebi.acmf.utils.ByteUtils.B;
import static ch.sebi.acmf.utils.ByteUtils.b;

/**
 * Created by Sebastian on 20.07.2017.
 */
public class ACMF {
    //pinging


    //send data


    //live mode
    public static final byte REQUEST_LIVE_MODE = 0x7;
    public static final byte LIVE_MODE_OK = 0x8;

    public static final byte SEND_LIVE_MODE_TEMPLATE = 0x9;

    public static final byte BACK_BUTTON_PRESSED = 0xA;
    public static final byte NEXT_BUTTON_PRESSED = 0xB;

    public static final byte LIVE_MODE_STOP = 0xC;

    public static final byte ERROR_OCCURED = 0xD;

    public static final byte STILL_HERE = 0xE;

    public static final byte UNKNOWN_STATE_ERROR = 0x1;
    public static final byte UNKNOWN_CMD_ERROR = 0x2;
    public static final byte VALUE_RECEIVING_TIMEOUT_ERROR = 0x3;
    public static final byte DISPLAY_WRITE_BUFFER_ERROR = 0x4;
    public static final byte WRONG_ARGUMENTS = 0x5;

    private SerialPort port;

    private Receiver receiver = new Receiver(this);

    private Thread portOpenTestThread;

    private boolean open;

    public ACMF(SerialPort port) {
        open = port.openPort();
        if(open == false) {
            throw new IllegalArgumentException("SerialPort '" + port.getDescriptivePortName() + "' couln't not be opened.");
        }

        port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        this.port = port;
        portOpenTestThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                if (!port.isOpen()) {
                    System.err.println("Port '" + port.getDescriptivePortName() + "' is no longer open!");
                    close();
                    return;
                }
            }
        });
        portOpenTestThread.start();

        getPort().addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if(event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
                byte[] bytes = new byte[getPort().bytesAvailable()];
                getPort().readBytes(bytes, getPort().bytesAvailable());
                receiver.receivedBytes(B(bytes)); //sends the bytes to the receiver which interprets the data and calls the CommandHandlers
            }
        });
    }

    public SerialPort getPort() {
        return port;
    }


    public void sendMagicNumber() {
        SerialPort port = getPort();
        //int r = port.writeBytes(b(bytes), bytes.length);
        int r = 1;
        try {
            OutputStream out = port.getOutputStream();
            //out.write(b(bytes));
            out.write(new byte[]{0, 0,   0, 0,   0, 0,   0, 0,   0, 0});//10 "0" are the magic number
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(getPort().getDescriptivePortName() + ": out> Magicnumber: 0 0   0 0   0 0   0 0   0 0");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send(@NotNull Command cmd) {
        SerialPort port = getPort();
        Byte[] bytes = cmd.toBytes();
        //int r = port.writeBytes(b(bytes), bytes.length);
        int r = 1;
        try {
            OutputStream out = port.getOutputStream();
            out.write(b(bytes));
            //out.write(new byte[]{1});
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(getPort().getDescriptivePortName() + ": out>" + Arrays.toString(b(bytes)));
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendSongSet(SongSet ss, ACMFMenuItem i) {
        sendSongs(ss.songs, i);
        sendTemplatsAndValues(ss.songs, i);
    }
    private void sendSongs(List<Song> songs, ACMFMenuItem item) {
        for(int i = 0; i < songs.size(); i++) {
            Song s = songs.get(i);
            item.setExportingSong(s);
            item.setProgress((float) (i+1)/songs.size());
            send(new SendSong(s));
        }
        send(new FinishedTransmittingData());
    }
    private void sendTemplatsAndValues(List<Song> songs, ACMFMenuItem item) {
       // float percentPerSong = 80/songs.size();
        for (int si = 0; si < songs.size(); si++) {
            Song s = songs.get(si);
            //float percentPerTemplate = percentPerSong/s.getTemplates().size();
            for(int ti = 0; ti < s.getTemplates().size(); ti++) {
                Template t = s.getTemplates().get(ti);
               // item.setProgress(20 + (si*percentPerSong/songs.size()) + (ti * percentPerTemplate / s.getTemplates().size()));
                item.setExportingTemplate(s, t);
                send(new SendTemplate(t,true, (byte) si));
                item.setExportingValues(s, t);
                send(new SendValue(t.getDefinedMidi1Values()));

                send(new SendTemplate(t,false, (byte) si));
                send(new SendValue(t.getDefinedMidi2Values()));
            }
        }
    }

    public <T extends Command> void send(Command cmd, T expectedReturnCmd, CommandHandler.OneTimeCmdListener<T> action) {
        send(cmd, expectedReturnCmd, action, 0, null);
    }

        /**
		 * Sends a command and waits for the expectedReturnCmd to be send from the acmf.
		 * If it is send the {@link ch.sebi.acmf.serial.cmd.CommandHandler.OneTimeCmdListener#received(Command, ACMF)}
		 * will be called. <br><br>
		 *
		 * If the command takes longer than the specified timout to come back, the timeoutlistener will be called. If the command comes later,
		 * the action's received function still will be called.<br><br>
		 *
		 * If timeout is less or equals to zero, the checking thread won't be started and thus the timeoutlistener won't be called.
		 *
		 * @param cmd the command to send
		 * @param expectedReturnCmd the expected command from the acmf as an answer
		 * @param action the action if the expected command comes back
		 * @param timeout the timeout when the timeoutlistener should be called
		 * @param timeoutListener the timeout listener
		 * @param <T> the type of the expected command
		 */
    public <T extends Command> void send(Command cmd, T expectedReturnCmd, CommandHandler.OneTimeCmdListener<T> action, long timeout, TimeoutListener timeoutListener) {
        final boolean[] gotCmd = {false};
        receiver.addOneTimeCmdListener(expectedReturnCmd, new CommandHandler.OneTimeCmdListener<T>() {
            @Override
            public void apply(T cmd, ACMF acmf) {
                action.apply(cmd, acmf);
                gotCmd[0] = true;
            }
        });
        if(timeout > 0) { //only starts the thread if the timeout is greater than 0
            new Thread(() -> {
                long startTime = System.currentTimeMillis();
                while (!gotCmd[0]) {
                    if (System.currentTimeMillis() - startTime > timeout) {
                        if(timeoutListener != null) {
                            timeoutListener.timeout(this);
                            return;
                        }
                    }
                }
            }).start();
        }
        send(cmd);
    }

    public void close() {
        port.closePort();
        acmfList.remove(this);
        portOpenTestThread.interrupt();
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public boolean isOpen() {
        return open;
    }

    @FunctionalInterface
    public static interface TimeoutListener {
        void timeout(ACMF acmf);
    }

    //static part
    //acmf finder
    private static ObservableList<ACMF> acmfList = FXCollections.observableArrayList();
    private static List<SerialPort> openPortList = new ArrayList<>();

    private static Executor threadPool = Executors.newCachedThreadPool();
    public static void searchACMFs() {
        List<SerialPort> acmfPorts = acmfList.stream().map(acmf -> acmf.getPort()).collect(Collectors.toList());

        List<SerialPort> notAcmfPorts = Arrays.stream(SerialPort.getCommPorts())
                .filter(serialPort -> !acmfPorts.contains(serialPort))
                .filter(serialPort -> !openPortList.contains(serialPort))
                .collect(Collectors.toList());

        for(SerialPort port : notAcmfPorts) {
            System.out.println("ACMF Search> seach port " + port.getDescriptivePortName());
           // port.setFlowControl(SerialPort.FLOW_CONTROL_DSR_ENABLED);
            if(port.openPort()) {
                System.out.println("ACMF Search> port " + port.getDescriptivePortName() + " was opened.");
                openPortList.add(port);
                ACMF acmf = new ACMF(port);
                acmf.getReceiver().addOneTimeCmdListener(new Ping(), new CommandHandler.OneTimeCmdListener() {
					@Override
					public void apply(Command cmd, ACMF acmf) {
						System.out.println("ACMF Search> found " + acmf.getPort().getDescriptivePortName());
						acmfList.add(acmf);
						openPortList.remove(acmf);
					}
				});
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                acmf.sendMagicNumber();
                /*acmf.send(new Ping(), new Ping(), (cmd, acmf1) -> {
                    acmfList.add(acmf);
                    openPortList.remove(port);
                    System.out.println("ACMF Search> found " + acmf.getPort().getDescriptivePortName());
                }, 7000, acmf1 -> { //timeout of 5000 miliseconds
					acmf.close();
					openPortList.remove(port);
					System.out.println("ACMF Search> triped Ping timout! (Port: " + acmf.getPort().getDescriptivePortName() + ")");
				});*/
                /*threadPool.execute(() -> {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(openPortList.remove(port)) {
                        acmf.close();
                        System.out.println("ACMF Search> port " + port.getDescriptivePortName() + " was closed due to a timeout");
                    }
                });*/
            } else {
                System.out.println("ACMF Search> could not open the port " + port.getDescriptivePortName());
            }
        }
    }

    public static ObservableList<ACMF> getAcmfList() {
        return acmfList;
    }
}