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
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bills")
@CrossOrigin
public class BillsRestController {

    private final static Logger log = LoggerFactory.getLogger(BillsRestController.class);
    private final BillsService billsService;
    private final ModelMapper modelMapper;
    private final BillDetailsDTOToBillConverter converter;

    @Autowired
    public BillsRestController(BillsService billsService, ModelMapper modelMapper,
        BillDetailsDTOToBillConverter converter) {
        this.billsService = billsService;
        this.modelMapper = modelMapper;
        this.converter = converter;
    }

    @PostMapping("/new")
    public ResponseEntity<BillDTO> createBill(@RequestBody @Valid BillDetailsDTO billDetailsDTO,
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

        return ResponseEntity.created(URI.create("http://localhost:80/bills/new"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(convertBillToBillDTO(bill));
    }

    @GetMapping
    public ResponseEntity<BillsResponse> getBills() {
        List<BillDTO> billDTOList = billsService.findAll().stream()
            .map(this::convertBillToBillDTO)
            .collect(Collectors.toList());
        BillsResponse response = new BillsResponse(billDTOList);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<BillErrorResponse> handleException(IncorrectBillDetailsException e) {
        log.debug("Request received with invalid bill data \"{}\"", e.getMessage());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
            .body(new BillErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    private BillDTO convertBillToBillDTO(Bill bill) {
        return modelMapper.map(bill, BillDTO.class);
    }
}
