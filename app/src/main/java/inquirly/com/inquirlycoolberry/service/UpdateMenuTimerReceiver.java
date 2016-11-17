package inquirly.com.inquirlycoolberry.service;

import android.util.Log;
import android.content.Intent;
import android.os.SystemClock;
import android.content.Context;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;

/**
 * Created by Vinayak on 9/26/2016.
 */
public class UpdateMenuTimerReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdateMenuTimerService";
    private static final int NOTIFICATIONS_INTERVAL = 5;

    @Override
    public void onReceive(Context context, Intent intent) {
        setupAlarm(context);
    }

    public static void setupAlarm(Context context) {
        Log.i(TAG,"check context received--" + context.getPackageName());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(context, AutoUpdateDataService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context,0,i,0);
        Log.i(TAG,"check elapsed time---" + AlarmManager.ELAPSED_REALTIME);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME
                , SystemClock.elapsedRealtime()+NOTIFICATIONS_INTERVAL,
                15 * 60000,alarmIntent);
    }
}
