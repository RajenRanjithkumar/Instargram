package com.restapi.insta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.restapi.insta.Fragments.postDetailFragment;
import com.restapi.insta.Model.Post;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public PhotoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.posts_item, parent, false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = mPosts.get(position);
        Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.postImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, ""+ holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putInt("postPosition", holder.getAdapterPosition()).apply();

                // fragment to fragment call
                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new postDetailFragment()).commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
        }
    }


}
