package com.trabajo.carlos.somefoodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabajo.carlos.somefoodserver.Common.Common;
import com.trabajo.carlos.somefoodserver.Interface.ItemClickListener;
import com.trabajo.carlos.somefoodserver.R;

/**
 * Created by Carlos Prieto on 02/10/2017.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView txvFoodName;
    public ImageView imvFoodImage;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        txvFoodName = (TextView) itemView.findViewById(R.id.food_txvName);
        imvFoodImage = (ImageView) itemView.findViewById(R.id.food_imvImage);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    /**
     * Metodo que crea un menu cuando se hace un longclick en un elemento de la lista
     *
     * @param contextMenu
     * @param view
     * @param contextMenuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Selecciona la acción");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);

    }
}

