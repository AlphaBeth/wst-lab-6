package ru.ifmo.wst.lab1;

import lombok.SneakyThrows;
import ru.ifmo.wst.lab1.client.ExterminatusResourceConsoleClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleClient {
    @SneakyThrows
    public static void main(String[] args) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String endpointUrl = "http://localhost:8080";
        System.out.print("Enter base exterminatus url (or empty string for default " + endpointUrl + ")\n> ");
        String line = bufferedReader.readLine();
        if (line == null) {
            return;
        }
        if (!line.trim().isEmpty()) {
            endpointUrl = line.trim();
        }

        ExterminatusResourceConsoleClient consoleClient = new ExterminatusResourceConsoleClient(endpointUrl);
        consoleClient.info();
        consoleClient.start();
    }

}
