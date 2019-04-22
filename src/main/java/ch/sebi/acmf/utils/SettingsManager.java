package ch.sebi.acmf.utils;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Created by Sebastian on 26.05.2017.
 */
public class SettingsManager {
    public static final String DEVICE_CONFIGURATION_DIRECTORY_ID = "DEVICE CONFIGURATION DIRECTORY";
    public static File DEVICE_CONFIGURATIO_DIRECTORY;

    public static final String SONG_DIRECTORY_ID = "SONG DIRECTORY";
    public static String SONG_DIRECTORY;

    public static Preferences preferences;

    static {
        reload();
    }

    public static void reload() {
        preferences = Preferences.userRoot().node(SettingsManager.class.getName());

        DEVICE_CONFIGURATIO_DIRECTORY = new File(preferences.get(DEVICE_CONFIGURATION_DIRECTORY_ID, System.getProperty("user.dir") + File.separator + "device_configurations"));
        if(!DEVICE_CONFIGURATIO_DIRECTORY.exists() || !DEVICE_CONFIGURATIO_DIRECTORY.isDirectory()) {
            DEVICE_CONFIGURATIO_DIRECTORY.mkdirs();
        }

        SONG_DIRECTORY = preferences.get(SONG_DIRECTORY_ID, "");
    }
}
