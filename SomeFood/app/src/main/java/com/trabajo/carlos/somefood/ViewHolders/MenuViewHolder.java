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

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txvMenuName;
    public ImageView imvImage;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        txvMenuName = (TextView) itemView.findViewById(R.id.menu_txvName);
        imvImage = (ImageView) itemView.findViewById(R.id.menu_imvImage);

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
