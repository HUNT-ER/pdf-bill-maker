package com.boldyrev.pdfbillcreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.boldyrev.pdfbillcreator.dto.BillDetailsDTO;
import com.boldyrev.pdfbillcreator.models.Bill;
import com.boldyrev.pdfbillcreator.repositories.dao.GoogleDriveDAO;
import com.boldyrev.pdfbillcreator.utils.converters.BillDetailsDTOToBillConverter;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class BillDetailsDTOToBillConverterTest {

    @MockBean
    private GoogleDriveDAO googleDriveDAO;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BillDetailsDTOToBillConverter converter;

    private static BillDetailsDTO getBillDetailsDTO(Long billNumber, LocalDate billDate,
        String recipientCredentials, String bankCredentials, String carrier, String customer,
        String customerCredentials, String route, Integer cost, String signatory, Boolean signed) {

        BillDetailsDTO details = new BillDetailsDTO();
        details.setBillNumber(billNumber);
        details.setBillDate(billDate);
        details.setRecipientCredentials(recipientCredentials);
        details.setBankCredentials(bankCredentials);
        details.setCarrier(carrier);
        details.setCustomer(customer);
        details.setCustomerCredentials(customerCredentials);
        details.setRoute(route);
        details.setCost(cost);
        details.setSignatory(signatory);
        details.setSigned(signed);
        return details;
    }

    @Test
    void convert_WithValidBillDetailsDTO_ReturnsNewBill() throws Exception {
        BillDetailsDTO details = getBillDetailsDTO(1l, LocalDate.now(), "Recipient", "Bank",
            "Carrier", "Customer", "CustomerCredentials", "Route", 5000, "Signatory", false);
        when(googleDriveDAO.saveAndGetUrl(Mockito.any())).thenReturn("download_link");
        Bill testBill = modelMapper.map(details, Bill.class);

        Bill bill = converter.convert(details);

        verify(googleDriveDAO).saveAndGetUrl(Mockito.any());
        assertThat(bill.getUrl()).isNotNull();
        assertThat(bill.getUrl()).isEqualTo("download_link");
        assertThat(bill.getPdfBill()).isNotNull();
        assertThat(bill.getPdfBill().exists()).isFalse();
        assertThat(bill.getBillDate()).isEqualTo(testBill.getBillDate());
        assertThat(bill.getBillNumber()).isEqualTo(testBill.getBillNumber());
        assertThat(bill.getCost()).isEqualTo(testBill.getCost());
        assertThat(bill.getCustomer()).isEqualTo(testBill.getCustomer());
        assertThat(bill.getRoute()).isEqualTo(testBill.getRoute());
    }

}
