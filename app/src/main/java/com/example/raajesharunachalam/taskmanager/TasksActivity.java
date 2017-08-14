package com.example.raajesharunachalam.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raajesharunachalam.taskmanager.endpoints.GroupEndpoints;
import com.example.raajesharunachalam.taskmanager.endpoints.TaskEndpoints;
import com.example.raajesharunachalam.taskmanager.responses.Group;
import com.example.raajesharunachalam.taskmanager.responses.GroupListResponse;
import com.example.raajesharunachalam.taskmanager.responses.Task;
import com.example.raajesharunachalam.taskmanager.responses.TaskListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksActivity extends AppCompatActivity {

    public static long gid;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Intent intent = getIntent();
        gid = intent.getLongExtra(IntentKeys.GID, 0L);

        rv = (RecyclerView) findViewById(R.id.recycle_tasks);

        initializeRecyclerView(gid);

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
                    TasksActivity.TasksAdapter tasksAdapter = new TasksActivity.TasksAdapter(tasks);
                    rv.setAdapter(tasksAdapter);

                    Log.d("TaskResponseInfo", "Code: " + String.valueOf(response.code()));
                    Log.d("TaskResponseInfo", "Task Length: " + String.valueOf(tasks.length));
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

    public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder>{

        Task[] tasks;
        public TasksAdapter(Task[] tasks){
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
