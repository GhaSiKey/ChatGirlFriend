package com.gaoshiqi.chatgirlfriend.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaoshiqi.chatgirlfriend.R;
import com.gaoshiqi.chatgirlfriend.bean.Msg;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Msg> data;
    private Context context;

    public MyAdapter(List<Msg> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.recycleview_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Msg message = data.get(position);
        if (message.getType() == Msg.MessageType.GPT) {
            holder.leftContent.setText(message.getContent());
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
        } else {
            holder.rightContent.setText(message.getContent());
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout leftLayout;
        private LinearLayout rightLayout;
        private TextView leftContent;
        private TextView rightContent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.left_layout);
            rightLayout = itemView.findViewById(R.id.right_layout);
            leftContent = itemView.findViewById(R.id.left);
            rightContent = itemView.findViewById(R.id.right);
        }
    }
}
