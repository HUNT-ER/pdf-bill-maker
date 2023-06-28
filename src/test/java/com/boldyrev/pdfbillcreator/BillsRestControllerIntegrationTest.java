package com.boldyrev.pdfbillcreator;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boldyrev.pdfbillcreator.dto.BillDTO;
import com.boldyrev.pdfbillcreator.dto.BillDetailsDTO;
import com.boldyrev.pdfbillcreator.exceptions.IncorrectBillDetailsException;
import com.boldyrev.pdfbillcreator.utils.responses.BillsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Sql("/sql/bill_rest_controller/init.sql")
@Transactional
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-integrationtest.properties")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
class BillsRestControllerIntegrationTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    BillsRestControllerIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void getBills_ReturnsOkStatusAndJsonBillsResponse() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bills");
        MvcResult result = mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    }

    @Test
    void getBills_ReturnsValidStoredBillsResponse() throws Exception {
        BillDTO bill1 = new BillDTO("test_url_0", 15l, LocalDate.of(2023, 6, 23), "Customer",
            "Route", 80000, null);
        BillDTO bill2 = new BillDTO("test_url_1", 16l, LocalDate.of(2023, 6, 24), "Customer",
            "Route", 50000, null);
        BillsResponse expectedBillsResponse = new BillsResponse(List.of(bill1, bill2));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bills");
        MvcResult result = mockMvc.perform(requestBuilder)
            .andExpect(jsonPath("$.bills").exists())
            .andExpect(jsonPath("$.bills", Matchers.hasSize(2)))
            .andReturn();

        BillsResponse factualBillsResponse = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            BillsResponse.class);

        assertThat(factualBillsResponse).isEqualTo(expectedBillsResponse);
    }


    @Test
    void createBills_BillDetailsDTOIsValid_ReturnsCreatedBillDTO() throws Exception {
        //given
        BillDetailsDTO details = getBillDetailsDTO(8l, LocalDate.now(), "Recipient", "Bank",
            "Carrier", "Customer", "Customer creds", "Route", 8000, "Signatory", false);
        String detailsJSON = objectMapper.writeValueAsString(details);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bills/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(detailsJSON);

        //when
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.url").exists())
            .andExpect(jsonPath("$.number").value(details.getBillNumber()))
            .andExpect(jsonPath("$.date").value(details.getBillDate().toString()))
            .andExpect(jsonPath("$.customer").value(details.getCustomer()))
            .andExpect(jsonPath("$.route").value(details.getRoute()))
            .andExpect(jsonPath("$.cost").value(details.getCost()));
    }

    @Test
    void createBills_BillDetailsDTOIsInvalid_ThrowsIncorrectBillDetailsException() throws Exception {
        BillDetailsDTO details = getBillDetailsDTO(8l, LocalDate.now(), "Recipient", "Bank",
            "Carrier", "Customer", "Customer creds", "Route", -1, "Signatory", false);
        String detailsJSON = objectMapper.writeValueAsString(details);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bills/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(detailsJSON);

        mockMvc.perform(requestBuilder)
            .andExpect(result -> Assertions.assertThatExceptionOfType(
                IncorrectBillDetailsException.class));
    }


    @ParameterizedTest
    @MethodSource("getIncorrectBillDetailsDTO")
    void createBills_BillsDTOIsInvalid_ReturnsBillErrorResponseJsonWithMessage(
        BillDetailsDTO billDetailsDTO, String errorMessage) throws Exception {

        String detailsJson = objectMapper.writeValueAsString(billDetailsDTO);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bills/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(detailsJson);

        MvcResult result = mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andReturn();

        String responseJson = result.getResponse().getContentAsString(Charset.forName("UTF-8"));

        assertThat(responseJson).contains(errorMessage);
    }


    private static Stream<Arguments> getIncorrectBillDetailsDTO() {
        return Stream.of(
            Arguments.of(getBillDetailsDTO(8l, LocalDate.now(), "Recipient", "Bank", "Carrier", "Customer",
                "Customer creds", "Route", -1, "Signatory", false), "Стоимость должна быть больше 0"),
            Arguments.of(getBillDetailsDTO(0l, LocalDate.now(), "Recipient", "Bank", "Carrier", "Customer",
                "Customer creds", "Route", 5000, "Signatory", false), "Номер счёта должен быть больше 1"),
            Arguments.of(getBillDetailsDTO(8l, null, "Recipient", "Bank", "Carrier", "Customer",
                "Customer creds", "Route", 5000, "Signatory", false), "Не указана дата"),
            Arguments.of(new BillDetailsDTO(), "Не указаны данные получателя")
        );
    }

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

}