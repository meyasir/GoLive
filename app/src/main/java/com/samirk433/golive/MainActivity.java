package com.samirk433.golive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

import utils.Broadcast;
import utils.MyAuth;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    // TAG for logcat
    private static final String TAG = MainActivity.class.getName();

    // Sign in request code..
    private static final int  SIGNIN_REQ_CODE = 420;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSign = (Button) findViewById(R.id.btn_signin);
        btnSign.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.btn_signin:

                //initialize GoogleSignInOptions instance
                GoogleSignInOptions signInOptions =
                       new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                       .requestEmail()
                       .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
                       .build();

                //initialize GoogleApiClient instance
                GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.d(TAG, "Connection Result Failed.");
                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                        .build();

                //initialize Intent for GoogleSignInApi
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(intent, SIGNIN_REQ_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Check for SignIn Request code
        if(requestCode == SIGNIN_REQ_CODE){
            GoogleSignInResult signInResult =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(signInResult.isSuccess()){

            GoogleSignInAccount account = signInResult.getSignInAccount();

                Broadcast broadcast = new Broadcast(MainActivity.this
                , account.getAccount());
                broadcast.start();

            } else {
                Log.d(TAG, "Sign in failed");
                Toast.makeText(MainActivity.this, "Sign in Failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
