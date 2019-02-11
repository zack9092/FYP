package com.example.elly_clarkson.fyp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import android.widget.*;


public class Fragment1 extends Fragment {

    Button button2;
    Button refresh;
    TextView textView;
    SwipeRefreshLayout layout;

    private OnClickListener  onClickListener= new View.OnClickListener() {
        public void onClick(View v) {
            testing();
        }
    };
    private OnClickListener  onClickListener2= new View.OnClickListener() {
        public void onClick(View v) {
            getSeats();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item1, null);
        layout=(SwipeRefreshLayout) view;
        layout.setOnRefreshListener(onSwipeToRefresh);
        LinearLayout linearLayout=view.findViewById(R.id.linearLayout);
        Button button=view.findViewById(R.id.testing);
        button.setOnClickListener(onClickListener);
        button2=new Button(getActivity());
        button2.setText("GetRequest");
        button2.setOnClickListener(onClickListener2);
        linearLayout.addView(button2);
        refresh=new Button(getActivity());
        refresh.setText("Refresh");
     //   refresh.setOnClickListener();
        linearLayout.addView(refresh);
        textView=(TextView)view.findViewById(R.id.textView1);
        return view;
    }



    public void testing(){
        textView.setText("Clicked!!");
    }

    private void getSeats(){
        LoginActivity.compositeDisposable.add(LoginActivity.iMyService.getSeats().subscribeOn(io.reactivex.schedulers.Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String res) throws Exception {
                button2.setText(res);
            }
            },new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(getContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
               // throwable.printStackTrace();
            }}));
    };

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            layout.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    textView.setText("fragment1");
                    button2.setText("GetRequest");
                    layout.setRefreshing(false);
                }
            }, 1000);
        }
    };


}
