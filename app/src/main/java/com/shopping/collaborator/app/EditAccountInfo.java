package com.shopping.collaborator.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shopping.collaborator.app.endpoints.UserEndpoints;
import com.shopping.collaborator.app.requests.UpdateUserRequest;
import com.shopping.collaborator.app.responses.UserInfoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccountInfo extends AppCompatActivity {

    static long uid;
    static String oldFirstName;
    static String oldLastName;
    static String oldEmail;
    static String oldPassword;

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
        Call<UserInfoResponse> call = UserEndpoints.userEndpoints.getUserInfo(uid);
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if(response.code()==ResponseCodes.HTTP_OK){
                    oldFirstName = response.body().getFirstName();
                    oldLastName = response.body().getLastName();
                    oldEmail = response.body().getEmail();
                    oldPassword = response.body().getPassword();
                    EditText firstNameEdit = (EditText) findViewById(R.id.first_name);
                    EditText lastNameEdit = (EditText) findViewById(R.id.last_name);
                    EditText emailEdit = (EditText) findViewById(R.id.emailfield);
                    EditText passwordEdit = (EditText) findViewById(R.id.passwordfield);
                    firstNameEdit.setText(oldFirstName);
                    lastNameEdit.setText(oldLastName);
                    emailEdit.setText(oldEmail);
                    passwordEdit.setText(oldPassword);
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {

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
        boolean firstNameFlag = oldFirstName.equals(firstName);
        boolean lastNameFlag = oldLastName.equals(lastName);
        boolean emailFlag = oldEmail.equals(email);
        boolean passwordFlag = oldPassword.equals(password);

        if( !firstNameFlag || !lastNameFlag || !emailFlag || !passwordFlag) {


            UpdateUserRequest request = new UpdateUserRequest();
            if(firstNameFlag){
                request.setFirstName(null);
            }else{
                request.setFirstName(firstName);
            }
            if(lastNameFlag){
                request.setLastName(null);
            }else{
                request.setLastName(lastName);
            }
            if(emailFlag){
                request.setEmail(null);
            }else{
                request.setEmail(email);
            }
            if(passwordFlag){
                request.setPassword(null);
            }else{
                request.setPassword(password);
            }

            Call<Void> call = UserEndpoints.userEndpoints.updateUser(uid, request);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == ResponseCodes.HTTP_NO_CONTENT) {
                        Toast.makeText(EditAccountInfo.this, R.string.account_updated, Toast.LENGTH_LONG).show();
                        EditAccountInfo.this.finish();
                    } else {
                        Toast.makeText(EditAccountInfo.this, R.string.email_not_unique, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditAccountInfo.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            Toast.makeText(EditAccountInfo.this, R.string.no_fields_changed, Toast.LENGTH_LONG).show();
        }
    }
}
