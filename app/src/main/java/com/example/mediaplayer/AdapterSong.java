package com.example.mediaplayer;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.viewHolder> {

    /** Var globales **/
    Context context;
    ArrayList<ModelSong> songArrayList;

    public OnItemClickListener onItemClickListener;

    /** Constructor **/

    public AdapterSong(Context context, ArrayList<ModelSong> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }



    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_song, parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.title.setText(songArrayList.get(position).getSongTitle());
        holder.artist.setText(songArrayList.get(position).getSongArtist());

        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_music_note_24)
                .placeholder(R.drawable.ic_music_note_24);
//      options de glide
        Context context = holder.cover.getContext();
        Uri imgUri = songArrayList.get(position).getSongCover();


        Glide.with(context)
                .load(imgUri)
                .apply(options)
                .fitCenter()
                .override(150,150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cover);

    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView cover;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            artist = itemView.findViewById(R.id.tvArtist);
            cover = itemView.findViewById(R.id.ivCover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int pos, View v);
    }
}
