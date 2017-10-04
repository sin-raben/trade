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
    protected static final String EXTRA_RESULT = "pro.gofman.trade.result";


    private DB db; //;
    private String sql = "";
    private JSONObject result = null;


    private String head = "";
    private JSONObject body = null;
    private Integer headid = 0;
    private Integer count = 1;

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


                // Полученный JSONObject
                result = new JSONObject(text);

                // Заголовок имя функции
                head = result.getString( Protocol.HEAD );

                // ID заголовка
                headid = result.optInt( Protocol.ID, 0 );

                // Тело функции содержит информацию и дополнительные параметры если они нужны
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
                                syncQuery( websocket, Protocol.SYNC_NEWS );

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

                                    JSONObject body = obj.optJSONObject(Protocol.BODY) != null ? obj.optJSONObject(Protocol.BODY) : getSyncData( obj.getString(Protocol.HEAD) );

                                    if ( body != null ) {
                                        syncCustomQuery(websocket, obj.getString(Protocol.HEAD), body);
                                    }

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
                                {"iu_base", "iu_base", "bool"},
                                {"iu_main", "iu_main", "bool"}
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
                                {"dp_active", "lcp_active", "bool"}
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

                    // Получает ответы сервера после передачи данных
                    case Protocol.RESULT_SYNC: {

                        Integer syncid = body.optInt( Protocol.SYNC_ID, 0 );

                        // Должно быть удаление успешно отправленных данных
                        switch ( body.optString(Protocol.NAME) ) {


                            // Ответ от отправки данных по звонкам
                            case Protocol.SYNC_CALLS: {

                                // Получили ответ синхронизации по ID
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


                count -= 1;
                // Log.i("COUNT", String.valueOf(count) + " " + head + " -" );

                // Больше не ждем данных можно и разорвать соединение
                if ( count < 1 ) {
                    websocket.sendClose(1000);

                    // Сообщаем MainActyvity что синхронизация окончена
                    Intent intent = new Intent();
                    intent.setAction( ACTION_SYNCDATA );
                    intent.putExtra( EXTRA_RESULT, new JSONObject().put("finish", true).toString() );
                    sendBroadcast( intent );

                    // websocket.disconnect();
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


            // Функция заполняет body для отправки данных на сервер
            private JSONObject getSyncData(String head) throws Exception {
                JSONObject b = new JSONObject();
                Integer syncid = 0;

                Log.i("GETBODY", "getData: " + head);

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
                        // Данные есть нужна синхронизация
                        if (NeedSync) {

                            // Добавляем в таблицу начало синхронизации
                            sql = "INSERT INTO sync (sdate) VALUES ( current_timestamp )";
                            db.execSQL(sql);

                            // Получаем ID синхронизации
                            sql = "SELECT last_insert_rowid()";
                            c = db.rawQuery(sql, null);
                            c.moveToFirst();
                            syncid = c.getInt( 0 );
                            c.close();
                            Log.i("GETBODY", "SyncID: " + String.valueOf(syncid) );

                            // Фиксируем данные для синхронизации на полученный ID синхронизации
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


                                b.put( Protocol.SYNC_ID, syncid );
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

                // Отправляем пакет для синхронизации
                syncCustomQuery( w, head, getSyncData(head) );
            }

            private void syncCustomQuery(WebSocket w, String head, JSONObject body) throws Exception {
                count += 1;
                Log.i("COUNT", String.valueOf(count) + " " + head );

                JSONObject r = new JSONObject();

                // Добавляем в пакет для отправки на сервер заголовок (имя функции)
                r.put( Protocol.HEAD, head );

                // Добавляем в пакет для отправки на сервер признак полной синхронизации
                if ( FullSync ) {
                    body.put( Protocol.FULL_SYNC, true );
                }

                // Добавлем в пакет отправки HEAD_ID который пришел с сервера
                if ( headid > 0 ) {
                    r.put( Protocol.ID, headid );
                }

                // Добавляем тело функции
                r.put( Protocol.BODY, body );

                Log.i( head, r.toString() );
                w.sendText( r.toString() );
            }


            // функция принимает данные от сервера и записывает в мобильную базу данных
            private void syncFunction(WebSocket w, String fun, String tn, String[][] f) throws Exception {

                //Log.i(fun, tn);
                // Получаем из тела функции набор данных который надо записать в базу данных
                JSONArray items = body.optJSONArray( Protocol.DATA );
                if ( items == null ) {
                    // Надо залогировать проблему
                    return;
                }

                // Если есть параметр FULLSYNC, то сначала обнуляем таблицу
                if ( FullSync ) {
                    db.execSQL("DELETE FROM " + tn);
                }

                // Идентификатор синхронизации
                Integer SyncID = body.optInt( Protocol.SYNC_ID, 0);

                try {

                    for (int i = 0; i < items.length(); i++ ) {
                        JSONObject t = items.getJSONObject(i);

                        ContentValues cv = new ContentValues();
                        for (int j = 0; j < f.length; j++ ) {
                            if ( f[j][2].equals("text") ) {
                                cv.put(f[j][0],  t.getString(f[j][1]));
                            } else if ( f[j][2].equals("int") ) {
                                cv.put(f[j][0],  t.getInt(f[j][1]));
                            } else if ( f[j][2].equals("bool") ) {
                                cv.put(f[j][0],  t.getBoolean(f[j][1]));
                            }

                        }
                        db.replace(tn, cv);

                        //Log.i(tn, cv.getAsString(f[0][0]));

                    }

                } catch (Exception e) {
                    Log.e("error", "syncFunction: ", e);
                }

                //Log.i("COUNT", tn);

                // Отправляем ответ об успешном приеме данных
                JSONObject r = new JSONObject();
                r.put( Protocol.HEAD, Protocol.RESULT_SYNC );
                r.put( Protocol.BODY,
                        new JSONObject()
                                .put( Protocol.NAME, fun )
                                .put( Protocol.SYNC_ID, SyncID )
                                .put( Protocol.RESULT, true )
                );

                count+=1;
                Log.i("COUNT", String.valueOf(count) + " " + Protocol.RESULT_SYNC+" "+tn );

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

