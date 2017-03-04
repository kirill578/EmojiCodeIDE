package kkulakov.emojicode;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by kirill on 17/02/17.
 */

public class UI {

    private static Handler ui = new Handler(Looper.getMainLooper());

    public static void post(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            ui.post(runnable);
        }
    }

}
