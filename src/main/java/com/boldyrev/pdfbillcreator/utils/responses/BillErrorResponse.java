package com.boldyrev.pdfbillcreator.utils.responses;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillErrorResponse {

    private String message;

    private LocalDateTime timestamp;

}
