package com.example.elly_clarkson.fyp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.ScanResult;
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

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    //char selectedBlock='A';
    String fileName;
    String pathName;
   // int selectedFloor=0;
    int width=0;
    int height=0;
    JSONObject obj;
    ConstraintLayout constraintLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item3, null);
        //testing = view.findViewById(R.id.testing);

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
        spinner2.setOnItemSelectedListener(new onItemSelectedListener());

        return view;
    }

    public class onItemSelectedListener implements OnItemSelectedListener{
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            frame.removeAllViews();
            final ImageView imageView = new ImageView(getActivity());
            System.out.println(""+spinner.getSelectedItem().toString().charAt(6)+ spinner2.getSelectedItem().toString().substring(0,spinner2.getSelectedItem().toString().indexOf('/')));
            String selectedBlock=""+spinner.getSelectedItem().toString().charAt(6);
            String selectedFloor=""+spinner2.getSelectedItem().toString().substring(0,spinner2.getSelectedItem().toString().indexOf('/'));
           // testing.setText("BLOCK"+selectedBlock+" "+floor[selectedFloor]);
            fileName=selectedBlock+""+selectedFloor;
            //pathName="http://10.0.2.2:3000/ou/"+fileName+".png";
            pathName="http://10.0.2.2:3000/ou/"+fileName+".png";
            Picasso.get().load(pathName).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            frame.addView(imageView);
            System.out.println(pathName);
            ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
            //ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(
                    //ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            //constraintLayout.setLayoutParams(constraintLayoutParams);
            constraintLayout.setId(View.generateViewId());






            frame.addView(constraintLayout);
            DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getActivity());
            databaseAccess.open();
            Cursor cursor = databaseAccess.getSeatsLocation(fileName);
            while(cursor.moveToNext()){
                ImageButton test=new ImageButton(getActivity());
                //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(40, 70);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(getResources().getDisplayMetrics().widthPixels/10, (getResources().getDisplayMetrics().heightPixels/10)-10);
                test.setLayoutParams(params);
                test.setScaleType(ImageView.ScaleType.FIT_XY);
                test.setBackgroundColor(Color.TRANSPARENT);
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

    /*public class onItemSelectedListener2 implements OnItemSelectedListener{
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
                ImageButton test=new ImageButton(getActivity());

                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(40, 70);
                test.setLayoutParams(params);
                test.setScaleType(ImageView.ScaleType.FIT_XY);
                test.setBackgroundColor(Color.TRANSPARENT);
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

    }*/
    public static void xxx(){
        System.out.println("XXX");
    }

    private void getSeatStatus(String place_id,float x,float y,ImageButton button){
        final ImageButton button1=button;
        final float tempX=x;
        final float tempY=y;
    //   final Cursor tempCursor=cursor;
            LoginActivity.compositeDisposable.add(LoginActivity.iMyService.seatStatus(place_id).subscribeOn(io.reactivex.schedulers.Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String res) throws Exception {
                    obj = new JSONObject(res);
                    System.out.println(obj);
                    try {
                        final float  max = obj.getInt("MaxSeats");
                        System.out.println(max);
                        final float peopleThere=obj.getInt("PeopleThere")+obj.getInt("booking");
                        System.out.println(peopleThere);
                        if(peopleThere/max>0.8) {
                            //button1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            button1.setImageResource(R.drawable.redpeople);
                            System.out.println("A");
                            final JSONObject newJson=obj;
                            button1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println(newJson);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    int temp=(int)max-(int)peopleThere;
                                    if(temp<0) temp=0;
                                    builder.setMessage("Seat:  "+temp+" / "+(int)max+" , Too many people here")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 左方按鈕方法
                                                }
                                            });
                                    AlertDialog about_dialog = builder.create();
                                    about_dialog.show();
                                }
                            });
                        }
                        else if(peopleThere/max>0.5) {
                            final JSONObject newJson=obj;
                           // button1.setBackgroundColor(Color.YELLOW);
                            button1.setImageResource(R.drawable.yellowpeople);
                            button1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println(newJson);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Seat:  "+((int)max-(int)peopleThere)+" / "+(int)max+" , Do you want to reserve a seat?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 左方按鈕方法
                                                    try{
                                                    final String place=newJson.getString("place");
                                                        String json="{\"userID\":\""+MainActivity.studentID+"\",\"place\":"+"\""+place +"\",\"BookingTime\":"+System.currentTimeMillis()+"}";
                                                        System.out.println(json);
                                                        JSONObject post_data=new JSONObject(json);
                                                            booking(post_data);
                                                    }catch(Exception e){System.out.println(e);}
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 右方按鈕方法
                                                }
                                            });
                                    AlertDialog about_dialog = builder.create();
                                    about_dialog.show();
                                }
                            });
                        }
                        else {
                            //button1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                            button1.setImageResource(R.drawable.greenpeople);
                            final JSONObject newJson=obj;
                            button1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println(newJson);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Seat:  "+((int)max-(int)peopleThere)+" / "+(int)max+" , Do you want to go there?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 左方按鈕方法
                                                    try{
                                                        final String place=newJson.getString("place");
                                                        String json="{\"userID\":\""+MainActivity.studentID+"\",\"place\":"+"\""+place +"\",\"BookingTime\":"+System.currentTimeMillis()+"}";
                                                        System.out.println(json);
                                                        JSONObject post_data=new JSONObject(json);
                                                        booking(post_data);
                                                    }catch(Exception e){
                                                        System.out.println(e);
                                                    }
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 右方按鈕方法
                                                }
                                            });
                                    AlertDialog about_dialog = builder.create();
                                    about_dialog.show();
                                }
                            });
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
    public void booking(JSONObject jsonObject){
        Call<String> call= LoginActivity.iMyService.booking(jsonObject);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                MainActivity.booked=true;
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
            }
        });
    };

}
