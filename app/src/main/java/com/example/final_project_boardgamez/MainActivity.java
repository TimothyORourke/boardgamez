package com.example.final_project_boardgamez;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.final_project_boardgamez.GameData.Game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GameManagerAdapter.OnGameClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mMainGameListRV;
    private GameManagerAdapter mAdapterRV;
    private GameManagerAdapter mFilteredAdapterRV;
    private RecyclerView.LayoutManager mLayoutManagerRV;
    private SavedGamesViewModel mSavedGamesViewModel;
    private TextView mAppliedFiltersTV;
    private String[] mFilterItems;
    private boolean[] mCheckedFilters;
    ArrayList<Integer> mSelectedFilters = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Main: CREATING... ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFilterItems = getResources().getStringArray(R.array.filter_list);
        mCheckedFilters = new boolean[mFilterItems.length];

        mAppliedFiltersTV = findViewById(R.id.tv_applied_filters);
        mAppliedFiltersTV.setVisibility(View.GONE);
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_add_sign);
*/
        mMainGameListRV = findViewById(R.id.rv_main);
        mMainGameListRV.setHasFixedSize(true);

        /* Setup layout manager */
        mLayoutManagerRV = new LinearLayoutManager(this);
        mMainGameListRV.setLayoutManager(mLayoutManagerRV);

        /* Setup adapter */
        mFilteredAdapterRV = new GameManagerAdapter(this);
        mAdapterRV = new GameManagerAdapter(this);
        mMainGameListRV.setAdapter(mAdapterRV);


        mSavedGamesViewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(SavedGamesViewModel.class);

        mSavedGamesViewModel.getAllSavedGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mAdapterRV.updateGameCollection(games);
            }
        });

        Button fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            onFilterSettingsClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGameClicked(Game game) {          // Passes the whole game item
        Log.d(TAG, "Main: Recognized the game click");
        // Handles games being clicked on in the main activity
        // Brings the user to the detailed page
        Intent intent = new Intent(this, GameDetailedActivity.class);
        intent.putExtra(GameDetailedActivity.EXTRA_GAME_INFO, game);
        startActivity(intent);
    }

    private void onFilterSettingsClicked() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Filter by Tag");
        mBuilder.setMultiChoiceItems(mFilterItems, mCheckedFilters, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mSelectedFilters.contains(position)){
                        mSelectedFilters.add(position);
                    }
                } else { // Filter was unchecked
                    if (mSelectedFilters.contains(position)) {
                        mSelectedFilters.remove(position);
                    }
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                List<Game> gameList = mAdapterRV.getGameList();         // Create a copy of games
                Log.d(TAG, "making new list");
                List<Game> gameListNew = new ArrayList<>();

                String filterItem = "";
                if (mSelectedFilters.size() > 0) {
                    for (int i = 0; i < mSelectedFilters.size(); i++) {
                        filterItem = filterItem + mFilterItems[mSelectedFilters.get(i)];
                        if (i != mSelectedFilters.size() - 1) {
                            filterItem = filterItem + ", ";
                        }
                        //Log.d(TAG, filterItem);
                        Log.d(TAG, mFilterItems[mSelectedFilters.get(i)]);

                        if(mFilterItems[mSelectedFilters.get(i)].equals("Owned")) {
                            for (int j = 0; j < gameList.size(); j++) {
                                if (gameList.get(j).tag_owned && !gameListNew.contains(gameList.get(j))){
                                    Log.d(TAG, "Adding Game1: " + gameList.get(j).name);
                                    gameListNew.add(gameList.get(j));
                                }
                            }
                        }
                        if(mFilterItems[mSelectedFilters.get(i)].equals("Wishlist")) {
                            for (int j = 0; j < gameList.size(); j++) {
                                if (gameList.get(j).tag_wishlist&& !gameListNew.contains(gameList.get(j))){
                                    Log.d(TAG, "Adding Game2: " + gameList.get(j).name);
                                    gameListNew.add(gameList.get(j));
                                }
                            }
                        }
                        if(mFilterItems[mSelectedFilters.get(i)].equals("Has Played")) {
                            for (int j = 0; j < gameList.size(); j++) {
                                if (gameList.get(j).tag_played&& !gameListNew.contains(gameList.get(j))){
                                    Log.d(TAG, "Adding Game3: " + gameList.get(j).name);
                                    gameListNew.add(gameList.get(j));
                                }
                            }
                        }

                        Log.d(TAG, String.valueOf(gameListNew.size()));
//                        for (int j = 0; j < gameListNew.size(); j++) {
//                            Log.d(TAG, gameList.get(j).name);
//                        }

                        mFilteredAdapterRV.updateGameCollection(gameListNew);
                        mMainGameListRV.setAdapter(mFilteredAdapterRV);

                    }
                    mAppliedFiltersTV.setText("Tag filters: " + filterItem);
                    mAppliedFiltersTV.setVisibility(View.VISIBLE);
                    // Set text here
                } else {
                    mAppliedFiltersTV.setVisibility(View.GONE);
                }



            }
        });

        mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < mCheckedFilters.length; i++) {  // Loop through checked items
                    mCheckedFilters[i] = false;
                    mSelectedFilters.clear();
                    mAppliedFiltersTV.setText("");
                    mAppliedFiltersTV.setVisibility(View.GONE);
                    // Clear text view if any
                }
                mMainGameListRV.setAdapter(mAdapterRV);
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
}
