package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity implements View.OnClickListener{
    Fragment login;
    Fragment register;
    FragmentManager fragmentManager;
    boolean register_state;
    static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnValidate = findViewById(R.id.btn_validate);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnValidate.setOnClickListener(this);

        this.login = new frag_login();
        this.register = new frag_register();
        this.fragmentManager = getSupportFragmentManager();

        this.fragmentManager.beginTransaction().replace(R.id.frame_layout, this.login).commit();
        this.register_state = false;

        Login.db = openOrCreateDatabase("WorkoutAppV5",MODE_PRIVATE,null);
        Login.db.execSQL("CREATE TABLE IF NOT EXISTS user" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, mail TEXT, login TEXT, password TEXT)");
        Login.db.execSQL("CREATE TABLE IF NOT EXISTS exercise" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, user INTEGER, name TEXT, type TEXT, weight INT, reps INT)");
        Login.db.execSQL("CREATE TABLE IF NOT EXISTS workout" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, user INTEGER, exercise INTEGER)");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                this.fragmentManager.beginTransaction().replace(R.id.frame_layout, this.login).commit();
                this.register_state = false;
                break;
            case R.id.btn_register:
                this.fragmentManager.beginTransaction().replace(R.id.frame_layout, this.register).commit();
                this.register_state = true;
                break;
            case R.id.btn_validate:
                if (this.register_state)
                    this.register();
                else
                    this.login();
                break;
            default:
                break;
        }
    }

    public void login() {
        int id = this.login(((EditText)findViewById(R.id.txt_login)).getText().toString(),
                ((EditText)findViewById(R.id.txt_password)).getText().toString());
        if (id != -1) {
            Intent exercise_list = new Intent(this, Exercise_list.class);
            exercise_list.putExtra("user" , id);
            startActivity(exercise_list);
        }
    }

    public void register() {
        if (this.createUser(((EditText)findViewById(R.id.txt_mail)).getText().toString(),
                ((EditText)findViewById(R.id.txt_login_register)).getText().toString(),
                ((EditText)findViewById(R.id.txt_password_register)).getText().toString())) {
            this.fragmentManager.beginTransaction().replace(R.id.frame_layout, this.login).commit();
            this.register_state = false;
        }
    }

    public boolean createUser(String mail, String login, String password) {
        if (mail.isEmpty() || login.isEmpty() || password.isEmpty())
            return false;

        @SuppressLint("Recycle") Cursor exist = Login.db.rawQuery(
                "SELECT * " +
                        "FROM user " +
                        "WHERE mail = '" + mail + "' " +
                        "OR login = '" + login + "'"
                , new String[]{});
        if (exist.getCount() > 0)
            return false;

        Login.db.execSQL("INSERT INTO user (mail, login, password) " +
                "VALUES ('" + mail + "', '" + login + "', '" + password + "')");
        return true;
    }

    public int login(String login, String password) {
        @SuppressLint("Recycle") Cursor exist = Login.db.rawQuery(
                "SELECT id " +
                        "FROM user " +
                        "WHERE login = '" + login + "' " +
                        "AND password = '" + password + "'"
                , new String[]{});
        if (exist.moveToFirst())
            return exist.getInt(exist.getColumnIndex("id"));
        else
            return -1;
    }
}