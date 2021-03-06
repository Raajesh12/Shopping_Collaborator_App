package com.shopping.collaborator.app;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shopping.collaborator.app.endpoints.GroupUserEndpoints;
import com.shopping.collaborator.app.requests.AddUserGroupRequest;
import com.shopping.collaborator.app.responses.User;
import com.shopping.collaborator.app.responses.UsersInGroupResponse;

import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersInGroupActivity extends AppCompatActivity {


    private static long gid;
    private RecyclerView rv;
    private UsersAdapter adapter;
    FloatingActionButton addUsers;
    HashSet<String> emailsInGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_in_group);
        gid = getIntent().getLongExtra(IntentKeys.GID,0L);
        rv = (RecyclerView) findViewById(R.id.recycle_group_users);
        initializeRecycler(gid);

        addUsers = (FloatingActionButton) findViewById(R.id.add_users_button);
        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UsersInGroupActivity.this);
                dialog.setTitle(R.string.add_user);
                dialog.setMessage(R.string.add_user_to_group_message);
                final EditText input = new EditText(UsersInGroupActivity.this);
                FrameLayout container = new FrameLayout(UsersInGroupActivity.this);
                FrameLayout.LayoutParams params =
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 100;
                params.rightMargin = 100;
                params.bottomMargin = 75;
                input.setLayoutParams(params);
                container.addView(input);
                dialog.setView(container);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String userEmail = input.getText().toString();
                        if(emailsInGroup.contains(userEmail)){
                            Toast.makeText(UsersInGroupActivity.this, R.string.add_user_already_in_group, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            return;
                        }
                        AddUserGroupRequest req = new AddUserGroupRequest(gid, userEmail);
                        Call<Void> call = GroupUserEndpoints.groupUserEndpoints.addUserToGroup(req);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==ResponseCodes.HTTP_CREATED){
                                    Toast.makeText(UsersInGroupActivity.this,"User at " + userEmail + " added.", Toast.LENGTH_LONG).show();
                                    resetRecyclerView(gid);
                                }
                                else if (response.code()==ResponseCodes.HTTP_BAD_REQUEST){
                                    Toast.makeText(UsersInGroupActivity.this, R.string.user_not_found,Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(UsersInGroupActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(UsersInGroupActivity.this, R.string.call_failed,Toast.LENGTH_LONG).show();

                            }
                        });
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    public void initializeRecycler(final long gid){
        Call<UsersInGroupResponse> call = GroupUserEndpoints.groupUserEndpoints.displayUsersInGroup(gid);

        call.enqueue(new Callback<UsersInGroupResponse>() {
            @Override
            public void onResponse(Call<UsersInGroupResponse> call, Response<UsersInGroupResponse> response) {
                if(response.code()==ResponseCodes.HTTP_OK){
                    LinearLayoutManager layoutManager = new LinearLayoutManager(UsersInGroupActivity.this);
                    rv.setLayoutManager(layoutManager);
                    User[] users = response.body().getUsers();
                    emailsInGroup = new HashSet<String>();
                    for(User user : users){
                        emailsInGroup.add(user.getEmail());
                    }
                    adapter = new UsersAdapter(users);
                    rv.setAdapter(adapter);
                }
                else{
                    Toast.makeText(UsersInGroupActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UsersInGroupResponse> call, Throwable t) {
                Toast.makeText(UsersInGroupActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void resetRecyclerView(final long gid) {
        Call<UsersInGroupResponse> call = GroupUserEndpoints.groupUserEndpoints.displayUsersInGroup(gid);

        call.enqueue(new Callback<UsersInGroupResponse>() {
            @Override
            public void onResponse(Call<UsersInGroupResponse> call, Response<UsersInGroupResponse> response) {
                if(response.code()==ResponseCodes.HTTP_OK){
                    User[] users = response.body().getUsers();
                    adapter.setUsers(users);
                    emailsInGroup.clear();
                    for(User user : users){
                        emailsInGroup.add(user.getEmail());
                    }
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(UsersInGroupActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UsersInGroupResponse> call, Throwable t) {
                Toast.makeText(UsersInGroupActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {
        User[] users;

        public UsersAdapter(User[] users){
            this.users = users;
        }

        public void setUsers(User[] users){
            this.users = users;
        }


        @Override
        public UsersInGroupActivity.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = UsersInGroupActivity.this;
            LayoutInflater myInflater = LayoutInflater.from(context);

            View view = myInflater.inflate(R.layout.users_item, parent, false);

            UsersInGroupActivity.UsersViewHolder viewHolder = new UsersInGroupActivity.UsersViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final UsersViewHolder holder, int position) {
            User user = users[position];
            String name = user.getFirstName() + " " + user.getLastName();
            holder.name.setText(name);

            Long uid = new Long(user.getUid());
            holder.itemView.setTag(uid);

            holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long uid = (Long) holder.itemView.getTag();
                    Call<Void> call = GroupUserEndpoints.groupUserEndpoints.deleteUserFromGroup(gid, uid.longValue());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.code() == ResponseCodes.HTTP_NO_CONTENT) {
                                StringBuilder message = new StringBuilder();
                                message.append("Removed User \"");
                                message.append(holder.name.getText().toString());
                                message.append("\" Successfully!");

                                Toast.makeText(UsersInGroupActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                                resetRecyclerView(gid);
                            } else if (response.code() == ResponseCodes.HTTP_BAD_REQUEST){
                                Toast.makeText(UsersInGroupActivity.this, R.string.remove_owner_from_group, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UsersInGroupActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(UsersInGroupActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return users.length;
        }
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public Button cancelButton;
        public UsersViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            cancelButton = (Button) itemView.findViewById(R.id.cancel_button);
        }
    }



}
