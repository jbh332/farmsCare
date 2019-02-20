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
import com.example.hong.boaaproject.mainActivity.Register2Activity;

public class SelectDialog extends Dialog {

    Button btnCamera, btnAlbum;

    public SelectDialog(@NonNull Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // 다이얼로그의 타이틀 바 제거
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 다이얼로그 화면 투명화
        setContentView(R.layout.dialog_select); // 커스텀 다이얼로그 xml 과 연결

        btnAlbum = findViewById(R.id.btnAlbum);
        btnCamera = findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Register2Activity) Register2Activity.mContext).captureCamera();
                dismiss(); // 다이얼로그 close
            }
        });
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Register2Activity) Register2Activity.mContext).getAlbum(); // 타 액티비티에서 앨범 함수 호출
                dismiss();
            }
        });
    }
}
