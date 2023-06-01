package com.boldyrev.pdfbillcreator.utils.responses;

import com.boldyrev.pdfbillcreator.dto.BillDTO;
import java.util.List;
import lombok.Data;
import org.springframework.boot.convert.DataSizeUnit;

@Data
public class BillsResponse {

    private List<BillDTO> bills;
}
