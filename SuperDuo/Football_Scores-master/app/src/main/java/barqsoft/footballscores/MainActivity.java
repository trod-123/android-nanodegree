package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import barqsoft.footballscores.sync.ScoresSyncAdapter;
import barqsoft.footballscores.utilities.Network;

public class MainActivity extends ActionBarActivity
{
    // Why is this static? What is this used for?
    // This is for keeping track of which element has been expanded so that upon activity
    //  recreation, that element remains expanded. Used in onSaveInstanceState().
    // It needs to be in the main activity because we are using pagers and the main activity
    //  keeps track of all the pagers and all the elements that are within each pager.
    //  Upon recreation, the activity should also remember which fragment is visible, so that
    //  it also loads that up.
    // TODO: HOWEVER. There is a bug in which if you expand 2 games from 2 different tabs, both
    //  may remain expanded, whereas the intent is that only one remains expanded, when you switch
    //  tabs.
    public static int selected_match_id;

    public static int current_fragment = 2;
    public static String LOG_TAG = "MainActivity";
    private final String save_tag = "Save Test";
    private PagerFragment my_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Reached MainActivity onCreate");
        if (savedInstanceState == null) {
            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();
        }
        // Make sure there is internet connection first before going to sync
        if (Network.isNetworkAvailable(this)) {
            ScoresSyncAdapter.initializeSyncAdapter(this);
        } else {
            Toast.makeText(this, getString(R.string.status_no_internet), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // This is for restoring the current fragment that is displayed when activity restarts itself
        // Uses a pager handler to handle all the fragments in a single object
        Log.v(save_tag,"will save");
        Log.v(save_tag,"fragment: "+String.valueOf(my_main.mPagerHandler.getCurrentItem()));
        Log.v(save_tag,"selected id: "+selected_match_id);
        outState.putInt("Pager_Current",my_main.mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match",selected_match_id);
        getSupportFragmentManager().putFragment(outState,"my_main",my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(save_tag,"will retrive");
        Log.v(save_tag,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(save_tag,"selected id: "+savedInstanceState.getInt("Selected_match"));
        current_fragment = savedInstanceState.getInt("Pager_Current");
        selected_match_id = savedInstanceState.getInt("Selected_match");
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,"my_main");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
