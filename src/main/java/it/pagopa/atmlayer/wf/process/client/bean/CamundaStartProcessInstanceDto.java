package it.pagopa.atmlayer.wf.process.client.bean;

import java.util.List;
import java.util.Map;


public class CamundaStartProcessInstanceDto {

    private Map<String, Object> variables;

    private String businessKey;

    private String caseInstanceId;

    private List<Object> startInstructions;

    private boolean skipCustomListeners;

    private boolean skipIoMappings;
    
    private boolean withVariablesInReturn = false;

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getCaseInstanceId() {
        return caseInstanceId;
    }

    public void setCaseInstanceId(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }

    public List<Object> getStartInstructions() {
        return startInstructions;
    }

    public void setStartInstructions(List<Object> startInstructions) {
        this.startInstructions = startInstructions;
    }

    public boolean isSkipCustomListeners() {
        return skipCustomListeners;
    }

    public void setSkipCustomListeners(boolean skipCustomListeners) {
        this.skipCustomListeners = skipCustomListeners;
    }

    public boolean isSkipIoMappings() {
        return skipIoMappings;
    }

    public void setSkipIoMappings(boolean skipIoMappings) {
        this.skipIoMappings = skipIoMappings;
    }

    public boolean isWithVariablesInReturn() {
        return withVariablesInReturn;
    }

    public void setWithVariablesInReturn(boolean withVariablesInReturn) {
        this.withVariablesInReturn = withVariablesInReturn;
    }
}
