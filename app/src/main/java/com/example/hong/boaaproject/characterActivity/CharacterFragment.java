package com.example.hong.boaaproject.characterActivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.FragmentCharacterBinding;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CharacterFragment extends Fragment {

    FragmentCharacterBinding binding;
    //ImageView h1;
    //List<ItemModel> itemModels;
    String userID, itemCode, itemURL;

    String hatURL, clothesURL, shoesURL;

    /*
        RecyclerView recyclerView;
        RecyclerView.LayoutManager manager;
        //ItemRecyclerAdapter itemRecyclerAdapter;

        String userItemURL;
        String FLAG = "0";
    */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_character, container, false);

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getActivity());
        userID = keepLoginActivity.getUserID();

        new BackgroundTask().execute();

        binding.LL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), InventoryActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        new BackgroundTask().execute();
        Log.d("JBH TEST : ", "onResume:CHARATER FRAGMENT ");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("JBH TEST : ", "onViewCreated:CHARATER FRAGMENT ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("JBH TEST : ", "onPause: CHARACTER FRAGMENT");
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            target = "http://jbh9730.cafe24.com/GetCharacter.php?userID=" + userID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {

                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                JSONObject object = jsonArray.getJSONObject(0);
                hatURL = object.getString("hat");
                clothesURL = object.getString("clothes");
                shoesURL = object.getString("shoes");

                Picasso.with(getContext())
                        .load(hatURL)
                        .into(binding.ivWear);

                Picasso.with(getContext())
                        .load(clothesURL)
                        .into(binding.ivWear2);

                Picasso.with(getContext())
                        .load(shoesURL)
                        .into(binding.ivWear3);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
