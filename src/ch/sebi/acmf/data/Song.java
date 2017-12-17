package ch.sebi.acmf.data;

import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.IOException;

/**
 * Created by Sebastian on 24.05.2017.
 */

@XmlRootElement
@XmlType(propOrder = {"name", "templates", "midi1DeviceConfiguration", "midi2DeviceConfiguration"})
public class Song {
    private StringProperty name;

    private ObjectProperty<DeviceConfiguration> midi1DeviceConfiguration, midi2DeviceConfiguration;
    private ListProperty<Template> templates = new SimpleListProperty<>(FXCollections.observableArrayList());

    private Song() {
        this.name = new SimpleStringProperty("");
        this.midi1DeviceConfiguration = new SimpleObjectProperty<>();
        this.midi2DeviceConfiguration = new SimpleObjectProperty<>();
    }

    public Song(String name, DeviceConfiguration midi1DC, DeviceConfiguration midi2DC) {
        this.name = new SimpleStringProperty(name);
        this.midi1DeviceConfiguration = new SimpleObjectProperty<>(midi1DC);
        this.midi2DeviceConfiguration = new SimpleObjectProperty<>(midi2DC);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @XmlElement
    public String getName() {
        return name.getValue();
    }
    public void setName(String s) {
        name = new SimpleStringProperty(s);
    }

    @XmlElement
    public ObservableList<Template> getTemplates() {
        return templates.get();
    }
    public ListProperty<Template> templatesProperty() {
        return templates;
    }
    public void setTemplates(ObservableList<Template> templates) {
        this.templates.set(templates);
    }

    @XmlElement(name="midi1_dc")
    public DeviceConfiguration getMidi1DeviceConfiguration() {
        return midi1DeviceConfiguration.get();
    }
    public ObjectProperty<DeviceConfiguration> midi1DeviceConfigurationProperty() {
        return midi1DeviceConfiguration;
    }
    public void setMidi1DeviceConfiguration(DeviceConfiguration midi1DeviceConfiguration) {
        this.midi1DeviceConfiguration.set(midi1DeviceConfiguration);
    }

    @XmlElement(name = "midi2_dc")
    public DeviceConfiguration getMidi2DeviceConfiguration() {
        return midi2DeviceConfiguration.get();
    }
    public ObjectProperty<DeviceConfiguration> midi2DeviceConfigurationProperty() {
        return midi2DeviceConfiguration;
    }
    public void setMidi2DeviceConfiguration(DeviceConfiguration midi2DeviceConfiguration) {
        this.midi2DeviceConfiguration.set(midi2DeviceConfiguration);
    }

    @Override
    public String toString() {
        return name.getValue();
    }



    public static class SongConverter extends StringConverter<Song> {

        @Override
        public String toString(Song object) {
            if(object == null) return "song is null";
            return object.toString();
        }

        @Override
        public Song fromString(String string) {
            return new Song(string, new DeviceConfiguration(string),
                    new DeviceConfiguration(string));
        }
    }

    public static Song load(File song) {
        try {
            JAXBContext context = JAXBContext.newInstance(Song.class);
            Unmarshaller um = context.createUnmarshaller();
            Song s = (Song) um.unmarshal(song);
            return s;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(Song s, File saveDir) {
        if(s == null) return;
        try {
            JAXBContext context = JAXBContext.newInstance(Song.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File f = new File(saveDir.getAbsolutePath() + "/" + s.getName() + ".song");
            if(f.exists()) f.delete();
            f.createNewFile();
            m.marshal(s, f);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }
}
