package pro.gofman.trade.CRM;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.util.Date;

import pro.gofman.trade.DB;
import pro.gofman.trade.MainActivity;
import pro.gofman.trade.Trade;


/**
 * Created by roman on 25.06.17.
 */

public class CallReceiver extends PhoneCallReceiver {

    public static final int CONTACT_QUERY_LOADER = 0;
    public static final String QUERY_KEY = "query";
    private DB db;


    /*
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            return;
        }

        int phoneColumnIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int emailColumnIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
        int nameColumnIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME);
        int lookupColumnIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.LOOKUP_KEY);
        int typeColumnIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.MIMETYPE);

        data.moveToFirst();
        String lookupKey = "";
        do {
            // BEGIN_INCLUDE(lookup_key)
            String currentLookupKey = data.getString(lookupColumnIndex);
            if (!lookupKey.equals(currentLookupKey)) {
                String displayName = data.getString(nameColumnIndex);
                //tv.append(displayName + "\n");
                lookupKey = currentLookupKey;
            }
            // END_INCLUDE(lookup_key)

            // BEGIN_INCLUDE(retrieve_data)
            // The data type can be determined using the mime type column.
            String mimeType = data.getString(typeColumnIndex);
            if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                //tv.append("\tPhone Number: " + data.getString(phoneColumnIndex) + "\n");
            } else if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                //tv.append("\tEmail Address: " + data.getString(emailColumnIndex) + "\n");
            }
            // END_INCLUDE(retrieve_data)

            // Look at DDMS to see all the columns returned by a query to Contactables.
            // Behold, the firehose!
            for(String column : data.getColumnNames()) {
                Log.d("", column + column + ": " +
                        data.getString(data.getColumnIndex(column)) + "\n");
            }
        } while (data.moveToNext());


    }
    */


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i("CallReceiver", "onIncomingCallEnded: " + number + " " + String.valueOf(start) + " " + String.valueOf(end));

        ContentValues cv = new ContentValues();
        cv.put("lc_stime", start.toString());
        long sec = (end.getTime() - start.getTime())/1000;
        cv.put("lc_billsec", sec);
        cv.put("lc_phone", number);
        cv.put("lc_name", "");
        cv.put("lc_incoming", 1);

        db.insert("log_calls", cv);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i("CallReceiver", "onOutgoingCallEnded: " + number + " " + String.valueOf(start) + " " + String.valueOf(end));

        ContentValues cv = new ContentValues();
        cv.put("lc_stime", start.toString());
        long sec = (end.getTime() - start.getTime())/1000;
        cv.put("lc_billsec", sec);
        cv.put("lc_phone", number);
        cv.put("lc_name", "");
        cv.put("lc_incoming", 0);

        db.insert("log_calls", cv);


    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.i("CallReceiver", "onMissedCall: " + number + " " + String.valueOf(start));

        ContentValues cv = new ContentValues();
        cv.put("lc_stime", start.toString());
        cv.put("lc_billsec", 0);
        cv.put("lc_phone", number);
        cv.put("lc_name", "");
        cv.put("lc_incoming", 1);

        db.insert("log_calls", cv);

    }

};