package com.trabajo.carlos.somefood.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabajo.carlos.somefood.Interface.ItemClickListener;
import com.trabajo.carlos.somefood.R;

/**
 * Created by Carlos Prieto on 06/09/2017.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txvName;
    public ImageView imvImage, imvFav;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        txvName = (TextView) itemView.findViewById(R.id.food_txvName);
        imvImage = (ImageView) itemView.findViewById(R.id.food_imvImage);
        imvFav = (ImageView) itemView.findViewById(R.id.food_imvFav);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }
}
