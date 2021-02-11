package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Exercise_edit extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    Type type;
    long id;
    int user;
    EditText txtName;
    EditText txtReps;
    EditText txtWeight;
    Spinner spinType;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_edit);

        this.spinType = findViewById(R.id.spin_type);
        this.spinType.setOnItemSelectedListener(this);

        List<Type> categories = new ArrayList<>();
        categories.add(Type.All);
        categories.add(Type.Arms);
        categories.add(Type.Back);
        categories.add(Type.Chest);
        categories.add(Type.Legs);

        ArrayAdapter<Type> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinType.setAdapter(dataAdapter);

        Button btnCancel = findViewById(R.id.btn_cancel_edit);
        Button btnValidate = findViewById(R.id.btn_validate_edit);
        Button btnDelete = findViewById(R.id.btn_delete);

        btnCancel.setOnClickListener(this);
        btnValidate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        this.txtName = findViewById(R.id.txt_exercise_name);
        this.txtReps = findViewById(R.id.txt_exercise_reps);
        this.txtWeight = findViewById(R.id.txt_exercise_weight);

        Bundle bundle = getIntent().getExtras();
        this.id = bundle.getLong("id");
        this.user = bundle.getInt("user");
        @SuppressLint("Recycle") Cursor exercise = Login.db.rawQuery(
                "SELECT id, name, type, reps, weight " +
                        "FROM exercise " +
                        "WHERE id = " + id
                , new String[]{});
        exercise.moveToFirst();
        String name = exercise.getString(exercise.getColumnIndex("name"));
        String type = exercise.getString(exercise.getColumnIndex("type"));
        int reps = exercise.getInt(exercise.getColumnIndex("reps"));
        int weight = exercise.getInt(exercise.getColumnIndex("weight"));
        this.txtName.setText(name);
        this.txtReps.setText("" + reps);
        this.txtWeight.setText("" + weight);
        this.spinType.setSelection(dataAdapter.getPosition(Type.valueOf(type)));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_validate_edit:
                Login.db.execSQL("UPDATE exercise SET name = '" + this.txtName.getText().toString()
                        + "', reps = " + this.txtReps.getText().toString()
                        + ", weight = " + this.txtWeight.getText().toString()
                        + ", type = '" + this.type.toString() +
                        "' WHERE id = " + this.id, new String[]{});
                break;
            case R.id.btn_delete:
                Login.db.execSQL("DELETE FROM exercise WHERE id = " + this.id, new String[]{});
                Login.db.execSQL("DELETE FROM workout WHERE exercise = " + this.id + " AND user = " + this.user, new String[]{});
                break;
            default:
                break;
        }
        Intent exercise_list = new Intent(this, Exercise_list.class);
        exercise_list.putExtra("user" , this.user);
        startActivity(exercise_list);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.type = (Type) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        this.type = Type.All;
    }
}