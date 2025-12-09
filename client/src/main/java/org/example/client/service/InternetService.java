package org.example.client.service;

import org.example.client.event.internet.InternetState;

public interface InternetService {

    void disableInternet();

    void enableInternet();

    InternetState getState();

}
