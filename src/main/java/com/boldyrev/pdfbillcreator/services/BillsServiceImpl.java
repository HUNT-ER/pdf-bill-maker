package com.boldyrev.pdfbillcreator.services;

import com.boldyrev.pdfbillcreator.models.Bill;
import com.boldyrev.pdfbillcreator.repositories.BillsRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillsServiceImpl implements BillsService {

    private static final Logger log = LoggerFactory.getLogger(BillsServiceImpl.class);
    private final BillsRepository billsRepository;

    public BillsServiceImpl(BillsRepository billsRepository) {
        this.billsRepository = billsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bill> findAll() {
        return billsRepository.findAll(Sort.by("createdAt"));
    }

    @Override
    @Transactional
    public void save(Bill bill) {
        billsRepository.save(enrich(bill));

        log.info("New bill was created: {}", bill);
    }

    private Bill enrich(Bill bill) {
        bill.setCreatedAt(LocalDateTime.now());
        return bill;
    }
}
