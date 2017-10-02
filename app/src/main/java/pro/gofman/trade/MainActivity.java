package pro.gofman.trade;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Toast;

//import com.mikepenz.fastadapter.adapters.FastItemAdapter;
//import com.mikepenz.iconics.IconicsDrawable;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import pro.gofman.trade.Coords.CoordsActivity;
import pro.gofman.trade.Countragents.DeliveryPointActivity;
import pro.gofman.trade.Docs.DocsActivity;
import pro.gofman.trade.Items.ItemsActivity;
import pro.gofman.trade.PrintForms.UPD;

public class MainActivity extends AppCompatActivity {

    //https://www.youtube.com/watch?v=LNF_yho6ook

    private static final String GPS_MONITORING_STATUS = "GPSMonitoringStatus";
    private int PERMISSION_REQUEST_CODE = 0;

    Drawer dw = null;
    int dwItemSelected = -1;
    boolean bGPSMonitoringStatus = false;

    private DB db;
    private JSONObject connectionData;
    private JSONObject userData;

    private Intent SyncDataIntent = null;
    private SyncDataReceive syncDataReceive;
    private JSONObject SyncDataResult = new JSONObject();


    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTheme(R.style.AppThemeBlue);
        super.onSaveInstanceState(outState);

        Log.i("GPSMonitoring", String.valueOf(bGPSMonitoringStatus));
        outState.putBoolean(GPS_MONITORING_STATUS, bGPSMonitoringStatus);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        bGPSMonitoringStatus = savedInstanceState.getBoolean(GPS_MONITORING_STATUS, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ( requestCode == PERMISSION_REQUEST_CODE )  {

            for (int i = 0; i < permissions.length; i++ ) {

                if ( permissions[i].equals(Manifest.permission.READ_PHONE_STATE) ) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            String imei = tm.getImei();
                        } else {
                            String imei = tm.getDeviceId();
                        }

                        Toast.makeText(this, "Доступ к телефону разрешен! ", Toast.LENGTH_LONG).show();
                    }
                }

