package com.example.sqlpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static int index;
    Connection connection;
    String ConnectionResult = "";
    Button btnRefresh, btnTransitionToUpdate, btnClearOrderBy;
    EditText searchET;
    Spinner orderByItemS, sortByS;
    String orderItem, sortBy;
    Statement statement;
    ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);

        btnTransitionToUpdate = findViewById(R.id.btnTransitionToUpdate);
        btnTransitionToUpdate.setOnClickListener(this);

        btnClearOrderBy = findViewById(R.id.clearOrderBy);
        btnClearOrderBy.setOnClickListener(this);

        searchET = findViewById(R.id.searchET);
        searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GetInfoFromSql();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        orderByItemS = findViewById(R.id.orderByItem);
        orderByItemS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(orderByItemS.getSelectedItemPosition())
                {
                    case 0:
                        orderItem = "";
                        break;
                    case 1:
                        orderItem = "goodName";
                        break;
                    case 2:
                        orderItem = "price";
                        break;
                    case 3:
                        orderItem = "quantity";
                        break;
                }
                GetInfoFromSql();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sortByS = findViewById(R.id.descOrAsc);
        sortByS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(sortByS.getSelectedItemPosition())
                {
                    case 0:
                        sortBy = "asc";
                        break;
                    case 1:
                        sortBy = "desc";
                        break;
                }
                GetInfoFromSql();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        GetInfoFromSql();
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

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnRefresh:

                GetInfoFromSql();

                Toast.makeText(this, "Таблица обновлена", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btnTransitionToUpdate:

                startActivity(new Intent(this, InsertDB.class));

                break;
            case R.id.clearOrderBy:
                searchET.setText("");
                orderByItemS.setSelection(0);
                sortByS.setSelection(0);
                break;
            default:

                index = v.getId();

                startActivity(new Intent(this, UpdateDB.class));

                break;
        }
    }

    public void GetInfoFromSql(){

        try{

            DBHelper connectionHelper = new DBHelper();
            connection = connectionHelper.connectionClass();

            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();

            String query = "select * from storage where goodName like '%" + searchET.getText() + "%'";

            if(!orderByItemS.getSelectedItem().toString().isEmpty())
            {
                query = "select * from storage where goodName like '%" + searchET.getText() + "%' order by " + orderItem + " " + sortBy;
            }
            else
            {
                query = "select * from storage where goodName like '%" + searchET.getText() + "%'";
            }

            if(connection!=null){

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