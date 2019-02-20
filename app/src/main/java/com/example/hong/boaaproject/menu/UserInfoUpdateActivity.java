package com.example.hong.boaaproject.menu;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityUserInfoUpdateBinding;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.example.hong.boaaproject.mainActivity.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserInfoUpdateActivity extends AppCompatActivity {

    ActivityUserInfoUpdateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info_update);


        binding.tBar.setTitle("");
        setSupportActionBar(binding.tBar);

        new BackgroundTask().execute();


        binding.btnNicNameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getNicNameChangeDialog();
            }
        });

        binding.btnPWChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getPWChangeDialog();
            }
        });

        binding.btnRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserInfoUpdateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void getNicNameChangeDialog() {

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        NicNameChangeDialog nicNameChangeDialog = new NicNameChangeDialog(this);
        WindowManager.LayoutParams wm = nicNameChangeDialog.getWindow().getAttributes();
        wm.copyFrom(nicNameChangeDialog.getWindow().getAttributes());
        wm.width = width / 1;
        wm.height = height / 3;

        nicNameChangeDialog.show();
    }

    private void getPWChangeDialog() {

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        PWChangeDialog pwChangeDialog = new PWChangeDialog(this);
        WindowManager.LayoutParams wm = pwChangeDialog.getWindow().getAttributes();
        wm.copyFrom(pwChangeDialog.getWindow().getAttributes());
        wm.width = width / 1;
        wm.height = height / 3;
        pwChangeDialog.show();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getApplicationContext());
        String userID = keepLoginActivity.getUserID();
        String target;

        @Override
        protected void onPreExecute() {
            target = "http://jbh9730.cafe24.com/UserInformation2.php?userID=" + userID;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream
                ));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {

                    stringBuilder.append(temp);
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

            String userNicName, userImgURL;

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                JSONObject object = jsonArray.getJSONObject(0);
                userNicName = object.getString("userNicName");
                userImgURL = object.getString("userImgURL");

                binding.tvNicName.setText(userNicName);
                binding.tvID.setText(userID);

                Picasso.with(getApplicationContext())
                        .load(userImgURL)
                        .into(binding.civUserPicture);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


}
