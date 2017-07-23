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


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i(TAG, "FROM: " + remoteMessage.getFrom() );

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
            try {
                // Получили объект с сервера, надо разобрать что это!
                JSONObject data = new JSONObject( remoteMessage.getData().get( Protocol.NOTIFICATION_DATA ) );

                // Обрабатываем запросы на синхронизацию
                syncCustomQuery( data.optJSONArray( Protocol.NOTIFICATION_DATA ) );

                // Загрузка объектов
                //saveObjectDB( data.optJSONObject( Protocol.NOTIFICATION_DATA ) );



                // Показываем уведомление на экране
                if ( data.optJSONObject(Protocol.NOTIFICATION_OBJECT) != null ) {
                    sendNotification( data.optJSONObject(Protocol.NOTIFICATION_OBJECT) );
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "BODY: " + remoteMessage.getNotification().getBody());

            //sendNotification( remoteMessage.getNotification().getBody() );
        }

    }

    private void sendNotification(JSONObject n) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.worker);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setColor( Color.parseColor( n.optString(Protocol.NOTIFICATION_COLOR, "#4B8A08") ) )
                .setStyle( new NotificationCompat.BigTextStyle().bigText( n.optString(Protocol.NOTIFICATION_BODY, "") ) )
                .setContentTitle( n.optString(Protocol.NOTIFICATION_TITLE, "") )
                .setContentText( n.optString(Protocol.NOTIFICATION_BODY, "") )
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setVibrate( new long[] { 1000, 1000, 1000, 1000, 1000 } )
                .setLights(Color.MAGENTA, 500, 1000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build() );

    }

    private void syncCustomQuery(JSONArray data) throws Exception {

        Log.d(TAG, "syncCustomQuery: "+ data.toString());

        DB db = Trade.getWritableDatabase();

        JSONObject connectionData = new JSONObject( db.getOptions( DB.OPTION_CONNECTION ) );
        JSONObject userData = new JSONObject( db.getOptions( DB.OPTION_AUTH ) );


        try {
            // Параметры для соединения с сервером
            connectionData.put( Protocol.USER_DATA, userData );
            connectionData.put( Protocol.CUSTOM_SYNC, true );
            connectionData.put( Protocol.DATA, data );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent( Trade.getAppContext(), SyncData.class);
        intent.setAction( Trade.SERVICE_SYNCDATA );
        intent.putExtra( Trade.SERVICE_PARAM, connectionData.toString() );

        startService( intent );
    }

    private void saveObjectDB( JSONObject obj ) throws Exception {

        DB db = Trade.getWritableDatabase();
        JSONObject t = obj.optJSONObject( Protocol.DB_NEWS );
        String[][] f =  Protocol.FIELDS_NEWS;

        if ( t != null ) {

            ContentValues cv = new ContentValues();
            for (int j = 0; j < f.length; j++) {
                if (f[j][2].equals("text")) {
                    cv.put(f[j][0], t.getString(f[j][1]));
                } else if (f[j][2].equals("int")) {
                    cv.put(f[j][0], t.getInt(f[j][1]));
                }

            }
            db.replace(Protocol.DB_NEWS, cv);

        }

    }
}
