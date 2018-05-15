package com.admin.demochat;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivityViewModel extends AndroidViewModel {

    // Server socket
    private ServerSocket serverSocket;

    // Client socket
    Socket clientSocket;

    // LiveData
    private MutableLiveData<String> liveData;

    // Key
    int key = 7;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    // Create liveData
    public MutableLiveData<String> getData() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    // Start server and ready to get data
    public void start(final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                serverSocket = new ServerSocket(port);
                clientSocket = null;
                while (true) {
                    clientSocket = serverSocket.accept();
                    liveData.postValue("System message: Соеденение установлено");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Listen message from companion
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                InputStream inputStream = clientSocket.getInputStream();
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    byteArrayOutputStream.reset();
                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                    // Decrypt
                                    Encryption encryption = new Encryption(key);
                                    liveData.postValue(new String("Companion: ")
                                            .concat(new String(encryption.decrypt(byteArrayOutputStream
                                                    .toString("UTF-16")),"UTF-16").substring(2)));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Send data
    public void sendData(final String message) {
        if (clientSocket == null || clientSocket.isClosed()) {
            liveData.postValue("System message: Подключение не создано или закрыто");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Encrypt
                        Encryption encryption = new Encryption(key);
                        // Send
                        clientSocket.getOutputStream().write(encryption.encrypt(message));
                        clientSocket.getOutputStream().flush();
                        liveData.postValue("Your: " + message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        liveData.postValue("System message: Невозможно отправить данные: " + e.getMessage());
                    }
                }
            }).start();
        }
    }

    // Close server
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        serverSocket.close();
        clientSocket.close();
    }
}