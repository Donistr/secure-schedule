package org.example.client;

import org.example.client.service.impl.InternetServiceImpl;

public class EnableInternet {

    public static void main(String[] args) {
        new InternetServiceImpl(null).enableInternet();
    }

}
