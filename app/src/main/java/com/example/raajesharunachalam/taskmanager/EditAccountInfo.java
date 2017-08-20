package com.example.raajesharunachalam.taskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.raajesharunachalam.taskmanager.endpoints.UserEndpoints;
import com.example.raajesharunachalam.taskmanager.requests.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccountInfo extends AppCompatActivity {

    static long uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_info);
        Button submit = (Button) findViewById(R.id.sumbit_button);
        uid = getIntent().getLongExtra(IntentKeys.UID, 0);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateInfo();
            }
        });
    }

    public void updateInfo(){
        EditText firstNameEdit = (EditText) findViewById(R.id.first_name);
        String firstName = firstNameEdit.getText().toString();
        EditText lastNameEdit = (EditText) findViewById(R.id.last_name);
        String lastName = lastNameEdit.getText().toString();
        EditText emailEdit = (EditText) findViewById(R.id.emailfield);
        String email = emailEdit.getText().toString();
        EditText passwordEdit = (EditText) findViewById(R.id.passwordfield);
        String password = passwordEdit.getText().toString();

        UpdateUserRequest request = new UpdateUserRequest();
        if(firstName.length() == 0){
            request.setFirstName(null);
        }else{
            request.setFirstName(firstName);
        }
        if(lastName.length() == 0){
            request.setLastName(null);
        }else{
            request.setLastName(lastName);
        }
        if(email.length() == 0){
            request.setEmail(null);
        }else{
            request.setEmail(email);
        }
        if(password.length() == 0){
            request.setPassword(null);
        }else{
            request.setPassword(password);
        }

        Call<Void> call = UserEndpoints.userEndpoints.updateUser(uid, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==ResponseCodes.HTTP_NO_CONTENT){
                    Toast.makeText(EditAccountInfo.this, R.string.account_updated,Toast.LENGTH_LONG).show();
                    EditAccountInfo.this.finish();
                }else{
                    Toast.makeText(EditAccountInfo.this, R.string.email_not_unique,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditAccountInfo.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }
}