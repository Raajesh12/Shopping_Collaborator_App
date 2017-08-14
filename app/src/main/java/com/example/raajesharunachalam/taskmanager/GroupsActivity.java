package com.example.raajesharunachalam.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.raajesharunachalam.taskmanager.responses.Group;

public class GroupsActivity extends AppCompatActivity {
    public static int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Intent intent = getIntent();
        uid = intent.getIntExtra(IntentKeys.UID, 0);
    }

    public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder>
    {
        Group[] groups;
        public GroupsAdapter(Group[] groups){
            this.groups = groups;
        }
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView groupName;

            public ViewHolder(View itemView)
            {
                super(itemView);

                groupName = (TextView) itemView.findViewById(R.id.group_name);
            }
        }

        @Override
        public GroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater myInflater = LayoutInflater.from(context);


            View groupView = myInflater.inflate(R.layout.group_item, parent, false);


            ViewHolder viewHolder = new ViewHolder(groupView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(GroupsAdapter.ViewHolder viewHolder, int position) {
            Group aGroup = groups[position];

            viewHolder.groupName.setText(aGroup.getGroupName());

        }

        @Override
        public int getItemCount() {
            return groups.length;
        }
    }
}
