package com.mm55.mtkbypass;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.SystemProperties;

public class BypassService extends Service {

    private static final String PROP_BYPASS = "persist.sys.mtk_bypass";
    private static final String PROP_AUTO = "persist.sys.mtk_bypass_auto";
    private static final String PROP_LIMIT = "persist.sys.mtk_bypass_limit";

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                boolean isAutoEnabled = SystemProperties.getInt(PROP_AUTO, 0) == 1;
                if (!isAutoEnabled) return;

                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level == -1 || scale == -1) return;

                int batteryPct = (int) ((level / (float) scale) * 100);
                int limit = SystemProperties.getInt(PROP_LIMIT, 80);

                if (batteryPct < limit) {
                    if (SystemProperties.getInt(PROP_BYPASS, 0) != 0) {
                        SystemProperties.set(PROP_BYPASS, "0");
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SystemProperties.set(PROP_BYPASS, "0");

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(batteryReceiver);
        } catch (Exception ignored) {}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
