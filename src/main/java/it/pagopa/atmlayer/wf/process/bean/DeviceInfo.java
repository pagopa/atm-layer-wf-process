package it.pagopa.atmlayer.wf.process.bean;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceInfo {

    @Schema(required = true, description = "Il codice identificativo della banca (o codice ABI)", example = "02008")
    @JsonProperty("bank_id")
    private String bankId;

    /*
     * Device branch ID.
     */
    @Schema(required = true, description = "Il codice identificativo della filiale (o codice CAB)", example = "12345")
    @JsonProperty("branch_id")
    private String branchId;

    /*
     * Device ID.
     */
    @Schema(description = "Il codice identificativo dello sportello ATM (Codice Sportello o S.A. del Quadro Informativo. SPE-DEF-200)", example = "0001")
    @JsonProperty("code")
    private String code;

    /*
     * Terminal ID.
     */
    @Schema(description = "Il codice identificativo del dispositivo (o Terminal ID)", example = "ABCD1234")
    @JsonProperty("terminal_id")
    private String termId;

    /*
     * Terminal operation timestamp.
     */
    @Schema(description = "Timestamp della richiesta", format = "timestamp", pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @JsonProperty("op_timestamp")
    private Instant opTimestamp;

    /*
     * Type of device.
     */
    @Schema(description = "Identificativo del tipo di device")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private DeviceType deviceType;

}
