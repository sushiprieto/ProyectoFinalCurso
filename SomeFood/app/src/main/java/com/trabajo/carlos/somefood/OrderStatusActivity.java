package com.trabajo.carlos.somefood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Utilidad.Request;
import com.trabajo.carlos.somefood.ViewHolders.OrderViewHolder;

public class OrderStatusActivity extends AppCompatActivity {

    public RecyclerView rcvListOrders;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference request;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        request = database.getReference("Requests");

        rcvListOrders = (RecyclerView) findViewById(R.id.order_rcvListOrders);
        rcvListOrders.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvListOrders.setLayoutManager(layoutManager);

        //Si iniciamos OrderStatusActivity desde el HomeActivity
        //no mandaremos un putExtra, solo hacemos el loadOrder con el telefono de Common
        if (getIntent() == null){

            loadOrders(Common.currentUser.getPhone());

        }else{

            loadOrders(getIntent().getStringExtra("userPhone"));

        }

    }

    private void loadOrders(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                request
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.txvId.setText(adapter.getRef(position).getKey());
                viewHolder.txvStatus.setText(Common.converCodeToStatus(model.getStatus()));
                viewHolder.txvPhone.setText(model.getPhone());
                viewHolder.txvAddress.setText(model.getAddress());

            }
        };

        rcvListOrders.setAdapter(adapter);

    }

}
