package com.boldyrev.pdfbillcreator.repositories;

import com.boldyrev.pdfbillcreator.models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillsRepository extends JpaRepository<Bill, Integer> {

}
