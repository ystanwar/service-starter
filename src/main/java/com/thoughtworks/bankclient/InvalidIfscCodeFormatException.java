package com.thoughtworks.bankclient;

public class InvalidIfscCodeFormatException extends Exception {
    public InvalidIfscCodeFormatException(String ifscCode) {
        super(ifscCode);
    }
}
