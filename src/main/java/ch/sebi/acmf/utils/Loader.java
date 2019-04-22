package ch.sebi.acmf.utils;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.SongSet;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.data.Value;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Created by Sebastian on 24.05.2017.
 */
public class Loader {
    public static Song loadSong(File f) {


        return null;
    }

    public static  void saveSong(File f, SongSet s) {
        try {
            JAXBContext context = JAXBContext.newInstance(SongSet.class, Song.class, Template.class, Value.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(s, System.out);


        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
