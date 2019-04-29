package com.example.elly_clarkson.fyp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.elly_clarkson.fyp.Retrofit.IMyService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {

    final String[] block ={"Block A", "Block B","Block C" };
    final String[] floor = {"0/F","1/F", "2/F", "3/F", "4/F", "5/F", "6/F", "7/F", "8/F", "9/F", "10/F"};
     Spinner spinner;
     Spinner spinner2;
    char blockPosition;
    int floorPosition=0;
    String yourLocation;
    TextView textView;
    TableLayout recommendArea;
    TableLayout locationArea;
    int check;
    public static List<ScanResult> srList;
    int temp;
    int temp2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item2, null);
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

        linearLayout.removeAllViews();
        if(srList!=null){
            showList();
        }
        //spinner = view.findViewById(R.id.spinner);
        spinner=new Spinner(this.getContext());
        ArrayAdapter<String> blockList = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                block);
        spinner.setAdapter(blockList);
/*
         temp = new Random().nextInt(3);
        System.out.println("Random1:  "+temp);
        temp2 = new Random().nextInt(10)+1;

        System.out.println("Random2: "+temp2);
*/
       // spinner2=view.findViewById(R.id.spinner2);
        spinner2=new Spinner(this.getContext());
        ArrayAdapter<String> floorList = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                floor);
        spinner2.setAdapter(floorList);

        spinner.setOnItemSelectedListener(new onItemSelectedListener());
        spinner2.setOnItemSelectedListener(new onItemSelectedListener2());
        View line1 = new View(this.getContext());
        line1.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
        ));
        line1.setBackgroundColor(Color.parseColor("#808080"));
        View line2 = new View(this.getContext());
        line2.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
        ));
        line2.setBackgroundColor(Color.parseColor("#808080"));
    linearLayout.addView(spinner);
        linearLayout.addView(line1);
    linearLayout.addView(spinner2);
        linearLayout.addView(line2);

      //  spinner.setSelection(temp);
       // spinner2.setSelection(temp2);
       // textView =  (TextView)view.findViewById(R.id.yourLocation);


        recommendArea=view.findViewById(R.id.recommendArea);
        //locationArea=view.findViewById(R.id.locationArea);


       /* Spinner newSpinner1=new Spinner(this.getContext());
        Spinner newSpinner2=new Spinner(this.getContext());
        newSpinner1.setAdapter(blockList);
        newSpinner1.setSelection(temp);
        newSpinner2.setAdapter(floorList);
        newSpinner2.setSelection(temp2);
        TableRow spinnerRow1=view.findViewById(R.id.spinnerRow1);
        spinnerRow1.removeView(spinner);
        spinnerRow1.addView(newSpinner1);
        TableRow spinnerRow2=view.findViewById(R.id.spinnerRow2);
        spinnerRow2.removeView(spinner2);
        spinnerRow2.addView(newSpinner2);
*/

        String selectedBlock = "" + spinner.getSelectedItem().toString().charAt(6);
        String selectedFloor = "" + spinner2.getSelectedItem().toString().substring(0,spinner2.getSelectedItem().toString().indexOf('/'));

        //yourLocation = selectedBlock + "" + selectedFloor;
       // textView.setText(yourLocation);
        //System.out.print(yourLocation);

        seatRecommended(Integer.parseInt(selectedFloor));
        //testing();
        return view;
    }

    public class onItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            String selectedBlock=""+spinner.getSelectedItem().toString().charAt(6);
            String selectedFloor = "" + spinner2.getSelectedItem().toString().substring(0,spinner2.getSelectedItem().toString().indexOf('/'));

            //blockPosition = (char) ('A' + position);
            yourLocation=selectedBlock+""+selectedFloor;

        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class onItemSelectedListener2 implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                //floorPosition=position;
                String selectedBlock = "" + spinner.getSelectedItem().toString().charAt(6);
                String selectedFloor = "" + spinner2.getSelectedItem().toString().substring(0,spinner2.getSelectedItem().toString().indexOf('/'));

                System.out.print(spinner.getSelectedItem().toString());
                System.out.println(spinner2.getSelectedItem().toString());

                yourLocation = selectedBlock + "" + selectedFloor;
               // yourLocation = selectedBlock + "" + floorPosition;

                seatRecommended(Integer.parseInt(selectedFloor));

        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public void booking(JSONObject jsonObject,int floorPosition){
        final int  temp= floorPosition;
        Call<String> call= LoginActivity.iMyService.booking(jsonObject);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                MainActivity.booked=true;
                seatRecommended(temp);
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
            }
        });
    };



    private void seatRecommended(int floorPosition) {

        Call<String> call = LoginActivity.iMyService.seatRecommended(floorPosition);
        final int  temp= floorPosition;
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                recommendArea.removeAllViews();
                if (response.body() != null) {
                    String res=(String)(response.body());

                   //System.out.println(res);

                    try {
                        JSONObject temptemp=new JSONObject(response.body().toString());
                        JSONArray lowerFloor=temptemp.getJSONArray("lowerFloor");
                        JSONArray upperFloor = temptemp.getJSONArray("upperFloor");
                        JSONArray jsonArray = findNearestSeat(temp,lowerFloor,upperFloor);

                        for(int i=0;i<jsonArray.length();i++) {
                            TableRow tableRow=new TableRow(getActivity());
                            TextView status = new TextView(getActivity());
                            status.setText("    ");
                            final float there=(float)jsonArray.getJSONObject(i).getInt("PeopleThere")+jsonArray.getJSONObject(i).getInt("booking");
                            final float max=(float)jsonArray.getJSONObject(i).getInt("MaxSeats");
                            if(there/max>0.5)
                                status.setBackgroundColor(Color.YELLOW);
                            else{
                                status.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                            }
                            TextView placeTextView= new TextView(getActivity());
                            placeTextView.setText("Place: " + jsonArray.getJSONObject(i).getString("place"));

                            TextView seatAvailable = new TextView(getActivity());
                            seatAvailable.setText("Seat: "+(jsonArray.getJSONObject(i).getInt("MaxSeats")-jsonArray.getJSONObject(i).getInt("PeopleThere")-jsonArray.getJSONObject(i).getInt("booking"))+"/"+jsonArray.getJSONObject(i).getInt("MaxSeats"));
                            Button button = new Button(getActivity());
                            final String place=jsonArray.getJSONObject(i).getString("place");
                            final int booking=jsonArray.getJSONObject(i).getInt("booking");
                            final JSONArray a= jsonArray;
                            final int b=i;
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Seat:  "+((int)max-(int)there)+" / "+(int)max+" , Do you want to go there?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 左方按鈕方法
                                            try{
                                                LayoutInflater li = LayoutInflater.from(getContext());
                                                View promptsView = li.inflate(R.layout.prompts, null);
                                                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                                                builder2.setView(promptsView);
                                                final EditText userInput = (EditText) promptsView
                                                        .findViewById(R.id.editTextDialogUserInput);
                                                builder2.setPositiveButton("OK", null)
                                                        .setNegativeButton("Cancel", null).create();
                                                final AlertDialog about_dialog = builder2.create();
                                                about_dialog.show();
                                                Button positiveButton = about_dialog.getButton(BUTTON_POSITIVE);
                                                positiveButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try{
                                                            if(Integer.parseInt(userInput.getText().toString())<=0||Integer.parseInt(userInput.getText().toString())>20){
                                                                System.out.println("Error");
                                                                userInput.setError("need>0 or <=20");
                                                            }
                                                            else{
                                                                System.out.println(userInput.getText());
                                                                String json="{\"userID\":\""+MainActivity.studentID+"\",\"place\":"+"\""+place +"\",\"peopleNumber\":"+Integer.parseInt(userInput.getText().toString())+",\"BookingTime\":"+System.currentTimeMillis()+"}";
                                                                System.out.println(json);
                                                                JSONObject post_data=new JSONObject(json);
                                                                booking(post_data,temp);
                                                                about_dialog.dismiss();
                                                            };
                                                        }catch(Exception e){
                                                            System.out.println(e);
                                                        }
                                                    }
                                                });



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
                                  //  try{JSONObject post_data=new JSONObject(json);
                                    //    booking(post_data,temp);
                                   // }catch(Exception e){}

                                }
                            });
                            button.setText("Go");
                            tableRow.addView(placeTextView);

                            tableRow.addView(seatAvailable);
                            tableRow.addView(status);
                            tableRow.addView(button);
                            recommendArea.addView(tableRow);
                        }
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println(t);
                Toast.makeText(getContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
            }
    });
    }
    public JSONArray findNearestSeat(int currentFloor,JSONArray lowerFloor,JSONArray upperFloor){
        try{
        if(lowerFloor.length()==0){
            return upperFloor;
        }
        if(upperFloor.length()==0){
            return lowerFloor;
        }

        JSONArray result=new JSONArray();
        int lowerFloorDistance;
        int upperFloorDistance;
        int lowerCheck=lowerFloor.length();
        int upperCheck=upperFloor.length();
        for(int i=0;i<lowerFloor.length();i++){
            lowerCheck--;
            lowerFloorDistance=currentFloor-lowerFloor.getJSONObject(i).getInt("floor");
            for(int x=0;x<upperFloor.length();x++){
                upperCheck--;
                upperFloorDistance=upperFloor.getJSONObject(x).getInt("floor")-currentFloor;
                if(lowerFloorDistance<=upperFloorDistance){
                    result.put(lowerFloor.getJSONObject(i));
                    if(lowerCheck==0)
                        result.put(upperFloor.getJSONObject(x));
                    break;
                }else{
                    result.put(upperFloor.getJSONObject(x));
                }
                if(result.length()>=3){
                    return result;
                }
            }
        }
        while(true){
            if(lowerCheck==0&&upperCheck==0||result.length()>=3){
                break;

            }
            if(lowerCheck==0&&upperCheck!=0){
                result.put(upperFloor.get(upperFloor.length()-upperCheck));
                upperCheck--;
            }
            if(lowerCheck!=0&&upperCheck==0){
                result.put(lowerFloor.get(lowerFloor.length()-lowerCheck));
                lowerCheck--;
            }
        }
            return result;
        }catch(Exception e){
            System.out.println(e);
        };
        return null;
    }


    private void showList(){
        System.out.print(" 可連線的wifi：\n");
        for (int i = 0; i < srList.size(); i++) {
            ScanResult sr = srList.get(i);
            System.out.println(sr.SSID+"         "+sr.level);
        }
    }

  /*  @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }*/




    public void testing(){
        textView=new TextView(this.getContext());
        textView.setText(yourLocation);
        spinner.setSelection(temp,true);
        spinner2.setSelection(temp2,true);
    }
}
