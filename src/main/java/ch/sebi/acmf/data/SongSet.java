package ch.sebi.acmf.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration;
import ch.sebi.acmf.utils.SettingsManager;

/**
 * Created by Sebastian on 24.05.2017.
 */
@XmlRootElement()
public class SongSet {
	private static Logger logger = LogManager.getLogger(SongSet.class.getName());
	public static SongSet currentSongSet = new SongSet();
	static {
		reload();
	}

	@XmlElement()
	public List<Song> songs;

	public SongSet() {
		songs = new ArrayList<>();
	}

	public SongSet(List<Song> songs) {
		this.songs = songs;
	}

	public static void reload() {
		File songDir = new File(SettingsManager.SONG_DIRECTORY);
		if(!songDir.isDirectory()) {
			logger.warn("SongDir has to be a directory");
			return;
		}

		Map<String, Song> songMap = new HashMap<>();
		for(File child : songDir.listFiles()) {
			if(child.getName().endsWith(".song")) {
				Song s = Song.load(child);
				songMap.put(s.getName(), s);
			}
		}


		try {
			File orderFile = new File(songDir.getAbsolutePath() + "/.songorder");
			if (!orderFile.exists() || orderFile.isDirectory()) {
				orderFile.createNewFile();
			}
			BufferedReader orderFileIn = new BufferedReader(new FileReader(orderFile));
			List<String> songNames = new ArrayList<>();
			String input;
			while((input = orderFileIn.readLine()) != null) {
				songNames.add(input);
			}

			currentSongSet = new SongSet();
			for(String name : songNames) {
				if(songMap.containsKey(name)) {
					currentSongSet.songs.add(songMap.remove(name));
				}
			}
			for(Song s : songMap.values()) {
				currentSongSet.songs.add(s);
			}
		} catch (IOException e) {
			logger.error("An io exception occured: ", e);
		}
	}

	public static void save(SongSet ss, File songDir) {
		if(!songDir.exists()) {
			songDir.mkdirs();
		}
		for(File child : songDir.listFiles()) {
			if(child.getName().endsWith(".song")) {
				child.delete();
			}
		}

		try {
			File orderFile = new File(songDir.getAbsolutePath() + "/.songorder");
			if(!orderFile.exists() || orderFile.isDirectory()) {
				orderFile.createNewFile();
			}
			PrintWriter orderFileOut = new PrintWriter(orderFile);
			for(Song s : ss.songs) {
				orderFileOut.println(s.getName());
				Song.save(s, songDir);
			}
			orderFileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean usesDeviceConfiguration(DeviceConfiguration dc) {
		if(dc == null) {
			return false;
		}
		for(Song s : songs) {
			/*if(dc.getId() == s.getMidi1DeviceConfiguration().getId())  return true;
            if(dc.getId() == s.getMidi2DeviceConfiguration().getId())  return true;*/
			if(dc.getName().equals(s.getMidi1DeviceConfiguration().getName())) {
				return true; //first midi config
			}
			if(dc.getName().equals(s.getMidi2DeviceConfiguration().getName())) {
				return true; //second midi config
			}
		}
		return false;
	}
}
