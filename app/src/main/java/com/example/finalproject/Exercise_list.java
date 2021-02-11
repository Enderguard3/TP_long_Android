package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;

public class Exercise_list extends AppCompatActivity implements View.OnClickListener {
    FragmentManager fragmentManager;
    int rowIds;
    int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        Bundle extras = getIntent().getExtras();
        this.user = extras.getInt("user");

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnWorkout = findViewById(R.id.btn_workout);

        btnAdd.setOnClickListener(this);
        btnWorkout.setOnClickListener(this);

        ViewGroup table_layout = findViewById(R.id.table_layout);
        this.fragmentManager = getSupportFragmentManager();
        @SuppressLint("Recycle") Cursor exercises = Login.db.rawQuery("SELECT id, user, name, type, reps, weight FROM exercise WHERE user = " + this.user, new String[]{});

        this.rowIds = 0;
        if (exercises.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putLong("id", exercises.getInt(exercises.getColumnIndex("id")));
                bundle.putInt("user", this.user);
                Fragment frag = new frag_exercise();
                frag.setArguments(bundle);

                TableRow row = new TableRow(this);
                row.setId(this.rowIds);
                table_layout.addView(row);

                fragmentManager.beginTransaction()
                        .add(row.getId(), frag).commit();

                this.rowIds++;
            } while (exercises.moveToNext());
        }
        exercises.close();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                this.add();
                break;
            case R.id.btn_workout:
                Intent workout = new Intent(this, Workout.class);
                workout.putExtra("user", this.user);
                startActivity(workout);
                break;
            default:
                break;
        }
    }

    public void add() {
        ContentValues values = new ContentValues();
        values.put("user", this.user);
        values.put("name", "name");
        values.put("type", "All");
        values.put("reps", 0);
        values.put("weight", 0);

        long id = Login.db.insert("exercise", null, values);
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Fragment frag = new frag_exercise();
        frag.setArguments(bundle);

        TableRow row = new TableRow(this);
        row.setId(this.rowIds);
        this.rowIds++;
        ViewGroup table_layout = findViewById(R.id.table_layout);
        table_layout.addView(row);

        this.fragmentManager.beginTransaction()
                .add(row.getId(), frag).commit();
    }
}