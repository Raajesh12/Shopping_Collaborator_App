package com.example.raajesharunachalam.taskmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.raajesharunachalam.taskmanager.responses.Task;

public class TasksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
    }

    public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder>{

        Task[] tasks;
        public TasksAdapter(Task[] tasks){
            this.tasks = tasks;
        }

        @Override
        public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(TasksActivity.this);
            View view = inflater.inflate(R.layout.task_item, parent, false);
            TasksViewHolder viewHolder = new TasksViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TasksViewHolder holder, int position) {
            Task task = tasks[position];
            holder.taskDescription.setText(task.getTaskDescription());
            String name = task.getFirstName() + " " + task.getLastName();
            holder.taskAuthor.setText(name);
        }

        @Override
        public int getItemCount() {
            return tasks.length;
        }

        public class TasksViewHolder extends RecyclerView.ViewHolder{
            TextView taskDescription;
            TextView taskAuthor;
            public TasksViewHolder(View itemView) {
                super(itemView);
                taskDescription = (TextView) findViewById(R.id.task_description);
                taskAuthor = (TextView) findViewById(R.id.task_author);
            }
        }
    }
}
