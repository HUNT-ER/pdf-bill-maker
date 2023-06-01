package com.boldyrev.pdfbillcreator.exceptions;

public class SignatureNotFoundException extends RuntimeException{

    public SignatureNotFoundException(String message) {
        super(message);
    }
}
