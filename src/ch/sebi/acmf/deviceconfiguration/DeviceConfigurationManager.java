package ch.sebi.acmf.deviceconfiguration;

import ch.sebi.acmf.utils.SettingsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sebastian on 17.08.2017.
 */
public class DeviceConfigurationManager {
    public static ObservableList<DeviceConfiguration> deviceConfigurations;
    static {
        deviceConfigurations = readDeviceConfigDir();
    }

    public static ObservableList<DeviceConfiguration> readDeviceConfigDir() {
        File dir = SettingsManager.DEVICE_CONFIGURATIO_DIRECTORY;
        ObservableList<DeviceConfiguration> dcList = FXCollections.observableArrayList();
        for(File f : dir.listFiles()) {
            if(!f.getName().endsWith(".json")) continue;
            try {
                ObservableList<DeviceConfiguration> l = readDeviceConfigFile(f);
                List<String> names = dcList.stream().map(DeviceConfiguration::getName).collect(Collectors.toList());
                for(DeviceConfiguration dc : l) {
                    if(names.contains(dc.getName())) {
                        throw new IllegalStateException("DeviceConfiguration with the name '" + dc.getName() + "' already exists! (File: " + f.getName() + "')");
                    }
                }
                dcList.addAll(l);
            } catch (IOException e) {
                e.printStackTrace();e.printStackTrace();
            }
        }
        for(DeviceConfiguration dc : dcList) {
            System.out.println(dc.toString());
        }
        return dcList;
    }

    private static ObservableList<DeviceConfiguration> readDeviceConfigFile(File f) throws IOException {
        String content = new String(Files.readAllBytes(f.toPath()));

        JSONArray jsonArray = new JSONArray(content);

        ObservableList<DeviceConfiguration> dcList = FXCollections.observableArrayList();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            DeviceConfiguration dc = toDeviceConfiguration(obj);
            dcList.add(dc);
        }
        List<String> testednames = new ArrayList<>();
        dcList.stream().map(DeviceConfiguration::getName).forEach(s -> {
            if(testednames.contains(s))
                throw new IllegalStateException("DeviceConfiguration with the name '" + s + "' already exists in this File (File : '" + f.getName() + "')!");
            testednames.add(s);
        });

        return dcList;
    }

    private static DeviceConfiguration toDeviceConfiguration(JSONObject obj) {
        if(!obj.has("name")) throw new IllegalStateException("Attribute name is not in the JSONObject (JSONObject: '" + obj.toString() + "')");
        if(!obj.has("values")) throw new IllegalStateException("Attribute name is not in the JSONObject (JSONObject: '" + obj.toString() + "')");

        DeviceConfiguration deviceConfiguration = new DeviceConfiguration(obj.getString("name"));
        ObservableList<DeviceConfiguration.TValue> tvalues = FXCollections.observableArrayList();
        JSONArray values = obj.getJSONArray("values");
        for(int i1 = 0; i1 < values.length(); i1++) {
            JSONObject valueJsonObj = values.getJSONObject(i1);
            if(!valueJsonObj.has("name")) throw new IllegalStateException("Attribute name is not in the JSONObject (JSONObject: '" + valueJsonObj.toString() + "')");
            if(!valueJsonObj.has("type")) throw new IllegalStateException("Attribute type is not in the JSONObject (JSONObject: '" + valueJsonObj.toString() + "')");
            if(!valueJsonObj.has("msg")) throw new IllegalStateException("Attribute msg is not in the JSONObject (JSONObject: '" + valueJsonObj.toString() + "')");

            DeviceConfiguration.TValueType type = (valueJsonObj.getString("type").equals("enum")? DeviceConfiguration.TValueType.Enum: DeviceConfiguration.TValueType.Int);
            DeviceConfiguration.TValue tv = new DeviceConfiguration.TValue(valueJsonObj.getString("name"), type);

            tv.setMin((byte) 0);
            tv.setMax((byte) 127);
            tv.setMsg((byte) valueJsonObj.getInt("msg"));
            if(valueJsonObj.has("value1")) {
                tv.setValue1(valueJsonObj.getInt("value1"));
            }

            if(type == DeviceConfiguration.TValueType.Int) {
                if(!valueJsonObj.has("min")) throw new IllegalStateException("Attribute min is not in the JSONObject (JSONObject: '" + valueJsonObj.toString() + "')");
                if(!valueJsonObj.has("max")) throw new IllegalStateException("Attribute max is not in the JSONObject (JSONObject: '" + valueJsonObj.toString() + "')");
                tv.setMin((byte) valueJsonObj.getInt("min"));
                tv.setMax((byte) valueJsonObj.getInt("max"));

                if(valueJsonObj.has("display-min"))
                    tv.setDisplayMin(valueJsonObj.getInt("display-min"));
                else
                	tv.setDisplayMin(tv.getMin());

                if(valueJsonObj.has("display-max"))
                    tv.setDisplayMax(valueJsonObj.getInt("display-max"));
                else
                	tv.setDisplayMax(tv.getMax());
            } else { //enums string
                if(!valueJsonObj.has("enums")) throw new IllegalStateException("Attribute enums is not in the JSONObject (JSONObject: '" + valueJsonObj.toString() + "')");//checks if enums attribute is there
                JSONArray enumsArray = valueJsonObj.getJSONArray("enums");
                ObservableList<DeviceConfiguration.TValue.TValueStringEnum> enums = FXCollections.observableArrayList();
                for(int i2 = 0; i2 < enumsArray.length(); i2++) {
                    JSONObject enumsJsonObj = enumsArray.getJSONObject(i2);
                    if(!enumsJsonObj.has("name")) throw new IllegalStateException("Attribute name is not in the JSONObject (JSONObject: '" + enumsJsonObj.toString() + "')");
                    if(!enumsJsonObj.has("value")) throw new IllegalStateException("Attribute value is not in the JSONObject (JSONObject: '" + enumsJsonObj.toString() + "')");

                    //add new TValueStringEnum wo the enums list which will be set to the TValue tv
                    enums.add(new DeviceConfiguration.TValue.TValueStringEnum(enumsJsonObj.getString("name"), (byte) enumsJsonObj.getInt("value"), tv));
                }
                tv.setStringEnums(enums);
            }

            tvalues.add(tv);
            System.out.printf("TValue %s {type: %s, min: %d, max: %d, msg: %d, value1: %d enums: %s}\n", tv.getName(), tv.getType().toString(), tv.getMin(), tv.getMax(), tv.getMsg(), tv.getValue1(), tv.getStringEnums().toString());
        }

        deviceConfiguration.setTValuesList(tvalues);
        return deviceConfiguration;
    }
}
