package main;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.*;

public class UDPDatagramServer implements Runnable{
    private Commands commands;
    private Logger logger;
    private InetAddress address;
    private int port;

    public UDPDatagramServer(InetAddress address, int port,Commands commands, Logger logger) {
        this.address=address;
        this.port=port;
        this.commands=commands;
        this.logger=logger;
    }

    @Override
    public void run() {
        logger.info("Сервер запущен по адресу " + address+":"+port);
        ThreadPoolExecutor executorReceiver = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        try{
           executorReceiver.execute(new UDPThreadReceiver(new InetSocketAddress(address, port),commands,logger));
        } catch (SocketException e) {
            System.out.println("Ошибка сокета");
            logger.log(Level.SEVERE,"Ошибка сокета", e);
        }
    }
}
