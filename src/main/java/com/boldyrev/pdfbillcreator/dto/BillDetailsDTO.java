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

    @NotNull(message = "�� ������ ����� �����")
    @Min(message = "����� ����� ������ ���� ������ 1", value = 1)
    @JsonProperty("number")
    private Long billNumber;

    @NotNull(message = "�� ������� ����")
    @JsonProperty("date")
    private LocalDate billDate;

    @NotNull(message = "�� ������� ������ ����������")
    @JsonProperty("recipient_cred")
    private String recipientCredentials;

    @NotNull(message = "�� ������ ���� ����������")
    @JsonProperty("bank_cred")
    private String bankCredentials;

    @NotNull(message = "�� ������ �����������")
    private String carrier;

    @NotNull(message = "�� ������� �����������")
    private String customer;

    @NotNull(message = "�� ������� ������ ���������")
    @JsonProperty("customer_cred")
    private String customerCredentials;

    @NotNull(message = "�� ������ �������")
    private String route;

    @NotNull(message = "�� ������� ���������")
    @Min(message = "��������� ������ ���� ������ 0", value = 1)
    private Integer cost;

    @NotNull(message = "�� ������ ���������")
    private String signatory;

    @NotNull(message = "�� ������� �������������� �������")
    @JsonProperty("signed")
    private Boolean signed;
}
