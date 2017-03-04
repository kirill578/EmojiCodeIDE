package kkulakov.emojicode;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

import kkulakov.emojicode.shell.Shell;
import kkulakov.emojicode.shell.ShellImpl;
import kkulakov.emojicode.shell.OnExecuteCallback;

/**
 * Created by kirill on 17/02/17.
 */

public class EmojiCodeCompiler {

    public static long MILLIS_COMPILATION_TIMEOUT = 10000;

    /**
     * Compiles emoji code to emoji byte code
     * @param ctx
     * @param source source file (.emojic)
     * @param output output file (.emojib)
     * @param callbacks all callbacks are delivered on UI thread
     */
    public static void compile(Context ctx, String source, String output, final CompilationCallbacks callbacks) {
        final ShellImpl shell = new ShellImpl();

        final AtomicReference<String> buffer = new AtomicReference<>("");

        final HandlerThread handlerThread = new HandlerThread("compilation-timeout-thread");
        handlerThread.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UI.post(new Runnable() {
                    @Override
                    public void run() {
                        callbacks.onFail("Compilation timeout\n\n" + buffer.get());
                    }
                });
            }
        }, MILLIS_COMPILATION_TIMEOUT);

        shell.exec("cd " + ctx.getFilesDir().getAbsolutePath());
        String packages= "EMOJICODE_PACKAGES_PATH=" + ctx.getFilesDir().getAbsolutePath() + "/packages";
        shell.exec(packages + " ./emojicodec -j -o " + output + " " + source);
        shell.setOnExecuteCallback(new OnExecuteCallback() {

            @Override
            public void onSuccess(String str) {
                buffer.set(buffer.get() + str);

                try {
                    JSONArray jsonArray = new JSONArray(buffer.get());
                    if (jsonArray.length() == 0) {
                        clean(handlerThread, shell, handler);
                        UI.post(new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onSuccess();
                            }
                        });
                    } else {
                        if (jsonArray.length() == 1) {
                            JSONObject object = jsonArray.getJSONObject(0);
                            if (object.getString("type").equals("error")) {
                                final long line = object.getLong("line");
                                final long character = object.getLong("character");
                                final String message = object.getString("message");
                                clean(handlerThread, shell, handler);
                                UI.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callbacks.onError(line, character, message);
                                    }
                                });
                            } else {
                                clean(handlerThread, shell, handler);
                                UI.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callbacks.onFail(buffer.get());
                                    }
                                });
                            }
                        } else {
                            clean(handlerThread, shell, handler);
                            UI.post(new Runnable() {
                                @Override
                                public void run() {
                                    callbacks.onFail(buffer.get());
                                }
                            });
                        }
                    }
                } catch (JSONException ignore) {
                    // still buffering
                }
            }

            @Override
            public void onFail() {
                clean(handlerThread, shell, handler);
                UI.post(new Runnable() {
                    @Override
                    public void run() {
                        callbacks.onFail(buffer.get());
                    }
                });
            }
        });
    }

    public interface CompilationCallbacks {
        void onSuccess();
        void onError(long line, long character, String msg);
        void onFail(String msg);
    }

    private static void clean(HandlerThread handlerThread, Shell shell, Handler handler) {
        handler.removeCallbacksAndMessages(null);
        handlerThread.quit();
        shell.close();
    }

}
