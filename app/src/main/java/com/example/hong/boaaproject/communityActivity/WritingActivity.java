package com.example.hong.boaaproject.communityActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityWritingBinding;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.example.hong.boaaproject.mainActivity.MainActivity;
import com.example.hong.boaaproject.menu.SelectDialog2;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WritingActivity extends AppCompatActivity {

    private ActivityWritingBinding a;
    public static Context mContext2;


    private String currentPhotoPath, uploadFileName, uploadFilePath, uploadServerUrl;
    private String userID, boardContent, boardImgURL, boardNum, boardDate;

    private static final int MY_PERMISSION_CAMERA = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;
    private int serverResponseCode = 0;

    private Uri imageUri;
    private Uri photoURI, albumURI;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = DataBindingUtil.setContentView(this, R.layout.activity_writing);
        mContext2 = this;
        uploadServerUrl = "http://jbh9730.cafe24.com/UploadToServer2.php";

        final SelectDialog2 selectDialog2 = new SelectDialog2(this);

        // 사진 등록 클릭
        a.LLPicRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectDialog2.show();
                checkPermission();


            }
        });

        //게시글 등록 버튼 클릭
        a.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //게시글 정보 서버에 입력
                boardRegisterComplete();

                //게시글 이미지 서버에 등록
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        uploadFile(uploadFilePath + "" + uploadFileName);

                    }

                }).start();

                Toast.makeText(WritingActivity.this, "게시글 등록이 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void boardRegisterComplete() {

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(this);

        userID = keepLoginActivity.getUserID();
        boardContent = a.etBoardContent.getText().toString();
        boardImgURL = "http://jbh9730.cafe24.com/boardFiles/" + uploadFileName;
        boardNum = String.valueOf((int) (Math.random() * 50000));

        // 현재 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA);

        boardDate = sdf.format(date);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        Toast.makeText(WritingActivity.this, "게시글 등록이 되었습니다.", Toast.LENGTH_SHORT).cancel();
                        Intent intent = new Intent(WritingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        BoardRegisterRequest boardRegisterRequest = new BoardRegisterRequest(userID, boardContent, boardImgURL, boardNum, boardDate, responseListener);
        RequestQueue queue = Volley.newRequestQueue(WritingActivity.this);
        queue.add(boardRegisterRequest);

    }

    private int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn;
        DataOutputStream dos;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            //Log.d("JBH", "UPLOAD FILE" + "SOURCE FILE NOT EXIST : " + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.d("JBH", "UPLOAD FILE" + "SOURCE FILE NOT EXIST : " + uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(uploadServerUrl);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = conn.getResponseCode();

                //Log.i("JBH", "UPLOADfILE" + "HTTP RESPONSE IS : " + serverResopnseMessage + ":" + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }

                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                //Log.e("JBH", "UPLOAD FILE TO SERVER" + "ERROR");
            } catch (Exception e) {

                e.printStackTrace();
            }

            return serverResponseCode;

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        // Log.d("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();

                        a.ivPicture.setImageURI(imageUri);
                    } catch (Exception e) {
                        // Log.d("REQUEST_TAKE_PHOTO", e.toString());
                    }


                } else {
                    Toast.makeText(WritingActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    if (data.getData() != null) {
                        try {
                            File albumFile;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (Exception e) {
                            //  Log.d("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {

                    galleryAddPic();
                    a.ivPicture.setImageURI(albumURI);
                }
                break;
        }
    }

    public void captureCamera() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (Exception e) {
                    //   Log.d("captureCamera ERROR : ", e.toString());
                }

                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "BOAA");

        uploadFilePath = storageDir.getAbsolutePath() + "/";
        uploadFileName = imageFileName;

        if (!storageDir.exists()) {
            //  Log.d("currentPhotoPath : ", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        currentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;

    }

    public void getAlbum() {

        // Log.d("getAlbum", "CALL");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);

    }

    private void galleryAddPic() {
        // Log.d("galleryAddPic : ", "CALL");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        intent.setData(contentUri);
        sendBroadcast(intent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void cropImage() {
        //  Log.d("cropImage", "CALL");
        //  Log.d("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(photoURI, "image/*");
        intent.putExtra("outputX", 200);
        intent.putExtra("outputy", 200);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectX", 1);
        intent.putExtra("scale", true);
        intent.putExtra("output", albumURI);
        startActivityForResult(intent, REQUEST_IMAGE_CROP);
    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package : " + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] < 0) {
                        Toast.makeText(WritingActivity.this, "해당 권한을 활성화하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }
}
