package pro.gofman.trade;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String GPS_MONITORING_STATUS = "GPSMonitoringStatus";

    Drawer dw = null;
    int dwItemSelected = -1;
    boolean bGPSMonitoringStatus = false;

    private DB db;

    final String ADDRESS = "ws://pol-ice.ru:8890/ws";
    private JSONObject userData;





    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.i("GPSMonitoring", String.valueOf(bGPSMonitoringStatus));
        outState.putBoolean(GPS_MONITORING_STATUS, bGPSMonitoringStatus);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null ) {
            bGPSMonitoringStatus = savedInstanceState.getBoolean(GPS_MONITORING_STATUS, false);
        }

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        /*
            При первом подключении отправляем IMEI, который является токеном, пользователь вводит логин и пароль
            и вытягиваем информацию о пользователе
         */
        db = Trade.getWritableDatabase();


        try {
            // Берем с базы данных информацию о пользователе
            userData = new JSONObject( db.getOptions( DB.OPTION_AUTH ) );
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
        dwb.withActionBarDrawerToggleAnimated(true);

        dwb.withAccountHeader( ah );

        dwb.addDrawerItems(
                new ExpandableDrawerItem().withName("Справочники").withIcon(R.drawable.items).withIdentifier(19).withSelectable(false).withSubItems(
                        new SecondaryDrawerItem().withName("Номенклатура").withLevel(2).withIcon(R.drawable.document).withIdentifier(2000),
                        new SecondaryDrawerItem().withName("Прайс-листы").withLevel(2).withIcon(R.drawable.document).withIdentifier(2001)
                ),
                new ExpandableDrawerItem().withName("Документы").withIcon(R.drawable.items).withIdentifier(20).withSelectable(false).withSubItems(
                        new SecondaryDrawerItem().withName("Заказ покупателя").withLevel(2).withIcon(R.drawable.document).withIdentifier(2002),
                        new SecondaryDrawerItem().withName("Оплата покупателя").withLevel(2).withIcon(R.drawable.document).withIdentifier(2003),
                        new SecondaryDrawerItem().withName("Фотография").withLevel(2).withIcon(R.drawable.document).withIdentifier(2004)
                ),
                new ExpandableDrawerItem().withName("Операции").withIcon(R.drawable.items).withIdentifier(21).withSelectable(false).withSubItems(
                        new SecondaryDrawerItem().withName("Полная синхронизация").withLevel(2).withIcon(R.drawable.document).withIdentifier(2005)
                ),
                // new SwitchDrawerItem().withName("Мониторинг").withIcon(R.drawable.items).withChecked(false).withOnCheckedChangeListener(onCheckedChangeListener)
                new ExpandableDrawerItem().withName("Мониторинг").withIcon(R.drawable.items).withIdentifier(22).withSelectable(false).withSubItems(
                        new SecondarySwitchDrawerItem().withName("Рубильник").withIcon(R.drawable.document).withLevel(2).withChecked(bGPSMonitoringStatus).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SecondaryDrawerItem().withName("Координаты").withLevel(2).withIcon(R.drawable.document).withIdentifier(2006)
                )
        );

        dwb.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                int id = (int)drawerItem.getIdentifier();

                if ( drawerItem.isSelectable() ) {

                    switch ( id ) {
                        case 2000: {
                            Log.i("FRAGMENT", "2000");
                            ItemsFragment f2000 = new ItemsFragment();

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment, f2000)
                                    .commit();


                            break;
                        }


                        case 2005: {

                            Intent intent = new Intent(MainActivity.this, SyncData.class);
                            intent.setAction("pro.gofman.trade.action.syncdata");
                            intent.putExtra("pro.gofman.trade.extra.PARAM1", userData.toString() );

                            startService( intent );

                            break;
                        }

                        case 2006: {

                            Log.i("FRAGMENT", "2006");
                            CoordsFragment f2000 = new CoordsFragment();

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment, f2000)
                                    .commit();


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

        /*
                Intent intent = new Intent(MainActivity.this, SyncData.class);
                intent.setAction(SyncData.ACTION_LOGCOORD);
                intent.putExtra("pro.gofman.trade.extra.PARAM1", "{}");
                startService( intent );


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });

        */

    }
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

            bGPSMonitoringStatus = isChecked;

            Intent intent = new Intent(MainActivity.this, SyncData.class);
            intent.setAction(SyncData.ACTION_LOGCOORD);
            intent.putExtra("pro.gofman.trade.extra.PARAM1", "{}");

            if ( isChecked ) {
                startService(intent);
            } else {
                stopService(intent);
            }
        }
    };

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

    @Override
    public void onBackPressed() {
        if (dw != null && dw.isDrawerOpen()) {
            dw.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


}
