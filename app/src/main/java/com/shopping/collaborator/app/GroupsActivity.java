package com.shopping.collaborator.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shopping.collaborator.app.endpoints.GroupEndpoints;
import com.shopping.collaborator.app.endpoints.GroupUserEndpoints;
import com.shopping.collaborator.app.endpoints.UserEndpoints;
import com.shopping.collaborator.app.requests.CreateGroupRequest;
import com.shopping.collaborator.app.requests.UpdateGroupRequest;
import com.shopping.collaborator.app.requests.ValidateCurrentUserRequest;
import com.shopping.collaborator.app.responses.GIDResponse;
import com.shopping.collaborator.app.responses.Group;
import com.shopping.collaborator.app.responses.GroupListResponse;

import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsActivity extends AppCompatActivity {
    private static long uid;
    private static int startCalls;
    private RecyclerView rv;
    private GroupsAdapter groupsAdapter;
    private FloatingActionButton addGroupButton;

    GroupResponseReceiver receiver;

    Calendar lastRefreshed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Intent intent = getIntent();
        uid = intent.getLongExtra(IntentKeys.UID, 0L);

        rv = (RecyclerView) findViewById(R.id.recycleGroups);
        addGroupButton = (FloatingActionButton) findViewById(R.id.add_group_button);

        initializeRecyclerView(uid);

        // Allows the user to delete a group by swiping left on the given row
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a GroupViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Long gid = (Long) viewHolder.itemView.getTag();
                Call<Void> call = GroupUserEndpoints.groupUserEndpoints.deleteUserFromGroup(gid.longValue(), uid);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == ResponseCodes.HTTP_NO_CONTENT) {
                            Toast.makeText(GroupsActivity.this, R.string.you_left_group, Toast.LENGTH_LONG).show();
                        } else if (response.code() == ResponseCodes.HTTP_BAD_REQUEST) {
                            Toast.makeText(GroupsActivity.this, R.string.owner_cant_leave, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                        }
                        refreshRecyclerView(uid);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(GroupsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                        refreshRecyclerView(uid);
                    }
                });
            }
        }).attachToRecyclerView(rv);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Long gid = (Long) viewHolder.itemView.getTag();
                GroupViewHolder holder = (GroupViewHolder) viewHolder;

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupsActivity.this);
                alertDialog.setTitle(R.string.update_group_title);
                alertDialog.setMessage(R.string.update_group_message);

                LinearLayout container = new LinearLayout(GroupsActivity.this);
                container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container.setOrientation(LinearLayout.VERTICAL);

                final EditText groupNameInput = new EditText(GroupsActivity.this);
                groupNameInput.setText(holder.groupName.getText().toString());
                groupNameInput.setHint(R.string.update_group_hint);
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemParams.topMargin = 0;
                itemParams.leftMargin = 100;
                itemParams.rightMargin = 100;
                itemParams.bottomMargin = 0;
                groupNameInput.setLayoutParams(itemParams);

                container.addView(groupNameInput);
                alertDialog.setView(container);

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String groupName = groupNameInput.getText().toString();
                        if (groupName.length() == 0) {
                            Toast.makeText(GroupsActivity.this, R.string.update_group_error, Toast.LENGTH_LONG).show();
                            refreshRecyclerView(uid);
                            return;
                        }
                        UpdateGroupRequest request = new UpdateGroupRequest(groupName);
                        Call<Void> call = GroupEndpoints.groupEndpoints.updateGroup(gid, request);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == ResponseCodes.HTTP_NO_CONTENT) {
                                    Toast.makeText(GroupsActivity.this, R.string.update_group_successful_message, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                }
                                refreshRecyclerView(uid);
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(GroupsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                                refreshRecyclerView(uid);
                            }
                        });
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshRecyclerView(uid);
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }).attachToRecyclerView(rv);

        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupsActivity.this);
                alertDialog.setTitle(R.string.add_group_title);
                alertDialog.setMessage(R.string.add_group_message);
                final EditText input = new EditText(GroupsActivity.this);
                FrameLayout container = new FrameLayout(GroupsActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 0;
                params.leftMargin = 100;
                params.rightMargin = 100;
                params.bottomMargin = 30;
                input.setLayoutParams(params);
                container.addView(input);
                alertDialog.setView(container);
                alertDialog.setPositiveButton(R.string.create_group_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = input.getText().toString();
                        if (inputText.length() == 0) {
                            Toast.makeText(GroupsActivity.this, R.string.create_group_no_text_message, Toast.LENGTH_LONG).show();
                        } else {
                            final CreateGroupRequest request = new CreateGroupRequest(uid, inputText);
                            Call<GIDResponse> call = GroupEndpoints.groupEndpoints.createGroup(request);

                            call.enqueue(new Callback<GIDResponse>() {
                                @Override
                                public void onResponse(Call<GIDResponse> call, Response<GIDResponse> response) {
                                    if (response.code() == ResponseCodes.HTTP_CREATED) {
                                        refreshRecyclerView(uid);
                                    } else {
                                        Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GIDResponse> call, Throwable t) {
                                    Toast.makeText(GroupsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.groups_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_account:
                AlertDialog.Builder dialog = new AlertDialog.Builder(GroupsActivity.this);
                dialog.setTitle(R.string.confirm_credentials_title);
                dialog.setMessage(R.string.confirm_credentials_for_delete);

                LinearLayout container = new LinearLayout(GroupsActivity.this);
                container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container.setOrientation(LinearLayout.VERTICAL);

                final EditText email = new EditText(GroupsActivity.this);
                email.setHint("Email");
                LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                emailParams.topMargin = 0;
                emailParams.leftMargin = 100;
                emailParams.rightMargin = 100;
                emailParams.bottomMargin = 0;
                email.setLayoutParams(emailParams);

                final EditText password = new EditText(GroupsActivity.this);
                password.setHint("Password");
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                LinearLayout.LayoutParams passwordParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                passwordParams.topMargin = 50;
                passwordParams.leftMargin = 100;
                passwordParams.rightMargin = 100;
                passwordParams.bottomMargin = 50;
                password.setLayoutParams(passwordParams);

                container.addView(email);
                container.addView(password);
                dialog.setView(container);

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailString = email.getText().toString();
                        String passwordString = password.getText().toString();
                        ValidateCurrentUserRequest request = new ValidateCurrentUserRequest(uid, emailString, passwordString);
                        Call<Void> call = UserEndpoints.userEndpoints.validateCurrentUser(request);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == ResponseCodes.HTTP_OK) {
                                    Call<Void> call1 = UserEndpoints.userEndpoints.deleteUser(uid);
                                    call1.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.code() == ResponseCodes.HTTP_NO_CONTENT) {
                                                Toast.makeText(GroupsActivity.this, R.string.account_deleted, Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(GroupsActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else if (response.code() == ResponseCodes.HTTP_UNAUTHORIZED){
                                                Toast.makeText(GroupsActivity.this, R.string.invalid_credentials, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(GroupsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else if (response.code() == ResponseCodes.HTTP_UNAUTHORIZED) {
                                    Toast.makeText(GroupsActivity.this, R.string.invalid_credentials, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(GroupsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

                dialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;

            case R.id.edit_account_info:
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(GroupsActivity.this);
                dialog1.setTitle(R.string.confirm_credentials_title);
                dialog1.setMessage(R.string.confirm_credentials_for_edit);

                LinearLayout container1 = new LinearLayout(GroupsActivity.this);
                container1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container1.setOrientation(LinearLayout.VERTICAL);

                final EditText email1 = new EditText(GroupsActivity.this);
                email1.setHint("Email");
                LinearLayout.LayoutParams emailParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                emailParams1.topMargin = 0;
                emailParams1.leftMargin = 100;
                emailParams1.rightMargin = 100;
                emailParams1.bottomMargin = 0;
                email1.setLayoutParams(emailParams1);

                final EditText password1 = new EditText(GroupsActivity.this);
                password1.setHint("Password");
                password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                LinearLayout.LayoutParams passwordParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                passwordParams1.topMargin = 50;
                passwordParams1.leftMargin = 100;
                passwordParams1.rightMargin = 100;
                passwordParams1.bottomMargin = 50;
                password1.setLayoutParams(passwordParams1);

                container1.addView(email1);
                container1.addView(password1);
                dialog1.setView(container1);

                dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailString = email1.getText().toString();
                        String passwordString = password1.getText().toString();
                        ValidateCurrentUserRequest request = new ValidateCurrentUserRequest(uid, emailString, passwordString);
                        Call<Void> call = UserEndpoints.userEndpoints.validateCurrentUser(request);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == ResponseCodes.HTTP_NO_CONTENT) {
                                    Intent i = new Intent(GroupsActivity.this, EditAccountInfo.class);
                                    i.putExtra(IntentKeys.UID, uid);
                                    startActivity(i);
                                } else if (response.code() == ResponseCodes.HTTP_UNAUTHORIZED) {
                                    Toast.makeText(GroupsActivity.this, R.string.invalid_credentials, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(GroupsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(GroupsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

                dialog1.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog1.show();
                return true;
            case R.id.groups_log_out_button:
                Intent intent = new Intent(GroupsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(GroupResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new GroupResponseReceiver();
        registerReceiver(receiver, filter);

        startCalls++;
        if (startCalls > 1) {
            refreshRecyclerView(uid);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startCalls = 0;
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

                    lastRefreshed = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

                    Intent intent = new Intent(GroupsActivity.this, GroupService.class);
                    intent.putExtra(IntentKeys.UID, uid);
                    intent.putExtra(IntentKeys.LAST_REFRESHED, lastRefreshed);
                    startService(intent);
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

                    lastRefreshed = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
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

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    public class GroupsAdapter extends RecyclerView.Adapter<GroupViewHolder> {

        Group[] groups;

        public GroupsAdapter(Group[] groups) {
            this.groups = groups;
        }

        public void setGroups(Group[] newGroups) {
            this.groups = newGroups;

        }

        @Override
        public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = GroupsActivity.this;
            LayoutInflater myInflater = LayoutInflater.from(context);

            View groupView = myInflater.inflate(R.layout.group_item, parent, false);

            GroupViewHolder groupViewHolder = new GroupViewHolder(groupView);
            return groupViewHolder;
        }

        @Override
        public void onBindViewHolder(final GroupViewHolder groupViewHolder, int position) {
            Group aGroup = groups[position];
            groupViewHolder.groupName.setText(aGroup.getGroupName());
            Long longer = new Long(aGroup.getGroupId());
            groupViewHolder.itemView.setTag(longer);

            groupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupsActivity.this, ItemsActivity.class);
                    Long longer = (Long) groupViewHolder.itemView.getTag();

                    long groupId = longer.longValue();
                    intent.putExtra(IntentKeys.GID, groupId);
                    intent.putExtra(IntentKeys.UID, uid);

                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groups.length;
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;

        public GroupViewHolder(View itemView) {
            super(itemView);

            groupName = (TextView) itemView.findViewById(R.id.group_name);
        }
    }

    public class GroupResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "Refresh RecyclerView";

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean shouldRefresh = intent.getBooleanExtra(IntentKeys.SHOULD_REFRESH, false);
            if(shouldRefresh) {
                GroupsActivity.this.refreshRecyclerView(uid);
            }
            Intent checkRefresh = new Intent(GroupsActivity.this, GroupService.class);
            checkRefresh.putExtra(IntentKeys.UID, uid);
            checkRefresh.putExtra(IntentKeys.LAST_REFRESHED, lastRefreshed);
            startService(checkRefresh);
        }
    }
}
