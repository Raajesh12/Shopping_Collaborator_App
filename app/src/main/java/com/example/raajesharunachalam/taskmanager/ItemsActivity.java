package com.example.raajesharunachalam.taskmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
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

import com.example.raajesharunachalam.taskmanager.endpoints.GroupEndpoints;
import com.example.raajesharunachalam.taskmanager.endpoints.GroupUserEndpoints;
import com.example.raajesharunachalam.taskmanager.endpoints.ItemEndpoints;
import com.example.raajesharunachalam.taskmanager.requests.AddUserGroupRequest;
import com.example.raajesharunachalam.taskmanager.requests.CreateItemRequest;
import com.example.raajesharunachalam.taskmanager.requests.UpdateItemRequest;
import com.example.raajesharunachalam.taskmanager.responses.Item;
import com.example.raajesharunachalam.taskmanager.responses.ItemIDResponse;
import com.example.raajesharunachalam.taskmanager.responses.ItemListResponse;
import com.example.raajesharunachalam.taskmanager.responses.ItemsCompletedResponse;
import com.example.raajesharunachalam.taskmanager.responses.TotalPriceResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String REFRESH_KEY = "Refresh";

    private static long gid;
    private static long uid;
    RecyclerView rv;
    ItemsAdapter adapter;
    TextView itemsBought;
    TextView totalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Intent intent = getIntent();
        gid = intent.getLongExtra(IntentKeys.GID, 0L);
        uid = intent.getLongExtra(IntentKeys.UID, 0L);

        rv = (RecyclerView) findViewById(R.id.recycle_items);
        initializeScreen(gid);

        itemsBought = (TextView) findViewById(R.id.tv_items_bought_actual);
        totalCost = (TextView) findViewById(R.id.tv_total_cost_actual);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ItemsActivity.this);
        Log.d("SharedPreferences", String.valueOf(sharedPreferences.getBoolean(REFRESH_KEY, false)));
        sharedPreferences.edit().putBoolean(REFRESH_KEY, false).apply();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.add_items_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItemsActivity.this);
                alertDialog.setTitle(R.string.add_items_title);
                alertDialog.setMessage(R.string.add_items_message);

                LinearLayout container = new LinearLayout(ItemsActivity.this);
                container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container.setOrientation(LinearLayout.VERTICAL);

                final EditText itemInput = new EditText(ItemsActivity.this);
                itemInput.setHint(R.string.item_input_hint);
                LinearLayout.LayoutParams itemParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemParams.topMargin = 0;
                itemParams.leftMargin = 100;
                itemParams.rightMargin = 100;
                itemParams.bottomMargin = 0;
                itemInput.setLayoutParams(itemParams);

                final EditText estimateInput = new EditText(ItemsActivity.this);
                estimateInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                estimateInput.setHint(R.string.estimate_input_hint);
                LinearLayout.LayoutParams estimateParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                estimateParams.topMargin = 50;
                estimateParams.leftMargin = 100;
                estimateParams.rightMargin = 100;
                estimateParams.bottomMargin = 50;
                estimateInput.setLayoutParams(estimateParams);

                container.addView(itemInput);
                container.addView(estimateInput);
                alertDialog.setView(container);

                alertDialog.setPositiveButton(R.string.add_item_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = itemInput.getText().toString();
                        String estimateString = estimateInput.getText().toString();
                        if(item.length() == 0 || estimateString.length() == 0){
                            Toast.makeText(ItemsActivity.this, R.string.create_item_no_text, Toast.LENGTH_LONG).show();
                        } else {
                            double estimate = Double.parseDouble(estimateString);
                            estimate = (int) (estimate * 100);
                            estimate =  estimate/100.0;
                            CreateItemRequest request = new CreateItemRequest(uid, gid, item, estimate);
                            Call<ItemIDResponse> call = ItemEndpoints.ITEM_ENDPOINTS.createItem(request);

                            call.enqueue(new Callback<ItemIDResponse>() {
                                @Override
                                public void onResponse(Call<ItemIDResponse> call, Response<ItemIDResponse> response) {
                                    if(response.code() == ResponseCodes.HTTP_CREATED) {
                                        refreshScreen(gid, true);
                                    } else {
                                        Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ItemIDResponse> call, Throwable t) {
                                    Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
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
        inf.inflate(R.menu.items_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_user:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.add_user);
                dialog.setMessage(R.string.add_user_to_group_message);
                final EditText input = new EditText(ItemsActivity.this);
                FrameLayout container = new FrameLayout(ItemsActivity.this);
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
                                    Toast.makeText(ItemsActivity.this,"User at " + userEmail + " added.", Toast.LENGTH_LONG).show();
                                }
                                else if (response.code()==ResponseCodes.HTTP_BAD_REQUEST){
                                    Toast.makeText(ItemsActivity.this, R.string.user_not_found,Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(ItemsActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ItemsActivity.this, R.string.call_failed,Toast.LENGTH_LONG).show();

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
                Intent intent = new Intent(ItemsActivity.this, UsersInGroupActivity.class);
                intent.putExtra(IntentKeys.GID, gid);
                startActivity(intent);
                return true;
            case R.id.clear_items:
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                dialog1.setTitle(R.string.delete_all_title);
                dialog1.setMessage(R.string.delete_all_warning);
                dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Void> call = ItemEndpoints.ITEM_ENDPOINTS.deleteAllItems(gid, uid);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==ResponseCodes.HTTP_NO_CONTENT){
                                    Toast.makeText(ItemsActivity.this, R.string.deleted_all_items,Toast.LENGTH_LONG).show();
                                    refreshScreen(gid, true);
                                }
                                else if(response.code()==ResponseCodes.HTTP_UNAUTHORIZED){
                                    Toast.makeText(ItemsActivity.this, R.string.only_owner_clear_message, Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
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
            case R.id.delete_group:
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
                dialog2.setTitle(R.string.delete_group_title);
                dialog2.setMessage(R.string.delete_group_warning);
                dialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Void> call = GroupEndpoints.groupEndpoints.deleteGroup(gid, uid);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==ResponseCodes.HTTP_NO_CONTENT){
                                    Toast.makeText(ItemsActivity.this, R.string.group_deleted,Toast.LENGTH_LONG).show();
                                    ItemsActivity.this.finish();
                                }
                                else if(response.code()==ResponseCodes.HTTP_UNAUTHORIZED){
                                    Toast.makeText(ItemsActivity.this, R.string.only_owner_delete_message, Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                dialog2.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog2.show();
                return true;
            case R.id.items_log_out_button:
                Intent homeIntent = new Intent(ItemsActivity.this, MainActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void initializeScreen(final long gid) {
        Call<ItemListResponse> call = ItemEndpoints.ITEM_ENDPOINTS.getItems(gid);

        call.enqueue(new Callback<ItemListResponse>() {
            @Override
            public void onResponse(Call<ItemListResponse> call, Response<ItemListResponse> response) {
                if (response.code() == ResponseCodes.HTTP_OK) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ItemsActivity.this);
                    rv.setLayoutManager(layoutManager);

                    Item[] items = response.body().getItems();
                    adapter = new ItemsAdapter(items);
                    rv.setAdapter(adapter);
                    initializeItemTouchHelper();
                } else {
                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ItemListResponse> call, Throwable t) {
                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });

        Call<TotalPriceResponse> priceCall = GroupEndpoints.groupEndpoints.getGroupPriceTotal(gid);
        priceCall.enqueue(new Callback<TotalPriceResponse>() {
            @Override
            public void onResponse(Call<TotalPriceResponse> call, Response<TotalPriceResponse> response) {
                if(response.code() == ResponseCodes.HTTP_OK){
                    totalCost.setText("$" + response.body().getTotal());
                } else {
                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TotalPriceResponse> call, Throwable t) {
                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });


        Call<ItemsCompletedResponse> itemsCompletedCall = GroupEndpoints.groupEndpoints.getItemsCompleted(gid);
        Log.d("ItemsURL", itemsCompletedCall.request().url().toString());
        itemsCompletedCall.enqueue(new Callback<ItemsCompletedResponse>() {
            @Override
            public void onResponse(Call<ItemsCompletedResponse> call, Response<ItemsCompletedResponse> response) {
                if(response.code() == ResponseCodes.HTTP_OK){
                    itemsBought.setText(response.body().getItemsString());
                } else {
                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ItemsCompletedResponse> call, Throwable t) {
                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initializeItemTouchHelper(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Long gid = (Long) viewHolder.itemView.getTag();
                ItemsViewHolder holder = (ItemsViewHolder) viewHolder;
                final int position = holder.getAdapterPosition();
                final String itemName = holder.itemName.getText().toString();
                final double estimate = Double.parseDouble(holder.estimate.getText().toString().substring(1));

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItemsActivity.this);
                alertDialog.setTitle(R.string.edit_info);

                LinearLayout container = new LinearLayout(ItemsActivity.this);
                container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container.setOrientation(LinearLayout.VERTICAL);

                final EditText itemInput = new EditText(ItemsActivity.this);
                itemInput.setText(itemName);
                itemInput.setHint(R.string.item_input_hint);
                LinearLayout.LayoutParams itemParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemParams.topMargin = 0;
                itemParams.leftMargin = 100;
                itemParams.rightMargin = 100;
                itemParams.bottomMargin = 0;
                itemInput.setLayoutParams(itemParams);

                final EditText estimateInput = new EditText(ItemsActivity.this);
                estimateInput.setText(estimate + "");
                estimateInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                estimateInput.setHint(R.string.estimate_input_hint);
                LinearLayout.LayoutParams estimateParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                estimateParams.topMargin = 50;
                estimateParams.leftMargin = 100;
                estimateParams.rightMargin = 100;
                estimateParams.bottomMargin = 50;
                estimateInput.setLayoutParams(estimateParams);

                container.addView(itemInput);
                container.addView(estimateInput);
                alertDialog.setView(container);

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newestimate = estimateInput.getText().toString();
                        String newname = itemInput.getText().toString();
                        if(newestimate.length() == 0 || newname.length() == 0){
                            Toast.makeText(ItemsActivity.this, R.string.fields_blank, Toast.LENGTH_LONG).show();
                            return;
                        }
                        double newestimate1 = Double.parseDouble(newestimate);
                        int estimateInt = (int) (newestimate1 * 100);
                        double estimateFinished = estimateInt/100.0;
                        UpdateItemRequest request = new UpdateItemRequest(newname, estimateFinished, 0.0, false);
                        Call<Void> call = ItemEndpoints.ITEM_ENDPOINTS.updateItem(gid, request);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code() == ResponseCodes.HTTP_NO_CONTENT){
                                    Toast.makeText(ItemsActivity.this, R.string.item_updated, Toast.LENGTH_LONG).show();
                                } else{
                                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                }
                                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ItemsActivity.this);
                                sharedPreferences.edit().putBoolean(REFRESH_KEY, true).apply();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ItemsActivity.this);
                                sharedPreferences.edit().putBoolean(REFRESH_KEY, true).apply();
                            }
                        });
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshScreen(gid, false);
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }).attachToRecyclerView(rv);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Long gid = (Long) viewHolder.itemView.getTag();
                ItemsViewHolder holder = (ItemsViewHolder) viewHolder;
                final int position = holder.getAdapterPosition();
                final String itemName = holder.itemName.getText().toString();
                final double estimate = Double.parseDouble(holder.estimate.getText().toString().substring(1));
                final boolean done = true;

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItemsActivity.this);
                alertDialog.setTitle(R.string.actual_price);
                alertDialog.setMessage(R.string.actual_price_message);

                LinearLayout container = new LinearLayout(ItemsActivity.this);
                container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container.setOrientation(LinearLayout.VERTICAL);

                final EditText actualPriceInput = new EditText(ItemsActivity.this);
                actualPriceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                actualPriceInput.setHint(R.string.actual_price);
                LinearLayout.LayoutParams actualPriceParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                actualPriceParams.topMargin = 0;
                actualPriceParams.leftMargin = 100;
                actualPriceParams.rightMargin = 100;
                actualPriceParams.bottomMargin = 0;
                actualPriceInput.setLayoutParams(actualPriceParams);

                container.addView(actualPriceInput);
                alertDialog.setView(container);

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String actualInput = actualPriceInput.getText().toString();
                        if(actualInput.length() == 0){
                            Toast.makeText(ItemsActivity.this, R.string.item_delete_error_message, Toast.LENGTH_LONG).show();
                            return;
                        }
                        double actual = Double.parseDouble(actualInput);
                        int actualInt = (int) (actual * 100);
                        actual =  actualInt/100.0;
                        UpdateItemRequest request = new UpdateItemRequest(itemName, estimate, actual, done);
                        Call<Void> call = ItemEndpoints.ITEM_ENDPOINTS.updateItem(gid, request);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code() == ResponseCodes.HTTP_NO_CONTENT){
                                    Toast.makeText(ItemsActivity.this, R.string.item_mark_done, Toast.LENGTH_LONG).show();
                                } else{
                                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                }
                                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ItemsActivity.this);
                                sharedPreferences.edit().putBoolean(REFRESH_KEY, true).apply();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ItemsActivity.this);
                                sharedPreferences.edit().putBoolean(REFRESH_KEY, true).apply();
                            }
                        });
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshScreen(gid, false);
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }).attachToRecyclerView(rv);
    }

    public void refreshScreen(final long gid, boolean makeAPICall) {
        if(makeAPICall) {
            Call<ItemListResponse> call = ItemEndpoints.ITEM_ENDPOINTS.getItems(gid);

            call.enqueue(new Callback<ItemListResponse>() {
                @Override
                public void onResponse(Call<ItemListResponse> call, Response<ItemListResponse> response) {
                    if (response.code() == ResponseCodes.HTTP_OK) {
                        Item[] items = response.body().getItems();
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ItemListResponse> call, Throwable t) {
                    Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }

        Call<TotalPriceResponse> call = GroupEndpoints.groupEndpoints.getGroupPriceTotal(gid);
        call.enqueue(new Callback<TotalPriceResponse>() {
            @Override
            public void onResponse(Call<TotalPriceResponse> call, Response<TotalPriceResponse> response) {
                if(response.code() == ResponseCodes.HTTP_OK){
                    totalCost.setText("$" + response.body().getTotal());
                } else {
                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<TotalPriceResponse> call, Throwable t) {
                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG);
            }
        });

        Call<ItemsCompletedResponse> itemsCompletedCall = GroupEndpoints.groupEndpoints.getItemsCompleted(gid);
        itemsCompletedCall.enqueue(new Callback<ItemsCompletedResponse>() {
            @Override
            public void onResponse(Call<ItemsCompletedResponse> call, Response<ItemsCompletedResponse> response) {
                if(response.code() == ResponseCodes.HTTP_OK){
                    itemsBought.setText(response.body().getItemsString());
                } else {
                    Toast.makeText(ItemsActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ItemsCompletedResponse> call, Throwable t) {
                Toast.makeText(ItemsActivity.this, R.string.call_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(REFRESH_KEY) && sharedPreferences.getBoolean(REFRESH_KEY, false) == true) {
            Log.d("SharedPreferences", "Refreshed");
            sharedPreferences.edit().putBoolean(REFRESH_KEY, false).apply();
            refreshScreen(gid, true);
        }
    }

    public class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolder>{

        Item[] items;
        public ItemsAdapter(Item[] items){
            this.items = items;
        }

        public void setItems(Item[] items){
            this.items = items;
        }

        @Override
        public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = ItemsActivity.this;
            LayoutInflater myInflater = LayoutInflater.from(context);

            View view = myInflater.inflate(R.layout.item, parent, false);

            ItemsViewHolder viewHolder = new ItemsViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ItemsViewHolder holder, int position) {
            Item item = items[position];
            if(item.getItemName() != null) {
                holder.itemName.setText(item.getItemName());

                String name = item.getFirstName() + " " + item.getLastName();
                holder.itemAuthor.setText(name);

                double estimate = item.getEstimate();
                int estimateInt = (int) (100 * estimate);
                String estimateString = String.valueOf(estimateInt);
                String beforeDecimal = estimateString.substring(0, estimateString.length() - 2);
                String afterDecimal = estimateString.substring(estimateString.length() - 2);
                StringBuilder estimateBuilder = new StringBuilder();
                estimateBuilder.append("$");
                estimateBuilder.append(beforeDecimal);
                estimateBuilder.append(".");
                estimateBuilder.append(afterDecimal);
                holder.estimate.setText(estimateBuilder.toString());

                Long itemId = new Long(item.getItemId());
                holder.itemView.setTag(itemId);
            }
        }

        @Override
        public int getItemCount() {
            return items.length;
        }
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder{
        public TextView itemName;
        public TextView itemAuthor;
        public TextView estimate;
        public ItemsViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_description);
            itemAuthor = (TextView) itemView.findViewById(R.id.item_author);
            estimate = (TextView) itemView.findViewById(R.id.item_estimate);
        }
    }
}
