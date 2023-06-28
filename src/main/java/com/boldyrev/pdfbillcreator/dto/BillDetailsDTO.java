package com.boldyrev.pdfbillcreator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillDetailsDTO {

    @NotNull(message = "Не указан номер счёта")
    @Min(message = "Номер счёта должен быть больше 1", value = 1)
    @JsonProperty("number")
    private Long billNumber;

    @NotNull(message = "Не указана дата")
    @JsonProperty("date")
    private LocalDate billDate;

    @NotNull(message = "Не указаны данные получателя")
    @JsonProperty("recipient_cred")
    private String recipientCredentials;

    @NotNull(message = "Не указан банк получателя")
    @JsonProperty("bank_cred")
    private String bankCredentials;

    @NotNull(message = "Не указан исполнитель")
    private String carrier;

    @NotNull(message = "Не указана организация")
    private String customer;

    @NotNull(message = "Не указаны данные заказчика")
    @JsonProperty("customer_cred")
    private String customerCredentials;

    @NotNull(message = "Не указан маршрут")
    private String route;

    @NotNull(message = "Не указана стоимость")
    @Min(message = "Стоимость должна быть больше 0", value = 1)
    private Integer cost;

    @NotNull(message = "Не указан подписант")
    private String signatory;

    @NotNull(message = "Не указана неообходимость подписи")
    @JsonProperty("signed")
    private Boolean signed;
}
