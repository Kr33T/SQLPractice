package com.example.sqlpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Connection connection;
    String ConnectionResult = "";

    Button btnRefresh, btnTransitionToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);

        btnTransitionToUpdate = (Button) findViewById(R.id.btnTransitionToUpdate);
        btnTransitionToUpdate.setOnClickListener(this);

        GetTextFromSql();
    }

    public void makeTableRowTextView(TableRow.LayoutParams params, String text, int size, TableRow tableRow){
        TextView textView = new TextView(this);
        params.weight = 1.0f;
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(size);
        tableRow.addView(textView);
    }

    public void makeTableRowButton(TableRow.LayoutParams params, int id, TableRow tableRow){
        Button deleteBtn = new Button(this);
        deleteBtn.setOnClickListener(this);
        params.weight = 1.0f;
        deleteBtn.setLayoutParams(params);
        deleteBtn.setText("upd/del");
        deleteBtn.setTextSize(12);
        deleteBtn.setId(id);
        tableRow.addView(deleteBtn);
    }

    public static int index;

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnRefresh:

                GetTextFromSql();

                Toast.makeText(this, "Таблица обновлена", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btnTransitionToUpdate:

                startActivity(new Intent(this, InsertDB.class));

                break;
            default:

                index = v.getId();

                startActivity(new Intent(this, UpdateDB.class));

                break;
        }
    }

    Statement statement;
    ResultSet resultSet;

    public void GetTextFromSql(){

        try{

            DBHelper connectionHelper = new DBHelper();
            connection = connectionHelper.connectionClass();

            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();

            if(connection!=null){
                String query = "select * from storage";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

                TableRow namingColumns = new TableRow(this);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                makeTableRowTextView(params, "Product", 22, namingColumns);
                makeTableRowTextView(params, "Price", 22, namingColumns);
                makeTableRowTextView(params, "Quantity", 22, namingColumns);
                makeTableRowTextView(params, "", 22, namingColumns);

                dbOutput.addView(namingColumns);

                while(resultSet.next()){
                    TableRow dbOutputRow = new TableRow(this);
                    dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    makeTableRowTextView(params, resultSet.getString(2), 17, dbOutputRow);
                    makeTableRowTextView(params, resultSet.getString(3), 17, dbOutputRow);
                    makeTableRowTextView(params, resultSet.getString(4), 17, dbOutputRow);

                    makeTableRowButton(params, Integer.parseInt(resultSet.getString(1)), dbOutputRow);

                    dbOutput.addView(dbOutputRow);
                }
            }
            else{
                ConnectionResult = "CheckConnection";
            }
        }
        catch (Exception ex){
            Log.e("Error", ex.getMessage());
        }
    }
}