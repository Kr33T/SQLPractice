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
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.transform.Result;

public class UpdateDB extends AppCompatActivity implements View.OnClickListener {

    Button btnUpdate, btnTransitionToMain2, btnDelete;

    Statement statement;
    ResultSet resultSet;
    Connection connection;

    EditText etProductUpdate, etPriceUpdate, etQuantityUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_db);

        getSupportActionBar().hide();

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        btnTransitionToMain2 = (Button) findViewById(R.id.btnTransitionToMain2);
        btnTransitionToMain2.setOnClickListener(this);

        etProductUpdate = findViewById(R.id.etProductUpdate);
        etPriceUpdate = findViewById(R.id.etPriceUpdate);
        etQuantityUpdate = findViewById(R.id.etQuantityUpdate);

        try{
            DBHelper connectionHelper = new DBHelper();
            connection = connectionHelper.connectionClass();

            String query = "select * from storage where id_good = " + MainActivity.index;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                etProductUpdate.setText(resultSet.getString(2));
                etPriceUpdate.setText(resultSet.getString(3));
                etQuantityUpdate.setText(resultSet.getString(4));
            }
        }
        catch(Exception ex){
            Log.e("Error", ex.getMessage());
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnUpdate:
                try{
                    String query = "update storage set goodName = '" + etProductUpdate.getText() + "', price = " + etPriceUpdate.getText() + ", quantity = " + etQuantityUpdate.getText() + " where id_good = " + MainActivity.index;
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