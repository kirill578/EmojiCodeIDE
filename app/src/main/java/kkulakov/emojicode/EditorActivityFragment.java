package kkulakov.emojicode;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class EditorActivityFragment extends Fragment {

    public static final String CODE = "CODE";
    private EditText codeView;

    public EditorActivityFragment() {
    }

    public static EditorActivityFragment create(String code) {
        EditorActivityFragment editorActivityFragment = new EditorActivityFragment();
        Bundle args = new Bundle();
        args.putString(CODE, code);
        editorActivityFragment.setArguments(args);
        return editorActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        // TODO make line number better
        codeView = (EditText) view.findViewById(R.id.text_code);
        TextView linesTV = (TextView) view.findViewById(R.id.tv_lines);
        String lines = "";
        for (int i = 1; i < 200; i++) {
            lines += i + "\n";
        }
        linesTV.setText(lines);

        if (getArguments() != null && getArguments().containsKey(CODE)) {
            String code = getArguments().getString(CODE);
            codeView.setText(code);
            getArguments().remove(CODE);
        }
        return view;
    }

    public String getCode() {
        return codeView.getText().toString();
    }

    public void setCode(String fibonaciiExample) {
        codeView.setText(fibonaciiExample);
    }
}
