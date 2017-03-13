package utils;

import android.accounts.Account;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.telecom.CallScreeningService;
import android.util.Log;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastSnippet;
import com.google.api.services.youtube.model.LiveBroadcastStatus;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamSnippet;

/**
 * Created by Samir KHan on 2/25/2017.
 */

public class Broadcast extends Thread {

    // TAG for logcat
    private static final String TAG = Broadcast.class.getName();

    // Define a global instance of the HTTP transport.
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    // Define a global instance of the Jackson Factory.
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();


    private Context context;

    // instance of User Acccount
    private Account account;

    //youtube instance
   YouTube youtube;

    public Broadcast(Context context, Account account){
        this.context = context;
        this.account = account;
    }

    public void run(){

        try {
            //authorized the request
            MyAuth auth = new MyAuth(context, account);
            GoogleAccountCredential credential = auth.getCredential();

            // golive-158012
            // 552079036475-s7id5gv25ii9djrk664r8tn9lptfbeco.apps.googleusercontent.com


            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("552079036475-s7id5gv25ii9djrk664r8tn9lptfbeco.apps.googleusercontent.com").build();

            // Create a snippet with the title and scheduled start and end
            // times for the broadcast. Currently, those times are hard-coded.
            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
            broadcastSnippet.setTitle("title");
            broadcastSnippet.setScheduledStartTime(new DateTime("2017-02-26T00:00:00.000Z"));
            broadcastSnippet.setScheduledEndTime(new DateTime("2017-02-27T00:00:00.000Z"));

            LiveBroadcastStatus status = new LiveBroadcastStatus();
            status.setPrivacyStatus("private");

            LiveBroadcast broadcast = new LiveBroadcast();
            broadcast.setKind("youtube#liveBroadcast");
            broadcast.setSnippet(broadcastSnippet);
            broadcast.setStatus(status);

            // Construct and execute the API request to insert the broadcast.
            YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
                    youtube.liveBroadcasts().insert("snippet,status", broadcast);
            LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();

            Log.d(TAG, "  - Id: " + returnedBroadcast.getId());
            Log.d(TAG, "  - Title: " + returnedBroadcast.getSnippet().getTitle());
            Log.d(TAG, "  - Description: " + returnedBroadcast.getSnippet().getDescription());
            Log.d(TAG, "  - Published At: " + returnedBroadcast.getSnippet().getPublishedAt());
            Log.d(TAG,
                    "  - Scheduled Start Time: " + returnedBroadcast.getSnippet().getScheduledStartTime());
            Log.d(TAG,
                    "  - Scheduled End Time: " + returnedBroadcast.getSnippet().getScheduledEndTime());

            // Create a snippet with the video stream's title.
            LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
            streamSnippet.setTitle("title 2");

            // Define the content distribution network settings for the
            // video stream. The settings specify the stream's format and
            // ingestion type. See:
            // https://developers.google.com/youtube/v3/live/docs/liveStreams#cdn
            CdnSettings cdnSettings = new CdnSettings();
            cdnSettings.setFormat("1080p");
            cdnSettings.setIngestionType("rtmp");

            LiveStream stream = new LiveStream();
            stream.setKind("youtube#liveStream");
            stream.setSnippet(streamSnippet);
            stream.setCdn(cdnSettings);
            

            // Construct and execute the API request to insert the stream.
            YouTube.LiveStreams.Insert liveStreamInsert =
                    youtube.liveStreams().insert("snippet,cdn", stream);
            LiveStream returnedStream = liveStreamInsert.execute();

            // Print information from the API response.
            Log.d(TAG, "\n================== Returned Stream ==================\n");
            Log.d(TAG, "  - Id: " + returnedStream.getId());
            Log.d(TAG, "  - Title: " + returnedStream.getSnippet().getTitle());
            Log.d(TAG, "  - Description: " + returnedStream.getSnippet().getDescription());
            Log.d(TAG, "  - Published At: " + returnedStream.getSnippet().getPublishedAt());

            // Construct and execute a request to bind the new broadcast
            // and stream.
            YouTube.LiveBroadcasts.Bind liveBroadcastBind =
                    youtube.liveBroadcasts().bind(returnedBroadcast.getId(), "id,contentDetails");
            liveBroadcastBind.setStreamId(returnedStream.getId());
            returnedBroadcast = liveBroadcastBind.execute();

            // Print information from the API response.
            Log.d(TAG,"\n================== Returned Bound Broadcast ==================\n");
            Log.d(TAG,"  - Broadcast Id: " + returnedBroadcast.getId());
            Log.d(TAG,
                    "  - Bound Stream Id: " + returnedBroadcast.getContentDetails().getBoundStreamId());
        } catch (Exception exp) {
            Log.d(TAG, exp.getMessage());
        } catch (Throwable t){
            Log.d(TAG, t.getMessage());
        }

    }



}
