package edu.heinz.ds.popularRepo;

import java.util.ArrayList;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

// Author: Guojiang Zhao & Yunxuan Yu
// AndrewID: guojianz & yunxuany
// This android project is developed mainly based on the androidInterestingPicture from lab 8

public class PopularRepo extends AppCompatActivity {

    PopularRepo pr = this;

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
                // Done asynchronously in another thread.
                // It calls pr.repoReady() in this thread when complete.
                gp.search(searchLanguage, pr, ma);
            }
        });
    }

    /*
     * This is called by the GetRepo object when the picture is ready.
     * This allows for passing back the repo info for updating the Listview
     */
    public void repoReady(JSONArray repos, String language) throws JSONException {
        TextView message = (TextView) findViewById(R.id.textView);
        ListView resultView = (ListView)findViewById(R.id.listview);
        ArrayList<String> repoInfo = new ArrayList<>();
        TextView searchView = (EditText)findViewById(R.id.searchLanguage);
        if (repos.length() != 0) {
            for (int i = 0; i < 5; i++){
                JSONObject json = repos.getJSONObject(i);
                String info = "Name: " + (String) json.get("name") + "\n"
                        + "Owner: " + (String) json.get("owner") + "\n"
                        + "Desc: " + (String) json.get("description") + "\n"
                        + "Stars: " + json.getInt("stars") + "\n"
                        + "Url: " + (String) json.get("url") + "\n";
                repoInfo.add(info);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, repoInfo);
            message.setText("Here are top five popular github repo of " + searchView.getText());
            resultView.setAdapter(arrayAdapter);
            resultView.setVisibility(View.VISIBLE);
        }
        // handle error
        else {
            // empty input
            if (searchView.getText().length() == 0) {
                message.setText("Input cannot be empty");
            } else {
                // empty result
                message.setText("No matching repos of " + searchView.getText());
            }
            resultView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, new ArrayList<>()));
            resultView.setVisibility(View.INVISIBLE);
        }
        searchView.setText("");
        resultView.invalidate();
    }
}
