package com.example.raajesharunachalam.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raajesharunachalam.taskmanager.endpoints.GroupEndpoints;
import com.example.raajesharunachalam.taskmanager.responses.Group;
import com.example.raajesharunachalam.taskmanager.responses.GroupListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsActivity extends AppCompatActivity {
    private static long uid;
    private RecyclerView rv;
    private GroupsAdapter groupsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Intent intent = getIntent();
        uid = intent.getLongExtra(IntentKeys.UID, 0L);

        rv = (RecyclerView) findViewById(R.id.recycleGroups);

        initializeRecyclerView(uid);

        // Allows the user to delete a group by swiping left on the given row
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Long gid = (Long) viewHolder.itemView.getTag();
                Call<Void> call = GroupEndpoints.groupEndpoints.deleteGroup(gid.longValue());

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == ResponseCodes.HTTP_NO_CONTENT){
                            refreshRecyclerView(uid);
                        } else {
                            Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                    }
                });

            }
        }).attachToRecyclerView(rv);
    }

    public void initializeRecyclerView(final long uid) {
        Call<GroupListResponse> call = GroupEndpoints.groupEndpoints.getGroups(uid);

        call.enqueue(new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, Response<GroupListResponse> response) {
                if (response.code() == ResponseCodes.HTTP_OK) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(GroupsActivity.this);
                    rv.setLayoutManager(layoutManager);

                    Group[] groups = response.body().getGroups();
                    groupsAdapter = new GroupsAdapter(groups);
                    rv.setAdapter(groupsAdapter);
                } else {
                    Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GroupListResponse> call, Throwable t) {
                Toast.makeText(GroupsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refreshRecyclerView(final long uid) {
        Call<GroupListResponse> call = GroupEndpoints.groupEndpoints.getGroups(uid);

        call.enqueue(new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, Response<GroupListResponse> response) {
                if (response.code() == ResponseCodes.HTTP_OK) {
                    Group[] groups = response.body().getGroups();
                    groupsAdapter.setGroups(groups);
                    groupsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GroupListResponse> call, Throwable t) {
                Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

        Group[] groups;

        public GroupsAdapter(Group[] groups) {
            this.groups = groups;
        }

        public void setGroups(Group[] newGroups) {
            this.groups = newGroups;

        }

        @Override
        public GroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = GroupsActivity.this;
            LayoutInflater myInflater = LayoutInflater.from(context);

            View groupView = myInflater.inflate(R.layout.group_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(groupView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final GroupsAdapter.ViewHolder viewHolder, int position) {
            Group aGroup = groups[position];
            viewHolder.groupName.setText(aGroup.getGroupName());
            Long longer = new Long(aGroup.getGroupId());
            viewHolder.itemView.setTag(longer);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupsActivity.this, TasksActivity.class);
                    Long longer = (Long) viewHolder.itemView.getTag();

                    long groupId = longer.longValue();
                    intent.putExtra(IntentKeys.GID, groupId);

                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groups.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView groupName;

            public ViewHolder(View itemView) {
                super(itemView);

                groupName = (TextView) itemView.findViewById(R.id.group_name);
            }
        }
    }
}
