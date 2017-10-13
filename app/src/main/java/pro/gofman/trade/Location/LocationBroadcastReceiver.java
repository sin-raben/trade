package pro.gofman.trade.Location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import org.json.JSONObject;

import java.util.List;

import pro.gofman.trade.Protocol;
import pro.gofman.trade.Utils;

/**
 * Created by roman on 11.10.17.
 */

public class LocationBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION_PROCESS_UPDATES = "pro.gofman.trade.location.action.PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( intent != null ) {
            final String action = intent.getAction();
            if ( ACTION_PROCESS_UPDATES.equals(action) ) {
                LocationResult result = LocationResult.extractResult(intent);

                if (result != null) {

                    List<Location> locations = result.getLocations();
                    Utils.setLocationUpdatesResult(context, locations);

                    try {

                        Utils.sendNotification(
                                context,
                                new JSONObject()
                                        .put( Protocol.NOTIFICATION_TITLE, Utils.getLocationResultTitle(context, locations) )
                                        .put( Protocol.NOTIFICATION_BODY, "Тест" )
                        );

                    } catch (Exception e) {

                    }
                    Log.i("", Utils.getLocationUpdatesResult(context));
                }
            }
        }

    }
}
