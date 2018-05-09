package com.example.acer.zebdashop;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by acer on 1/22/2018.
 */

public class RecyclerAdapter_discount extends RecyclerView.Adapter<RecyclerAdapter_discount.ViewHolder> {
    List<discount_class> list;
    Context context;

    public RecyclerAdapter_discount(List<discount_class> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @Override
    public RecyclerAdapter_discount.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);//
        return new RecyclerAdapter_discount.ViewHolder(view);//from here the item layout has been shown by viewholderName
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter_discount.ViewHolder holder, final int position) {
        final discount_class item = list.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice()+" شيكل ");
        holder.desc.setText(item.getDesc());
        //Glide.with(context).load(list.get(position).getImg_URI()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        Glide.with(context).load(item.getImg_URI()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, price;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name_incard);
            desc = itemView.findViewById(R.id.product_desc_incard);
            price = itemView.findViewById(R.id.product_price_incard);
            imageView = itemView.findViewById(R.id.product_image_incard);
        }
    }
}
