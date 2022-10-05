package com.example.sqlpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

        btnUpdate = (Button) findViewById(R.id.btnInsert);
        btnUpdate.setOnClickListener(this);

        btnTransitionToMain = (Button) findViewById(R.id.btnTransitionToMain);
        btnTransitionToMain.setOnClickListener(this);

        edProduct = (EditText) findViewById(R.id.etProduct);
        edPrice = (EditText) findViewById(R.id.etPrice);
        edQuantity = (EditText) findViewById(R.id.etQuantity);
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
                    Statement statement = connection.createStatement();
                    statement.executeQuery(query);

                    Toast.makeText(this, "Запись добавлена", Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex){

                }
                break;
        }
    }
}