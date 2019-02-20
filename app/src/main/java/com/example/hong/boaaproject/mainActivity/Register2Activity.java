package com.example.hong.boaaproject.mainActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityRegister2Binding;
import com.example.hong.boaaproject.menu.SelectDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register2Activity extends AppCompatActivity {

    private ActivityRegister2Binding binding;
    private ArrayAdapter<String> adapter;
    private String userID, userHeight, userWeight, userGender, userBirth, userImgURL, currentPhotoPath, imageFileName, uploadFilePath, uploadServerUri;
    private String userGoalWater, userGoalSleep, userGoalSteps, userGoalCalorie;
    private String uploadFileName = null;

    private static final int MY_PERMISSION_CAMERA = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;
    private int serverResponseCode = 0;

    private Uri imageUri;
    private Uri photoURI, albumURI;


    public static Context mContext; // 다른 액티비티에서 현재 액티비티의 함수 호출을 위한 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register2);
        mContext = this;
        uploadServerUri = "http://jbh9730.cafe24.com/UploadToServer.php";

        //유저 아이디 획득
        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(this);
        userID = keepLoginActivity.getUserID();

        // 성별 선택
        genderSelect();

        //서버 정보 호출
        new BackgroundTask().execute();

        //스피너
        final String[] year = getResources().getStringArray(R.array.Year);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, year);
        binding.spnYear.setAdapter(adapter); //TODO 스피너 좀 더 이쁘게?

        // 추가 정보 등록 버튼
        binding.btnRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 추가정보 등록 완료 ( 서버 연동 )
                register2Complete();

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                //Log.d("HONG","UPLOADING");
                            }
                        });

                        uploadFile(uploadFilePath + "" + uploadFileName);

                    }
                }).start();
            }
        });

        // 다이얼로그 호출 > 앨범 OR 카메라 함수 호출
        binding.civUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDialog();
                checkPermission();
            }
        });
    }

    // 파일 업로드 클래스
    public int uploadFile(String sourceFileUri) {

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

            //Log.d("HONG", "UPLOAD FILE" + "SOURCE FILE NOT EXIST : " + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.d("HONG", "UPLOAD FILE" + "SOURCE FILE NOT EXIST : " + uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(uploadServerUri);

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

                //Log.i("HONG", "UPLOADfILE" + "HTTP RESPONSE IS : " + serverResopnseMessage + ":" + serverResponseCode);

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
                //Log.e("HONG", "UPLOAD FILE TO SERVER" + "ERROR");
            } catch (Exception e) {

                e.printStackTrace();
            }

            return serverResponseCode;

        }

    }

    // 다이얼로그 생성 함수
    private void getDialog() {

        SelectDialog selectDialog = new SelectDialog(this); // 해당 다이얼로그의 자바파일
        selectDialog.show();
    }

    // 앨범이냐, 카메라냐, 크롭이냐 선택 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 사진 촬영
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        //Log.d("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();

                        binding.civUserPicture.setImageURI(imageUri);


                    } catch (Exception e) {
                        Log.d("REQUEST_TAKE_PHOTO", e.toString());
                    }


                } else {
                    Toast.makeText(Register2Activity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            // 갤러리 호출
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
                            Log.d("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            // 크롭 호출
            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {

                    galleryAddPic();
                    binding.civUserPicture.setImageURI(albumURI);
                }
                break;
        }
    }


    //카메라 호출 함수
    public void captureCamera() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //카메라 호출

            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (Exception e) {
                    //Log.d("captureCamera ERROR : ", e.toString());
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

    // 이미지 파일 만드는 메소드
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + ".jpg"; // 이미지 이름 설정
        File imageFile;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "BOAA"); // 저장 경로

        uploadFilePath = storageDir.getAbsolutePath() + "/";
        uploadFileName = imageFileName;


        if (!storageDir.exists()) { // 존재하지 않다면 디렉토리 생성
            //Log.d("currentPhotoPath : ", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName); // 파일위치, 파일명
        currentPhotoPath = imageFile.getAbsolutePath(); // 파일의 절대 경로 ?

        return imageFile;

    }

    // 갤러리 호출 함수
    public void getAlbum() {

        //Log.d("getAlbum", "CALL");
        Intent intent = new Intent(Intent.ACTION_PICK); // 갤러리 실행
        intent.setType("image/*"); // image 타입의 파일들 모두 출력
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);

    }

    // 앨범에 사진 저장
    private void galleryAddPic() {
        //Log.d("galleryAddPic : ", "CALL");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        intent.setData(contentUri);
        sendBroadcast(intent); // 폰의 앨범에 크롭된 사진을 갱신하는 함수, 안쓰면 크롭된 사진을 저장해도 보이지 않는다.
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // 이미지 자르기 호출
    public void cropImage() {
        //Log.d("cropImage", "CALL");
        //Log.d("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent intent = new Intent("com.android.camera.action.CROP"); // 크롭 화면 호출

        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(photoURI, "image/*"); // photoURI에 저장
        intent.putExtra("outputX", 200);
        intent.putExtra("outputy", 200);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectX", 1);
        intent.putExtra("scale", true);
        intent.putExtra("output", albumURI);
        startActivityForResult(intent, REQUEST_IMAGE_CROP); // 크롭 case 문으로 이동
    }

    //권한 메소드
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
                        Toast.makeText(Register2Activity.this, "해당 권한을 활성화하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    // 유저 정보 등록
    private void register2Complete() {

        userHeight = binding.etHeight.getText().toString();
        userWeight = binding.etWeight.getText().toString();
        userBirth = binding.spnYear.getSelectedItem().toString();

        //유저 사진 등록하지 않았을 때 null 입력 방지
        if (uploadFileName == null || uploadFileName.equals("") == true) {

            //Log.d("JBH : ", " 업로드 널일때");
        } else {
            userImgURL = "http://jbh9730.cafe24.com/uploads/" + uploadFileName;
        }

        // 유저의 입력 정보에 대한 섭취량 산출
        userDefault();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                        Toast.makeText(Register2Activity.this, "추가정보 등록에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register2Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Register2Request register2Request = new Register2Request(userID, userHeight, userWeight, userGender, userBirth, userImgURL, userGoalWater, userGoalSleep, userGoalSteps, userGoalCalorie, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Register2Activity.this);
        queue.add(register2Request);
    }

    private void userDefault() {

        if (userGoalCalorie.equals("0") || userGoalSleep.equals("0") || userGoalSteps.equals("0") || userGoalWater.equals("0")) {

            userGoalWater = "2000";
            userGoalSteps = "4000";

            //수면량
            if (Integer.parseInt(binding.spnYear.getSelectedItem().toString()) >= 14 && Integer.parseInt(binding.spnYear.getSelectedItem().toString()) <= 17) {
                userGoalSleep = "9";
            } else if (Integer.parseInt(binding.spnYear.getSelectedItem().toString()) >= 18 && Integer.parseInt(binding.spnYear.getSelectedItem().toString()) <= 25) {
                userGoalSleep = "8";
            } else if (Integer.parseInt(binding.spnYear.getSelectedItem().toString()) >= 26) {
                userGoalSleep = "7";
            } else {
                userGoalSleep = "10";
            }

            userGoalCalorie = "2200"; //TODO 칼로리계산식 수정
        } else {

            return;

        }
    }

    //성별 선택
    private void genderSelect() {

        binding.tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.tvFemale.setBackgroundColor(getResources().getColor(R.color.mainColor));
                binding.tvFemale.setTextColor(Color.WHITE);
                binding.tvMale.setBackgroundColor(Color.WHITE);
                binding.tvMale.setTextColor(Color.BLACK);
                userGender = binding.tvFemale.getText().toString();

            }
        });

        binding.tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.tvMale.setBackgroundColor(getResources().getColor(R.color.mainColor));
                binding.tvMale.setTextColor(Color.WHITE);
                binding.tvFemale.setBackgroundColor(Color.WHITE);
                binding.tvFemale.setTextColor(Color.BLACK);
                userGender = binding.tvMale.getText().toString();

            }
        });
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

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
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                JSONObject object = jsonArray.getJSONObject(0);
                userImgURL = object.getString("userImgURL");
                userGoalCalorie = object.getString("userGoalCalorie");
                userGoalSleep = object.getString("userGoalSleep");
                userGoalSteps = object.getString("userGoalSteps");
                userGoalWater = object.getString("userGoalWater");

                Picasso.with(Register2Activity.this)
                        .load(userImgURL)
                        .into(binding.civUserPicture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
