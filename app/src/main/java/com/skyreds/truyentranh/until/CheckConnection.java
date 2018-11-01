package com.skyreds.truyentranh.until;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;


@SuppressWarnings("StatementWithEmptyBody")
public class CheckConnection {
    private final Context context;
    private final View view;

    public CheckConnection(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public void checkConnection(){
        if(isOnline()){
        }else{
            Snackbar snackbar = Snackbar
                    .make(view, " Không có kết nối internet!", Snackbar.LENGTH_LONG)
                    .setAction("Bật 3G/Wifi", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

}
