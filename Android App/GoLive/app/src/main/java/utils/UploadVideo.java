package utils;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samir KHan on 3/13/2017.
 */

public class UploadVideo extends AsyncTask {

    // declare CONSTANTS
    public final static String FILE =
            Environment.getExternalStorageDirectory() + "/DCIM/Camera/1.mp4";

    // Declare variables
    private YouTube.Videos.Insert mVideoInsert;
    private Context mContext;
    private YouTube mYoutube;
    private Account mAccount;


    public UploadVideo(Context context, Account account) {
        this.mContext = context;
        this.mAccount = account;
    }


    @Override
    protected void onPreExecute() {

        try {
            // get credentials
            GoogleAccountCredential credential;
            credential = new MyAuth(mContext, mAccount).getCredential();

            mYoutube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                    credential).setApplicationName(
                    "golive").build();

            // for metadata
            Video v = new Video();

            VideoStatus videoStatus = new VideoStatus();
            videoStatus.setPrivacyStatus("public");
            v.setStatus(videoStatus);

            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle("Testing 1, 2, 3");
            snippet.setDescription("Testing uploading..");

            // add tags
            List<String> tags = new ArrayList<>();
            tags.add("api");
            tags.add("test");

            snippet.setTags(tags);

            v.setSnippet(snippet);

            File file = new File(FILE);
            InputStreamContent content = new InputStreamContent("video/*",
                    new BufferedInputStream(new FileInputStream(file)));
            content.setLength(file.length());


            mVideoInsert = mYoutube.videos()
                    .insert("snippet,statistics,status", v, content);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = mVideoInsert.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);


            // upload event listener
            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                    switch (uploader.getUploadState()) {
                        case INITIATION_STARTED:
                            Log.d(UploadVideo.class.getName(), "Initiation Started");
                            break;
                        case INITIATION_COMPLETE:
                            System.out.println("Initiation Completed");
                            Log.d(UploadVideo.class.getName(), "Initiation Completed");
                            break;
                        case MEDIA_IN_PROGRESS:
                            System.out.println("Upload in progress");
                            //    System.out.println("Upload percentage: " + uploader.getProgress());
                            Log.d(UploadVideo.class.getName(), "Upload percentage: ");
                            break;
                        case MEDIA_COMPLETE:
                            System.out.println("Upload Completed!");
                            Log.d(UploadVideo.class.getName(), "Upload Completed!");
                            break;
                        case NOT_STARTED:
                            System.out.println("Upload Not Started!");
                            Log.d(UploadVideo.class.getName(), "Upload Not Started!");
                            break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            // Call the API and upload the video.
            Video returnedVideo = mVideoInsert.execute();
            return returnedVideo;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.toString();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        Video returnedVideo = (Video) o;

        // Print data about the newly inserted video from the API response.
        System.out.println("\n================== Returned Video ==================\n");
        System.out.println("  - Id: " + returnedVideo.getId());
        System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
        System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
        System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
        System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());
        System.out.println("  - Upload Status: " + returnedVideo.getStatus().getUploadStatus());
        System.out.println("  - Failed Reason: " + returnedVideo.getStatus().getFailureReason());

        super.onPostExecute(o);
    }

    /*
    public void upload() {

        try {
            GoogleAccountCredential credential = new MyAuth(mContext, mAccount).getCredential();
            mYoutube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                    credential).setApplicationName(
                    "golive").build();

            Video v = new Video();
            VideoStatus videoStatus = new VideoStatus();
            videoStatus.setPrivacyStatus("public");
            v.setStatus(videoStatus);

            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle("Testing 1, 2, 3, 4..");
            snippet.setDescription("Testing an API");

            List<String> tags = new ArrayList<>();
            tags.add("api");
            tags.add("test");

            snippet.setTags(tags);
            v.setSnippet(snippet);

            InputStreamContent content = new InputStreamContent("video*/
/*",
                    new ByteArrayInputStream(FILE.getBytes()));


            final YouTube.Videos.Insert videoInsert = mYoutube.videos()
                    .insert("snippet,statistics,status", v, content);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            // Indicate whether direct media upload is enabled. A value of
            // "True" indicates that direct media upload is enabled and that
            // the entire media content will be uploaded in a single request.
            // A value of "False," which is the default, indicates that the
            // request will use the resumable media upload protocol, which
            // supports the ability to resume an upload operation after a
            // network interruption or other transmission failure, saving
            // time and bandwidth in the event of network failures.
            uploader.setDirectUploadEnabled(false);


            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                    switch (uploader.getUploadState()) {
                        case INITIATION_STARTED:
                            Log.d(UploadVideo.class.getName(), "Initiation Started");
                            break;
                        case INITIATION_COMPLETE:
                            System.out.println("Initiation Completed");
                            Log.d(UploadVideo.class.getName(), "Initiation Completed");
                            break;
                        case MEDIA_IN_PROGRESS:
                            System.out.println("Upload in progress");
                            //    System.out.println("Upload percentage: " + uploader.getProgress());
                            Log.d(UploadVideo.class.getName(), "Upload percentage: ");
                            break;
                        case MEDIA_COMPLETE:
                            System.out.println("Upload Completed!");
                            Log.d(UploadVideo.class.getName(), "Upload Completed!");
                            break;
                        case NOT_STARTED:
                            System.out.println("Upload Not Started!");
                            Log.d(UploadVideo.class.getName(), "Upload Not Started!");
                            break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);

            // Call the API and upload the video.

            Video returnedVideo = videoInsert.execute();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
*/

}
