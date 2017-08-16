package com.example.raajesharunachalam.taskmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raajesharunachalam.taskmanager.endpoints.GroupUserEndpoints;
import com.example.raajesharunachalam.taskmanager.endpoints.TaskEndpoints;
import com.example.raajesharunachalam.taskmanager.requests.AddUserGroupRequest;
import com.example.raajesharunachalam.taskmanager.responses.Task;
import com.example.raajesharunachalam.taskmanager.responses.TaskListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksActivity extends AppCompatActivity {

    public static long gid;
    RecyclerView rv;
    TasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Intent intent = getIntent();
        gid = intent.getLongExtra(IntentKeys.GID, 0L);

        rv = (RecyclerView) findViewById(R.id.recycle_tasks);

        initializeRecyclerView(gid);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.tasks_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_user:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.add_user);
                dialog.setMessage(R.string.add_user_to_group_message);
                final EditText input = new EditText(TasksActivity.this);
                FrameLayout container = new FrameLayout(TasksActivity.this);
                FrameLayout.LayoutParams params =
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 50;
                params.leftMargin = 100;
                params.rightMargin = 100;
                params.bottomMargin = 50;
                input.setLayoutParams(params);
                container.addView(input);
                dialog.setView(container);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String userEmail = input.getText().toString();
                        AddUserGroupRequest req = new AddUserGroupRequest(gid, userEmail);
                        Call<Void> call = GroupUserEndpoints.groupUserEndpoints.addUserToGroup(req);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==ResponseCodes.HTTP_CREATED){
                                    Toast.makeText(TasksActivity.this,"User at " + userEmail + " added.", Toast.LENGTH_LONG).show();
                                }
                                else if (response.code()==ResponseCodes.HTTP_BAD_REQUEST){
                                    Toast.makeText(TasksActivity.this, R.string.user_not_found,Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(TasksActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(TasksActivity.this, R.string.call_failed,Toast.LENGTH_LONG).show();

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
                return true;
            case R.id.view_users:
                Intent intent = new Intent(TasksActivity.this, UsersInGroupActivity.class);
                intent.putExtra(IntentKeys.GID, gid);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void initializeRecyclerView(final long gid) {
        Call<TaskListResponse> call = TaskEndpoints.taskEndpoints.getTasks(gid);

        call.enqueue(new Callback<TaskListResponse>() {
            @Override
            public void onResponse(Call<TaskListResponse> call, Response<TaskListResponse> response) {
                if (response.code() == ResponseCodes.HTTP_OK) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(TasksActivity.this);
                    rv.setLayoutManager(layoutManager);

                    Task[] tasks = response.body().getTasks();
                    adapter = new TasksActivity.TasksAdapter(tasks);
                    rv.setAdapter(adapter);
                } else {
                    Toast.makeText(TasksActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TaskListResponse> call, Throwable t) {
                Toast.makeText(TasksActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void resetRecyclerView(final long gid) {
        Call<TaskListResponse> call = TaskEndpoints.taskEndpoints.getTasks(gid);

        call.enqueue(new Callback<TaskListResponse>() {
            @Override
            public void onResponse(Call<TaskListResponse> call, Response<TaskListResponse> response) {
                if(response.code() == ResponseCodes.HTTP_OK){
                    Task[] tasks = response.body().getTasks();
                    adapter.setTasks(tasks);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(TasksActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TaskListResponse> call, Throwable t) {
                Toast.makeText(TasksActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder>{

        Task[] tasks;
        public TasksAdapter(Task[] tasks){
            this.tasks = tasks;
        }

        public void setTasks(Task[] tasks){
            this.tasks = tasks;
        }

        @Override
        public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = TasksActivity.this;
            LayoutInflater myInflater = LayoutInflater.from(context);

            View view = myInflater.inflate(R.layout.task_item, parent, false);

            TasksViewHolder viewHolder = new TasksViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TasksViewHolder holder, int position) {
            Task task = tasks[position];
            if(task.getTaskDescription() != null) {
                holder.taskDescription.setText(task.getTaskDescription());
                String name = task.getFirstName() + " " + task.getLastName();
                holder.taskAuthor.setText(name);
            }
        }

        @Override
        public int getItemCount() {
            return tasks.length;
        }

        public class TasksViewHolder extends RecyclerView.ViewHolder{
            public TextView taskDescription;
            public TextView taskAuthor;
            public TasksViewHolder(View itemView) {
                super(itemView);
                taskDescription = (TextView) itemView.findViewById(R.id.task_description);
                taskAuthor = (TextView) itemView.findViewById(R.id.task_author);
            }
        }
    }
}