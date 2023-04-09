package com.example.myapplication;

import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.functions.Consumer;

public class ErrorUtils {
    public static Consumer<Throwable> onError(View view) {
        return throwable -> {
            Snackbar.make(view, throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            Log.e("RX ERROR", throwable.getLocalizedMessage());
        };
    }
}
