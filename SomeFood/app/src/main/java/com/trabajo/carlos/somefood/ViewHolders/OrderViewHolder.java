package com.trabajo.carlos.somefood.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trabajo.carlos.somefood.Interface.ItemClickListener;
import com.trabajo.carlos.somefood.R;

/**
 * Created by Carlos Prieto on 07/09/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txvId, txvStatus, txvPhone, txvAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txvAddress = (TextView) itemView.findViewById(R.id.order_txvAddress);
        txvStatus = (TextView) itemView.findViewById(R.id.order_txvStatus);
        txvPhone = (TextView) itemView.findViewById(R.id.order_txvPhone);
        txvId = (TextView) itemView.findViewById(R.id.order_txvId);

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
