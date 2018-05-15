package com.admin.appclientchat;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class MainActivityViewModel extends AndroidViewModel {

    // Client clientSocket
    private Socket clientSocket;

    // LiveData
    private MutableLiveData<String> liveData;

    // Key
    int keyCrypto = 7;


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

    // Open connection
    public void openConnection(final String address, final int port) {
        // Close connection
        closeConnection();
        //Create connection
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(address, port);
                    liveData.postValue("System message: Соеденение установлено");
                    // Listen message from companion
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    InputStream inputStream = clientSocket.getInputStream();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.reset();
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        // Decrypt
                        Encryption encryption = new Encryption(keyCrypto);
                        liveData.postValue(new String("Companion: ")
                                .concat(new String(encryption.decrypt(byteArrayOutputStream
                                        .toString("UTF-16")),"UTF-16").substring(2)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    liveData.postValue("System message: Невозможно создать сокет: " + e.getMessage());
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
                        Encryption encryption = new Encryption(keyCrypto);
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

    // Close connection
    public void closeConnection() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue("System message: Невозможно закрыть сокет: " + e.getMessage());
            } finally {
                clientSocket = null;
            }
        }
        clientSocket = null;

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }

}


