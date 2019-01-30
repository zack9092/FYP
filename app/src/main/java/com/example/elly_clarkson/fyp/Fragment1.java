package com.example.elly_clarkson.fyp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class Fragment1 extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item1, null);
        Button button=view.findViewById(R.id.testing);
        button.setOnClickListener(onClickListener);
        return view;
    }
    private View.OnClickListener  onClickListener= new View.OnClickListener() {
        public void onClick(View v) {
            testing();
        }
    };

    public void testing(){
        TextView textView=(TextView)getView().findViewById(R.id.textView1);
        textView.setText("Clicked!!");
    }

}
