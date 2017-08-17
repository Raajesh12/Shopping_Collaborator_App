package com.example.raajesharunachalam.taskmanager;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raajesharunachalam.taskmanager.endpoints.GroupUserEndpoints;
import com.example.raajesharunachalam.taskmanager.responses.Task;
import com.example.raajesharunachalam.taskmanager.responses.User;
import com.example.raajesharunachalam.taskmanager.responses.UsersInGroupResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.y;

public class UsersInGroupActivity extends AppCompatActivity {


    private static long gid;
    private RecyclerView rv;
    private UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_in_group);
        gid = getIntent().getLongExtra(IntentKeys.GID,0L);
        rv = (RecyclerView) findViewById(R.id.recycle_group_users);
        initializeRecycler(gid);
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
                                Toast.makeText(UsersInGroupActivity.this, R.string.remove_self_from_group, Toast.LENGTH_LONG).show();
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
