package com.audsnap.workingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.audsnap.R;
import com.audsnap.camera.DemoCamera;

/**
 * A placeholder fragment containing a simple view.
 */
public class InboxActivityFragment extends Fragment {

    public InboxActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        Button btn_open_cam = (Button) view.findViewById(R.id.btn_open_cam);

        btn_open_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DemoCamera.class));
            }
        });

        return view;
    }
}
