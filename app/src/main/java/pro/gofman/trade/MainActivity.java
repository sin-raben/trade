package pro.gofman.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final String ADDRESS = "ws://pol-ice.ru:8890/ws";
    //private WebSocket webSocketConnection;
    //private MessageListenerInterface messageListener;

    private EditText editMessage;
    private Button btn_send;
    private TextView txtMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        txtMessages = (TextView) findViewById(R.id.txt_msg);


        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                txtMessages.append("FAB");
            }
        });

        Log.d("FAB", "Заработало");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, SyncData.class);
                intent.setAction("pro.gofman.trade.action.syncdata");
                intent.putExtra("pro.gofman.trade.extra.PARAM1", "1");
                intent.putExtra("pro.gofman.trade.extra.PARAM2", "2");

                startService( intent );
            }
        });

        //defineUIWidgets();
       // connectWebSocket(ADDRESS);
        //webSocketConnection.connect();


    }

    @Override
    protected void onDestroy() {
        //webSocketConnection.disconnect();
        //stopService( new Intent(MainActivity.this, SyncData.class)  );

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
   /* private void connectWebSocket(String address){
        messageListener = new MessageListenerTextView(txtMessages);
        webSocketConnection = new WebSocket(address, messageListener);
    }

    private void defineUIWidgets(){
        editMessage = (EditText) findViewById(R.id.ed_msg);
        btn_send = (Button) findViewById(R.id.btn_send);
        txtMessages = (TextView) findViewById(R.id.txt_msg);

        setUpButtons();
    }

    private void setUpButtons(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = editMessage.getText().toString().trim();

        Log.d("WS", message);
        if (message.isEmpty()) {
            return;
        }

        Log.d("WS", message);
        webSocketConnection.sendMessage(message);
        editMessage.setText("");
    }*/


}
