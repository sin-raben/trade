package pro.gofman.trade;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

    private LocationListener ll;

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
                Intent intent = new Intent(MainActivity.this, SyncData.class);
                intent.setAction(SyncData.ACTION_LOGCOORD);
                intent.putExtra("pro.gofman.trade.extra.PARAM1", "{}");
                startService( intent );
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, SyncData.class);
                intent.setAction("pro.gofman.trade.action.syncdata");
                intent.putExtra("pro.gofman.trade.extra.PARAM1", "{}");

                startService( intent );
            }
        });

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

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
