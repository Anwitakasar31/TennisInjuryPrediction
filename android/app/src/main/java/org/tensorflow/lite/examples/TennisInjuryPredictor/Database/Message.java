package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

import android.widget.Toast;
import android.content.Context;

public class Message {
    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
