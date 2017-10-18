package cn.chinaunicom.monitor.console;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.chinaunicom.monitor.R;

public class ConsoleFragment extends Fragment {

    @Bind(R.id.txtViewTitle)
    TextView title;

    public ConsoleFragment() {
        // Required empty public constructor
    }

    public static ConsoleFragment getInstance() {
        ConsoleFragment fragment = new ConsoleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console, container, false);
        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        title.setText("控制台");
    }

}
