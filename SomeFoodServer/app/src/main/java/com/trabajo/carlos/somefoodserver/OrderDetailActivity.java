package com.trabajo.carlos.somefoodserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.trabajo.carlos.somefoodserver.Common.Common;
import com.trabajo.carlos.somefoodserver.ViewHolder.OrderDetailAdapter;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView txvId, txvPhone, txvAddress, txvTotal, txvComment;

    String order_id_value = "";

    RecyclerView rcvListFoods;
    RecyclerView.LayoutManager layoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        txvId = (TextView)findViewById(R.id.orderDetail_txvId);
        txvPhone = (TextView)findViewById(R.id.orderDetail_txvPhone);
        txvAddress = (TextView)findViewById(R.id.orderDetail_txvAddress);
        txvTotal = (TextView)findViewById(R.id.orderDetail_txvTotal);
        txvComment = (TextView)findViewById(R.id.orderDetail_txvComment);

        rcvListFoods = (RecyclerView)findViewById(R.id.orderDetail_rcvListFood);
        rcvListFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvListFoods.setLayoutManager(layoutManager);

        if (getIntent() != null){

            order_id_value = getIntent().getStringExtra("OrderId");

            //Establecemos los valores
            txvId.setText(order_id_value);
            txvPhone.setText(Common.currentRequest.getPhone());
            txvAddress.setText(Common.currentRequest.getAddress());
            txvTotal.setText(Common.currentRequest.getTotal());
            txvComment.setText(Common.currentRequest.getComment());

            OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
            adapter.notifyDataSetChanged();
            rcvListFoods.setAdapter(adapter);

        }

    }
}
