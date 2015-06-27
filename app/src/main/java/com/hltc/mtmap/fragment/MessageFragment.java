package com.hltc.mtmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.activity.MainActivity;
import com.hltc.mtmap.activity.start.StartActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MessageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (MainActivity.isVisitor) {
            View view = inflater.inflate(R.layout.window_remind_login, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.btn_remind_login);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StartActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            return view;
        } else {
            View view = inflater.inflate(R.layout.fragment_message, container, false);
            return view;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
