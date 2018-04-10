package com.trabajo.carlos.somefoodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trabajo.carlos.somefoodserver.Interface.ItemClickListener;
import com.trabajo.carlos.somefoodserver.R;

/**
 * Created by Carlos Prieto on 05/10/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txvId, txvStatus, txvPhone, txvAddress;
    public Button btnEdit, btnRemove, btnDetail, btnDirection;

    //private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txvAddress = (TextView) itemView.findViewById(R.id.order_txvAddress);
        txvStatus = (TextView) itemView.findViewById(R.id.order_txvStatus);
        txvPhone = (TextView) itemView.findViewById(R.id.order_txvPhone);
        txvId = (TextView) itemView.findViewById(R.id.order_txvId);
        btnEdit = itemView.findViewById(R.id.order_btnEdit);
        btnRemove = itemView.findViewById(R.id.order_btnRemove);
        btnDetail = itemView.findViewById(R.id.order_btnDetail);
        btnDirection = itemView.findViewById(R.id.order_btnDirection);

        //itemView.setOnClickListener(this);

        //itemView.setOnCreateContextMenuListener(this);

    }

    /*public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Selecciona la Acción");
        contextMenu.add(0, 0, getAdapterPosition(), "Actualizar");
        contextMenu.add(0, 1, getAdapterPosition(), "Borrar");

    }*/


}