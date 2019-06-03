package map.com.maptask.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import map.com.maptask.R;
import map.com.maptask.modal.TrendingImageModal;

public class TrendingImageAdapter extends RecyclerView.Adapter<TrendingImageAdapter.ImageViewHolder> {
    private Context context;
    private List<TrendingImageModal> imageModalList;

    public TrendingImageAdapter(Context context, List<TrendingImageModal> imageModalList) {
        this.context = context;
        this.imageModalList = imageModalList;
    }

    @NonNull
    @Override
    public TrendingImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trending_image_layout, viewGroup, false);
        return new TrendingImageAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingImageAdapter.ImageViewHolder imageViewHolder, int i) {
        final TrendingImageModal newsInfoModal = imageModalList.get(i);
        final String imgUrl = newsInfoModal.getImgUrl();

        Glide.with(context)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return imageModalList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_img);
        }
    }
}


