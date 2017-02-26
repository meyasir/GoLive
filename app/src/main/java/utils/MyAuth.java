package utils;


import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.Collections;


/**
 * Created by Samir KHan on 2/21/2017.
 */

public class MyAuth {

    // TAG for logcat
    private static final String TAG = MyAuth.class.getName();

    private Context context;

    // instance of User Acccount
    private Account account;

    /**
     * Constructor
     * @param context ->
     * @param account  -> User account
     */
    public MyAuth(Context context, Account account) {
        this.context = context;
        this.account = account;
    }

    /**
     * get GoogleAccountCredential instance
     */
    public GoogleAccountCredential getCredential() {
        try {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    context, Collections.singleton(
                            "https://www.googleapis.com/auth/youtube")
            );
            credential.setSelectedAccount(account);

            return credential;
        }
        catch(Exception exp){
            Log.d(TAG, exp.getMessage());
        }
        return null;
    }
}
