package pro.gofman.trade.CRM;

import android.content.Context;
import android.util.Log;

import java.util.Date;

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