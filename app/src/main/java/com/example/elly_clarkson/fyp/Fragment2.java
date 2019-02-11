package com.example.elly_clarkson.fyp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item2, null);
        spinner = view.findViewById(R.id.spinner);
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
        textView =  (TextView)view.findViewById(R.id.yourLocation);
        recommendArea=view.findViewById(R.id.recommendArea);
        return view;
    }

    public class onItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            blockPosition = (char) ('A' + position);
            yourLocation=""+blockPosition+floorPosition;
            textView.setText(yourLocation);
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class onItemSelectedListener2 implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            floorPosition=position;
            yourLocation=""+blockPosition+floorPosition;
            textView.setText(yourLocation);
            seatRecommended(floorPosition);
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }



    private void seatRecommended(int floorPosition) {
        Call<String> call = LoginActivity.iMyService.seatRecommended(floorPosition);
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                recommendArea.removeAllViews();
                if (response.body() != null) {
                    System.out.println(response.body());
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for(int i=0;i<jsonArray.length();i++) {
                            TableRow tableRow=new TableRow(getActivity());
                            TextView status = new TextView(getActivity());
                            status.setText("   ");
                            status.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                            TextView floor = new TextView(getActivity());
                            floor.setText("Floor: "+jsonArray.getJSONObject(i).getString("floor"));
                            TextView block = new TextView(getActivity());
                            block.setText("Block: "+jsonArray.getJSONObject(i).getString("block"));
                            TextView seatAvailable = new TextView(getActivity());
                            seatAvailable.setText("Seat Available: "+(jsonArray.getJSONObject(i).getInt("MaxSeats")-jsonArray.getJSONObject(i).getInt("PeopleThere")));
                            Button button = new Button(getActivity());
                            button.setText("Book位");
                            tableRow.addView(floor);
                            tableRow.addView(block);
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
                Toast.makeText(getContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
            }
    });
    }
}
