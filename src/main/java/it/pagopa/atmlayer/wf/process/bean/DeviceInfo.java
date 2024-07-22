package it.pagopa.atmlayer.wf.process.bean;

import java.util.Date;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Le info del dispositivo")
@JsonInclude(Include.NON_NULL)
public class DeviceInfo {

    @NotNull(message = "bankId non può essere null")
    @Schema(required = true, description = "Il codice identificativo della banca (o codice ABI)", example = "02008", format="string", maxLength = 5)
    @Size(max = 5)
    private String bankId;

    /*
     * Device branch ID.
     */
    @NotNull(message = "branchId non può essere null")
    @Schema(required = true, description = "Il codice identificativo della filiale (o codice CAB)", example = "12345", format="string", maxLength = 5)
    @Size(max = 5)
    private String branchId;

    /*
     * Device ID.
     */
    @NotNull(message = "code non può essere null")
    @Schema(description = "Il codice identificativo dello sportello ATM (Codice Sportello o S.A. del Quadro Informativo. SPE-DEF-200)", example = "0001", format="string", maxLength = 10)
    private String code;

    /*
     * Terminal ID.
     */
    @NotNull(message = "terminalId non può essere null")
    @Schema(description = "Il codice identificativo del dispositivo (o Terminal ID)", example = "ABCD1234", format="string", maxLength = 100)
    @Size(max = 100)
    private String terminalId;

    /*
     * Terminal operation timestamp.
     */
    @NotNull(message = "opTimestamp non può essere null")
    @Schema(description = "Timestamp della richiesta", implementation = Date.class, format = "timestamp", maxLength = 20)
    private Date opTimestamp;

    /*
     * Type of device.
     */
    @NotNull(message = "channel non può essere null")
    @Schema(description = "Tipo di dispositivo", enumeration = "[\"ATM\", \"KIOSK\"]", type = SchemaType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private DeviceType channel;
}
