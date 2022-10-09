package com.example.sqlpractice;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Base64;

public class InsertDB extends AppCompatActivity implements View.OnClickListener {

    Button btnTransitionToMain, btnUpdate;

    EditText edProduct, edPrice, edQuantity;

    ImageView imageV2;

    String encodedImage;

    Connection connection;
    String ConnectionResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_db);

        getSupportActionBar().hide();

        btnUpdate = findViewById(R.id.btnInsert);
        btnUpdate.setOnClickListener(this);

        btnTransitionToMain = findViewById(R.id.btnTransitionToMain);
        btnTransitionToMain.setOnClickListener(this);

        edProduct = findViewById(R.id.etProduct);
        edPrice = findViewById(R.id.etPrice);
        edQuantity = findViewById(R.id.etQuantity);

        imageV2 = findViewById(R.id.imageV2);
        imageV2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent); });
    }

    //отдельный метод для открытия
    private final ActivityResultLauncher<Intent> pickImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imageV2.setImageBitmap(bitmap);
                    encodedImage = encodeImage(bitmap);
                } catch (Exception e) {

                }
            }
        }
    });

    //Изображение в строку
    private String encodeImage(Bitmap bitmap) {
        int prevW = 150;
        int prevH = bitmap.getHeight() * prevW / bitmap.getWidth();
        Bitmap b = Bitmap.createScaledBitmap(bitmap, prevW, prevH, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        return "";
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnTransitionToMain:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnInsert:
                try{
                    DBHelper connectionHelper = new DBHelper();
                    connection = connectionHelper.connectionClass();

                    String query = "insert into storage (goodName, price, quantity, goodImage) values ('" + edProduct.getText() + "', " + edPrice.getText() + ", " + edQuantity.getText() + ", Convert(varbinary(max), '" + encodedImage + "'))";

                    Statement statement = connection.createStatement();
                    statement.execute(query);

                    startActivity(new Intent(this, MainActivity.class));
                }
                catch (Exception ex){
                    Log.e("Error", ex.getMessage());
                }
                break;
        }
    }
}