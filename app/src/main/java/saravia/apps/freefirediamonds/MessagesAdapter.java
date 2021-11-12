package saravia.apps.freefirediamonds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages>messagesArrayList;

    int ITEM_SEND=1;
    int ITEM_RECEIVER=2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SEND){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_chat_layout,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_chat_layout,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages=messagesArrayList.get(position);
        if (holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            viewHolder.textView_Message.setText(messages.getMessage());
            viewHolder.textView_Time_of_message.setText(messages.getCurrentTime());
        }else
        {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.textView_Message.setText(messages.getMessage());
            viewHolder.textView_Time_of_message.setText(messages.getCurrentTime());
        }
    }


    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderID())){
            return ITEM_SEND;
        }else {
            return ITEM_RECEIVER;
        }
    }


    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    class SenderViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView_Message;
        TextView textView_Time_of_message;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_Message=itemView.findViewById(R.id.sender_message_tw);
            textView_Time_of_message=itemView.findViewById(R.id.time_of_message);
        }
    }


    class ReceiverViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView_Message;
        TextView textView_Time_of_message;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_Message=itemView.findViewById(R.id.receiver_message_tw);
            textView_Time_of_message=itemView.findViewById(R.id.time_of_message);
        }
    }

}
