package com.example.hong.boaaproject.communityActivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hong.boaaproject.R;
import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<CommentModel> commentModels;

    public CommentRecyclerAdapter(Context context, List<CommentModel> commentModels) {
        this.context = context;
        this.commentModels = commentModels;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);


        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((CustomViewHolder) holder).tvNicName.setText(commentModels.get(position).userID);
        ((CustomViewHolder) holder).tvContent.setText(commentModels.get(position).boardComment);
        ((CustomViewHolder) holder).tvDate.setText(commentModels.get(position).boardDate);


        ((CustomViewHolder) holder).tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tvNicName, tvComment, tvDate, tvContent;
        ImageView iv;

        public CustomViewHolder(View view) {
            super(view);

            tvComment = view.findViewById(R.id.tvComment);
            tvContent = view.findViewById(R.id.tvContent);
            tvDate = view.findViewById(R.id.tvDate);
            tvNicName = view.findViewById(R.id.tvNicName);
            iv = view.findViewById(R.id.iv);
        }
    }
}
