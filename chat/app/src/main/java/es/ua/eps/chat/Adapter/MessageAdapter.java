package es.ua.eps.chat.Adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import es.ua.eps.chat.Message.Message;
import es.ua.eps.chat.R;

/*
    Se implementa un adaptador personalizado para el RecyclerView
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private int layout;
    private onItemClickListener itemClickListener;

    public MessageAdapter(List<Message> message, int layout, onItemClickListener listener) {
        this.messages = message;
        this.layout = layout;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.bind(messages.get(position), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nickname;
        private TextView content;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.msgNickname);
            content = itemView.findViewById(R.id.msgContent);
            layout = itemView.findViewById(R.id.messageItem);
        }

        public void bind(final Message message, final onItemClickListener listener) {
            nickname.setText(message.getNickname() + ":");
            content.setText(message.getContent());
            layout.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.corner_radius));
            GradientDrawable drawable = (GradientDrawable) layout.getBackground();
            drawable.setColor(message.getColor());
            itemView.setOnClickListener(v -> listener.onItemClick(message, getAdapterPosition()));
        }
    }

    public interface onItemClickListener {
        void onItemClick(Message message, int position);
    }
}