                if ( permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) ) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    }

                }


            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null ) {
            bGPSMonitoringStatus = savedInstanceState.getBoolean(GPS_MONITORING_STATUS, false);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // https://github.com/firebase/quickstart-android/tree/master/messaging


        /*
            При первом подключении отправляем IMEI, который является токеном, пользователь вводит логин и пароль
            и вытягиваем информацию о пользователе
         */
        db = Trade.getWritableDatabase();



        try {
            // Берем с базы данных информацию подключения к серверу и пользователе
            connectionData = new JSONObject( db.getOptions( DB.OPTION_CONNECTION ) );
            userData = new JSONObject( db.getOptions( DB.OPTION_AUTH ) );
            // При авторизации надо отправить на сервер, FCM Token, чтобы можно было пушить на устройство
            userData.put( Protocol.FCM_TOKEN, Trade.getFcmToken() );

            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_PHONE_STATE }, PERMISSION_REQUEST_CODE );
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        AccountHeader ah = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withCompactStyle(true)
                //.withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(userData.optString("FullName")).withEmail(userData.optString("Email")) //.withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();




        DrawerBuilder dwb = new DrawerBuilder(this);
        dwb.withActivity(this);
        dwb.withDrawerLayout(R.layout.material_drawer_fits_not);
        dwb.withDisplayBelowStatusBar(true);
        dwb.withRootView(R.id.drawer_layout);
        dwb.withToolbar(toolbar);
        dwb.withActionBarDrawerToggle(true);
        dwb.withActionBarDrawerToggleAnimated(true);

        dwb.withAccountHeader( ah );

        dwb.addDrawerItems(
                new ExpandableDrawerItem().withName("Новости").withIcon(R.drawable.items).withIdentifier(23).withSelectable(false).withSubItems(
                        new SecondaryDrawerItem()
                                .withName("Акции")
                                .withLevel(2)
                                .withIcon(R.drawable.worker)
                                .withIdentifier(2008)
                ),

                new ExpandableDrawerItem().withName("Справочники").withIcon(R.drawable.items).withIdentifier(19).withSelectable(false).withSubItems(
                        new SecondaryDrawerItem()
                                .withName("Точки доставки")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2007),

                        new SecondaryDrawerItem()
                                .withName("Номенклатура")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2000),

                        new SecondaryDrawerItem()
                                .withName("Прайс-листы")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2001)

                ),

                new ExpandableDrawerItem().withName("Документы").withIcon(R.drawable.items).withIdentifier(20).withSelectable(false).withSubItems(
                        new SecondaryDrawerItem()
                                .withName("Заказ покупателя")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2002),

                        new SecondaryDrawerItem()
                                .withName("Оплата покупателя")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2003),

                        new SecondaryDrawerItem()
                                .withName("Фотография")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2004)
                ),

                new ExpandableDrawerItem().withName("Операции").withIcon(R.drawable.items).withIdentifier(21).withSelectable(false).withSubItems(
                        new SecondaryDrawerItem()
                                .withName("Cинхронизация")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2009),

                        new SecondaryDrawerItem()
                                .withName("Полная синхронизация")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2005)
                ),

                new ExpandableDrawerItem().withName("Мониторинг").withIcon(R.drawable.items).withIdentifier(22).withSelectable(false).withSubItems(
                        new SecondarySwitchDrawerItem()
                                .withName("Рубильник")
                                .withIcon(R.drawable.document)
                                .withLevel(2)
                                .withChecked(bGPSMonitoringStatus)
                                .withOnCheckedChangeListener(onCheckedChangeListener),

                        new SecondaryDrawerItem()
                                .withName("Координаты")
                                .withLevel(2)
                                .withIcon(R.drawable.document)
                                .withIdentifier(2006)
                )
        );

        dwb.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                int id = (int)drawerItem.getIdentifier();

                if ( drawerItem.isSelectable() ) {

                    switch ( id ) {
                        case 2000: {
                            // Номенклатура
                            Intent i = new Intent( MainActivity.this, ItemsActivity.class );
                            startActivity( i );

                            break;
                        }
                        case 2001: {
                            // Прайс-листы
                            Toast.makeText(view.getContext(), R.string.door, Toast.LENGTH_SHORT).show();

                            break;
                        }
                        case 2002: {
                            // Заказ покупателя
                            Intent i = new Intent( MainActivity.this, DocsActivity.class);
                            startActivity( i );

                            break;
                        }
                        case 2003: {
                            // Оплаты покупателя
                            Toast.makeText(view.getContext(), R.string.door, Toast.LENGTH_SHORT).show();

                            break;
                        }
                        case 2004: {
                            // Фотографии
                            Toast.makeText(view.getContext(), R.string.door, Toast.LENGTH_SHORT).show();


                            Cursor c = db.rawQuery("SELECT lc_id, lс_stime, lc_billsec, lc_phone, lc_name, lc_incoming FROM log_calls", null);
                            Log.d("CALLS", "SELECT lc_id, lс_stime, lc_billsec, lc_phone, lc_name, lc_incoming FROM log_calls" );
                            if ( c != null ) {
                                if ( c.moveToFirst() ) {
                                    do {

                                        Log.d("CALLS", String.valueOf(c.getLong(c.getColumnIndex("lc_id"))) + " " + c.getString( c.getColumnIndex("lс_stime") )  + " " + c.getString( c.getColumnIndex("lc_phone") )  );


                                    } while ( c.moveToNext() );

                                    c.close();
                                }
                            }

                            break;
                        }

                        case 2005: {
                            // Синхронизация полная
                            try {
                                // Параметры для соединения с сервером
                                connectionData.put( Protocol.USER_DATA, userData );
                                connectionData.put( Protocol.COMMAND_SYNC, true );
                                // Признак полной синхронизации (перед загрузкой будет очищать таблицы)
                                connectionData.put( Protocol.FULL_SYNC, true );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if ( SyncDataIntent == null ) {
                                SyncDataIntent = new Intent(MainActivity.this, SyncData.class);
                                SyncDataIntent.setAction( Trade.SERVICE_SYNCDATA );
                                SyncDataIntent.putExtra( Trade.SERVICE_PARAM, connectionData.toString() );
                                startService( SyncDataIntent );
                            } else {
                                Toast.makeText(view.getContext(), "Синхронизация уже запущена!", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        }

                        case 2009: {
                            // Синхронизация только изменения
                            try {
                                // Параметры для соединения с сервером
                                connectionData.put( Protocol.USER_DATA, userData );
                                connectionData.put( Protocol.COMMAND_SYNC, true );
                                connectionData.put( Protocol.FULL_SYNC, false );

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if ( SyncDataIntent == null ) {

                                SyncDataIntent = new Intent(MainActivity.this, SyncData.class);
                                SyncDataIntent.setAction( Trade.SERVICE_SYNCDATA );
                                SyncDataIntent.putExtra( Trade.SERVICE_PARAM, connectionData.toString() );
                                startService( SyncDataIntent );

                            } else {
                                Toast.makeText(view.getContext(), "Синхронизация уже запущена!", Toast.LENGTH_SHORT).show();
                            }


                            break;
                        }

                        case 2006: {
                            // Координаты
                            Log.i("DRAWER", "2006");
                            Intent i = new Intent(MainActivity.this, CoordsActivity.class);
                            startActivity( i );

                            break;
                        }

                        case 2007: {
                            // Точки доставки
                            Intent i = new Intent(MainActivity.this, DeliveryPointActivity.class);
                            startActivity( i );

                            break;
                        }

                        case 2008: {
                            // Акции
                            Toast.makeText(view.getContext(), R.string.door, Toast.LENGTH_SHORT).show();

                            Log.i("CD", "onItemClick: " + Environment.getExternalStorageDirectory().getPath() );

                            UPD t = new UPD();
                            try {



                                t.createPdf( "file2.pdf");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            break;
                        }

                        default:

                            break;

                    }

                } // isSelectable
                return false;
            }
        });

        dw = dwb.build();

        //Log.i("FCM_TOKEN", Trade.getFcmToken() );


        // Уведомления
        // Переход на другие активити в зависимости параметров в уведомлениях
        if (getIntent().getExtras() != null) {

            String cmd = getIntent().getExtras().getString( Protocol.COMMAND_SERVER );

            switch ( cmd ) {
                case Protocol.CMD_OPENITEMS: {

                    // Номенклатура
                    String s = "";

                    OpenItems( s );
                    break;
                }

            }

            /*
            Log.i("ПРИВЕТ", "dfdf");

            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.i("MainActivity", "Key: " + key + " Value: " + value);
            }
            */
        }

        syncDataReceive = new SyncDataReceive();
        IntentFilter intentFilter = new IntentFilter(
            SyncData.ACTION_SYNCDATA
        );
        registerReceiver( syncDataReceive, intentFilter );











    }

    class SyncDataReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String SyncData = intent.getStringExtra( pro.gofman.trade.SyncData.EXTRA_RESULT );

            try {
                SyncDataResult = new JSONObject( SyncData );

                // Сервис синхронизация закончил свою работу
                if ( SyncDataResult.optBoolean( "finish", false ) ) {
                    stopService( SyncDataIntent );
                    SyncDataIntent = null;

                    Toast.makeText( context, "Синхронизация завершена!", Toast.LENGTH_SHORT).show();
                }

            } catch ( JSONException e ) {

            }


        }
    }

    private void OpenItems( String s ) {

        Intent i = new Intent(MainActivity.this, ItemsActivity.class);
        if ( !s.isEmpty() ) {
            i.setAction(ItemsActivity.ITEMS_ACTION);
            i.putExtra(ItemsActivity.ITEMS_PARAM, s );
        }
        startActivity( i );
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

            bGPSMonitoringStatus = isChecked;

            if ( ContextCompat.checkSelfPermission(buttonView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_CODE );
            }

            Intent intent = new Intent(Trade.getAppContext(), SyncData.class);
            intent.putExtra( Trade.SERVICE_PARAM, "{}");

            if ( isChecked ) {
                intent.setAction(SyncData.ACTION_LOGCOORD);
                startService(intent);
            } else {
                intent.setAction(SyncData.ACTION_LOGCOORD_STOP);
                startService(intent);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    @Override
    public void onBackPressed() {
        if ( dw != null && dw.isDrawerOpen() ) {
            dw.closeDrawer();
        } else {
            super.onBackPressed();
        }

    }
}





