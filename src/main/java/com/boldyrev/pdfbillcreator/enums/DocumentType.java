package com.boldyrev.pdfbillcreator.enums;

public enum DocumentType {
    INVOICE("—чет на оплату"), ACT("јкт оказанных услуг");
    private String value;
    DocumentType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
