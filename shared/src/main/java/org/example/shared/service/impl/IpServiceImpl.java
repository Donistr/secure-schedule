package org.example.shared.service.impl;

import org.example.shared.exception.GetIpException;
import org.example.shared.service.IpService;
import org.springframework.stereotype.Service;

import java.net.DatagramSocket;
import java.net.InetAddress;

@Service
public class IpServiceImpl implements IpService {

    @Override
    public InetAddress getIpAddress() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10053);
            return socket.getLocalAddress();
        } catch (Exception e) {
            throw new GetIpException(e);
        }
    }

}
