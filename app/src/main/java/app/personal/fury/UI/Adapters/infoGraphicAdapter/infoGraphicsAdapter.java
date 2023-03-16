package app.personal.fury.UI.Adapters.infoGraphicAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Objects;

import app.personal.fury.R;

public class infoGraphicsAdapter extends PagerAdapter {

    private final int[] images;
    private final LayoutInflater inflater;

    public infoGraphicsAdapter(Context context, int[] images) {
        this.images = images;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((FrameLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = inflater.inflate(R.layout.item_activity_infographics, container, false);
        ImageView imageView = itemView.findViewById(R.id.infoGraphics);
        try{
            imageView.setImageResource(images[position]);
        }catch (Exception ignored){}
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}
