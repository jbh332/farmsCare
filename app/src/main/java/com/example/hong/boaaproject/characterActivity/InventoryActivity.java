package com.example.hong.boaaproject.characterActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityInventoryBinding;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.example.hong.boaaproject.mainActivity.water.WaterGoalChangeRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    List<ItemModel> itemModels;
    RecyclerView.LayoutManager manager;
    ItemRecyclerAdapter itemRecyclerAdapter;

    String userID, itemCode, itemURL;

    String userItemURL, hatURL, clothesURL, shoesURL;
    ImageView ivWear, ivWear2, ivWear3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        ivWear = findViewById(R.id.ivWear);
        ivWear2 = findViewById(R.id.ivWear2);
        ivWear3 = findViewById(R.id.ivWear3);


        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(this);
        userID = keepLoginActivity.getUserID();

        Toolbar toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new BackgroundTask().execute();
        new BackgroundTask2().execute();


        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        itemModels = new ArrayList<>();
        itemRecyclerAdapter = new ItemRecyclerAdapter(this, itemModels);

    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            target = "http://jbh9730.cafe24.com/UserItems.php?userID=" + userID;
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

            int count = 0;

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                while (count < jsonArray.length()) {

                    JSONObject object = jsonArray.getJSONObject(count);
                    itemCode = object.getString("itemCode");
                    itemURL = object.getString("itemURL");

                    ItemModel itemModel = new ItemModel(itemURL, itemCode);
                    itemModels.add(itemModel);
                    count++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            recyclerView.setAdapter(itemRecyclerAdapter);
        }
    }


    class BackgroundTask2 extends AsyncTask<Void, Void, String> {

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

                Picasso.with(getApplicationContext())
                        .load(hatURL)
                        .into(ivWear2);

                Picasso.with(getApplicationContext())
                        .load(clothesURL)
                        .into(ivWear);

                Picasso.with(getApplicationContext())
                        .load(shoesURL)
                        .into(ivWear3);


            } catch (Exception e) {
                e.printStackTrace();
            }
            //recyclerView.setAdapter(itemRecyclerAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public class ItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        List<ItemModel> itemModels;
        LayoutInflater layoutInflater;

        public ItemRecyclerAdapter(Context context, List<ItemModel> itemModels) {
            this.context = context;
            this.itemModels = itemModels;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);

            return new CustomViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            Picasso.with(context)
                    .load(itemModels.get(position).itemURL)
                    .into(((CustomViewHolder) holder).ivItem);


            //Log.d("JBH : ", itemModels.get(position).itemURL.substring(32, 33));
            //TODO 최적화 필요

            if (itemModels.get(position).itemURL.substring(32, 33).equals("0")) {

                ((CustomViewHolder) holder).tvItem.setText("청멜빵 바지");

            } else if (itemModels.get(position).itemURL.substring(32, 33).equals("1")) {

                ((CustomViewHolder) holder).tvItem.setText("작업복");

            } else if (itemModels.get(position).itemURL.substring(32, 33).equals("2")) {

                ((CustomViewHolder) holder).tvItem.setText("새마을 모자");

            } else if (itemModels.get(position).itemURL.substring(32, 33).equals("3")) {

                ((CustomViewHolder) holder).tvItem.setText("밀짚 모자");

            } else if (itemModels.get(position).itemURL.substring(32, 33).equals("4")) {

                ((CustomViewHolder) holder).tvItem.setText("네이비색 장화");

            } else if (itemModels.get(position).itemURL.substring(32, 33).equals("5")) {

                ((CustomViewHolder) holder).tvItem.setText("검정 단화");

            }

        }


        @Override
        public int getItemCount() {
            return itemModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            ImageView ivItem, ivItemWear;
            TextView tvItem;


            public CustomViewHolder(View view) {
                super(view);

                ivItem = view.findViewById(R.id.ivItem);
                tvItem = view.findViewById(R.id.tvItem);


                ivItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {

                            ItemModel index = itemModels.get(pos);

                            userItemURL = index.itemURL;
                            //Log.d("JBH TEST : ", userItemURL);


                            //TODO 최적화 필요
                            if (userItemURL.substring(32, 33).equals("0") || userItemURL.substring(32, 33).equals("1")) {

                                Picasso.with(context)
                                        .load(userItemURL)
                                        .into(ivWear);

                                clothesURL = userItemURL;
                                clothesRegisterComplete();
                            }
                            if (userItemURL.substring(32, 33).equals("2") || userItemURL.substring(32, 33).equals("3")) {

                                Picasso.with(context)
                                        .load(userItemURL)
                                        .into(ivWear2);

                                hatURL = userItemURL;
                                hatRegisterComplete();

                            }
                            if (userItemURL.substring(32, 33).equals("4") || userItemURL.substring(32, 33).equals("5")) {

                                Picasso.with(context)
                                        .load(userItemURL)
                                        .into(ivWear3);

                                shoesURL = userItemURL;
                                shoesRegisterComplete();
                            }
                        }

                    }
                });

            }


            private void hatRegisterComplete() {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {

                                // Log.d("JBH CHECK : ", " COMPLETE");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                HatRegisterRequest hatRegisterRequest = new HatRegisterRequest(userID, hatURL, responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(hatRegisterRequest);
            }

            private void clothesRegisterComplete() {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {

                                //    Log.d("JBH CHECK : ", " COMPLETE");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                ClothesRegisterRequest clothesRegisterRequest = new ClothesRegisterRequest(userID, clothesURL, responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(clothesRegisterRequest);
            }

            private void shoesRegisterComplete() {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {

                                //     Log.d("JBH CHECK : ", " COMPLETE");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                ShoesRegisterRequest shoesRegisterRequest = new ShoesRegisterRequest(userID, shoesURL, responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(shoesRegisterRequest);
            }
        }
    }
}

