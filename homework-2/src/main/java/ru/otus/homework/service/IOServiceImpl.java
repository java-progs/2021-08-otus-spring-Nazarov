package ru.otus.homework.service;

import java.util.Scanner;

public class IOServiceImpl implements IOService {

    @Override
    public void sendMessage(String message) {
        System.out.print(message);
    }

    @Override
    public String getMessage() {
        Scanner in = new Scanner(System.in);
        String message = in.nextLine();
        in.close();
        return message;
    }
}
