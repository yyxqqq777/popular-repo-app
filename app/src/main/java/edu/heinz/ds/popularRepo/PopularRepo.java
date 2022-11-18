package edu.heinz.ds.popularRepo;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PopularRepo extends AppCompatActivity {

    PopularRepo me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from Flickr, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this InterestingPicture.
         */
        final PopularRepo ma = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);


        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchLanguage = ((EditText)findViewById(R.id.searchLanguage)).getText().toString();
                System.out.println("searchLanguage = " + searchLanguage);
                GetRepo gp = new GetRepo();
                gp.search(searchLanguage, me, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void repoReady(JSONArray repos) throws JSONException {
        ListView resultView = (ListView)findViewById(R.id.listview);
        TextView searchView = (EditText)findViewById(R.id.searchLanguage);
        TextView message = (TextView) findViewById(R.id.textView);
        ArrayList<String> repoUrl = new ArrayList<>();
        if (repos.length() != 0) {
            for (int i = 0; i < 5; i++){
                JSONObject json = repos.getJSONObject(i);
                String info = "Name: " + (String) json.get("name") + "\n"
                        + "Owner: " + (String) json.get("owner") + "\n"
                        + "Desc: " + (String) json.get("description") + "\n"
                        + "Stars: " + json.getInt("stars") + "\n"
                        + "Url: " + (String) json.get("url") + "\n";
                repoUrl.add(info);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, repoUrl);
            resultView.setAdapter(arrayAdapter);
            message.setText("Here are the three most popular github repo of " + searchView.getText());
            resultView.setVisibility(View.VISIBLE);
        } else {
            message.setText("No matching repos of " + searchView.getText());
            resultView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, new ArrayList<>()));
            resultView.setVisibility(View.INVISIBLE);
        }
        searchView.setText("");
        resultView.invalidate();
    }
}
