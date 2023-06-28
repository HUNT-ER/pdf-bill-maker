package com.boldyrev.pdfbillcreator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {

    @JsonProperty("url")
    private String url;

    @NotNull(message = "�� ������ ����� �����")
    @Min(message = "����� ����� ������ ���� ������ 1", value = 1)
    @JsonProperty("number")
    private Long billNumber;

    @NotNull(message = "�� ������� ����")
    @JsonProperty("date")
    private LocalDate billDate;

    @NotNull(message = "�� ������� �����������")
    @JsonProperty("customer")
    private String customer;

    @NotNull(message = "�� ������ �������")
    @JsonProperty("route")
    private String route;

    @NotNull(message = "�� ������� ���������")
    @Min(message = "��������� ������ ���� ������ 0", value = 1)
    @JsonProperty("cost")
    private Integer cost;

    @JsonProperty("created_at")
    @Exclude
    private LocalDateTime createdAt;
}
