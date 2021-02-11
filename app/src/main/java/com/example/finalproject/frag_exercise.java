package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.finalproject.databinding.FragmentFragExerciseBinding;

public class frag_exercise extends Fragment implements View.OnClickListener {
    Exercise exercise;
    boolean workout;
    Button btnAdd;
    int user;

    public Exercise getExercise() { return exercise; }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long id = getArguments().getLong("id");
        this.user = getArguments().getInt("user");

        @SuppressLint("Recycle") Cursor exercise = Login.db.rawQuery(
                "SELECT id, name, type, reps, weight " +
                        "FROM exercise " +
                        "WHERE id = " + id
                , new String[]{});

        exercise.moveToFirst();
        this.exercise = new Exercise(id,
                exercise.getString(exercise.getColumnIndex("name")),
                Type.valueOf(exercise.getString(exercise.getColumnIndex("type"))),
                exercise.getInt(exercise.getColumnIndex("reps")),
                exercise.getInt(exercise.getColumnIndex("weight")));
        exercise.close();

        FragmentFragExerciseBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_frag_exercise, container, false);
        binding.setExercise(this.exercise);

        this.btnAdd = binding.getRoot().findViewById(R.id.btn_add_exercise);
        this.btnAdd.setOnClickListener(this);

        Button btnEdit = binding.getRoot().findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(this);

        @SuppressLint("Recycle") Cursor existInWorkout = Login.db.rawQuery(
                "SELECT * " +
                        "FROM workout " +
                        "WHERE exercise = " + id + " " +
                        "AND user = " + this.user
                , new String[]{});
        if (existInWorkout.getCount() > 0) {
            this.workout = true;
            this.btnAdd.setBackgroundColor(Color.GREEN);
        }
        else {
            this.workout = false;
            this.btnAdd.setBackgroundColor(Color.RED);
        }
        existInWorkout.close();

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_exercise:
                this.state_workout();
                break;
            case R.id.btn_edit:
                this.edit();
                break;
            default:
                break;
        }
    }

    public void state_workout() {
        if (this.workout) {
            Login.db.execSQL("DELETE FROM workout WHERE exercise = " + this.exercise.id + " AND user = " + this.user, new String[]{});
            this.btnAdd.setBackgroundColor(Color.RED);
        }
        else {
            Login.db.execSQL("INSERT INTO workout (user, exercise) VALUES (" + this.user + ", " + this.exercise.id + ")", new String[]{});
            this.btnAdd.setBackgroundColor(Color.GREEN);
        }

        this.workout = !this.workout;
    }

    public void edit() {
        Intent exercise_edit = new Intent(getActivity(), Exercise_edit.class);
        exercise_edit.putExtra("id" , this.exercise.id);
        exercise_edit.putExtra("user" , this.user);
        startActivity(exercise_edit);
    }
}