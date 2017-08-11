package com.example.raajesharunachalam.taskmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void validatepassword()
    {
        EditText emailfield = (EditText) findViewById(R.id.emailfield);
        String email = "" + emailfield.getText();

        EditText passwordfield = (EditText) findViewById(R.id.passwordfield);
        String password = "" + passwordfield.getText();
    }
}
