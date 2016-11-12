package pro.gofman.trade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by gofman on 03.11.16.
 */

public class CallCRM extends BroadcastReceiver {
    private static boolean incomingCall = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("CRM", "1");
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra( TelephonyManager.EXTRA_STATE );

            Log.i("CRM", "2");
            Log.i("CRM", ":" + phoneState+":"+TelephonyManager.EXTRA_STATE_RINGING+":");
            //Log.i("CRM", TelephonyManager.EXTRA_STATE_RINGING);
            
            if ( phoneState.equals( TelephonyManager.EXTRA_STATE_RINGING ) ) {
                Log.i("CRM", "3");
                //Трубка не поднята, телефон звонит
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCall = true;
                Log.i("CRM", "Show window: " + phoneNumber);

            } else if ( phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) ) {
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
                if (incomingCall) {
                    Log.i("CRM", "Close window.1");
                    incomingCall = false;
                }
            } else if ( phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE) ) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок".
                if (incomingCall) {
                    Log.i("CRM", "Close window.2");
                    incomingCall = false;
                }
            }
        }

    }
}
