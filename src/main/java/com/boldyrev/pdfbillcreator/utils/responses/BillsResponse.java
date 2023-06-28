package com.boldyrev.pdfbillcreator.utils.responses;

import com.boldyrev.pdfbillcreator.dto.BillDTO;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class BillsResponse {

    @NonNull
    private List<BillDTO> bills;
}
