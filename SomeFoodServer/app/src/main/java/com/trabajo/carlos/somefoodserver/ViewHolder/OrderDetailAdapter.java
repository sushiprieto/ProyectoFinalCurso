package com.trabajo.carlos.somefoodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trabajo.carlos.somefoodserver.Model.Order;
import com.trabajo.carlos.somefoodserver.R;

import java.util.List;

/**
 * Created by Carlos Prieto on 25/10/2017.
 */

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView txvName, txvQuantity, txvPrice, txvDiscount;

    public MyViewHolder(View itemView) {

        super(itemView);
        txvName = (TextView)itemView.findViewById(R.id.orderDetail_txvProductName);
        txvQuantity = (TextView)itemView.findViewById(R.id.orderDetail_txvProductQuantity);
        txvPrice = (TextView)itemView.findViewById(R.id.orderDetail_txvProductPrice);
        txvDiscount = (TextView)itemView.findViewById(R.id.orderDetail_txvProductDiscount);

    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Order order = myOrders.get(position);
        holder.txvName.setText(String.format("Nombre : %s", order.getProductName()));
        holder.txvQuantity.setText(String.format("Cantidad : %s", order.getQuantity()));
        holder.txvPrice.setText(String.format("Precio : %s", order.getPrice()));
        holder.txvDiscount.setText(String.format("Descuento : %s", order.getDiscount()));

    }

    @Override
    public int getItemCount() {

        return myOrders.size();

    }
}
