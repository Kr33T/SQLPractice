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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

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

    //Из строки в изображение
    private Bitmap getImgBitmap(String encodedImg) {
        if (encodedImg != null) {
            byte[] bytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                bytes = Base64.getDecoder().decode(encodedImg);
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return BitmapFactory.decodeResource(MainActivity.this.getResources(),
                R.drawable.empty);
    }

    public void makeTableRowImageView(TableRow.LayoutParams params, String res, TableRow tableRow)
    {
        ImageView image = new ImageView(this);
        params.weight = 1.0f;
        image.setImageBitmap(getImgBitmap(res));
        params.width = 100;
        params.height = 150;
        params.gravity = Gravity.CENTER_VERTICAL;
        params.bottomMargin = 50;
        params.topMargin = 50;
        image.setLayoutParams(params);
        tableRow.addView(image);
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
        deleteBtn.setText("upd");
        deleteBtn.setTextSize(10);
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
                query = "select *, convert(varchar(max), goodImage) from storage where goodName like '%" + searchET.getText() + "%' order by " + orderItem + " " + sortBy;
            }
            else
            {
                query = "select *, convert(varchar(max), goodImage) from storage where goodName like '%" + searchET.getText() + "%'";
            }

            if(connection!=null){

                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

                TableRow namingColumns = new TableRow(this);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                makeTableRowTextView(params, "Image", 18, namingColumns);
                makeTableRowTextView(params, "Product", 18, namingColumns);
                makeTableRowTextView(params, "Price", 18, namingColumns);
                makeTableRowTextView(params, "Quantity", 18, namingColumns);
                makeTableRowTextView(params, "", 18, namingColumns);

                dbOutput.addView(namingColumns);

                while(resultSet.next()){
                    TableRow dbOutputRow = new TableRow(this);
                    dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    makeTableRowImageView(params, resultSet.getString(6), dbOutputRow);
                    makeTableRowTextView(params, resultSet.getString(2), 15, dbOutputRow);
                    makeTableRowTextView(params, resultSet.getString(3), 15, dbOutputRow);
                    makeTableRowTextView(params, resultSet.getString(4), 15, dbOutputRow);

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