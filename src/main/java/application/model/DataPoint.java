package application.model;

public class DataPoint {

    private String key;

    private String value;


    public DataPoint(String key, String value) {
        this.key = key.replace(" ", "");
        this.value = value.replace(" ", "");
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.trim();
    }

    @Override
    public String toString() {
        return "[" + key + "] : [" + value + "]";
    }
}
