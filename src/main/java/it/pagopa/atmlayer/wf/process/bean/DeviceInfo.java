package it.pagopa.atmlayer.wf.process.bean;

import java.time.Instant;
import java.util.Date;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceInfo {

    @Schema(required = true, description = "Il codice identificativo della banca (o codice ABI)", example = "02008")
    private String bankId;

    /*
     * Device branch ID.
     */
    @Schema(required = true, description = "Il codice identificativo della filiale (o codice CAB)", example = "12345")
    private String branchId;

    /*
     * Device ID.
     */
    @Schema(description = "Il codice identificativo dello sportello ATM (Codice Sportello o S.A. del Quadro Informativo. SPE-DEF-200)", example = "0001")
    private String code;

    /*
     * Terminal ID.
     */
    @Schema(description = "Il codice identificativo del dispositivo (o Terminal ID)", example = "ABCD1234")
    private String terminalId;

    /*
     * Terminal operation timestamp.
     */
    @Schema(description = "Timestamp della richiesta", format = "timestamp", pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm")
    @JsonProperty("opTimestamp")
    private Date opTimestamp;

    /*
     * Type of device.
     */
    @Schema(description = "Identificativo del tipo di device")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private DeviceType deviceType;

}
