package ch.sebi.acmf.data;

import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration;
import ch.sebi.acmf.undomanager.UndoManager;
import ch.sebi.acmf.undomanager.ValueChangeAction;
import javafx.beans.property.*;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Sebastian on 24.05.2017.
 */
public class Value {
    private ObjectProperty<DeviceConfiguration.TValue> tValue = new SimpleObjectProperty<>();


    private IntegerProperty value = new SimpleIntegerProperty(-1);
    private static int nextId = 0;
    private int id;

    private BooleanProperty undefined = new SimpleBooleanProperty(true);

    private Value() {
        id = nextId++;
        value.addListener((observable, oldValue, newValue) -> {
        	if(newValue.intValue() == -1) return;
            if(newValue.intValue() > getTValue().getMax()) {
                setValue(getTValue().getMax());
            } else if(newValue.intValue() < getTValue().getMin()) {
                setValue(getTValue().getMin());
            }
		});
        value.addListener((observable, oldValue, newValue) -> {
            setUndefined(false);
        });
    }

    public Value(DeviceConfiguration.TValue tValue) {
        this();
        this.tValue = new SimpleObjectProperty<>(tValue);
        value.addListener((observable, oldValue, newValue) -> {
			if(newValue.intValue() == -1) return;
			if(newValue.intValue() > getTValue().getMax()) {
                setValue(getTValue().getMax());
            } else if(newValue.intValue() < getTValue().getMin()) {
                setValue(getTValue().getMin());
            }
			//UndoManager.MAIN.add(new ValueChangeAction(null, null, valueProperty()));
        });
        value.addListener((observable, oldValue, newValue) -> {
            setUndefined(false);
        });
    }

    public Value(Value v) {
		set(v);
    }

    @XmlElement
    public DeviceConfiguration.TValue getTValue() {
        return tValue.get();
    }
    public ObjectProperty<DeviceConfiguration.TValue> tValueProperty() {
        return tValue;
    }
    public void setTValue(DeviceConfiguration.TValue tValue) {
        this.tValue.set(tValue);
    }

    @XmlElement
    public byte getValue() {
        return (byte) value.get();
    }
    public IntegerProperty valueProperty() {
        return value;
    }
    public void setValue(byte value) {
        this.value.set(value);
    }

    @XmlElement
    public boolean isUndefined() {
        return undefined.get();
    }
    public BooleanProperty undefinedProperty() {
        return undefined;
    }
    private void setUndefined(boolean undefined) {
        this.undefined.set(undefined);
    }
    public void setUndefined() {
        //setValue((byte) 0);
        this.value.set(-1);
        setUndefined(true); //importent that it comes after the this.value.set call, because a listener of value will set undefined to false

	}

    @Override
    public String toString() {
        return "Value {" + getTValue().getName() + ", value=" + getValue() + "}";
    }

    public int getId() {
        return id;
    }

    public void set(Value v) {
    	if(v == null) return;
		this.id = v.getId();
		this.tValue.set(v.getTValue());
		this.value.set(v.getValue());
	}
}
