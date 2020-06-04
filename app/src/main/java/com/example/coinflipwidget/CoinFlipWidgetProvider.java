package com.example.coinflipwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Random;


public class CoinFlipWidgetProvider extends AppWidgetProvider {
    public static String ACTION_APPWIDGET_BUTTON_CLICK = "action.APP_WIDGET_ACTION_BUTTON_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int appWidgetId : appWidgetIds){
            RemoteViews views = setViews(context, appWidgetId);
            views.setTextViewText(R.id.coin_button, context.getString(R.string.question_mark));
            appWidgetManager.updateAppWidget(appWidgetId, views);
            Log.d("onUpdate()", " ID = " + appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "onReceive()", Toast.LENGTH_SHORT).show();
        super.onReceive(context, intent);
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_coin_flip);
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if(extras != null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if(action != null){
            if(action.equals(ACTION_APPWIDGET_BUTTON_CLICK)) {
                int rand = new Random().nextInt(2);
                views.setTextViewText(R.id.coin_button, context.getString( (rand == 0) ? R.string.tails : R.string.heads));
            } else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                for(int id : appWidgetManager.getAppWidgetIds(new ComponentName(context, CoinFlipWidgetProvider.class))){
                    views = setViews(context, id, views);
                    appWidgetManager.updateAppWidget(id, views);
                }
            }
        }

        Log.d("onReceive", " ID = " + appWidgetId + " Action = "  + action);

        views.setOnClickPendingIntent(R.id.coin_button, getPendingClickIntent(context, appWidgetId));

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preferences_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for(int appWidgetId : appWidgetIds){
            editor.remove(context.getString(R.string.widget_theme_key) + appWidgetId);
            Log.d("onDeleted()", " ID = " + appWidgetId);
        }
        editor.apply();
    }

    public PendingIntent getPendingClickIntent(Context context, int appWidgetId){
        Intent clickIntent = new Intent(context, CoinFlipWidgetProvider.class);
        clickIntent.setAction(CoinFlipWidgetProvider.ACTION_APPWIDGET_BUTTON_CLICK);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, clickIntent, 0);
    }

    public RemoteViews setViews(Context context, int appWidgetId){
        int theme = context.getSharedPreferences(context.getString(R.string.preferences_key),Context.MODE_PRIVATE).getInt(context.getString(R.string.widget_theme_key) + appWidgetId, 1);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_coin_flip);
        views.setInt(R.id.coin_button, "setBackgroundResource", (theme == 0) ? R.drawable.light_coin_selector : R.drawable.dark_coin_selector);
        views.setTextColor(R.id.coin_button, (theme == 0) ? Color.BLACK : Color.WHITE);
        views.setOnClickPendingIntent(R.id.coin_button, getPendingClickIntent(context, appWidgetId));
        Log.d("setViews()", "called Context : " + context.toString());
        return views;
    }

    public RemoteViews setViews(Context context, int appWidgetId, RemoteViews views){
        int theme = context.getSharedPreferences(context.getString(R.string.preferences_key),Context.MODE_PRIVATE).getInt(context.getString(R.string.widget_theme_key) + appWidgetId, 1);
        views.setInt(R.id.coin_button, "setBackgroundResource", (theme == 0) ? R.drawable.light_coin_selector : R.drawable.dark_coin_selector);
        views.setTextColor(R.id.coin_button, (theme == 0) ? Color.BLACK : Color.WHITE);
        views.setOnClickPendingIntent(R.id.coin_button, getPendingClickIntent(context, appWidgetId));
        Log.d("setViews()", "called Context : " + context.toString());
        return views;
    }

}
