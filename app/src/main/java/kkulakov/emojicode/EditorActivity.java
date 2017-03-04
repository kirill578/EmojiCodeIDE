package kkulakov.emojicode;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import kkulakov.emojicode.shell.FakeShellKeeper;
import kkulakov.emojicode.shell.OnExecuteCallback;

public class EditorActivity extends AppCompatActivity {

    private FakeShellKeeper shell;
    private Toolbar toolbar;

    String fibonaciiExample =
            "\uD83C\uDFC1 \uD83C\uDF47\n" +
            "  \uD83D\uDC74 Let’s print the first 15 fibonaccis.\n" +
            "\n" +
            "  \uD83C\uDF6E a 0\n" +
            "  \uD83C\uDF6E b 1\n" +
            "\n" +
            "  \uD83D\uDD02 i ⏩ 0 15 \uD83C\uDF47\n" +
            "    \uD83C\uDF66 r ➕ a b\n" +
            "    \uD83C\uDF6E a b\n" +
            "    \uD83C\uDF6E b r\n" +
            "    \uD83D\uDE00 \uD83D\uDD21 r 10\n" +
            "  \uD83C\uDF49\n" +
            "\uD83C\uDF49";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String code = EmojiCodeManager.loadSavedCode(EditorActivity.this);
        if (code == null || code.isEmpty())
            code = fibonaciiExample;

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_holder, EditorActivityFragment.create(code))
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
                if (fragment instanceof EditorActivityFragment) {
                    String code = ((EditorActivityFragment) fragment).getCode();
                    EmojiCodeManager.save(EditorActivity.this, code);
                }

                EmojiCodeCompiler.compile(getApplicationContext(), EmojiCodeManager.CODE_FILE, "exec.emojib", new EmojiCodeCompiler.CompilationCallbacks() {
                    @Override
                    public void onSuccess() {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.fragment_holder, RunnerFragment.create("exec.emojib"))
                                .commit();
                    }

                    @Override
                    public void onError(long line, long character, String msg) {
                        Snackbar.make(toolbar, line + ":" + character + " - " + msg, Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                setupDependencies();
            }
        }).start();

    }

    private void setupDependencies() {
        try {
            AssetsManager.copyDirorfileFromAssetManager(EditorActivity.this, "emojicode03/exec/" + getArchitecture(), "/packages");
            AssetsManager.copyDirorfileFromAssetManager(EditorActivity.this, "emojicode03/packages", "/");
            shell = FakeShellKeeper.getInstance();
            shell.exec("cd " + getFilesDir().getAbsolutePath());
            shell.exec("chmod 777 emojicode");
            shell.exec("chmod 777 emojicodec");
        } catch (IOException e) {
            UI.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EditorActivity.this, "unable to copy depndencies ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static String getArchitecture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS[0];
        } else {
            return Build.CPU_ABI;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_fibonacci_example) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
            if (fragment instanceof EditorActivityFragment) {
                EditorActivityFragment editorActivityFragment = (EditorActivityFragment) fragment;
                editorActivityFragment.setCode(fibonaciiExample);
            }
        }
        return true;
    }
}
