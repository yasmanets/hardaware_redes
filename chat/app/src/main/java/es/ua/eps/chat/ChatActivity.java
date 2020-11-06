package es.ua.eps.chat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.ua.eps.chat.Message.Message;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import es.ua.eps.chat.Adapter.MessageAdapter;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private String name;
    private String ipDestiny;
    private Thread serverThread = null;
    private Thread thread = null;
    private Handler handler;
    private InetAddress inetAddress;
    public static final int SERVER_PORT = 4200;

    private List<Message> messages;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button sendButton;
    private EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getIntentValues();
        initVariables();
        initChat();
    }

    // Función encargada de unicializar las variables.
    private void initVariables() {
        handler = new Handler();
        sendButton = findViewById(R.id.send);
        editMessage = findViewById(R.id.message);
        sendButton.setOnClickListener(this);

        messages = new ArrayList<>();
        recyclerView = findViewById(R.id.messagesList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messages, R.layout.message_item, (messageClicked, position) -> {
            deleteDialog(position);
        });
        recyclerView.setAdapter(adapter);
    }

    private void getIntentValues() {
        Intent intent = getIntent();
        name = intent.getStringExtra("nickname");
        ipDestiny = intent.getStringExtra("ipDestiny");
        try {
            inetAddress = InetAddress.getByName(ipDestiny);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void initChat() {
        Thread server = new Thread(new Server());
        server.start();
        return;
    }

    @Override
    public void onClick(View v) {
        String message = editMessage.getText().toString().trim();
        ClientTask client = new ClientTask();
        client.execute(inetAddress.getHostAddress(), message);
        editMessage.setText("");
    }

    /*
        Función encargada de mostrar y controlar el dialog  después de pulsar
        sobre un mensaje.
     */
     public void deleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.deletionAsk);
        builder.setPositiveButton("OK", (dialog, which) -> {
            messages.remove(position);
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /* ---------------------------------------------*/
    /*
        Esta función inicia el Socket que da vida al servidor. Inicia un hilo sobre el que
        siempre estará escuchando el servidor salvo que se produzca un error o se cierre la
        aplicación.

        El servidor, está escuchando los posibles mensajes que lleguen de un cliente.
     */
    class Server implements Runnable {
        ServerSocket serverSocket;
        Socket socket;
        DataInputStream dataInputStream;
        String message;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream((socket.getInputStream()));
                    message = dataInputStream.readUTF();
                    if (message == null || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        socket.close();
                        serverSocket.close();
                        message = getText(R.string.serverDisconnected).toString();
                        break;
                    }
                    showMessage(name, message, Color.parseColor("#2962FF"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Clase que extiende de AsyncTask para poder ejecutar un hilo "Cliente" y enviar
        los mensajes en segundo plane. De esta forma no se bloquea el hilo principal y
        puede seguir recibiendo mensajes de otro cliente.
     */
    class ClientTask extends AsyncTask<String,Void,String> {

        Socket client;
        DataOutputStream dataOutputStream;
        String ip, message;

        /*
            Función que inicia la comunicación hacia un cliente, para ello hay que pasar
            una IP de destino y el puerto. Es capaz de procesar un mensaje y enviarlo a través
            de su OutputStream.
         */
        @Override
        protected String doInBackground(String... params) {
            ip = params[0];
            message = params[1];
            try {
                inetAddress = InetAddress.getByName(ip);
                client = new Socket(inetAddress, SERVER_PORT);
                dataOutputStream = new DataOutputStream(client.getOutputStream());
                if (message == null || message.trim().isEmpty()) {
                    message = getText(R.string.emptyMessage).toString();
                }
                dataOutputStream.writeUTF(message);
                showMessage(getText(R.string.me).toString(), message, Color.parseColor("#448AFF"));
                dataOutputStream.close();
                client.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /*
        Función encargarda de prpcesar el mensaje en la vista. Cuando le llega un mensaje,
        lo añade a la lista de mensajes para que el RecyclerView pueda inicializar
        sus propiedades y mostrarlo al usuario.
     */
    private void showMessage(String nick, String message, int color) {
        handler.post(() -> {
            Message msg = new Message(nick, message, color);
            messages.add(msg);
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(messages.size() - 1);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != serverThread) {
            serverThread.interrupt();
            serverThread = null;
        }
    }
}

