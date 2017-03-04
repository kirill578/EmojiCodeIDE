package kkulakov.emojicode;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import kkulakov.emojicode.shell.OnExecuteCallback;

public class RunnerFragment extends Fragment implements OnExecuteCallback {

    public static final String EXECUTABLE = "EXECUTABLE";
    private TextView outputTextView;
    private EmojiCodeRunner emojiCodeRunner;
    private String buffer = "";

    public RunnerFragment() {
        this.setRetainInstance(true);
    }

    public static RunnerFragment create(String executableName) {
        RunnerFragment fragment = new RunnerFragment();
        Bundle args = new Bundle();
        args.putString(EXECUTABLE, executableName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (emojiCodeRunner == null) {
            emojiCodeRunner = new EmojiCodeRunner(getActivity());
            emojiCodeRunner.run(getArguments().getString(EXECUTABLE), this);
        } else {
            outputTextView.setText(buffer);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_runner, container, false);
        outputTextView = (TextView) view.findViewById(R.id.output_textview);
        return view;
    }

    @Override
    public void onSuccess(String str) {
        buffer += str;
        outputTextView.setText(buffer);
    }

    @Override
    public void onFail() {
        Snackbar.make(outputTextView, "Shell failed", Snackbar.LENGTH_LONG).show();
    }
}
