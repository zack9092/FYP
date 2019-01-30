package com.example.elly_clarkson.fyp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elly_clarkson.fyp.Retrofit.IMyService;
import com.example.elly_clarkson.fyp.Retrofit.RetrofitClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    MaterialEditText login_userName,login_userPassword;
    Button loginButton;
    CompositeDisposable compositeDisposable= new CompositeDisposable();
    IMyService iMyService;

    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iMyService = retrofitClient.create(IMyService.class);
    login_userName = (MaterialEditText) findViewById(R.id.userName);
    login_userPassword = (MaterialEditText) findViewById(R.id.userPassword);
    loginButton = (Button) findViewById(R.id.login);
    loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            loginUser(login_userName.getText().toString(), login_userPassword.getText().toString());

        }
    });


    }

    private void loginUser(String userName, String password) {
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "User name cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "User name cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        compositeDisposable.add(iMyService.loginUser(userName,password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String res) throws Exception {
                Toast.makeText(LoginActivity.this, ""+res, Toast.LENGTH_SHORT).show();
                if(res.contains("success")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }));
    }
}
