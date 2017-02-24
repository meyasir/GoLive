package utils;

import com.google.android.gms.auth.api.Auth;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Created by Samir KHan on 2/21/2017.
 */

public class MyAuth {

    List<String> scopes;
    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public MyAuth() {
        scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
    }

    public void auth() {

        try {

        // Load client secrets.
        Reader clientSecretReader = new InputStreamReader(MyAuth.class.getResourceAsStream("/client_secrets.json"));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);
            GoogleClientSecrets gcs = new GoogleClientSecrets();

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential "
                            + "into src/main/resources/client_secrets.json");
            System.exit(1);
        }
    } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
