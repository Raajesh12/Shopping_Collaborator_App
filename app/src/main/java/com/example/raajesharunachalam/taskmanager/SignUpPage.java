package com.example.raajesharunachalam.taskmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        Button submit = (Button) findViewById(R.id.sumbit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitinfo();
            }
        });
    }

    public void submitinfo()
    {
        EditText first_name_edit = (EditText) findViewById(R.id.first_name);
        String first_name = first_name_edit.getText().toString();

        EditText last_name_edit = (EditText) findViewById(R.id.last_name);
        String last_name = last_name_edit.getText().toString();

        EditText emailfield = (EditText) findViewById(R.id.emailfield);
        String email = emailfield.getText().toString();

        EditText passwordfield = (EditText) findViewById(R.id.passwordfield);
        String password = passwordfield.getText().toString();
    }

}
