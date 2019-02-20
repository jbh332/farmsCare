package com.example.hong.boaaproject.menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.communityActivity.WritingActivity;

public class SelectDialog2 extends Dialog {

    Button btnCamera, btnAlbum;

    public SelectDialog2(@NonNull Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_select);

        btnAlbum = findViewById(R.id.btnAlbum);
        btnCamera = findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((WritingActivity) WritingActivity.mContext2).captureCamera();
                dismiss();
            }
        });
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((WritingActivity) WritingActivity.mContext2).getAlbum();
                dismiss();
            }
        });
    }
}
