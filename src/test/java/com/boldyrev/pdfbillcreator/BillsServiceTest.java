package com.boldyrev.pdfbillcreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.boldyrev.pdfbillcreator.models.Bill;
import com.boldyrev.pdfbillcreator.repositories.BillsRepository;
import com.boldyrev.pdfbillcreator.services.BillsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-unittest.properties")
//@AutoConfigureTestDatabase(replace = Replace.ANY)
public class BillsServiceTest {

    @MockBean
    private BillsRepository billsRepository;

    @Autowired
    private BillsService billsService;

    @Test
    void save_PersistBillInRepository() {
        Bill bill = new Bill();
        when(billsRepository.save(bill)).thenReturn(bill);

        billsService.save(bill);

        verify(billsRepository).save(bill);
        assertThat(bill.getCreatedAt()).isNotNull();
    }

    @Test
    void findAll_ShouldInvokeRepositoryMethodFindAll() {
        when(billsRepository.findAll(Sort.by("createdAt"))).thenReturn(List.of());

        billsService.findAll();

        verify(billsRepository).findAll(Sort.by("createdAt"));
    }

}
