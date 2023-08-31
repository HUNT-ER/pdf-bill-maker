package com.boldyrev.pdfbillcreator.utils.converters;

import com.boldyrev.pdfbillcreator.dto.BillDetailsDTO;
import com.boldyrev.pdfbillcreator.enums.DocumentType;
import com.boldyrev.pdfbillcreator.exceptions.DocumentNotCreatedException;
import com.boldyrev.pdfbillcreator.exceptions.FontNotCreatedException;
import com.boldyrev.pdfbillcreator.exceptions.GoogleDriveFileNotSavedException;
import com.boldyrev.pdfbillcreator.exceptions.SignatureNotFoundException;
import com.boldyrev.pdfbillcreator.models.Bill;
import com.boldyrev.pdfbillcreator.repositories.dao.GoogleDriveDAO;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class BillDetailsDTOToBillConverter {

    private final static Logger log = LoggerFactory.getLogger(BillDetailsDTOToBillConverter.class);
    private final ModelMapper modelMapper;
    private final int DEFAULT_FONT_SIZE = 11;
    private final int TITLE_FONT_SIZE = 16;
    @Value("${pdf.font.regular}")
    private String regularFontPath;
    @Value("${pdf.font.bold}")
    private String boldFontPath;
    @Value("${pdf.signatures.signature1}")
    private String signature1Path;
    @Value("${pdf.signatures.signature2}")
    private String signature2Path;
    private PdfFont regularFont;
    private PdfFont boldFont;
    private final GoogleDriveDAO googleDriveDAO;

    @Autowired
    public BillDetailsDTOToBillConverter(ModelMapper modelMapper, GoogleDriveDAO googleDriveDAO) {
        this.modelMapper = modelMapper;
        this.googleDriveDAO = googleDriveDAO;
    }

    public Bill convert(BillDetailsDTO billDetailsDTO) {
        try {
            ClassPathResource regFont = new ClassPathResource(regularFontPath);
            ClassPathResource bFont = new ClassPathResource(boldFontPath);
            regularFont = PdfFontFactory.createFont(IOUtils.toByteArray(regFont.getInputStream()),
                PdfEncodings.IDENTITY_H);
            boldFont = PdfFontFactory.createFont(IOUtils.toByteArray(bFont.getInputStream()),
                PdfEncodings.IDENTITY_H);
        } catch (IOException e) {
            log.error(
                "Fail to find regular font \"{}\" or bold font \"{}\". Add correct path in application.properties",
                regularFontPath, boldFontPath);
            throw new FontNotCreatedException(e.getMessage());
        }
        Bill bill = modelMapper.map(billDetailsDTO, Bill.class);
        File pdfBill = createBill(billDetailsDTO);
        bill.setPdfBill(pdfBill);

        try {
            bill.setUrl(googleDriveDAO.saveAndGetUrl(pdfBill));
        } catch (IOException e) {
            log.error("Error connecting to google drive.");
            throw new GoogleDriveFileNotSavedException(e.getMessage());
        }

        pdfBill.delete();

        return bill;
    }

    private File createBill(BillDetailsDTO billDetailsDTO) {
        StringBuilder billName = new StringBuilder();
        billName.append("СЧЕТ_АКТ_")
            .append(billDetailsDTO.getBillNumber()).append(" ")
            .append(billDetailsDTO.getBillDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY")));
        if (!billDetailsDTO.getSigned()) {
            billName.append("(Не подписанный)");
        }
        billName.append(".pdf");

        try (PdfDocument pdfDocument = new PdfDocument(
            new PdfWriter(billName.toString())); Document document = new Document(pdfDocument)) {
            createDocumentPage(DocumentType.INVOICE, document, billDetailsDTO,
                billDetailsDTO.getSigned());
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            createDocumentPage(DocumentType.ACT, document, billDetailsDTO,
                billDetailsDTO.getSigned());
        } catch (FileNotFoundException e) {
            log.error("Document \"{}\" not found", billName.toString());
            throw new DocumentNotCreatedException(e.getMessage());
        }
        return new File(billName.toString());
    }

    private Document createDocumentPage(DocumentType docType, Document document,
        BillDetailsDTO billDetailsDTO, boolean withSignature) {
        if (docType == DocumentType.INVOICE) {
            document.add(setVerticalAlignment(createCredentialsTable("Получатель", "Банк",
                    billDetailsDTO.getRecipientCredentials(), billDetailsDTO.getBankCredentials()),
                VerticalAlignment.MIDDLE));
        }
        document.add(getIndent(1));
        document.add(createTitleBlock(docType, billDetailsDTO.getBillNumber(),
            billDetailsDTO.getBillDate()));
        document.add(getIndent(1));
        document.add(setVerticalAlignment(removeBorders(
                createCredentialsTable("Поставщик:", "Покупатель:", billDetailsDTO.getCarrier(),
                    billDetailsDTO.getCustomer() + " " + billDetailsDTO.getCustomerCredentials())),
            VerticalAlignment.TOP));
        document.add(getIndent(1));
        document.add(setVerticalAlignment(
            createServiceTable(billDetailsDTO.getRoute(), billDetailsDTO.getCost()),
            VerticalAlignment.MIDDLE));
        this.addServiceSumBlock(document, billDetailsDTO.getCost());
        document.add(getIndent(1));
        if (docType == DocumentType.ACT) {
            document.add(createConfirmationBlock());
        }
        document.add(getIndent(1));
        document.add(createSignatoryBlock(docType, billDetailsDTO.getSignatory()));

        if (withSignature) {
            document.add(getSignature(docType));
        }
        return document;
    }

    private Paragraph createTitleBlock(DocumentType docType, long number, LocalDate date) {

        return new Paragraph().setFont(boldFont).setFontSize(TITLE_FONT_SIZE)
            .setTextAlignment(TextAlignment.CENTER).add(setTitle(docType, number, date));
    }

    private String setTitle(DocumentType documentType, long number, LocalDate date) {

        return new StringBuilder().append(documentType.getValue()).append(" №").append(number)
            .append(" от ").append(date.format(DateTimeFormatter.ofPattern("dd.MM.YYYY")))
            .toString();
    }

    private Table createCredentialsTable(String rowName1, String rowName2, String credentials1,
        String credentials2) {

        Table table = new Table(2).setFont(regularFont).setFontSize(DEFAULT_FONT_SIZE)
            .setHorizontalAlignment(HorizontalAlignment.CENTER).setHeight(120).setWidth(500);
        table.addCell(new Cell().add(new Paragraph(rowName1)))
            .addCell((new Cell().add(new Paragraph(credentials1))))
            .addCell((new Cell().add(new Paragraph(rowName2))))
            .addCell((new Cell().add(new Paragraph(credentials2))));

        return table;
    }

    private Table removeBorders(Table table) {
        table.getChildren().forEach(el -> ((Cell) el).setBorder(Border.NO_BORDER));

        return table;
    }

    private Table setVerticalAlignment(Table table, VerticalAlignment alignment) {
        table.getChildren().forEach(el -> ((Cell) el).setVerticalAlignment(alignment));
        return table;
    }

    private Paragraph getIndent(int indentCount) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indentCount; i++) {
            builder.append("\n");
        }
        return new Paragraph(builder.toString());
    }

    private Table createServiceTable(String route, int cost) {
        String[] headers = new String[]{"№", "Товары (работы , услуги)", "Кол-во", "Ед", "Цена",
            "Сумма"};
        Table servicesTable = new Table(6).setFont(regularFont).setFontSize(DEFAULT_FONT_SIZE)
            .setHorizontalAlignment(HorizontalAlignment.CENTER).setHeight(140).setWidth(500);

        for (int i = 0; i < headers.length; i++) {
            servicesTable.addCell(new Cell().add(new Paragraph(headers[i]).setFont(boldFont)));
        }

        Cell routeCell = new Cell();
        routeCell.add(new Paragraph("Оказание транспортных услуг по маршруту " + route)
            .setFont(regularFont).setFontSize(8));
        routeCell.setHeight(100);

        servicesTable.addCell(new Paragraph("1"))
            .addCell(routeCell)
            .addCell(new Paragraph("1")).addCell(new Paragraph("рейс"))
            .addCell(new Paragraph(String.format("%d.00", cost)))
            .addCell(new Paragraph(String.format("%d.00", cost)));

        return servicesTable;
    }

    private Div createSumInfoBlock(int cost) {

        Div sumInfo = new Div().setFont(boldFont).setTextAlignment(TextAlignment.RIGHT)
            .setHeight(100).setVerticalAlignment(VerticalAlignment.MIDDLE);
        Paragraph paragraph = new Paragraph();

        paragraph.add(String.format("ИТОГО: %d.00", cost)).add("\n").add("Без налога (НДС): 0.00")
            .add("\n").add(String.format("Всего к оплате: %d.00", cost));

        return sumInfo.add(paragraph);
    }

    private Div createTotalSumInfoBlock(int cost) {
        Div totalSumInfo = new Div().setFont(regularFont);

        totalSumInfo.add(new Paragraph(
            String.format("Всего наименований 1, на сумму %d рублей 00 копеек", cost)));

        return totalSumInfo;
    }

    private void addServiceSumBlock(Document document, int cost) {
        document.add(createSumInfoBlock(cost));
        document.add(createTotalSumInfoBlock(cost));
    }

    private Div createSignatoryBlock(DocumentType docType, String signatory) {
        Div signatoryDiv = new Div().setFont(regularFont).setHeight(50)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
        String docSignatory;
        if (docType == DocumentType.INVOICE) {
            docSignatory = "Руководитель";
        } else {
            docSignatory = "Исполнитель";
        }

        String singleSignatory = String.format("%s______________/%s/", docSignatory, signatory);
        Paragraph signatoryLine = new Paragraph();
        signatoryLine.add(singleSignatory);

        if (docType == DocumentType.ACT) {
            signatoryLine.add("         ").add("Заказчик______________/__________/");
        }
        signatoryDiv.add(signatoryLine);

        return signatoryDiv;
    }

    private Div createConfirmationBlock() {
        Div block = new Div().setFont(regularFont).setHeight(50)
            .setVerticalAlignment(VerticalAlignment.MIDDLE);

        block.add(new Paragraph(
            "Вышеперечисленные услуги выполнены полностью и в срок. Заказчик претензий по объему, качеству и срокам оказания услуг не имеет."));

        return block;
    }

    private Image getSignature(DocumentType docType) {
        Image signature = null;
        try {
            InputStream signature1 = new ClassPathResource(signature1Path).getInputStream();
            InputStream signature2 = new ClassPathResource(signature2Path).getInputStream();

            if (docType == DocumentType.INVOICE) {
                ImageData imageData = ImageDataFactory.create(IOUtils.toByteArray(signature1));
                signature = new Image(imageData).setFixedPosition(1, 115, 89).scale(0.25f, 0.25f);
            } else {
                ImageData imageData = ImageDataFactory.create(IOUtils.toByteArray(signature2));
                signature = new Image(imageData).setFixedPosition(2, 107, 147).scale(0.25f, 0.25f);
            }

        } catch (IOException e) {
            log.error("Signatures files not found in classpath \"{}\", \"{}\"", signature1Path,
                signature2Path);
            throw new SignatureNotFoundException(e.getMessage());
        }
        return signature;
    }
}
