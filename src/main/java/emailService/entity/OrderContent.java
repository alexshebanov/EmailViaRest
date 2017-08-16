package emailService.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderContent {
    public final static String TEXT_PLAIN_TYPE = "text/plain";
    public final static String TEXT_HTML_TYPE = "text/html";

    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
