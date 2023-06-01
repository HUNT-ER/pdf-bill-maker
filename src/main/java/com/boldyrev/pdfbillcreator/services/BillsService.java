package com.boldyrev.pdfbillcreator.services;

import com.boldyrev.pdfbillcreator.models.Bill;
import com.boldyrev.pdfbillcreator.dto.BillDetailsDTO;
import com.boldyrev.pdfbillcreator.repositories.BillsRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillsService {

    private final BillsRepository billsRepository;

    public BillsService(BillsRepository billsRepository) {
        this.billsRepository = billsRepository;
    }

    @Transactional(readOnly = true)
    public List<Bill> findAll() {
        return billsRepository.findAll(Sort.by("createdAt"));
    }

    @Transactional
    public void save(Bill bill) {
        billsRepository.save(enrich(bill));
    }

    //todo 10 last
    private Bill enrich(Bill bill) {
        bill.setCreatedAt(LocalDateTime.now());
        return bill;
    }
}
