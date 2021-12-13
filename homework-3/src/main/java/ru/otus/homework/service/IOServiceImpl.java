package ru.otus.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

@Service
public class IOServiceImpl implements IOService {

    private final Scanner scanner;
    private final OutputStream writer;

    public IOServiceImpl(@Value("#{ T(java.lang.System).in}") InputStream is,
                         @Value("#{ T(java.lang.System).out}") OutputStream os) {
        this.scanner = new Scanner(is);
        this.writer = os;
    }

    @Override
    public void sendMessage(String message) {
        try {
            writer.write(message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMessage() {
        return scanner.nextLine();
    }
}
