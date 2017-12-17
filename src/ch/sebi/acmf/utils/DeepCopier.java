package ch.sebi.acmf.utils;

import java.io.*;

/**
 * Created by Sebastian on 26.05.2017.
 */
public class DeepCopier {
    public static  <T> T deepCopy(T original) {
        T rObj = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(100);
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            rObj = (T) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return rObj;
    }
}
