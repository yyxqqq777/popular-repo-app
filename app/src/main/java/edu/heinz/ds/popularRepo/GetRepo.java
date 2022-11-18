package edu.heinz.ds.popularRepo;

import android.app.Activity;
//import android.os.Build;
//import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 * This class provides capabilities to search for an image on Flickr.com given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of inner class BackgroundTask that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView pictureReady method to do the update.
 * 
 * Method BackgroundTask.doInBackground( ) does the background work
 * Method BackgroundTask.onPostExecute( ) is called when the background work is
 *    done; it calls *back* to ip to report the results
 *
 */
public class GetRepo {
    PopularRepo ip = null;   // for callback
    String searchLanguage = null;       // search Flickr for this word
    JSONArray repos = null;          // returned from Flickr

    // search( )
    // Parameters:
    // String searchTerm: the thing to search for on flickr
    // Activity activity: the UI thread activity
    // InterestingPicture ip: the callback method's class; here, it will be ip.pictureReady( )
    public void search(String searchLanguage, Activity activity, PopularRepo ip) {
        this.ip = ip;
        this.searchLanguage = searchLanguage;
        new BackgroundTask(activity).execute();
    }

    // class BackgroundTask
    // Implements a background thread for a long running task that should not be
    //    performed on the UI thread. It creates a new Thread object, then calls doInBackground() to
    //    actually do the work. When done, it calls onPostExecute(), which runs
    //    on the UI thread to update some UI widget (***never*** update a UI
    //    widget from some other thread!)
    //
    // Adapted from one of the answers in
    // https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives
    // Modified by Barrett
    //
    // Ideally, this class would be abstract and parameterized.
    // The class would be something like:
    //      private abstract class BackgroundTask<InValue, OutValue>
    // with two generic placeholders for the actual input value and output value.
    // It would be instantiated for this program as
    //      private class MyBackgroundTask extends BackgroundTask<String, Bitmap>
    // where the parameters are the String url and the Bitmap image.
    //    (Some other changes would be needed, so I kept it simple.)
    //    The first parameter is what the BackgroundTask looks up on Flickr and the latter
    //    is the image returned to the UI thread.
    // In addition, the methods doInBackground() and onPostExecute( ) could be
    //    abstract methods; would need to finesse the input and ouptut values.
    // The call to activity.runOnUiThread( ) is an Android Activity method that
    //    somehow "knows" to use the UI thread, even if it appears to create a
    //    new Runnable.

    private class BackgroundTask {

        private Activity activity; // The UI thread

        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {
                        doInBackground();
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        private void execute(){
            // There could be more setup here, which is why
            //    startBackground is not called directly
            startBackground();
        }

        // doInBackground( ) implements whatever you need to do on
        //    the background thread.
        // Implement this method to suit your needs
        private void doInBackground() {
            try {
                repos = search(searchLanguage);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // onPostExecute( ) will run on the UI thread after the background
        //    thread completes.
        // Implement this method to suit your needs
        public void onPostExecute() {
            try {
                ip.repoReady(repos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*
         * Search through heroku for the searchTerm argument, and return ArrayList String that can be put in an ListView
         */
        private JSONArray search(String language) throws UnsupportedEncodingException {
//            ArrayList<String> NameDate = new ArrayList<String>();
            language = URLEncoder.encode(language.toLowerCase(), "UTF-8");
            // search for repo
            String URL = "https://thawing-refuge-82904.herokuapp.com/language/"
                    + language;
            return fetch(URL);
        }

        // fetch album name and release date
        private JSONArray fetch(String urlString) {
            String response = "";
            JSONArray result = new JSONArray() ;
            try {
                URL url = new URL(urlString);
//                System.out.println("-------urlString is " + urlString);
                /*
                 * Create an HttpURLConnection.  This is useful for setting headers
                 * and for getting the path of the resource that is returned (which
                 * may be different from the URL above if redirected).
                 * HttpsURLConnection (with an "s") can be used if required by the site.
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String str;
                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }
                in.close();
                JSONObject responseJSON = new JSONObject(response);
//                System.out.println("***** status_code is " + (int)responseJSON.get("status_code"));
                if ((int)responseJSON.get("status_code") == 200) {
                    result = (JSONArray) responseJSON.get("result");
                }
//                result = (JSONArray) responseJSON.get("result");
//                System.out.println("----response app is " + response);

            } catch (IOException e) {
                System.out.println("***** exception *****");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

    }
}

