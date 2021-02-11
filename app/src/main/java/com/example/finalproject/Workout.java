package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

public class Workout extends AppCompatActivity implements View.OnClickListener {
    int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Bundle extras = getIntent().getExtras();
        this.user = extras.getInt("user");

        /*
        Je n'utilise pas https://api.quotable.io/random car l'api ne marche pas
        J'utilise les phrases du TP2 a la place
         */
        Ion.with(this)
                .load("http://api.icndb.com/jokes/random")
                .asJsonObject()
                .setCallback((e, result) -> {
                    TextView title = findViewById(R.id.lbl_title_workout);
                    String text = result.get("value").getAsJsonObject().get("joke").getAsString();
                    title.setText(text);
                });

        Button btnExercises = findViewById(R.id.btn_exercises);
        Button btnEmpty = findViewById(R.id.btn_empty);

        btnExercises.setOnClickListener(this);
        btnEmpty.setOnClickListener(this);

        ViewGroup table_layout = findViewById(R.id.table_layout_workout);
        FragmentManager fragmentManager = getSupportFragmentManager();
        @SuppressLint("Recycle") Cursor exercises = Login.db.rawQuery(
                "SELECT id, exercise " +
                        "FROM workout" +
                        " WHERE user = " + this.user
                , new String[]{});

        if (exercises.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putLong("id", exercises.getInt(exercises.getColumnIndex("exercise")));
                Fragment frag = new frag_exercise_workout();
                frag.setArguments(bundle);

                TableRow row = new TableRow(this);
                row.setId(exercises.getInt(exercises.getColumnIndex("id")));
                table_layout.addView(row);

                fragmentManager.beginTransaction()
                        .add(row.getId(), frag).commit();

            } while (exercises.moveToNext());
        }
        exercises.close();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() ==  R.id.btn_empty) {
            Login.db.execSQL("DELETE FROM workout", new String[]{});
        }

        Intent exercises_list = new Intent(this, Exercise_list.class);
        exercises_list.putExtra("user", this.user);
        startActivity(exercises_list);
    }
}