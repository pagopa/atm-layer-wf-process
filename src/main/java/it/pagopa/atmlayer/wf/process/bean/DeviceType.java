package it.pagopa.atmlayer.wf.process.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "La tipologia del dispositivo")
public enum DeviceType {
    ATM,
    KIOSK
}
