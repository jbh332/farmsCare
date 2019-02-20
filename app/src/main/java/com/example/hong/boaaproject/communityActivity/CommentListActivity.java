package com.example.hong.boaaproject.communityActivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityCommentListBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommentListActivity extends AppCompatActivity {

    ActivityCommentListBinding binding;

    RecyclerView.LayoutManager manager;
    List<CommentModel> commentModels;
    CommentRecyclerAdapter commentRecyclerAdapter;

    String boardNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment_list);

        Toolbar toolbar = findViewById(R.id.tBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        binding.recycleView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycleView.setLayoutManager(manager);

        commentModels = new ArrayList<>();
        commentRecyclerAdapter = new CommentRecyclerAdapter(this, commentModels);

        Intent intent = getIntent();
        boardNum = intent.getStringExtra("boardNum");

        new BackgroundTask().execute();

    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target, commentDate, boardComment, userID;


        @Override
        protected void onPreExecute() {
            target = "http://jbh9730.cafe24.com/CommentList.php?boardNum=" + boardNum;
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
                    userID = object.getString("userID");
                    boardComment = object.getString("boardComment");
                    commentDate = object.getString("commentDate");

                    CommentModel commentModel = new CommentModel(userID, boardComment, commentDate);
                    commentModels.add(commentModel);
                    count++;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.recycleView.setAdapter(commentRecyclerAdapter);
        }
    }
}
