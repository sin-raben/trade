package pro.gofman.trade.CRM;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Date;

import static android.R.attr.phoneNumber;

/**
 * Created by roman on 25.06.17.
 */

public class CallReceiver extends PhoneCallReceiver {

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i("CallReceiver", "onIncomingCallEnded: " + number + " " + String.valueOf( start ) + " " + String.valueOf( end ));

        String[] projection = new String[] { ContactsContract.Data.CONTACT_ID, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.STARRED, ContactsContract.Contacts.CONTACT_STATUS, ContactsContract.Contacts.CONTACT_PRESENCE };

        String selection = "PHONE_NUMBERS_EQUAL(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",?) AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";
        String selectionArgs = String.valueOf(new String[] { number });
        Cursor cursor = ctx.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, null);



    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i("CallReceiver", "onOutgoingCallEnded: " + number + " " + String.valueOf( start ) + " " + String.valueOf( end ));
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.i("CallReceiver", "onMissedCall: " + number + " " + String.valueOf( start ) );
    }

}