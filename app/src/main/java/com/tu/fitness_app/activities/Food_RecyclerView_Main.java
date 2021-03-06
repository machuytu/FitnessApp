package com.tu.fitness_app.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tu.fitness_app.Adapter.Food_MyRecycleAdapter;
import com.tu.fitness_app.JSON.FoodDataJson;
import com.tu.fitness_app.R;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;

public class Food_RecyclerView_Main extends Fragment {
    private static final String ARG_MOVIE = "R.id.mdf_main_replace";
    FoodDataJson foodData;
    private RecyclerView mRecyclerView;
    private LayoutManager mLayoutManager;
    private Food_MyRecycleAdapter mRecyclerViewAdapter;
    private FloatingActionButton voice;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String voice_query = "";

    public Food_RecyclerView_Main() {
        // Constructor
    }



    public static Food_RecyclerView_Main newInstance() {
        Food_RecyclerView_Main fragment = new Food_RecyclerView_Main();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE, "R.id.rcmain");
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        foodData = new FoodDataJson();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_recyclerview_activity, container, false);
        mRecyclerView = rootView.findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new Food_MyRecycleAdapter(getActivity(), foodData.foodList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        // Animation
        mRecyclerView.setItemAnimator(new ScaleInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(100);
        mRecyclerView.getItemAnimator().setRemoveDuration(1000);
        mRecyclerView.getItemAnimator().setChangeDuration(100);
        mRecyclerView.getItemAnimator().setMoveDuration(100);
        ScaleInBottomAnimator animator = new ScaleInBottomAnimator();
        mRecyclerView.setItemAnimator(animator);

        // Adapter Animation
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(mRecyclerViewAdapter));
        ScaleInAnimationAdapter alphaApdater = new ScaleInAnimationAdapter(mRecyclerViewAdapter);
        alphaApdater.setDuration(300);
        mRecyclerView.setAdapter(alphaApdater);
        // Voice
//        voice = (FloatingActionButton) rootView.findViewById(R.id.vsfb);
//        voice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                promptSpeechInput();
//            }
//        });
        return rootView;
    }

    /**
     * To do show google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Not Supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voice_query = (result.get(0));
                    Log.d("voice", voice_query);
                }
                break;
            }
        }
    }

    // Search Menu
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_actionview, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView == null) {
            if (voice_query != null) {
                searchView.setIconifiedByDefault(true);
                searchView.setQuery(voice_query,true);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        String food;
                        food = query;
                        food = food.replace("", "");
                        String f_url = "https://api.nutritionix.com/v1_1/search/" +
                                food +
                                "?results=0%3A20" +
                                "&cal_min=0" +
                                "&cal_max=50000" +
                                "&fields=item_name" +
                                "%2Cbrand_name" +
                                "%2Citem_id" +
                                "%2Cbrand_id" +
                                "%2Citem_description" +
                                "%2Cnf_protein" +
                                "%2Cnf_calories" +
                                "%2Cnf_total_carbohydrate" +
                                "%2Cnf_total_fat" +
                                "&appId=03ec51c4" +
                                "&appKey=6dd7550cf2d47e287a47bbb7aa34a27e";
                        MyDownloadJsonAsyncTask downloadJson = new MyDownloadJsonAsyncTask(mRecyclerViewAdapter);
                        Log.d("Tu:", f_url);
                        downloadJson.execute(f_url);
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        }
        if (searchView != null) {
            Log.d("voice 1:", voice_query);
            searchView.setIconifiedByDefault(true);
            searchView.setQuery(voice_query,true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String food;
                    food = query;
                    food = food.replace("","");
                    String f_url = "https://api.nutritionix.com/v1_1/search/" +
                            food +
                            "?results=0%3A20" +
                            "&cal_min=0" +
                            "&cal_max=50000" +
                            "&fields=item_name" +
                            "%2Cbrand_name" +
                            "%2Citem_id" +
                            "%2Cbrand_id" +
                            "%2Citem_description" +
                            "%2Cnf_protein" +
                            "%2Cnf_calories" +
                            "%2Cnf_total_carbohydrate" +
                            "%2Cnf_total_fat" +
                            "&appId=03ec51c4" +
                            "&appKey=6dd7550cf2d47e287a47bbb7aa34a27e";
                    MyDownloadJsonAsyncTask downloadJson = new MyDownloadJsonAsyncTask(mRecyclerViewAdapter);
                    Log.d("Tu:", f_url);
                    downloadJson.execute(f_url);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    // Select Item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private class MyDownloadJsonAsyncTask extends AsyncTask<String, Void, FoodDataJson> {
        private final WeakReference<Food_MyRecycleAdapter> adapterReference;

        private MyDownloadJsonAsyncTask(Food_MyRecycleAdapter adapter) {
            adapterReference = new WeakReference<Food_MyRecycleAdapter>(adapter);
        }

        @Override
        protected FoodDataJson doInBackground(String... urls) {
            FoodDataJson threadMovieData = new FoodDataJson();
            for (String url : urls) {
                try {
                    threadMovieData.downloadFoodDataJson(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return threadMovieData;
        }

        protected void onPostExecute(FoodDataJson threadMovieData) {
            foodData.foodList.clear();
            for (int i = 0; i < threadMovieData.getSize(); i++) {
                foodData.foodList.clear();
            }
            for (int k = 0; k < threadMovieData.getSize(); k++) {
                foodData.foodList.add(threadMovieData.foodList.get(k));
            }
            if (adapterReference != null) {
                final Food_MyRecycleAdapter adapter = adapterReference.get();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
