package pro.gofman.trade;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.ServiceCompat;
import android.support.v4.database.DatabaseUtilsCompat;
import android.util.Log;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.ThreadType;
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
import java.lang.reflect.Array;
import java.util.Arrays;
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
    protected static final String ACTION_LOGCOORD_STOP = "pro.gofman.trade.action.logcoord_stop";

    protected static final String EXTRA_PARAM1 = "pro.gofman.trade.extra.param";


    private DB db; //;
    private String sql = "";
    private JSONObject result = null;

    private String head = "";
    private JSONObject body = null;
    private Integer syncid = 0;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        intent.setAction( Trade.SERVICE_SYNCDATA );
        intent.putExtra( Trade.SERVICE_PARAM, j.toString() );

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
        intent.setAction( Trade.SERVICE_LOGCOORD );
        intent.putExtra( Trade.SERVICE_PARAM, j.toString());

        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if ( Trade.SERVICE_SYNCDATA.equals(action) ) {

                Notification n;
                NotificationCompat.Builder mNB = new NotificationCompat.Builder(Trade.getAppContext())
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.worker)
                        .setContentTitle("Синхронизация")
                        .setContentText("Подождите окончания синхронизации")
                        .setWhen(System.currentTimeMillis());


                n = mNB.build();

                JSONObject p;
                try {
                    p = new JSONObject( intent.getStringExtra( Trade.SERVICE_PARAM ) );

                    handleActionSyncData(p, n);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                if ( Trade.SERVICE_LOGCOORD.equals(action) ) {
                    JSONObject p;
                    try {

                        Notification n;
                        NotificationCompat.Builder mNB = new NotificationCompat.Builder(this)
                                .setOngoing(true)
                                .setSmallIcon(R.drawable.worker)
                                .setContentTitle("Собираем координаты")
                                .setContentText("Необходимо чтобы датчик GPS был включен")
                                .setWhen(System.currentTimeMillis());


                        n = mNB.build();


                        p = new JSONObject(intent.getStringExtra( Trade.SERVICE_PARAM ));
                        handleActionLogCoord(p, n);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    if (ACTION_LOGCOORD_STOP.equals(action)) {
                        Log.d("StopService", "123");
                        stopForeground(true);
                        stopSelf();
                    }
                }
            }
        }
    }


    private String getConnectionUrl(JSONObject c) {
        String r = "";

        try {
            r = c.getString("Protocol") + "://" + c.getString("Host") + ":" + String.valueOf( c.getInt("Port") ) + c.getString("Path");
        } catch (JSONException e) {
            return r;
        }

        return r;
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSyncData(final JSONObject p, Notification n) throws IOException {
        // TODO: Handle action SyncData
        Log.i("SyncData", "SyncData стартанул");

        final Boolean FullSync = p.optBoolean( Protocol.FULL_SYNC, false );




        startForeground(778, n);

        String url = "";
        try {
            url = getConnectionUrl( p.getJSONObject( Protocol.CONNECTION_BEGIN ) );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("SyncData", url);

        WebSocket ws = new WebSocketFactory().createSocket( url );
        //ws.setMaxPayloadSize()

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
                //Log.i("WS", "Text: " + text);

                result = new JSONObject(text);
                head = result.getString( Protocol.HEAD );
                syncid = result.optInt( Protocol.ID, 0 );
                body = result.getJSONObject( Protocol.BODY );

                ContentValues cv;
                switch (head) {
                    case Protocol.AUTH_USER: {
                        // Успешная авторизация запуск процедуры обмена
                        mAuth = body.optBoolean(Protocol.RESULT, false);

                        if ( mAuth ) {

                            // Делаем синхронизацию, инициатор пользователь
                            if ( p.optBoolean( Protocol.COMMAND_SYNC, false ) ) {

                                //Log.i("WS", "sendCoord");
                                // Отправляем координаты
                                //sendCoord(websocket);

                                // Запрос новостей
                                //getNews(websocket, par );

                                // Запрашиваем номенклатуру и всё что с ней связано
                                syncQuery( websocket, Protocol.SYNC_ITEMS );
                                syncQuery( websocket, Protocol.SYNC_ITEMGROUPTYPES );
                                syncQuery( websocket, Protocol.SYNC_ITEMGROUPS );
                                syncQuery( websocket, Protocol.SYNC_LINKITEMGROUP );
                                syncQuery( websocket, Protocol.SYNC_ITEMUNITS );
                                syncQuery( websocket, Protocol.SYNC_LINKITEMUNIT );
                                syncQuery( websocket, Protocol.SYNC_ITEMSEARCH );


                                // Запрашиваем контрагентов
                                syncQuery( websocket, Protocol.SYNC_COUNTERAGENTS );
                                syncQuery( websocket, Protocol.SYNC_DELIVERYPOINTS );
                                syncQuery( websocket, Protocol.SYNC_LINKCOUNTERAGENTPOINT );
                                syncQuery( websocket, Protocol.SYNC_COUNTERAGENTADDRESS );
                                syncQuery( websocket, Protocol.SYNC_POINTSEARCH );

                                // Запрашиваем цены
                                //getPrices(websocket);

                                // Запрашиваем остатки
                                //getAmounts(websocket);
                            }

                            // Делаем синхронизацию, инициатор сервер,
                            // CUSTOM_SYNC приходит через уведомления или системные изменения требующие отправки данных на сервер
                            if ( p.optBoolean( Protocol.CUSTOM_SYNC, false ) ) {

                                JSONObject obj = null;
                                // Параметр DATA должен быть типа JSONArray
                                for ( Integer i = 0; i < p.optJSONArray(Protocol.DATA).length(); i++ ) {

                                    obj = p.optJSONArray(Protocol.DATA).getJSONObject(i);
                                    JSONObject body = obj.optJSONObject(Protocol.BODY) != null ? obj.optJSONObject(Protocol.BODY) : getBody( obj.getString(Protocol.HEAD) );

                                    syncCustomQuery(websocket, obj.getString(Protocol.HEAD), body );

                                    // Надо придумать как разорвать соединение!!!
                                    // Нужно еще от сервера получить ответ и если положительный зачистить синхронизированные данные
                                    // Кол-во ответов должно быть равно количеству запросов



                                }

                            }

                    }

                        break;
                    }

                    case Protocol.SYNC_NEWS: {

                        syncFunction(websocket, Protocol.SYNC_NEWS, Protocol.DB_NEWS, Protocol.FIELDS_NEWS );

                        break;
                    }

                    // Номенклатура
                    case Protocol.SYNC_ITEMS: {
                        String[][] a = {
                                {"i_id", "i_id", "int"},
                                {"i_name", "i_name", "text"}
                        };
                        syncFunction(websocket, Protocol.SYNC_ITEMS, "items", a );

                        break;
                    }

                    // Типы групп номенклатуры
                    case Protocol.SYNC_ITEMGROUPTYPES: {
                        String[][] a = {
                                {"igt_id", "igt_id", "int"},
                                {"igt_name", "igt_name", "text"}
                        };

                        syncFunction(websocket, Protocol.SYNC_ITEMGROUPTYPES, "item_group_types", a );

                        break;
                    }
                    // Группы номенклатуры
                    case Protocol.SYNC_ITEMGROUPS: {
                        String[][] a = {
                                {"ig_id", "ig_id", "int"},
                                {"igt_id", "igt_id", "int"},
                                {"ig_name", "ig_value", "text"}
                        };

                        syncFunction(websocket, Protocol.SYNC_ITEMGROUPS, "item_groups", a );

                        break;
                    }

                    // Cвязи групп и номенклатуры
                    case Protocol.SYNC_LINKITEMGROUP: {
                        String[][] a = {
                                {"lig_id", "lig_id", "int"},
                                {"i_id", "i_id", "int"},
                                {"igt_id", "igt_id", "int"},
                                {"ig_id", "ig_id", "int"}
                        };

                        syncFunction(websocket, Protocol.SYNC_LINKITEMGROUP, "link_item_groups", a );

                        break;
                    }

                    // Единицы измерения
                    case Protocol.SYNC_ITEMUNITS: {
                        String[][] a = {
                                {"iut_id", "iut_id", "int"},
                                {"iut_name", "iut_name", "text"}
                        };

                        syncFunction(websocket, Protocol.SYNC_ITEMUNITS, "item_unit_types", a );

                        break;
                    }

                    // Связи номенклатуры и единиц измерения
                    case Protocol.SYNC_LINKITEMUNIT: {
                        String[][] a = {
                                {"iu_id", "iu_id", "int"},
                                {"i_id", "i_id", "int"},
                                {"iut_id", "iut_id", "int"},
                                {"iu_krat", "iu_krat", "int"},
                                {"iu_num", "iu_num", "int"},
                                {"iu_denum", "iu_denum", "int"},
                                {"iu_gros", "iu_gros", "int"},
                                {"iu_length", "iu_length", "int"},
                                {"iu_width", "iu_width", "int"},
                                {"iu_height", "iu_height", "int"},
                                {"iu_area", "iu_area", "int"},
                                {"iu_volume", "iu_volume", "int"},
                                {"iu_base", "iu_base", "int"},
                                {"iu_main", "iu_main", "int"}
                        };

                        syncFunction(websocket, Protocol.SYNC_LINKITEMUNIT, "item_units", a );

                        break;
                    }

                    // Поисковая строка номенклатуры
                    case Protocol.SYNC_ITEMSEARCH: {
                        String[][] a = {
                                {"i_id", "i_id", "int"},
                                {"value", "value", "text"}
                        };

                        syncFunction(websocket, Protocol.SYNC_ITEMSEARCH, "item_search", a );
                        break;
                    }

                    //  Контрагенты
                    case Protocol.SYNC_COUNTERAGENTS: {
                        String[][] a = {
                                {"ca_id", "ca_id", "int"},
                                {"ca_type", "ca_type", "int"},
                                {"ca_name", "ca_name", "text"},
                                {"ca_inn", "ca_inn", "text"},
                                {"ca_kpp", "ca_kpp", "text"}
                        };

                        syncFunction(websocket, Protocol.SYNC_COUNTERAGENTS, "countragents", a );

                        break;
                    }

                    // Точки доставки
                    case Protocol.SYNC_DELIVERYPOINTS: {
                        String[][] a = {
                                {"dp_id", "dp_id", "int"},
                                {"dp_name", "dp_name", "text"}
                        };

                        syncFunction(websocket, Protocol.SYNC_DELIVERYPOINTS, "point_delivery", a );
                        break;
                    }

                    // Связи точек доставки и контрагентов
                    case Protocol.SYNC_LINKCOUNTERAGENTPOINT: {
                        String[][] a = {
                                {"ca_id", "ca_id", "int"},
                                {"dp_id", "dp_id", "int"},
                                {"dp_active", "lcp_active", "int"}
                        };
                        syncFunction(websocket, Protocol.SYNC_LINKCOUNTERAGENTPOINT, "ca_dp_link", a );
                        break;
                    }

                    // Поисковые строки точек доставки
                    case Protocol.SYNC_POINTSEARCH: {
                        String[][] a = {
                                {"dp_id", "dp_id", "int"},
                                {"value", "value", "text"}
                        };
                        syncFunction(websocket, Protocol.SYNC_POINTSEARCH, "dp_search", a );
                        break;
                    }

                    // Адреса контрагентов и точек доставки
                    case Protocol.SYNC_COUNTERAGENTADDRESS: {
                        String[][] a = {
                                {"adr_id", "adr_id", "int"},
                                {"any_id", "any_id", "int"},
                                {"adrt_id", "adrt_id", "int"},
                                {"adr_str", "adr_str", "text"}
                        };
                        syncFunction(websocket, Protocol.SYNC_COUNTERAGENTADDRESS, "addresses", a );

                        break;
                    }

                    /*
                    case "getStocks": {

                        if (body.has(Protocol.STORES)) {

                            db.execSQL("DELETE FROM stores");
                            JSONArray ig = body.getJSONArray(Protocol.STORES);

                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put("sr_id", t.getInt("sr_id"));
                                cv.put("sr_type", t.getInt("sr_type"));
                                cv.put("sr_name", t.getString("sr_name"));


                                if (t.getInt("sr_id") > 0) {
                                    db.insert("stores", cv);
                                    Log.i("STORES", t.getString("sr_name"));
                                }
                            }
                        }

                        if (body.has(Protocol.LINK_STORES)) {

                            db.execSQL("DELETE FROM store_link");
                            JSONArray ig = body.getJSONArray(Protocol.LINK_STORES);

                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put("srl_parent", t.getInt("sr_id"));
                                cv.put("srl_child", t.getInt("sr_type"));
                                cv.put("srl_prior", t.getInt("sr_name"));


                                if (t.getInt("sr_id") > 0) {
                                    db.insert("store_link", cv);
                                    Log.i("LINK STORES", t.getString("sr_name"));
                                }
                            }
                        }


                        if (body.has(Protocol.STOCKS)) {

                            db.execSQL("DELETE FROM stocks");
                            JSONArray ig = body.getJSONArray(Protocol.STOCKS);

                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put("i_id", t.getInt("i_id"));
                                cv.put("sr_id", t.getInt("sr_id"));
                                cv.put("sc_amount", t.getInt("sc_amount"));


                                if (t.getInt("sc_amount") > 0) {
                                    db.insert("stocks", cv);
                                    Log.i("STOCKS", t.getString("sr_name"));
                                }
                            }
                        }


                    }

                    case "getPrices": {


                        //Log.i("getPrices", Boolean.toString(body.has(Protocol.PRICE)) );
                        // Загрузка цен
                        if ( body.has(Protocol.PRICE) ) {

                            db.execSQL("DELETE FROM price");
                            JSONArray ig = body.getJSONArray(Protocol.PRICE);

                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put( "pl_id", t.getInt("pl_id") );
                                cv.put( "i_id", t.getInt("i_id") );
                                cv.put("p_date_b", t.getLong("p_date_b"));
                                cv.put("p_date_e", t.getLong("p_date_e"));
                                cv.put( "p_cn", t.getInt("p_cn") );

                                //t.getDouble()

                                if ( cv.getAsInteger("p_cn") > 0 ) {
                                    db.insert("price", cv);
                                    Log.i("PRICE", cv.getAsString("p_cn"));
                                }
                            }
                        }

                        Log.i("getPrices", Boolean.toString(body.has(Protocol.PRICELISTS)) );
                        // Загрузка прайслистов
                        if ( body.has(Protocol.PRICELISTS) ) {



                            db.execSQL("DELETE FROM pricelists");
                            JSONArray ig = body.getJSONArray(Protocol.PRICELISTS);

                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                Log.i("getPrices", t.toString());

                                cv = new ContentValues();
                                cv.put( "pl_id", t.getInt("pl_id") );
                                cv.put( "pl_name", t.getString("pl_name") );

                                if ( !cv.getAsString("pl_name").isEmpty() ) {
                                    db.insert("pricelists", cv);
                                    Log.i("PRICELISTS", cv.getAsString("pl_name"));
                                }
                            }
                        }


                        // Загрузка связей прайслистов
                        if ( body.has(Protocol.LINK_PRICELISTS) ) {

                            db.execSQL("DELETE FROM pricelist_link");
                            JSONArray ig = body.getJSONArray(Protocol.LINK_PRICELISTS);

                            for (int i = 0; i < ig.length(); i++) {
                                JSONObject t = ig.getJSONObject(i);

                                cv = new ContentValues();
                                cv.put( "pll_parent", t.getInt("pl_parent") );
                                cv.put( "pll_child", t.getInt("pl_child") );
                                cv.put( "pll_prior", t.getInt("pll_prior") );

                                db.insert("pricelist_link", cv);
                                Log.i("LINK_PRICELISTS", cv.getAsString("pll_parent"));

                            }
                        }

                        break;
                    }
                    */

                    // Получает ответы сервера после передачи данных
                    case Protocol.RESULT_SYNC: {
                        // Должно быть удаление успешно отправленных данных
                        switch ( body.optString(Protocol.NAME) ) {


                            // Ответ от отправки данных по звонкам
                            case Protocol.SYNC_CALLS: {

                                // Получили ответ по синхронизации по ID
                                if ( syncid > 0 ) {
                                    clearSyncData( syncid );
                                }

                                // Удаляем данные


                                // Удаляем данные из таблиц синхронизации


                                break;
                            }

                            default:
                                break;
                        }



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
            public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

            }

            @Override
            public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

            }

            @Override
            public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

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

            /*
            public void sendCoord(WebSocket websocket) throws Exception {

                Log.i("setCoord", "1");

                websocket.sendText( db.getCoords2().toString() );
            }


            public void getPrices(WebSocket websocket) throws Exception {
                JSONObject r = new JSONObject();
                r.put( Protocol.HEAD, "getPrices" );

                JSONObject body = new JSONObject();
                body.put( Protocol.PRICE, "all" );
                body.put( Protocol.PRICELISTS, "all" );
                body.put( Protocol.LINK_PRICELISTS, "all" );

                r.put(Protocol.BODY, body);

                //Log.i("getCountragent", r.toString() );
                websocket.sendText(r.toString());
            }

            public void getAmounts(WebSocket websocket) throws Exception {
                JSONObject r = new JSONObject();
                r.put( Protocol.HEAD, "getStocks" );

                JSONObject body = new JSONObject();
                body.put(Protocol.STORES, "all");
                body.put(Protocol.LINK_STORES, "all");
                body.put(Protocol.STOCKS, "all");

                r.put( Protocol.BODY, body );

                //Log.i("getCountragent", r.toString() );
                websocket.sendText( r.toString() );
            }

            private void getNews(WebSocket w, JSONObject p) throws Exception {
                JSONObject r = new JSONObject();
                r.put( Protocol.HEAD, Protocol.SYNC_NEWS );
                r.put( Protocol.BODY, p );

                Log.i(Protocol.SYNC_NEWS, r.toString() );
                w.sendText( r.toString() );
            }
            */

            // Функция заполняет body для отправки данных на сервер
            private JSONObject getBody(String head) throws Exception {
                JSONObject b = new JSONObject();
                syncid = 0;

                Log.i("GETBODY", "getBody: " + head);

                switch (head) {

                    // obj_id = 1 - это звонки
                    case Protocol.SYNC_CALLS: {
                        Log.i("GETBODY", "SYNC_CALLS: " + head);

                        // Проверяем есть ли данные для отправки на сервер
                        Boolean NeedSync = false;
                        String sql = "SELECT c.lc_id as any_id FROM log_calls c LEFT JOIN sync_data sd ON (c.lc_id = sd.any_id AND sd.obj_id = 1) WHERE sd.sd_id ISNULL;";
                        Cursor c = db.rawQuery(sql, null);
                        if (c != null) {
                            NeedSync = c.getCount() > 0;

                            Log.i("GETBODY", "getBody: " + String.valueOf( c.getCount() ) );
                            c.close();
                        }

                        NeedSync = true;

                        if (NeedSync) {

                            // Записываем данные в таблицу синхронизации и получаем ID синхронизации
                            /*sql = "INSERT INTO sync (sdate) VALUES ( current_timestamp )";
                            db.execSQL(sql);

                            sql = "INSERT INTO sync_data (obj_id, any_id, s_id)\n" +
                                    "SELECT\n" +
                                    "  1 as obj_id,\n" +
                                    "  c.ROWID as any_id,\n" +
                                    "  last_insert_rowid() as s_id\n" +
                                    "FROM\n" +
                                    "  log_calls c\n" +
                                    "  LEFT JOIN sync_data sd ON (c.ROWID = sd.any_id AND sd.obj_id = 1)\n" +
                                    "WHERE\n" +
                                    "  sd.ROWID ISNULL;";

                            db.execSQL(sql);

                            sql = "SELECT sd.s_id as s_id FROM sync_data sd WHERE sd.ROWID = last_insert_rowid();";


                            Log.d("GETBODY", "SQL: " + sql);
                            c = db.rawQuery(sql, null);
                            Log.d("GETBODY", "rawQuery");
                            Integer SyncID = 0;
                            if (c != null) {
                                Log.d("GETBODY", "getColumnCount: " + String.valueOf(c.getColumnCount()) );
                                c.moveToFirst();
                                SyncID = c.getInt( c.getColumnIndex( "s_id" ) );
                                Log.d("GETBODY", "getBody: " + String.valueOf(SyncID) );
                                c.close();
                            } else {
                                Log.d("GETBODY", "Cursor null");
                            }*/

                            sql = "INSERT INTO sync (sdate) VALUES ( current_timestamp )";
                            db.execSQL(sql);

                            sql = "SELECT last_insert_rowid()";
                            c = db.rawQuery(sql, null);
                            c.moveToFirst();
                            syncid = c.getInt( 0 );
                            c.close();

                            Log.i("GETBODY", "SyncID: " + String.valueOf(syncid) );


                            sql = "INSERT INTO sync_data (obj_id, any_id, s_id)\n" +
                                    "SELECT\n" +
                                    "  1 as obj_id,\n" +
                                    "  c.lc_id as any_id,\n" +
                                    " " + String.valueOf(syncid) +" as s_id\n" +
                                    "FROM\n" +
                                    "  log_calls c\n" +
                                    "  LEFT JOIN sync_data sd ON (c.lc_id = sd.any_id AND sd.obj_id = 1)\n" +
                                    "WHERE\n" +
                                    "  sd.sd_id ISNULL;";
                            db.execSQL(sql);

                            // Формируем пакет данных для отправки на сервер
                            sql = "SELECT c.* FROM sync_data sd JOIN log_calls c ON (sd.any_id = c.lc_id AND sd.obj_id = 1) WHERE sd.s_id = " + String.valueOf(syncid);
                            c = db.rawQuery(sql, null);
                            if ( c != null ) {
                                Log.d("GETBODY", "getBody c2: " + String.valueOf(c.getCount()));
                                c.moveToFirst();
                                JSONArray a = new JSONArray();
                                do {
                                    Log.d("GETBODY", "getBody: " + c.getString( c.getColumnIndex("lc_phone") ) );
                                    a.put(
                                            new JSONObject()
                                                .put("lc_id", c.getLong( c.getColumnIndex("lc_id") ) )
                                                .put("lc_stime", Long.valueOf( c.getString( c.getColumnIndex("lc_stime") )))
                                                .put("lc_billsec", c.getInt( c.getColumnIndex("lc_billsec") ))
                                                .put("lc_phone", c.getString( c.getColumnIndex("lc_phone") ))
                                                .put("lc_name", c.getString( c.getColumnIndex("lc_name") ))
                                                .put("lc_incoming", c.getInt( c.getColumnIndex("lc_incoming") ))
                                    );

                                } while ( c.moveToNext() );
                                c.close();


                                b.put( Protocol.DATA, a );

                            }

                        }


                        Log.i("GETBODY", "getBody - b " + b.toString() );
                        return b;
                    }

                    case Protocol.SYNC_COORDS: {


                        return b;
                    }

                    default:

                        break;

                }

                return b;
            }

            private Boolean clearSyncData( Integer SyncID ) {
                if ( SyncID < 1 ) return false;

                String so_table = "";
                Integer obj_id = 0;

                // Узнаем тип объета который надо зачистить
                String sql = "SELECT so.so_table, so.so_id FROM sync s JOIN sync_object so ON (s.obj_id = so.so_id) WHERE s.s_id = " + String.valueOf(SyncID);
                Cursor c = db.rawQuery(sql, null);
                if ( c != null ) {
                    c.moveToFirst();
                    so_table = c.getString(0);
                    obj_id = c.getInt(1);
                    c.close();
                }

                // Чистка таблицы с данными
                switch (so_table) {

                    case "log_calls": {
                        sql = "DELETE FROM log_calls lc WHERE EXISTS ( SELECT sd.any_id FROM sync_data sd WHERE sd.obj_id = " + String.valueOf(obj_id) + " AND sd.any_id = lc.lc_id AND sd.s_id = "+ String.valueOf(SyncID) +" )";
                        db.execSQL(sql);

                        break;
                    }

                    default:

                        break;

                }


                // Чистка таблицы синхронизации
                sql = "DELETE FROM sync_data sd WHERE sd.obj_id = " + String.valueOf(obj_id) + " AND sd.s_id = " + so_table;
                db.execSQL(sql);

                sql = "DELETE FROM sync s WHERE s.s_id = " + String.valueOf(SyncID);
                db.execSQL(sql);



                return true;
            }

            private void syncQuery(WebSocket w, String head ) throws Exception {
                syncCustomQuery(w, head, getBody(head) );
            }

            private void syncCustomQuery(WebSocket w, String head, JSONObject body ) throws Exception {
                JSONObject r = new JSONObject();
                r.put( Protocol.HEAD, head );

                if (FullSync) {
                    body.put( Protocol.FULL_SYNC, true );
                }

                if ( syncid > 0 ) {
                    r.put( Protocol.ID, syncid );
                }

                r.put( Protocol.BODY, body );

                Log.d( head, r.toString() );
                w.sendText( r.toString() );
            }

            private void syncFunction(WebSocket w, String fun, String tn, String[][] f) throws Exception {
                JSONArray items = body.optJSONArray( Protocol.DATA );
                if ( items == null ) {
                    // Надо залогировать проблему
                    return;
                }


                if ( FullSync ) {
                    db.execSQL("DELETE FROM " + tn);
                }

                // Идентификатор синхронизации
                Integer SyncID = body.optInt( Protocol.SYNC_ID, 0);

                for (int i = 0; i < items.length(); i++ ) {
                    JSONObject t = items.getJSONObject(i);

                    ContentValues cv = new ContentValues();
                    for (int j = 0; j < f.length; j++ ) {
                        if ( f[j][2].equals("text") ) {
                            cv.put(f[j][0],  t.getString(f[j][1]));
                        } else if ( f[j][2].equals("int") ) {
                            cv.put(f[j][0],  t.getInt(f[j][1]));
                        }

                    }
                    db.replace(tn, cv);

                    Log.i(tn, cv.getAsString(f[0][0]));

                }

                // Отправляем ответ об успешном приеме данных
                JSONObject r = new JSONObject();
                r.put( Protocol.HEAD, Protocol.RESULT_SYNC );
                r.put( Protocol.BODY,
                        new JSONObject()
                                .put( Protocol.NAME, fun )
                                .put( Protocol.ID, SyncID )
                                .put( Protocol.RESULT, true )
                );

                w.sendText( r.toString() );
            }



        });

        try {

            // Соединение с сервером
            ws.connect();

            // Проверка соединения
            if ( ws.isOpen() ) {

                // Log.i("auth", p.toString());
                // Формирование и отправка команды авторизации
                ws.sendText(
                        new JSONObject()
                                .put( Protocol.HEAD, Protocol.AUTH_USER )
                                .put( Protocol.BODY, p.getJSONObject( Protocol.USER_DATA ) )
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
    private void handleActionLogCoord(JSONObject p, Notification n) {

        //long minTime = 60 * 60 * 1000;  // 1 час
        long minTime = 60 * 1000;  // 1 минута
        float minDistance = 0;


        mNM.notify( 1, n );
        startForeground(777, n );


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
