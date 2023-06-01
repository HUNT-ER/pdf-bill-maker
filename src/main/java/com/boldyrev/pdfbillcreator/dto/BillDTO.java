package com.boldyrev.pdfbillcreator.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillDTO {

    @Column(name = "url")
    private String url;

    @Column(name = "bill_number")
    @NotNull(message = "Не указан номер счёта")
    @Min(message = "Номер счёта должен быть больше 1", value = 1)
    private Long billNumber;

    @Column(name = "date")
    @NotNull(message = "Не указана дата")
    private LocalDate billDate;

    @Column(name = "customer")
    @NotNull(message = "Не указана организация")
    private String customer;

    @Column(name = "route")
    @NotNull(message = "Не указан маршрут")
    private String route;

    @Column(name = "cost")
    @NotNull(message = "Не указана цена")
    @Min(message = "Цена должна быть больше 0", value = 1)
    private Integer cost;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
