package com.boldyrev.pdfbillcreator.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
public class Bill {

    @Id
    @Column(name = "bill_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int billId;

    @Column(name = "url")
    @NotNull(message = "Не указан URL для скачивания")
    private String url;

    @Column(name = "bill_number")
    @NotNull(message = "Не указан номер счёта")
    @Min(message = "Номер счёта должен быть больше 0", value = 1)
    private Long billNumber;

    @Column(name = "bill_date")
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
    @NotNull(message = "Отсутствует дата создания")
    private LocalDateTime createdAt;

    @Transient
    private File pdfBill;
}
