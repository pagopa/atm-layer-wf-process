package it.pagopa.atmlayer.wf.process.database.dynamo.entity;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.atmlayer.wf.process.database.dynamo.service.contract.InstanceVariablesService;
import lombok.Data;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Data
@RegisterForReflection
public class InstanceVariables {
    
    private String name;

    private Object value;

    public InstanceVariables() {
    }

    public static InstanceVariables from(Map<String, AttributeValue> item) {
        InstanceVariables instanceVariables = new InstanceVariables();
        if (item != null && !item.isEmpty()) {
            instanceVariables.setName(item.get(InstanceVariablesService.INSTANCE_VARIABLES_NAME_COL).s());
            switch (item.get(InstanceVariablesService.INSTANCE_VARIABLES_VALUE_COL).type()) {
                case BOOL:
                    instanceVariables.setValue(item.get(InstanceVariablesService.INSTANCE_VARIABLES_VALUE_COL).bool());
                    break;
                case S:
                    instanceVariables.setValue(item.get(InstanceVariablesService.INSTANCE_VARIABLES_VALUE_COL).s());
                    break;
                //other cases might be implemented later
                default:
                    break;
            }
        }
        return instanceVariables;
    }
    
    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Object return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

}