package com.example.finalproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalproject.databinding.FragmentFragExerciseWorkoutBinding;

public class frag_exercise_workout extends Fragment {
    Exercise exercise;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long id = getArguments().getLong("id");

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

        FragmentFragExerciseWorkoutBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_frag_exercise_workout, container, false);
        binding.setExercise(this.exercise);

        return binding.getRoot();
    }
}