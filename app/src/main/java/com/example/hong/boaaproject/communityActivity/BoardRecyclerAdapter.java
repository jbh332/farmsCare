package com.example.hong.boaaproject.communityActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BoardRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<BoardModel> boardModels;
    String userID, boardNum, comment;

    public BoardRecyclerAdapter(Context context, List<BoardModel> boardModels) {
        this.context = context;
        this.boardModels = boardModels;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);


        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        userID = boardModels.get(position).myNicName; // 여기서 userID는 코멘트 작성자 ( 본인 )

        ((CustomViewHolder) holder).tvNicName.setText(boardModels.get(position).userID); // 글작성자
        ((CustomViewHolder) holder).tvContent.setText(boardModels.get(position).boardContent);
        ((CustomViewHolder) holder).tvDate.setText(boardModels.get(position).boardDate);
        ((CustomViewHolder) holder).tvMyNicName.setText(boardModels.get(position).myNicName); // 본인

        Picasso.with(context)
                .load(boardModels.get(position).boardImgURL)
                .into(((CustomViewHolder) holder).ivImage);



        // 좋아요 버튼 클릭
        ((CustomViewHolder) holder).ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (true) {
                    ((CustomViewHolder) holder).ivFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                } else {
                    ((CustomViewHolder) holder).ivFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }
        });

    }

    //댓글 등록 클래스
    private void registerComment(String comment, String userID, String boardNum) {
        String commentNum, commentDate, boardComment;
        boardComment = comment;

        commentNum = String.valueOf((int) (Math.random() * 50000));

        // 현재 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA);

        commentDate = sdf.format(date);


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        Toast.makeText(context, "댓글 등록이 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "댓글 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(userID, boardComment, boardNum, commentNum, commentDate, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(commentRegisterRequest);
    }

    @Override
    public int getItemCount() {
        return boardModels.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView iv, ivFavorite, ivImage;
        TextView tvNicName, tvContent, tvFavorite, tvMyNicName, tvDate, tvCommentRegister, tvComment;
        EditText etComment;


        public CustomViewHolder(View view) {
            super(view);

            iv = view.findViewById(R.id.iv);
            ivFavorite = view.findViewById(R.id.ivFavorite);
            ivImage = view.findViewById(R.id.ivImage);
            tvNicName = view.findViewById(R.id.tvNicName);
            tvContent = view.findViewById(R.id.tvContent);
            tvFavorite = view.findViewById(R.id.tvFavorite);
            tvMyNicName = view.findViewById(R.id.tvMyNicName);
            tvComment = view.findViewById(R.id.tvComment);
            etComment = view.findViewById(R.id.etComment);
            tvCommentRegister = view.findViewById(R.id.tvCommentRegister);
            tvDate = view.findViewById(R.id.tvDate);

            // 댓글 등록
            tvCommentRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 해당 아이템뷰 인덱스 획득
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {

                        BoardModel index = boardModels.get(pos);

                        comment = etComment.getText().toString(); // 모든 아이템 뷰가 공통된 에딧텍스트..
                        boardNum = index.getBoardNum(); // 해당 인덱스의 게시글 번호를 얻어옴

                        // 댓글 서버에 등록
                        registerComment(comment, userID, boardNum);
                    }
                }
            });

            // 해당 게시글의 댓글보기로 이동
            tvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {

                        BoardModel index = boardModels.get(pos);
                        boardNum = index.getBoardNum();

                        Intent intent = new Intent(context, CommentListActivity.class);
                        intent.putExtra("boardNum", boardNum);
                        context.startActivity(intent);
                    }
                }
            });

        }
    }
}
