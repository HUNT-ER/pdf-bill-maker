package com.boldyrev.pdfbillcreator.enums;

public enum DocumentType {
    INVOICE("���� �� ������"), ACT("��� ��������� �����");
    private String value;
    DocumentType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
