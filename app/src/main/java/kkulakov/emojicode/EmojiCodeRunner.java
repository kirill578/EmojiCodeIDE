package kkulakov.emojicode;

import android.content.Context;

import kkulakov.emojicode.shell.OnExecuteCallback;
import kkulakov.emojicode.shell.ShellImpl;

/**
 * Created by kirill on 17/02/17.
 */

public class EmojiCodeRunner {

    private final ShellImpl shell;

    public EmojiCodeRunner(Context ctx) {
        String absolutePath = ctx.getFilesDir().getAbsolutePath();
        shell = new ShellImpl();
        shell.exec("cd " + absolutePath);
    }

    /**
     * Executes emoji code byte code file, all callbacks are on main thread
     * @param executable
     * @param callbacks
     */
    public void run(String executable, final OnExecuteCallback callbacks) {
        shell.setOnExecuteCallback(new OnExecuteCallback() {
            @Override
            public void onSuccess(final String buffer) {
                UI.post(new Runnable() {
                    @Override
                    public void run() {
                        callbacks.onSuccess(buffer);
                    }
                });
            }

            @Override
            public void onFail() {
                UI.post(new Runnable() {
                    @Override
                    public void run() {
                        callbacks.onFail();
                    }
                });
            }
        });
        shell.exec("./emojicode " + executable);
    }

    /**
     * kills the proccess
     */
    public void quit() {
        shell.close();
    }

}
