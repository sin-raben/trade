package pro.gofman.trade.Messages;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pro.gofman.trade.DB;
import pro.gofman.trade.Protocol;
import pro.gofman.trade.SyncData;
import pro.gofman.trade.Trade;

/**
 * Created by gofman on 15.06.17.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";


    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        try {

            // Заполняем командой отправки токена
            JSONArray a = new JSONArray();
            a.put(
                    new JSONObject()
                            .put( Protocol.HEAD, Protocol.SYNC_FCMTOKEN )
                            .put( Protocol.BODY,
                                    new JSONObject()
                                        .put( "token", refreshedToken )
                            )
            );

            // Отправляем в сервис синхронизации
            syncCustomQuery( a );

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "refreshedToken: " + refreshedToken);
        
    }

    // Функция запускает сервис синхронизации, список функций должен быть в data
    private void syncCustomQuery(JSONArray data) throws Exception {
        DB db = Trade.getWritableDatabase();

        JSONObject connectionData = new JSONObject( db.getOptions( DB.OPTION_CONNECTION ) );
        JSONObject userData = new JSONObject( db.getOptions( DB.OPTION_AUTH ) );


        try {
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

    }

}
