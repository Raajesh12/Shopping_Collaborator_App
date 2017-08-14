package com.example.raajesharunachalam.taskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.raajesharunachalam.taskmanager.endpoints.UserEndpoints;
import com.example.raajesharunachalam.taskmanager.requests.ValidateUserRequest;
import com.example.raajesharunachalam.taskmanager.responses.UIDResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void validatepassword() {
        EditText emailfield = (EditText) findViewById(R.id.emailfield);
        String email = emailfield.getText().toString();

        EditText passwordfield = (EditText) findViewById(R.id.passwordfield);
        String password = passwordfield.getText().toString();

        ValidateUserRequest request = new ValidateUserRequest(email, password);
        Call<UIDResponse> call = UserEndpoints.userEndpoints.validateUser(request);

        call.enqueue(new Callback<UIDResponse>() {
            @Override
            public void onResponse(Call<UIDResponse> call, Response<UIDResponse> response) {
                if (response.code() == ResponseCodes.HTTP_OK) {
                    long uid = response.body().getUid();
                    Intent intent = new Intent(LoginActivity.this, GroupsActivity.class);
                    intent.putExtra(IntentKeys.UID, uid);
                    startActivity(intent);
                } else if (response.code() == ResponseCodes.HTTP_UNAUTHORIZED){
                    Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UIDResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getString(R.string.call_failed), Toast.LENGTH_LONG).show();
            }
        });
    }
}
