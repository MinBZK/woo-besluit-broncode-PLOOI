package nl.overheid.koop.plooi.dcn.integration.test.client.actuator;

import java.util.ArrayList;
import java.util.List;

public class JolokiaRequest {

    private static final String DEFAULT_MBEAN = "org.apache.camel:context=camel-1,name=\"camel-1\",type=context";

    protected String type;
    protected final String mbean = DEFAULT_MBEAN;
    protected String operation;
    protected List<Object> arguments = new ArrayList<>();

    public String getType() {
        return type;
    }

    public String getMbean() {
        return mbean;
    }

    public String getOperation() {
        return operation;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

}
