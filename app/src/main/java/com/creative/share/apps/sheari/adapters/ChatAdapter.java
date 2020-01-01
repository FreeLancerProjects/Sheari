package com.creative.share.apps.sheari.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.sheari.R;
import com.creative.share.apps.sheari.databinding.ChatMessageLeftRowBinding;
import com.creative.share.apps.sheari.databinding.ChatMessageRightRowBinding;
import com.creative.share.apps.sheari.databinding.LoadMoreRowBinding;
import com.creative.share.apps.sheari.models.MessageModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {

    private final int ITEM_MESSAGE_LEFT = 1;
    private final int ITEM_MESSAGE_RIGHT = 2;

    private final int ITEM_LOADMORE = 5;



    private List<MessageModel> messageModelList;
    private int current_user_id;
    private String chat_user_image;
    private Context context;

    public ChatAdapter(List<MessageModel> messageModelList, int current_user_id, String chat_user_image, Context context) {
        this.messageModelList = messageModelList;
        this.current_user_id = current_user_id;
        this.chat_user_image = chat_user_image;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_MESSAGE_LEFT) {

            ChatMessageLeftRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chat_message_left_row, parent, false);
            return new MsgLeftHolder(binding);

        } else if (viewType == ITEM_MESSAGE_RIGHT) {

            ChatMessageRightRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chat_message_right_row, parent, false);
            return new MsgRightHolder(binding);

        }else{

            LoadMoreRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.load_more_row, parent, false);
            return new LoadMoreHolder(binding);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MsgLeftHolder) {

            MsgLeftHolder msgLeftHolder = (MsgLeftHolder) holder;
            MessageModel messageModel = messageModelList.get(msgLeftHolder.getAdapterPosition());
            msgLeftHolder.binding.setMessageModel(messageModel);
            msgLeftHolder.binding.setEndPoint(chat_user_image);

        } else if (holder instanceof MsgRightHolder) {
            MsgRightHolder msgRightHolder = (MsgRightHolder) holder;
            MessageModel messageModel = messageModelList.get(msgRightHolder.getAdapterPosition());

            msgRightHolder.binding.setMessageModel(messageModel);


        } else if (holder instanceof LoadMoreHolder) {
            LoadMoreHolder typingHolder = (LoadMoreHolder) holder;


        }


    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MsgLeftHolder extends RecyclerView.ViewHolder {

        private ChatMessageLeftRowBinding binding;

        public MsgLeftHolder(ChatMessageLeftRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    public class MsgRightHolder extends RecyclerView.ViewHolder {
        private ChatMessageRightRowBinding binding;

        public MsgRightHolder(ChatMessageRightRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }


    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {

        private LoadMoreRowBinding binding;

        public LoadMoreHolder(LoadMoreRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }



    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel = messageModelList.get(position);

       if (messageModel == null) {

            return ITEM_LOADMORE;

        } else {


            if (messageModel.getReceiver_id().equals(String.valueOf(current_user_id))) {
                return ITEM_MESSAGE_LEFT;
            } else {


                return ITEM_MESSAGE_RIGHT;

            }
        }


    }


}
