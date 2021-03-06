package com.zacharee1.systemuituner.receivers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.services.ShutDownListen;

/**
 * Created by Zacha on 5/1/2017.
 */

@SuppressWarnings("deprecation")
public class OnBootReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getText(R.string.sharedprefs_id).toString(), Context.MODE_PRIVATE);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals(Intent.ACTION_REBOOT) ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") ||
                intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED) ||
                intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON")) {

            if (sharedPreferences.getBoolean("safeStatbar", false)) {
                runReceive(context);
            }
            startWakefulService(context, new Intent(context, ShutDownListen.class).setData(Uri.parse("http://test.com")));
        }
    }

    private void runReceive(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getText(R.string.sharedprefs_id).toString(), Context.MODE_PRIVATE);
        final String blacklist_bak = Settings.Secure.getString(context.getContentResolver(), "icon_blacklist2");

        try {
            Settings.Secure.putString(context.getContentResolver(), "icon_blacklist", null);

            Handler restore_state = new Handler();
            restore_state.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Settings.Secure.putString(context.getContentResolver(), "icon_blacklist", blacklist_bak);
                    Toast.makeText(context, context.getResources().getText(R.string.boot_message_icon_blacklist), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putBoolean("isBooted", true).apply();
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getResources().getText(R.string.permissions_failed), Toast.LENGTH_LONG).show();
        }

        try {
            Settings.Secure.putInt(context.getContentResolver(), "sysui_qs_fancy_anim", 1);
            Thread.sleep(1000);
            final int fancy_qs_anim_temp = Settings.Secure.getInt(context.getContentResolver(), "sysui_qs_fancy_anim2", 1);
            Settings.Secure.putInt(context.getContentResolver(), "sysui_qs_fancy_anim", fancy_qs_anim_temp);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getResources().getText(R.string.permissions_failed), Toast.LENGTH_LONG).show();
        }

    }
}
