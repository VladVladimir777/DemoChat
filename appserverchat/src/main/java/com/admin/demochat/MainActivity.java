package com.admin.demochat;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    // PORT
    private static final int PORT = 6789;

    // UI components
    private TextView tvAddress;
    private TextView tvHistory;
    private EditText etEnterMessage;
    private Button btnSend;

    // Message builder
    private StringBuilder message;

    // MainActivityViewModel
    private MainActivityViewModel mainActivityViewModel;
    // LiveData
    private LiveData<String> liveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI components
        tvAddress = findViewById(R.id.tvAddress);
        tvHistory = findViewById(R.id.tvHistory);
        etEnterMessage = findViewById(R.id.etEnterMessage);
        btnSend = findViewById(R.id.btnSend);

        // Create message builder
        message = new StringBuilder();

        // Create MainActivityViewModel
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        // Start server
        mainActivityViewModel.start(PORT);

        // Observe for data
        liveData = mainActivityViewModel.getData();
        liveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                message.append(s + "\n\t *** \n");
                tvHistory.setText(message);
            }
        });

        // Set address
        tvAddress.setText(getIpAddress());

        //Send message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityViewModel.sendData(String.valueOf(etEnterMessage.getText()));
                etEnterMessage.setText("");
            }
        });
    }

    // Get address
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Address: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
