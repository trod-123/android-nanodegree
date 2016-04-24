package com.thirdarm.jokesui;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.thirdarm.jokes.backend.jokesApi.JokesApi;
import com.thirdarm.jokes.backend.jokesApi.model.Joke;
import com.thirdarm.jokes.backend.jokesApi.model.JokesContainer;

import java.io.IOException;
import java.util.List;

/**
 * Created by TROD on 20160416.
 */
public class ResetJokesDatabaseTask extends AsyncTask<Void, Void, List<Joke>> {
    private static final String LOG_TAG = ResetJokesDatabaseTask.class.getSimpleName();

    private static JokesApi myApiService = null;
    private TaskFinishedListener listener;

    public ResetJokesDatabaseTask(TaskFinishedListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Joke> doInBackground(Void... params) {
        if (myApiService == null) {  // Only do this once
            // Connect to the GCE backend via App Engine (this is also reference via webapp/WEB-INF/appengine.web.xml
            JokesApi.Builder builder = new JokesApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://jokesapp-1239.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        // This part attempts to remove a joke from the container
        // TODO: Here is where you can call a specific method in the API, from where you want the
        //  response. Each AsyncTask can be used to call a specific method.
        try {
            JokesContainer collection = myApiService.resetJokesContainer().execute();
            return collection != null ? collection.getAllJokes() : null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "There was an error removing the joke: " + e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Joke> result) {
        if (listener != null) {
            listener.onTaskFinished(result);
        }
    }
}
