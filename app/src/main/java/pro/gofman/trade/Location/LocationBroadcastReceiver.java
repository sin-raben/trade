package pro.gofman.trade.Location;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;


import com.google.android.gms.location.LocationResult;

import org.json.JSONObject;

import java.util.List;

import pro.gofman.trade.DB;
import pro.gofman.trade.Protocol;
import pro.gofman.trade.Trade;
import pro.gofman.trade.Utils;

/**
 * Created by roman on 11.10.17.
 */

public class LocationBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATES = "pro.gofman.trade.location.action.PROCESS_UPDATES";
    public static final String ACTION_EVENT = "pro.gofman.trade.location.param.ACTION_EVENT";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("COORD","e12");

        if ( intent != null ) {
            Log.i("COORD","e13");
            final String action = intent.getAction();

            Log.i("COORD","e14");



            if ( ACTION_PROCESS_UPDATES.equals(action) ) {

                Log.i("COORD","e15");

                LocationResult result = LocationResult.extractResult(intent);

                Log.i("COORD","e16");

                if (result != null) {
                    Log.i("COORD","e0");

                    try {
                        Log.i("COORD","e17");
                        //JSONObject p = new JSONObject( intent.getStringExtra( Trade.SERVICE_PARAM ) );



                        List<Location> locations = result.getLocations();


                        DB db = Trade.getWritableDatabase();


                        for ( int i = 0; i < locations.size(); i++ ) {

                            ContentValues cv = new ContentValues();

                            long time = locations.get(i).getTime() / 1000;
                            cv.put("lc_lat", String.valueOf(locations.get(i).getLatitude()));
                            cv.put("lc_lon", String.valueOf(locations.get(i).getLongitude()));
                            cv.put("lc_time", (int) time);
                            cv.put("lc_provider", locations.get(i).getProvider());
                            cv.put("lc_event", 1 /*p.optInt(Protocol.EVENT, Protocol.EVENT_UNKNOW) */ );

                            try {

                                Log.d("COORD", String.valueOf(locations.get(i).getLatitude()) + " "+ String.valueOf(locations.get(i).getLongitude()));

                                db.insert("log_coords", cv);
                            } catch (Exception e){
                                Log.d("COORD","e1");
                                e.printStackTrace();
                            }

                        }


                        Utils.sendNotification(
                                context,
                                new JSONObject()
                                        .put( Protocol.NOTIFICATION_TITLE, "Тест" )
                                        .put( Protocol.NOTIFICATION_TICKER, "Получили координаты!")
                                        .put( Protocol.NOTIFICATION_BODY, Utils.getLocationResultTitle(context, locations) )
                        );

                    } catch (Exception e) {
                        Log.d("COORD","e2");
                        e.printStackTrace();
                    }


                }
            }
        }

    }
}
