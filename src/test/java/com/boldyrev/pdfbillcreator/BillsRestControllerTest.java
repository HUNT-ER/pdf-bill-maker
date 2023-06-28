package com.boldyrev.pdfbillcreator;

import static org.assertj.core.api.Assertions.assertThat;

import com.boldyrev.pdfbillcreator.controllers.BillsRestController;
import com.boldyrev.pdfbillcreator.dto.BillDTO;
import com.boldyrev.pdfbillcreator.dto.BillDetailsDTO;
import com.boldyrev.pdfbillcreator.exceptions.IncorrectBillDetailsException;
import com.boldyrev.pdfbillcreator.models.Bill;
import com.boldyrev.pdfbillcreator.services.BillsService;
import com.boldyrev.pdfbillcreator.utils.converters.BillDetailsDTOToBillConverter;
import com.boldyrev.pdfbillcreator.utils.responses.BillErrorResponse;
import com.boldyrev.pdfbillcreator.utils.responses.BillsResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
public class BillsRestControllerTest {

    @Mock
    private BillsService billsService;
    @Spy
    private ModelMapper modelMapper;
    @Mock
    private BillDetailsDTOToBillConverter converter;
    @Mock
    BindingResult bindingResult;

    @InjectMocks
    private BillsRestController controller;

    BillDetailsDTO getValidBillDetailsDTO() {
        BillDetailsDTO dto = new BillDetailsDTO();
        dto.setBillDate(LocalDate.now());
        dto.setBillNumber(1l);
        dto.setCarrier("Carrier");
        dto.setBankCredentials("Bank credentials");
        dto.setCost(500);
        dto.setCustomer("Customer");
        dto.setRoute("Route");
        dto.setCustomerCredentials("Customer credentials");
        dto.setSignatory("Signatory");
        dto.setSigned(true);
        dto.setRecipientCredentials("Recipient credentials");
        return dto;
    }

    @ParameterizedTest
    @MethodSource("getDifferentSizeBillsLists")
    void getBills_ReturnsValidBillsResponse(List<Bill> bills) {

        Mockito.when(billsService.findAll()).thenReturn(bills);

        ResponseEntity<BillsResponse> response = controller.getBills();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody().getBills().size()).isEqualTo(bills.size());
    }


    @Test
    void createBill_BillDetailsDTOIsValid_ReturnsCreatedBillDTO() {
        //добавляет файл
        //добавляет ссылку
        BillDetailsDTO billDetailsDTO = getValidBillDetailsDTO();
        Bill bill = modelMapper.map(billDetailsDTO, Bill.class);
        bill.setUrl("Some URL");
        bill.setCreatedAt(LocalDateTime.now());

        Mockito.when(converter.convert(Mockito.any())).thenReturn(bill);

        ResponseEntity<BillDTO> response = controller.createBill(billDetailsDTO, bindingResult);

        Mockito.verify(billsService).save(bill);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isInstanceOf(BillDTO.class);
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    void createBill_BillDetailsDTOIsInvalid_ThrowsException() {
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        Assertions.assertThatExceptionOfType(IncorrectBillDetailsException.class)
            .isThrownBy(() -> controller.createBill(new BillDetailsDTO(), bindingResult));

        Mockito.verifyNoInteractions(billsService);
    }

    @Test
    void handleException_IncorrectBillDetailsException_ReturnsBillErrorResponse() {

        ResponseEntity<BillErrorResponse> errorResponse = controller.handleException(
            new IncorrectBillDetailsException("error fields message"));

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getHeaders().getContentType()).isEqualTo(
            MediaType.APPLICATION_JSON);
        assertThat(errorResponse.getBody().getMessage()).isEqualTo("error fields message");
    }


    public static Stream<List<Bill>> getDifferentSizeBillsLists() {
        Bill bill = new Bill();
        return Stream.of(List.of(), List.of(bill), List.of(bill, bill), List.of(bill, bill, bill));
    }

}
