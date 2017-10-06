package pro.gofman.trade.Messages;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pro.gofman.trade.DB;
import pro.gofman.trade.MainActivity;
import pro.gofman.trade.Protocol;
import pro.gofman.trade.R;
import pro.gofman.trade.SyncData;
import pro.gofman.trade.Trade;

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

                Log.i("MESSAGE", data.toString() );

                // Обрабатываем запросы на синхронизацию
                arr = data.optJSONArray( Protocol.NOTIFICATION_DATA );
                Log.i("MESSAGE-arr", String.valueOf( arr.length() ) );
                if ( arr.length() > 0 ) {
                    if ( this.syncCustomQuery( arr ) ) {
                        Log.i(TAG, "Запрос передан в сервис синхронизации" );
                    }
                }

                // Загрузка объектов в базу данных
                if ( ! data.isNull( Protocol.NOTIFICATION_DBDATA ) ) {
                    saveObjectDB( data.getJSONObject(Protocol.NOTIFICATION_DBDATA) );
                }

                // Прислали уведомление, показываем на экране
                // Реализовать переход на привязанные объект, если он есть!!!
                obj = data.getJSONObject( Protocol.NOTIFICATION_OBJECT );
                //Log.i("MESSAGE-obj", obj.toString() );
                if ( obj != null ) {
                    sendNotification( context, obj );
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

    }

    private void sendNotification(Context context, JSONObject n) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.worker);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setColor( Color.parseColor( n.optString(Protocol.NOTIFICATION_COLOR, "#4B8A08") ) )
                .setStyle( new NotificationCompat.BigTextStyle().bigText( n.optString(Protocol.NOTIFICATION_BODY, "") ) )
                .setContentTitle( n.optString(Protocol.NOTIFICATION_TITLE, "") )
                .setContentText( n.optString(Protocol.NOTIFICATION_BODY, "") )
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setVibrate( new long[] { 1000, 1000, 1000, 1000, 1000 } )
                .setLights(Color.RED, 500, 1000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build() );

    }

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