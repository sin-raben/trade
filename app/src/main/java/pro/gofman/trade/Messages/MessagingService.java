package pro.gofman.trade.Messages;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
        //      Задание на синхронизацию
        //      Новости с диалогом


        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "DATA: " + remoteMessage.getData() );
            try {
                syncCustomQuery( String.valueOf(remoteMessage.getData()) );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "BODY: " + remoteMessage.getNotification().getBody());

            //sendNotification( remoteMessage.getNotification().getBody() );
        }

    }

    private void sendNotification(String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Cloud Messaging")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build() );

    }

    private void syncCustomQuery(String data) throws Exception {
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
}
