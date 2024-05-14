package main;

import com.google.common.primitives.Bytes;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.logging.Logger;
/**
 * Класс отвечает за обмен данными между клиентом и сервером
 * по протоколу UDP с использованием Datagram
 * @author Matvei Baranov
 */
public class UDPDatagramClient {
  private final int PacketSize = 1024;
  private final int DataSize = PacketSize - 1;

  private final DatagramChannel client;
  private final InetSocketAddress addr;

  private final Logger logger;

  public UDPDatagramClient(InetSocketAddress addr, Logger logger) throws IOException {
    this.addr = addr;
    this.logger = logger;
    this.client = DatagramChannel.open().bind(null).connect(addr);
    this.client.configureBlocking(false);
    logger.info("DatagramChannel подключен к " + addr);
  }

  public ResponseList sendCommandAndReceiveResponse(Request request) throws IOException {
    var data = SerializationUtils.serialize(request);
    var responseBytes = sendAndReceiveData(data);

    ResponseList response = SerializationUtils.deserialize(responseBytes);
    logger.info("Получен ответ от сервера:  " + response);
    return response;
  }

  public ResponseList sendCommandWithPersonAndReceiveResponse(RequestPerson request) throws IOException {
    var data = SerializationUtils.serialize(request);
    var responseBytes = sendAndReceiveData(data);

    ResponseList response = SerializationUtils.deserialize(responseBytes);
    logger.info("Получен ответ от сервера:  " + response);
    return response;
  }

  public ResponseList ReceiveResponse() throws IOException {
    var responseBytes = receiveData();
    ResponseList response = SerializationUtils.deserialize(responseBytes);
    logger.info("Получен ответ от сервера:  " + response);
    return response;
  }

  public ResponsePersonList ReceiveResponsePerson() throws IOException {
    var responseBytes = receiveData();
    ResponsePersonList  response = SerializationUtils.deserialize(responseBytes);
    logger.info("Получен ответ от сервера:  " + response);
    return response;
  }

  private void sendData(byte[] data) throws IOException {
    byte[][] ret = new byte[(int)Math.ceil(data.length / (double)DataSize)][DataSize];

    int start = 0;
    for(int i = 0; i < ret.length; i++) {
      ret[i] = Arrays.copyOfRange(data, start, start + DataSize);
      start += DataSize;
    }

    logger.info("Отправляется " + ret.length + " чанков...");

    for(int i = 0; i < ret.length; i++) {
      var chunk = ret[i];
      if (i == ret.length - 1) {
        var lastChunk = Bytes.concat(chunk, new byte[]{1});
        client.send(ByteBuffer.wrap(lastChunk), addr);
        //logger.info("Последний чанк размером " + lastChunk.length + " отправлен на сервер.");
      } else {
        var answer = Bytes.concat(chunk, new byte[]{0});
        client.send(ByteBuffer.wrap(answer), addr);
        //logger.info("Чанк размером " + answer.length + " отправлен на сервер.");
      }
    }
    logger.info("Отправка данных завершена.");
  }

  private byte[] receiveData() throws IOException {
    var received = false;
    var result = new byte[0];

    while(!received) {
      var data = receiveData(PacketSize);
      //logger.info("Получено \"" + new String(data) + "\"");
      //logger.info("Последний байт: " + data[data.length - 1]);

      if (data[data.length - 1] == 1) {
        received = true;
        //logger.info("Получение данных окончено");
      }
      result = Bytes.concat(result, Arrays.copyOf(data, data.length - 1));
    }

    return result;
  }

  private byte[] receiveData(int bufferSize) throws IOException {
    var buffer = ByteBuffer.allocate(bufferSize);
    SocketAddress address = null;
    while(address == null) {
      address = client.receive(buffer);
    }
    return buffer.array();
  }

  private byte[] sendAndReceiveData(byte[] data) throws IOException {
    sendData(data);
    return receiveData();
  }
}
