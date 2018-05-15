package com.admin.appclientchat;

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

public class MainActivity extends AppCompatActivity {

    // PORT
    private static final int PORT = 6789;

    // UI components
    private EditText etAddress;
    private Button btnConnect;
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
        etAddress = findViewById(R.id.etAddress);
        btnConnect = findViewById(R.id.btnConnect);
        tvHistory = findViewById(R.id.tvHistory);
        etEnterMessage = findViewById(R.id.etEnterMessage);
        btnSend = findViewById(R.id.btnSend);

        // Create message builder
        message = new StringBuilder();

        // Create MainActivityViewModel
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // Observe for data
        liveData = mainActivityViewModel.getData();
        liveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                message.append(s + "\n\t *** \n");
                tvHistory.setText(message);

            }
        });

        // Connect
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityViewModel.openConnection(String.valueOf(etAddress.getText().toString()), PORT);
            }
        });

        //Send message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityViewModel.sendData(String.valueOf(etEnterMessage.getText()));
                etEnterMessage.setText("");
            }
        });
    }
}
