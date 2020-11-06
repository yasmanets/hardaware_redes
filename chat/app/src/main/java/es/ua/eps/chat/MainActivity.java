package es.ua.eps.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.format.Formatter;
import android.widget.Toast;

import es.ua.eps.chat.Validators.IpAddressValidator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nickname;
    private EditText ipDestiny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView host = findViewById(R.id.host);
        nickname = findViewById(R.id.nicknameEditText);
        ipDestiny = findViewById(R.id.ipEditText);
        Button startChat = findViewById(R.id.start);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        host.setText("HOST: " + Formatter.formatIpAddress(ipAddress));
        startChat.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        String nick = nickname.getText().toString();
        String ip = ipDestiny.getText().toString();
        if(checkInputs(nick, ip)) {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("nickname", nick);
            intent.putExtra("ipDestiny", ip);
            startActivity(intent);
        }
    }

    /*
        Esta es la función encargada de validar las entradas que introduce el
        ususario.
     */
    private Boolean checkInputs(String nickname, String ip) {
        if (nickname.trim().isEmpty()) {
            Toast.makeText(this, getText(R.string.invalidNick).toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        if (ip.trim().isEmpty() || !IpAddressValidator.isValid(ip)) {
            Toast.makeText(this, getText(R.string.invalidIp).toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}