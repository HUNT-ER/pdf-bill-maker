package com.boldyrev.pdfbillcreator.controllers;

import com.boldyrev.pdfbillcreator.dto.BillDTO;
import com.boldyrev.pdfbillcreator.dto.BillDetailsDTO;
import com.boldyrev.pdfbillcreator.exceptions.IncorrectBillDetailsException;
import com.boldyrev.pdfbillcreator.models.Bill;
import com.boldyrev.pdfbillcreator.services.BillsService;
import com.boldyrev.pdfbillcreator.utils.converters.BillDetailsDTOToBillConverter;
import com.boldyrev.pdfbillcreator.utils.responses.BillErrorResponse;
import com.boldyrev.pdfbillcreator.utils.responses.BillsResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bills")
public class BillsController {

    private final BillsService billsService;
    private final ModelMapper modelMapper;
    private final BillDetailsDTOToBillConverter converter;

    @Autowired
    public BillsController(BillsService billsService, ModelMapper modelMapper,
        BillDetailsDTOToBillConverter converter) {
        this.billsService = billsService;
        this.modelMapper = modelMapper;
        this.converter = converter;
    }

    @PostMapping("/new")
    public BillDTO createBill(@RequestBody @Valid BillDetailsDTO billDetailsDTO,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();

            bindingResult.getFieldErrors().forEach(
                e -> builder.append(e.getField())
                    .append(" - ")
                    .append(e.getDefaultMessage())
                    .append(";")
            );

            throw new IncorrectBillDetailsException(builder.toString());
        }

        Bill bill = converter.convert(billDetailsDTO);
        billsService.save(bill);

        return convertBillToBillDTO(bill);
    }

    @GetMapping
    public BillsResponse getBills() {
        List<BillDTO> billDTOList = billsService.findAll().stream().map(this::convertBillToBillDTO)
            .collect(Collectors.toList());
        BillsResponse response = new BillsResponse();
        response.setBills(billDTOList);

        return response;
    }

    @ExceptionHandler
    public ResponseEntity<BillErrorResponse> handleException(IncorrectBillDetailsException e) {
        return new ResponseEntity<>(new BillErrorResponse(e.getMessage(), LocalDateTime.now()),
            HttpStatus.BAD_REQUEST);
    }

    private BillDTO convertBillToBillDTO(Bill bill) {
        return modelMapper.map(bill, BillDTO.class);
    }

}
