package main;

import java.util.ArrayList;
import java.util.List;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.logging.Logger;
import java.io.IOException;

public class ClientApplicationManager {
    private final List<String> addresslist = new ArrayList<String>();
    public static Logger logger;
    public ClientApplicationManager(Logger logger){
        this.logger=logger;
    }
    public synchronized void addClient(String ip){
        int i = 0;
        while(i< addresslist.size()){
            if (Objects.equals(addresslist.get(i),ip)){
                return;
            }
            i++;
        }
        addresslist.add(ip);
    }
    private boolean sendToClient(ResponseManager responsemanager, String ip){
        logger.info("Отправка всех объектов клиенту " + ip);
        try {
            UDPDatagramClient client = new UDPDatagramClient(new InetSocketAddress(ip, 30200),logger);
            client.sendPersons(responsemanager);
            return true;
        } catch (IOException e) {
            //logger.log(Level.SEVERE,"Невозможно подключиться к клиенту.", e);
            return false;
        }
    }
    public synchronized void sendToAllClients(Persons persons){
        logger.info("Отправка всех объектов клиенту!");
        ResponseManager responsemanager=persons.show();
        int i = 0;
        while(i< addresslist.size()){
            if (sendToClient(responsemanager,addresslist.get(i))){
                i++;
            }
            else{
                addresslist.remove(i);
            }
        }
    }
}
