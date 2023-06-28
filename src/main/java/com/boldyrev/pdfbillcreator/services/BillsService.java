package com.boldyrev.pdfbillcreator.services;

import com.boldyrev.pdfbillcreator.models.Bill;
import java.util.List;

public interface BillsService {

    List<Bill> findAll();
    void save(Bill bill);
}
