package ru.smak.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Communicator {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private volatile boolean isActive;

    private final List<Consumer<String>> dataListeners = new ArrayList<>();

    public void addDataListener(Consumer<String> c){
        dataListeners.add(c);
    }

    public void removeDataListener(Consumer<String> c){
        dataListeners.remove(c);
    }

    public Communicator(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(
            new InputStreamReader(
                socket.getInputStream(),
                StandardCharsets.UTF_8
            ));
        out = new PrintWriter(
                socket.getOutputStream(),
                true,
                StandardCharsets.UTF_8
        );
    }

    public void start(){
        isActive = true;
        new Thread(()-> {
            try {
                while (isActive) {
                    var data = in.readLine();
                    if (data == null) break;
                    for (var dataListener : dataListeners) {
                        dataListener.accept(data);
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка чтения данных из сети");
                System.err.println(e.getMessage());
            }
            finally {
                stop();
            }
        }).start();
    }

    public void sendData(String data){
        synchronized (out) {
            if (!isActive || socket.isClosed()) {
                throw new IllegalStateException("Отправка невозможна. Нет соединения");
            }
            out.println(data);
            if (out.checkError()) {
                stop();
                throw new IllegalStateException("Ошибка записи в поток");
            }
        }
    }

    public void stop(){
        if (!isActive) return;
        isActive = false;
        try {
            if (in != null) in.close();
            if (out != null) synchronized (out) {
                out.close();
            }
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии ресурсов");
            System.err.println(e.getMessage());
        }
    }

    public boolean isActive() {
        return isActive;
    }
}
