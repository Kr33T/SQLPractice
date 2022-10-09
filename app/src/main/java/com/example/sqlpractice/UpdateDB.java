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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

import javax.xml.transform.Result;

public class UpdateDB extends AppCompatActivity implements View.OnClickListener {

    Button btnUpdate, btnTransitionToMain2, btnDelete;

    Statement statement;
    ResultSet resultSet;
    Connection connection;

    ImageView imageV;

    EditText etProductUpdate, etPriceUpdate, etQuantityUpdate;

    String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_db);

        getSupportActionBar().hide();

        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        btnTransitionToMain2 = findViewById(R.id.btnTransitionToMain2);
        btnTransitionToMain2.setOnClickListener(this);

        etProductUpdate = findViewById(R.id.etProductUpdate);
        etPriceUpdate = findViewById(R.id.etPriceUpdate);
        etQuantityUpdate = findViewById(R.id.etQuantityUpdate);

        imageV = findViewById(R.id.imageV);
        imageV.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);});

        try{
            DBHelper connectionHelper = new DBHelper();
            connection = connectionHelper.connectionClass();

            String query = "select *, Convert(varchar(max), goodImage) from storage where id_good = " + MainActivity.index;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                etProductUpdate.setText(resultSet.getString(2));
                etPriceUpdate.setText(resultSet.getString(3));
                etQuantityUpdate.setText(resultSet.getString(4));
                imageV.setImageBitmap(getImgBitmap(resultSet.getString(6)));
            }
        }
        catch(Exception ex){
            Log.e("Error", ex.getMessage());
        }
    }

    //Из строки в изображение
    private Bitmap getImgBitmap(String encodedImg) {
        if (encodedImg != null) {
            byte[] bytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                bytes = Base64.getDecoder().decode(encodedImg);
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return BitmapFactory.decodeResource(UpdateDB.this.getResources(),
                R.drawable.pustoe);
    }

    //отдельный метод для открытия
    private final ActivityResultLauncher<Intent> pickImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imageV.setImageBitmap(bitmap);
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
            case R.id.btnUpdate:
                try{
                    String query = "update storage set goodName = '" + etProductUpdate.getText() + "', price = " + etPriceUpdate.getText() + ", quantity = " + etQuantityUpdate.getText() + ", goodImage = convert(varbinary(max), '" + encodedImage + "') where id_good = " + MainActivity.index;
                    statement.executeUpdate(query);

                    Toast.makeText(this, "Запись обновлена", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(this, MainActivity.class));
                }
                catch (Exception ex){
                    Log.e("Error", ex.getMessage());
                }
                break;
            case R.id.btnDelete:
                try{
                    String query = "delete from storage where id_good = " + MainActivity.index;
                    statement.executeUpdate(query);

                    Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(this, MainActivity.class));
                }
                catch (Exception ex){
                    Log.e("Error", ex.getMessage());
                }
                break;
            case R.id.btnTransitionToMain2:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}