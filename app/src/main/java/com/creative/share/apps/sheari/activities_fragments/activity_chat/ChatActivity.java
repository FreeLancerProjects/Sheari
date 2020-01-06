package com.creative.share.apps.sheari.activities_fragments.activity_chat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.adapters.ChatAdapter;
import com.creative.share.apps.sheari.databinding.ActivityChatBinding;
import com.creative.share.apps.sheari.interfaces.Listeners;
import com.creative.share.apps.sheari.language.LanguageHelper;
import com.creative.share.apps.sheari.models.ChatUserModel;
import com.creative.share.apps.sheari.models.MessageDataModel;
import com.creative.share.apps.sheari.models.MessageModel;
import com.creative.share.apps.sheari.models.SingleMessageDataModel;
import com.creative.share.apps.sheari.models.UserModel;
import com.creative.share.apps.sheari.preferences.Preferences;
import com.creative.share.apps.sheari.remote.Api;
import com.creative.share.apps.sheari.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityChatBinding binding;
    private String lang;
    private LinearLayoutManager manager;
    private List<MessageModel> messageModelList;
    private ChatAdapter adapter;
    private Preferences preferences;
    private UserModel userModel;
    private ChatUserModel chatUserModel;
    private int current_page = 1;
    private boolean isLoading = false;
    private MediaPlayer mp;
    private boolean hasNewMsg = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase,"ar"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent != null) {
            chatUserModel = (ChatUserModel) intent.getSerializableExtra("chat_user_data");
        }
    }
    private void initView()
    {
        EventBus.getDefault().register(this);

        messageModelList = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setChatUser(chatUserModel);
        preferences.create_update_ChatUserData(this,chatUserModel);
        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.recView.setLayoutManager(manager);
        adapter = new ChatAdapter(messageModelList, userModel.getData().getId(), chatUserModel.getImage(), this);
        binding.recView.setAdapter(adapter);
        binding.recView.setHasFixedSize(true);
        binding.recView.setItemViewCacheSize(20);
        binding.recView.setDrawingCacheEnabled(true);
        binding.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    int lastItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_items = adapter.getItemCount();

                    if (lastItemPos == (total_items - 2) && !isLoading) {
                        isLoading = true;
                        messageModelList.add(0, null);
                        adapter.notifyItemInserted(0);
                        int next_page = current_page + 1;
                        loadMore(next_page);


                    }
                }
            }
        });

        removeNotificationFromBackGround();
        getChatMessages();

        binding.imageCall.setOnClickListener((v)->{
            String phone = chatUserModel.getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
            startActivity(intent);

        });


        binding.imageSend.setOnClickListener((v) -> {
            String msg = binding.edtMsgContent.getText().toString().trim();
            if (!msg.isEmpty()) {
                binding.edtMsgContent.setText("");
                sendMessage(msg);
            }

        });



    }

    private void removeNotificationFromBackGround() {

        new Handler()
                .postDelayed(() -> {
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (manager!=null)
                    {
                        manager.cancel(12352);
                    }
                },100);
    }

    private void sendMessage(String message)
    {
        try {


            Api.getService(Tags.base_url)
                    .sendChatMessage("Bearer "+userModel.getData().getToken(),chatUserModel.getId(),chatUserModel.getOrder_id(),message)
                    .enqueue(new Callback<SingleMessageDataModel>() {
                        @Override
                        public void onResponse(Call<SingleMessageDataModel> call, Response<SingleMessageDataModel> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body().isValue())
                                {
                                    hasNewMsg =  true;
                                    messageModelList.add(response.body().getData());
                                    adapter.notifyItemInserted(messageModelList.size()-1);
                                    scrollToLastPosition();
                                }else
                                    {
                                        Toast.makeText(ChatActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                    }


                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (response.code() == 500) {
                                    Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleMessageDataModel> call, Throwable t) {
                            try {
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ChatActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void getChatMessages() {
        try {


            Api.getService(Tags.base_url)
                    .getRoomMessages("Bearer "+userModel.getData().getToken(),chatUserModel.getId(), chatUserModel.getOrder_id(), 1)

                    .enqueue(new Callback<MessageDataModel>() {
                        @Override
                        public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body().isValue())
                                {
                                    messageModelList.clear();
                                    messageModelList.addAll(response.body().getInbox());
                                    adapter.notifyDataSetChanged();
                                    scrollToLastPosition();
                                }else
                                    {
                                        Toast.makeText(ChatActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                    }


                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MessageDataModel> call, Throwable t) {
                            try {
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ChatActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void loadMore(int next_page) {
        try {

            Api.getService(Tags.base_url)
                    .getRoomMessages("Bearer "+userModel.getData().getToken(),chatUserModel.getId(), chatUserModel.getOrder_id(), 1)
                    .enqueue(new Callback<MessageDataModel>() {
                        @Override
                        public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                            isLoading = false;
                            messageModelList.remove(0);
                            adapter.notifyItemRemoved(0);
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body().isValue())
                                {
                                    current_page = response.body().getPaginate().getCount();
                                    messageModelList.addAll(0, response.body().getInbox());
                                    adapter.notifyItemRangeInserted(0, response.body().getInbox().size());

                                }else
                                    {
                                        Toast.makeText(ChatActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                    }


                            } else {

                                if (response.code() == 500) {
                                    Toast.makeText(ChatActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MessageDataModel> call, Throwable t) {
                            try {
                                isLoading = false;

                                if (messageModelList.get(0) == null) {
                                    messageModelList.remove(0);
                                    adapter.notifyItemRemoved(0);
                                }
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ChatActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChatActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void scrollToLastPosition()
    {

        new Handler()
                .postDelayed(() -> binding.recView.scrollToPosition(messageModelList.size()-1),10);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNewMessage(MessageModel messageModel)
    {
        messageModelList.add(messageModel);
        scrollToLastPosition();
    }


    @Override
    public void back() {

        Back();
    }

    private void Back() {
        if (hasNewMsg)
        {
            Intent intent = getIntent();
            if (intent!=null&&hasNewMsg)
            {

                intent.putExtra("new_msg",true);
                setResult(RESULT_OK,intent);
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
        preferences.clearChatUserData(this);
    }
}
