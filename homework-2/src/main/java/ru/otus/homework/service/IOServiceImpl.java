package ru.otus.homework.service;

import java.util.Scanner;

public class IOServiceImpl implements IOService {

    Scanner in = new Scanner(System.in);

    @Override
    public void sendMessage(String message) {
        System.out.print(message);
    }

    @Override
    public String getMessage() {
        String message = in.next();
        return message;
    }
}
