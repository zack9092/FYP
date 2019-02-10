package com.example.elly_clarkson.fyp;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import android.widget.AdapterView.*;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment {
    protected boolean isVisible;
    final String[] block ={"Block A", "Block B","Block C" };
    final String[] floor = {"0/F","1/F", "2/F", "3/F", "4/F", "5/F", "6/F", "7/F", "8/F", "9/F", "10/F"};
    Spinner spinner;
    Spinner spinner2;
    TextView testing;
    ImageView imageView;
    FrameLayout frame;
    char selectedBlock='A';
    String fileName;
    String pathName;
    int selectedFloor=0;
    int width=0;
    int height=0;
    JSONObject obj;
    ConstraintLayout constraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item3, null);
        testing = view.findViewById(R.id.testing);
        spinner = view.findViewById(R.id.spinner);
        frame=view.findViewById(R.id.frame);
        ArrayAdapter<String> blockList = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                block);
        spinner.setAdapter(blockList);
        spinner.setOnItemSelectedListener(new onItemSelectedListener());
        spinner2=view.findViewById(R.id.spinner2);
        ArrayAdapter<String> floorList = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                floor);
        spinner2.setAdapter(floorList);
        spinner2.setOnItemSelectedListener(new onItemSelectedListener2());

        return view;
    }

    public class onItemSelectedListener implements OnItemSelectedListener{
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            frame.removeAllViews();
            final ImageView imageView = new ImageView(getActivity());
            selectedBlock=(char)('A'+position);
           // testing.setText("BLOCK"+selectedBlock+" "+floor[selectedFloor]);
            fileName=selectedBlock+""+selectedFloor;
            pathName="http://10.0.2.2:3000/ou/"+fileName+".png";
            Picasso.get().load(pathName).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            frame.addView(imageView);
            System.out.println(pathName);
            ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
            ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            constraintLayout.setLayoutParams(constraintLayoutParams);
            constraintLayout.setId(View.generateViewId());
            frame.addView(constraintLayout);
            DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getActivity());
            databaseAccess.open();
            Cursor cursor = databaseAccess.getSeatsLocation(fileName);
            while(cursor.moveToNext()){
                Button test=new Button(getActivity());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(20, 20);
                test.setLayoutParams(params);
                float x = cursor.getFloat(cursor.getColumnIndex("x"));
                System.out.println(x);
                float y = cursor.getFloat(cursor.getColumnIndex("y"));
                test.setId(View.generateViewId());
                constraintLayout.addView(test);
                ConstraintSet set = new ConstraintSet();
                set.clone(constraintLayout);
                set.connect(test.getId(), ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(test.getId(), ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.setHorizontalBias(test.getId(), x);
                set.connect(test.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                set.connect(test.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                set.setVerticalBias(test.getId(),y);
                set.applyTo(constraintLayout);
                getSeatStatus( cursor.getString(cursor.getColumnIndex("id")),cursor.getFloat(cursor.getColumnIndex("x")),cursor.getFloat(cursor.getColumnIndex("y")),test);
            }
            databaseAccess.close();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class onItemSelectedListener2 implements OnItemSelectedListener{
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            frame.removeAllViews();
            ImageView imageView = new ImageView(getActivity());
            selectedFloor=position;
         //   testing.setText("BLOCK"+selectedBlock+" "+floor[selectedFloor]);
            fileName=selectedBlock+""+selectedFloor;
            pathName="http://10.0.2.2:3000/ou/"+fileName+".png";
            Picasso.get().load(pathName).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            frame.addView(imageView);
            System.out.println(pathName);
            ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
            ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            constraintLayout.setLayoutParams(constraintLayoutParams);
            constraintLayout.setId(View.generateViewId());
            frame.addView(constraintLayout);
            DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getActivity());
            databaseAccess.open();
            Cursor cursor = databaseAccess.getSeatsLocation(fileName);
            while(cursor.moveToNext()){
                Button test=new Button(getActivity());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(20, 20);
                test.setLayoutParams(params);
                float x = cursor.getFloat(cursor.getColumnIndex("x"));
                System.out.println(x);
                float y = cursor.getFloat(cursor.getColumnIndex("y"));
                test.setId(View.generateViewId());
                constraintLayout.addView(test);
                ConstraintSet set = new ConstraintSet();
                set.clone(constraintLayout);
                set.connect(test.getId(), ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(test.getId(), ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.setHorizontalBias(test.getId(), x);
                set.connect(test.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                set.connect(test.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                set.setVerticalBias(test.getId(),y);
                set.applyTo(constraintLayout);
                getSeatStatus( cursor.getString(cursor.getColumnIndex("id")),cursor.getFloat(cursor.getColumnIndex("x")),cursor.getFloat(cursor.getColumnIndex("y")),test);
            }
            databaseAccess.close();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    }
    public static void xxx(){
        System.out.println("XXX");
    }

    private void getSeatStatus(String place_id,float x,float y,Button button){
        final Button button1=button;
        final float tempX=x;
        final float tempY=y;
    //   final Cursor tempCursor=cursor;
            LoginActivity.compositeDisposable.add(LoginActivity.iMyService.seatStatus(place_id).subscribeOn(io.reactivex.schedulers.Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String res) throws Exception {
                    obj = new JSONObject(res);
                    System.out.println(obj);
                    try {
                        float max = obj.getInt("MaxSeats");
                        System.out.println(max);
                        float peopleThere=obj.getInt("PeopleThere");
                        System.out.println(peopleThere);
                        if(peopleThere/max>0.8) {
                            button1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            System.out.println("A");
                        }
                        else if(peopleThere/max>0.5) {
                            button1.setBackgroundColor(Color.YELLOW);
                            System.out.println("B");
                        }
                        else {
                            button1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                            System.out.println("C");
                        }
                    }catch(Exception e){
                        System.out.println(e);
                    }

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(getContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
                    // throwable.printStackTrace();
                }
            }));

    };

}
