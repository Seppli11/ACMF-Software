package ch.sebi.acmf.data;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sebastian on 24.05.2017.
 */
@XmlType(propOrder = {"name", "midi1Values", "midi2Values"})
public class Template {
    private StringProperty name = new SimpleStringProperty("");

    @XmlElement(name = "midi1")
    private ListProperty<Value> midi1Values = new SimpleListProperty<>(FXCollections.observableArrayList());
    @XmlElement(name = "midi2")
    private ListProperty<Value> midi2Values = new SimpleListProperty<>(FXCollections.observableArrayList());

    private Template() {
    }

    public Template(String name) {
        setName(name);
    }

    public String getName() {
        return name.getValue();
    }
    public void setName(String s) {
        name = new SimpleStringProperty(s);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObservableList<Value> getMidi1Values() {
        return midi1Values.get();
    }

    public ListProperty<Value> midi1ValuesProperty() {
        return midi1Values;
    }

    public ObservableList<Value> getMidi2Values() {
        return midi2Values.get();
    }

    public ListProperty<Value> midi2ValuesProperty() {
        return midi2Values;
    }

    public List<Value> getDefinedMidi1Values() {
        return midi1Values.stream().filter(value -> !value.isUndefined()).collect(Collectors.toList());
    }
    public List<Value> getDefinedMidi2Values() {
        return midi2Values.stream().filter(value -> !value.isUndefined()).collect(Collectors.toList());
    }

}
