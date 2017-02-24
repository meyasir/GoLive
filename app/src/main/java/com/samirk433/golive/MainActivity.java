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

import utils.MyAuth;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    final int  SIGNIN_REQ_CODE = 420;


    /**
     * Define a global instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

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

               GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                       .requestEmail()
                       .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
                       .build();

                GoogleApiClient gac = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(gac);
                startActivityForResult(intent, SIGNIN_REQ_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SIGNIN_REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
            GoogleSignInAccount acc = result.getSignInAccount();

               check1(acc.getAccount());

            } else {
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void check1(Account acc) {

        try {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    MainActivity.this,
                    Collections.singleton(
                            "https://www.googleapis.com/auth/youtube")
            );
            credential.setSelectedAccount(acc);

            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("golive-158012").build();
        }
        catch(Exception exp){
            Log.d("Class", exp.getMessage());
        }

    }
}
