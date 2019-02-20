package com.example.hong.boaaproject.mainActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.mainActivity.water.WaterItemRequest;

import org.json.JSONObject;

import java.util.Random;

public class GetBoxDialog extends Dialog {

    LottieAnimationView lottieAnimationView;
    ImageView ivItem;
    Button btnGetItem, btnGetBox, btnDismiss;

    String itemURL, userID;
    Random random = new Random();
    int numb = random.nextInt(6);
    int[] items = {R.drawable.item_cloth_farmer, R.drawable.item_cloth_farmer2,
            R.drawable.item_hat_saemaeul, R.drawable.item_hat_straw,
            R.drawable.item_shoe_farmer, R.drawable.item_shoe_farmer2};
    int rst = items[numb];

    public GetBoxDialog(final Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_get_box);

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(context);
        userID = keepLoginActivity.getUserID();

        lottieAnimationView = findViewById(R.id.lottie);
        ivItem = findViewById(R.id.ivItem);
        btnGetItem = findViewById(R.id.btnGetItem);
        btnGetBox = findViewById(R.id.btnGetBox);
        btnDismiss = findViewById(R.id.btnDismiss);

        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);

        btnGetBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 열지 않은 상자 개수
                Toast.makeText(context, "열지 않은 상자는 인벤토리에 저장됩니다.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        btnGetItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivItem.setImageResource(rst);

                btnGetBox.setEnabled(false);
                lottieAnimationView.setVisibility(View.GONE);
                btnDismiss.setVisibility(View.VISIBLE);
                btnGetItem.setVisibility(View.INVISIBLE);
                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });

                itemURL = "http://jbh9730.cafe24.com/items/" + numb + ".jpg";
                uploadItems();
            }
        });

    }
    private void uploadItems() {

        String itemCode = String.valueOf((int) (Math.random() * 50000));

        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        //Log.d("JBH : ", " 아이템 업로드 완료");
                    } else {
                        //Log.d("JBH : ", " 아이템 업로드 실패");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        WaterItemRequest waterItemRequest = new WaterItemRequest(itemCode, itemURL, userID, responseListener2);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(waterItemRequest);
    }
}
