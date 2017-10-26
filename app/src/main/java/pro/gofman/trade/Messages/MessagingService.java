package pro.gofman.trade.Messages;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import pro.gofman.trade.DB;
import pro.gofman.trade.MainActivity;
import pro.gofman.trade.Protocol;
import pro.gofman.trade.R;
import pro.gofman.trade.SyncData;
import pro.gofman.trade.Trade;
import pro.gofman.trade.Utils;

import static pro.gofman.trade.Utils.sendNotification;


/**
 * Created by gofman on 15.06.17.
 */

// https://habrahabr.ru/post/303514/

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";
    private Context context = null;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Log.i(TAG, "FROM: " + remoteMessage.getFrom() );

        // Получение данных от Firebase Cloud Messaging
        // Приходят сообщения разного типа
        //      Задание на синхронизацию (JSONArray)
        //          Запрос координат или телефонных звонков
        //      Новости с действиями (JSONObject)
        //          Новости требующие отклика, обратной связи, последовательность действий
        //      Новости связанные с объектами системы (JSONObject)
        //          Добавлена новая номенклатура или контрагент, тап открывает карточку объекта


        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "DATA: " + remoteMessage.getData() );

            context = Trade.getAppContext();

            try {
                // Получили объект с сервера, надо разобрать что это!
                JSONObject data = new JSONObject( remoteMessage.getData().get( Protocol.NOTIFICATION_DATA ) );
                JSONArray arr;
                JSONObject obj;
                Boolean delaySync = false;

                Log.i("MESSAGE", data.toString() );


                /*

                    Через уведомления могут прийти несколько управляющих команд
                    нужно пустить их все через обработку и только потом вызывать синхронизацию
                    f020004s - команда без параметра, обычная синхронизация локальной таблицы с сервером
                    f020004s - команда с указанным параметром
                     {
                        "body": {
                            "now": true
                        }
                     }
                    заставляет устройство получить координату, записать в локальную таблицу и только потом
                    выполняется синхронизация


                 */

                // Обрабатываем запросы на синхронизацию
                // получаем массив запросов
                arr = data.optJSONArray( Protocol.NOTIFICATION_DATA );
                if ( arr != null ) {
                    if ( arr.length() > 0 ) {

                        for (int i = 0; i < arr.length(); i++) {
                            // Выясняем есть ли управляющие команды, признаки находятся в BODY
                            // если есть, то синхронизацию откладываем пока не получим результат
                            if ( arr.getJSONObject(i).optString(Protocol.HEAD).equals(Protocol.SYNC_COORDS) ) {
                                if (  !arr.getJSONObject(i).isNull(Protocol.BODY) ) {
                                    if ( arr.getJSONObject(i).getJSONObject(Protocol.BODY).optBoolean(Protocol.NOW, false) ) {

                                        Intent intent = new Intent(Trade.getAppContext(), SyncData.class);

                                        intent.putExtra(
                                                Trade.SERVICE_PARAM,
                                                new JSONObject()
                                                        .put(Protocol.EVENT, Protocol.EVENT_QUERY)
                                                        .toString()
                                        );

                                        Log.i("ACTION_LOGCOORD", "1");
                                        intent.setAction( SyncData.ACTION_LOGCOORD );
                                        startService( intent );

                                    }
                                }
                            }

                        }


                        Log.i("MESSAGE-arr", String.valueOf( arr.length() ) );
                        if ( Utils.sendCustomSync( arr ) ) {
                            Log.i(TAG, "Запрос передан в сервис синхронизации");
                        }
                    }
                }

                // Загрузка объектов в базу данных
                if ( ! data.isNull( Protocol.NOTIFICATION_DBDATA ) ) {
                    saveObjectDB( data.getJSONObject(Protocol.NOTIFICATION_DBDATA) );
                }

                // Прислали уведомление, показываем на экране
                // Реализовать переход на привязанные объект, если он есть!!!
                obj = data.optJSONObject( Protocol.NOTIFICATION_OBJECT );
                //
                if ( obj != null ) {
                    Log.i("MESSAGE-obj", obj.toString() );
                    sendNotification( context, obj );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
/*
    private Boolean syncCustomQuery(JSONArray data)   {

        Log.i(TAG, "syncCustomQuery: "+ data.toString() );
        DB db = Trade.getWritableDatabase();



        try {

            JSONObject connectionData = new JSONObject( db.getOptions( DB.OPTION_CONNECTION ) );
            JSONObject userData = new JSONObject( db.getOptions( DB.OPTION_AUTH ) );

            // Параметры для соединения с сервером
            connectionData.put( Protocol.USER_DATA, userData );
            connectionData.put( Protocol.CUSTOM_SYNC, true );
            connectionData.put( Protocol.DATA, data );

            Intent intent = new Intent( Trade.getAppContext(), SyncData.class);
            intent.setAction( Trade.SERVICE_SYNCDATA );
            intent.putExtra( Trade.SERVICE_PARAM, connectionData.toString() );

            startService( intent );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
*/
    // Сохранение объектов в базе данных
    private void saveObjectDB( JSONObject obj ) throws Exception {

        DB db = Trade.getWritableDatabase();
        String head = obj.optString( Protocol.NOTIFICATION_HEAD, "" );

        switch ( head ) {

            // Сохраняем новости
            case Protocol.SYNC_NEWS: {

                String[][] f = Protocol.FIELDS_NEWS;
                JSONArray items = obj.optJSONArray( Protocol.NOTIFICATION_DATA );

                if ( items != null ) {

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject t = items.optJSONObject(i);

                        if ( t != null ) {

                            ContentValues cv = new ContentValues();
                            for (int j = 0; j < f.length; j++) {
                                if (f[j][2].equals("text")) {
                                    cv.put(f[j][0], t.getString(f[j][1]));
                                } else if (f[j][2].equals("int")) {
                                    cv.put(f[j][0], t.getInt(f[j][1]));
                                } else if (f[j][2].equals("bool")) {
                                    cv.put(f[j][0], t.getBoolean(f[j][1]));
                                }
                            }
                            db.replace(Protocol.DB_NEWS, cv);
                            Log.i("saveObjectDB", t.toString() );


                        } // !null элемент массива

                    } // Цикл по элементам массива

                } // !null массив


                break;
            }

            default:
                break;
        }

    }

/*

    // Запрос координат нужно сначала получить координаты
                                        if (obj.optString(Protocol.HEAD, "").equals( Protocol.SYNC_COORDS )) {
        Log.i("CUSTOM_SYNC", "1");
        try {

            mFusedLocationClient.getLastLocation().addOnCompleteListener( onCompleteListener );

        } catch ( SecurityException e ) {
            Log.i("CUSTOM_SYNC", "2");
        }
    }

    private OnCompleteListener onCompleteListener = new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
            Location mLastLocation;
            if (task.isSuccessful() && task.getResult() != null) {

                mLastLocation = task.getResult();




                Log.i("OnCompleteListener", String.format(Locale.ROOT, "%s: %f х %f : %s", "lan",
                        mLastLocation.getLatitude(), mLastLocation.getLongitude(), mLastLocation.getProvider() ));

                Log.i("OnCompleteListener", String.valueOf( mLastLocation.getTime() ));

            } else {
                Log.i("addOnCompleteListener", "getLastLocation:exception", task.getException());

            }
        }
    };

*/

}


/*

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
 */