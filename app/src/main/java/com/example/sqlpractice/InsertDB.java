package com.example.sqlpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;

public class InsertDB extends AppCompatActivity implements View.OnClickListener {

    Button btnTransitionToMain, btnUpdate;

    EditText edProduct, edPrice, edQuantity;

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

                    String query = "insert into storage (goodName, price, quantity) values ('" + edProduct.getText() + "', " + edPrice.getText() + ", " + edQuantity.getText() + ")";

                    edProduct.setText("");
                    edPrice.setText("");
                    edQuantity.setText("");
                    Toast.makeText(this, "Запись добавлена", Toast.LENGTH_SHORT).show();

                    Statement statement = connection.createStatement();
                    statement.executeQuery(query);
                }
                catch (Exception ex){
                    Log.e("Error", ex.getMessage());
                }
                break;
        }
    }
}