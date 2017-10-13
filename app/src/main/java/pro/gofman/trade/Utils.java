package pro.gofman.trade;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.preference.PreferenceManager;
import android.location.Location;
import android.util.Log;

import org.json.JSONObject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by gofman on 10.10.17.
 */

public class Utils {
    final static String KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested";
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";

    public static void sendNotification(Context context, JSONObject n) {

        // Переход по клику
        Intent notificationIntent = new Intent(context, MainActivity.class);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);
        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);





        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.worker);



        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(largeIcon)
                .setColor( Color.parseColor( n.optString(Protocol.NOTIFICATION_COLOR, "#4B8A08") ) )
                .setStyle( new NotificationCompat.BigTextStyle().bigText( n.optString(Protocol.NOTIFICATION_BODY, "") ) )
                .setContentTitle( n.optString(Protocol.NOTIFICATION_TITLE, "") )
                .setContentText( n.optString(Protocol.NOTIFICATION_BODY, "") )
                .setTicker("Hello")
                .setAutoCancel(true)
                //.setSound(notificationSound)
                //.setVibrate( new long[] { 1000, 1000, 1000, 1000, 1000 } )
                .setLights(Color.RED, 500, 1000)
                .setContentIntent(notificationPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build() );

    }


    public static void createLocationRequest( LocationRequest lr ) {

        lr = new LocationRequest();

        lr.setInterval( Protocol.LOCATION_UPDATE_INTERVAL );
        lr.setFastestInterval( Protocol.LOCATION_FASTEST_UPDATE_INTERVAL );
        lr.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        lr.setMaxWaitTime( Protocol.LOCATION_MAX_WAIT_TIME );

    }

    public static String getLocationResultTitle(Context context, List<Location> locations) {
        String numLocationsReported = context.getResources().getQuantityString(
                R.plurals.num_locations_reported, locations.size(), locations.size());
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
    }

    /**
     * Returns te text for reporting about a list of  {@link Location} objects.
     *
     * @param locations List of {@link Location}s.
     */
    public static String getLocationResultText(Context context, List<Location> locations) {
        if (locations.isEmpty()) {
            return "Координат нет";
        }
        StringBuilder sb = new StringBuilder();
        for (Location location : locations) {
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");
        }

        Log.i("getLocationResultText", sb.toString() );

        return sb.toString();
    }

    public static void setLocationUpdatesResult(Context context, List<Location> locations) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations))
                .apply();
    }

    public static String getLocationUpdatesResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "");
    }


}
