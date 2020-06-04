package com.example.coinflipwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetConfigActivity extends AppCompatActivity {

    Button confirm;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    int theme = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultIntent);


        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if(extras != null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish();

        confirm = findViewById(R.id.button_confirm);
    }

    public void OnRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        if(checked){
            switch (view.getId()){
                case R.id.rad_light: {
                    theme = 0;
                    confirm.setBackgroundColor(Color.WHITE);
                    confirm.setTextColor(Color.BLACK);
                    break;
                }
                case R.id.rad_dark: {
                    theme = 1;
                    confirm.setBackgroundColor(Color.BLACK);
                    confirm.setTextColor(Color.WHITE);
                    break;
                }
            }
        }
    }

    public void OnConfirmButtonClicked(View view){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.widget_theme_key) + appWidgetId, theme);
        editor.apply();

        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_coin_flip);

        views.setInt(R.id.coin_button, "setBackgroundResource", (theme == 0) ? R.drawable.light_coin_selector : R.drawable.dark_coin_selector);
        views.setTextColor(R.id.coin_button, (theme == 0) ? Color.BLACK : Color.WHITE);

        Intent intent = new Intent(getApplicationContext(), CoinFlipWidgetProvider.class);
        intent.setAction(CoinFlipWidgetProvider.ACTION_APPWIDGET_BUTTON_CLICK);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setOnClickPendingIntent(R.id.coin_button, PendingIntent.getBroadcast(getApplicationContext(), appWidgetId, intent, 0));

        AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(appWidgetId, views);

        Log.d("onConfigConfirm", "ID: " + appWidgetId + " Theme = " + theme);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
