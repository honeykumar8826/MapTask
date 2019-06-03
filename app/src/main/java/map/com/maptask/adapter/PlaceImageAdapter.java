package map.com.maptask.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import map.com.maptask.R;
import map.com.maptask.modal.PlaceImageModal;

public class PlaceImageAdapter extends RecyclerView.Adapter<PlaceImageAdapter.ImageViewHolder> {
    private Context context;
    private List<PlaceImageModal> imageModalList;

    public PlaceImageAdapter(Context context, List<PlaceImageModal> imageModalList) {
        this.context = context;
        this.imageModalList = imageModalList;
    }

    @NonNull
    @Override
    public PlaceImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_places_on_map_layout, viewGroup, false);
        return new PlaceImageAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceImageAdapter.ImageViewHolder imageViewHolder, int i) {
        final PlaceImageModal placeImageModal = imageModalList.get(i);
        final String imgUrl = placeImageModal.getImgUrl();

        Glide.with(context)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.drawable.ic_no_image)
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
            imageView = itemView.findViewById(R.id.place_img);
        }
    }
}


