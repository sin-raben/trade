package pro.gofman.trade.Location;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;


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
        if ( intent != null ) {
            final String action = intent.getAction();




            if ( ACTION_PROCESS_UPDATES.equals(action) ) {
                LocationResult result = LocationResult.extractResult(intent);

                if (result != null) {

                    try {
                        JSONObject p = new JSONObject( intent.getStringExtra( Trade.SERVICE_PARAM ) );



                        List<Location> locations = result.getLocations();


                        DB db = Trade.getWritableDatabase();


                        for ( int i = 0; i < locations.size(); i++ ) {

                            ContentValues cv = new ContentValues();

                            long time = locations.get(i).getTime()/1000;
                            cv.put( "lc_lat",  String.valueOf( locations.get(i).getLatitude() ) );
                            cv.put( "lc_lon",  String.valueOf( locations.get(i).getLongitude() ) );
                            cv.put( "lc_time", (int) time );
                            cv.put( "lc_provider", locations.get(i).getProvider() );
                            cv.put( "lc_event", p.optInt(Protocol.EVENT, Protocol.EVENT_UNKNOW) );

                            db.insert( "log_coords", cv );

                        }


                        Utils.sendNotification(
                                context,
                                new JSONObject()
                                        .put( Protocol.NOTIFICATION_TITLE, "Тест" )
                                        .put( Protocol.NOTIFICATION_TICKER, "Получили координаты!")
                                        .put( Protocol.NOTIFICATION_BODY, Utils.getLocationResultTitle(context, locations) )
                        );

                    } catch (Exception e) {

                    }


                }
            }
        }

    }
}
