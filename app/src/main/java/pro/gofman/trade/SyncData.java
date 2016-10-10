package pro.gofman.trade;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.database.DatabaseUtilsCompat;
import android.util.Log;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SyncData extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    protected static final String ACTION_SYNCDATA = "pro.gofman.trade.action.syncdata";
    protected static final String ACTION_LOGCOORD = "pro.gofman.trade.action.logcoord";

    protected static final String EXTRA_PARAM1 = "pro.gofman.trade.extra.PARAM1";


    private DB db; //;
    private String sql = "";
    private JSONObject result = null;

    private String head = "";
    private JSONObject body = null;

    private boolean mAuth = false;

    private NotificationManager mNM;


    public SyncData() {
        super("SyncData");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = Trade.getWritableDatabase();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSyncData(Context context, JSONObject j) {
        Intent intent = new Intent(context, SyncData.class);
        intent.setAction(ACTION_SYNCDATA);
        intent.putExtra(EXTRA_PARAM1, j.toString() );
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionLogCoord(Context context, JSONObject j) {
        Intent intent = new Intent(context, SyncData.class);
        intent.setAction(ACTION_LOGCOORD);
        Log.i("LOG", j.toString() );
        intent.putExtra(EXTRA_PARAM1, j.toString());
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_SYNCDATA.equals(action)) {
                JSONObject p;
                try {
                    p = new JSONObject( intent.getStringExtra(EXTRA_PARAM1) );
                    handleActionSyncData(p);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (ACTION_LOGCOORD.equals(action)) {
                JSONObject p;
                try {
                    p = new JSONObject( intent.getStringExtra(EXTRA_PARAM1) );
                    handleActionLogCoord(p);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSyncData(JSONObject p) throws IOException {
        // TODO: Handle action SyncData
        Log.i("SyncData", "SyncData стартанул");



        WebSocket ws = new WebSocketFactory().createSocket("ws://pol-ice.ru:8890/ws");
        WebSocket webSocket = ws.addListener(new WebSocketListener() {
            @Override
            public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {

            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                Log.i("WS", "Connected");
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                Log.i("WS", "onConnectError");
            }

            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                Log.i("WS", "onConnectError");
            }

            @Override
            public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                Log.i("WS", "Text: " + text);

                result = new JSONObject(text);
                head = result.getString("head");
                body = result.getJSONObject("body");


                switch (head) {
                    case "authUser": {
                        // успешная авторизация запуск процедуры обмена
                        mAuth = body.optBoolean("result", false);

                        if ( mAuth ) {
                            Log.i("WS", "sendCoord");
                            // Отправляем координаты
                            //sendCoord(websocket);

                            // Запрашиваем номенклатуру
                            getItems(websocket);

                            // Запрашиваем контрагентов
                            //getCountragents(websocket);

                        }

                        break;
                    }

                    case "getItems": {
                        /*
                            {"items":[{Номенклатура}],"count":1000}
                         */
                        ContentValues cv;

                        // Загрузка номенклатуры
                        if ( body.has(Protocol.ITEMS) ) {

                            db.execSQL("DELETE FROM items");
                            JSONArray items = body.getJSONArray(Protocol.ITEMS);
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject t = items.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put("i_id", t.getInt("i_id"));
                                cv.put("i_name", t.getString("i_name"));

                                db.insert("items", cv);
                                Log.i("TOV", cv.getAsString("i_name"));

                            }
                            Log.i("SQL", "Всего записей: " + String.valueOf(db.getItemsCount()));
                        }


                        // Загрузка типов групп
                        if ( body.has(Protocol.ITEM_GROUP_TYPES) ) {

                            db.execSQL("DELETE FROM item_group_types");
                            // Загрузка типы группировок
                            JSONArray igt = body.getJSONArray(Protocol.ITEM_GROUP_TYPES);
                            for (int i = 0; i < igt.length(); i++) {
                                JSONObject t = igt.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put("igt_id", t.getInt("igt_id"));
                                cv.put("igt_name", t.getString("igt_name"));

                                db.insert("item_group_types", cv);
                                Log.i("GROUPTYPES", cv.getAsString("igt_name"));

                            }
                            //Log.i("SQL", "Всего записей: " + String.valueOf( db.getItemsCount() ) );
                        }

                        // Загрузка групп
                        if ( body.has(Protocol.ITEM_GROUPS) ) {

                            db.execSQL("DELETE FROM item_groups");
                            // Загрузка типы группировок
                            JSONArray ig = body.getJSONArray(Protocol.ITEM_GROUPS);
                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put("ig_id", t.getInt("ig_id"));
                                cv.put("igt_id", t.getInt("igt_id"));
                                cv.put("ig_name", t.getString("ig_value"));


                                db.insert("item_groups", cv);
                                Log.i("GROUPS", cv.getAsString("igt_id") + " " + cv.getAsString("ig_name"));

                            }

                            //Log.i("SQL", "Всего записей: " + String.v
                        }

                        // Загрузка связей групп и номенклатуры
                        if ( body.has(Protocol.LINK_ITEM_GROUPS) ) {

                            db.execSQL("DELETE FROM link_item_groups");

                            JSONArray ig = body.getJSONArray(Protocol.LINK_ITEM_GROUPS);
                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put( "lig_id", t.getInt("lig_id") );
                                cv.put( "i_id", t.getInt("i_id") );
                                cv.put( "igt_id", t.getInt("igt_id") );
                                cv.put( "ig_id", t.getInt("ig_id") );

                                db.insert("link_item_groups", cv);
                                Log.i("LINK_GROUPS", cv.getAsString("lig_id") + " " + cv.getAsString("i_id"));
                            }
                        }

                        // Загрузка поисковых строк
                        if ( body.has(Protocol.ITEMS_SEARCH) ) {

                            Log.d("Search", "2222");
                            db.execSQL("DELETE FROM item_search");

                            JSONArray ig = body.getJSONArray(Protocol.ITEMS_SEARCH);
                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put( "i_id", t.getInt("i_id") );
                                cv.put( "value", t.getString("search") );


                                db.insert("item_search", cv);
                                Log.i("ITEMS_SEARCH", cv.getAsString("i_id") );
                            }
                        }

                        break;
                    }

                    case "getCountragents": {
                        /*
                            {"items":[{Торговая точка}],"count":600}
                         */

                        Log.i("returnCountragent", body.toString() );

                        break;
                    }

                    case "getMbClient": {
                        /*
                            {"items":[{Контрагент}],"count":600}
                         */
                        break;
                    }

                    default:

                        break;
                }

            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {

            }

            @Override
            public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {

            }

            @Override
            public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {

            }

            @Override
            public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {

            }

            @Override
            public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {

            }

            @Override
            public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {

            }

            @Override
            public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {

            }

            @Override
            public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {

            }

            public void sendCoord(WebSocket websocket) throws Exception {

                Log.i("setCoord", "1");

                websocket.sendText( db.getCoords2().toString() );
            }

            public void getItems(WebSocket websocket) throws Exception {
            /*
                "head":"getItems",
                "body": {
                    "items": "all",
                            "itemsGroupType": "all",
                            "itemsGroup": "all",
                            "itemsUnitType": "all",
                            "itemsUnit": "all"
                }
            */

                JSONObject r = new JSONObject();
                r.put( "head", "getItems" );

                JSONObject body = new JSONObject();
                body.put( Protocol.ITEMS, "all" );
                body.put( Protocol.ITEMS_SEARCH, "all");
                //body.put( Protocol.ITEM_GROUP_TYPES, "all" );
                //body.put( Protocol.ITEM_GROUPS, "all" );
                //body.put( Protocol.LINK_ITEM_GROUPS, "all" );

                r.put( "body", body );

                Log.i("getItems", r.toString() );
                websocket.sendText( r.toString() );
            }

            public void getCountragents(WebSocket websocket) throws Exception {
                JSONObject r = new JSONObject();
                r.put( "head", "getCountragents" );

                JSONObject body = new JSONObject();
                body.put( "countragents", "all" );

                r.put( "body", body );

                Log.i("getCountragent", r.toString() );
                websocket.sendText( r.toString() );
            }


        });

        try
        {


            // Соединение с сервером
            ws.connect();

            // Проверка соединения
            if ( ws.isOpen() ) {

                Log.i("auth", p.toString());
                // Формирование и отправка команды авторизации
                ws.sendText(
                        new JSONObject()
                                .put("head", "authUser")
                                .put("body", p )
                                .toString()

                );
            }
        }
        catch (OpeningHandshakeException e) {
            // A violation against the WebSocket protocol was detected
            // during the opening handshake.
        }
        catch (WebSocketException e)
        {
            // Failed to establish a WebSocket connection.
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLogCoord(JSONObject p) {

        //long minTime = 60 * 60 * 60 * 1000;  // 1 час
        long minTime = 60 * 1000;  // 1 час
        float minDistance = 0;


        NotificationCompat.Builder mNB = new NotificationCompat.Builder(this)
                .setOngoing( true )
                .setSmallIcon( R.drawable.worker )
                .setContentTitle( "Собираем координаты" )
                .setContentText( "Необходимо чтобы датчик GPS был включен" )
                .setWhen( System.currentTimeMillis() );

        mNM.notify( 1, mNB.build() );
        //startForeground(777, mNB.build() );


        if ( p.optLong("minTime") > 0 ) {
            minTime = p.optLong("minTime");
        }
        if ( p.optInt("minDistance") > 0 ) {
            minDistance = (float) p.optLong("minDistance");
        }

        Log.i("LOG", "1");

        Log.i("LOG", "2");

        LocationListener ll = new LocationListener() {



            @Override
            public void onLocationChanged(Location location) {
                Log.i("LOG", "7");

                DB db = Trade.getWritableDatabase();

                if ( location != null ) {
                    Log.i("GPS", "Широта="+location.getLatitude());
                    Log.i("GPS", "Долгота="+location.getLongitude());

                    Log.i("LOG", "8");

                    long time = System.currentTimeMillis() / 1000;
                    String sql = "INSERT INTO coords (\"lat\", \"lon\", \"atime\", \"provider\" ) VALUES (";
                    sql += "\"" + location.getLatitude() + "\", \"" + location.getLongitude() + "\"," + String.valueOf( time ) + ", \"" + location.getProvider() + "\"";
                    sql += ");";


                    Log.i("TIME", String.valueOf( System.currentTimeMillis() ) + " -- " + String.valueOf(time) );

                    Log.i("SQL", sql);
                    db.execSQL(sql);
                    Log.i("LOG", "11");
                }
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Log.i("LOG", "3");

        LocationManager lm = (LocationManager) getSystemService( LOCATION_SERVICE );
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("LOG", "4");
            return;
        }
        //Log.i("LOG", "5");
        lm.requestLocationUpdates( LocationManager.GPS_PROVIDER, minTime, minDistance, ll, Looper.getMainLooper() );
        //Log.i("LOG", "6");

    }
}
