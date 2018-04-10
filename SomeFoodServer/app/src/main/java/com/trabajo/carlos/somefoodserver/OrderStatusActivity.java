package com.trabajo.carlos.somefoodserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.trabajo.carlos.somefoodserver.Common.Common;
import com.trabajo.carlos.somefoodserver.Interface.ItemClickListener;
import com.trabajo.carlos.somefoodserver.Model.MyResponse;
import com.trabajo.carlos.somefoodserver.Model.Notification;
import com.trabajo.carlos.somefoodserver.Model.Request;
import com.trabajo.carlos.somefoodserver.Model.Sender;
import com.trabajo.carlos.somefoodserver.Model.Token;
import com.trabajo.carlos.somefoodserver.Remote.APIService;
import com.trabajo.carlos.somefoodserver.ViewHolder.OrderViewHolder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusActivity extends AppCompatActivity {

    private RecyclerView rcvListOrders;
    public RecyclerView.LayoutManager layoutManager;

    private MaterialSpinner spnStatus;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Service
        mService = Common.getFCMClient();

        rcvListOrders = (RecyclerView) findViewById(R.id.order_rcvListOrders);
        rcvListOrders.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvListOrders.setLayoutManager(layoutManager);

        loadOrders();

    }

    private void loadOrders() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {

                viewHolder.txvId.setText(adapter.getRef(position).getKey());
                viewHolder.txvStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txvPhone.setText(model.getPhone());
                viewHolder.txvAddress.setText(model.getAddress());

                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));

                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        deleteOrder(adapter.getRef(position).getKey());

                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent orderDetail = new Intent(OrderStatusActivity.this, OrderDetailActivity.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);

                    }
                });

                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent tracking = new Intent(OrderStatusActivity.this, TrackingOrderActivity.class);
                        Common.currentRequest = model;
                        startActivity(tracking);

                    }
                });

                /*viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        if (!isLongClick){

                            Intent tracking = new Intent(OrderStatusActivity.this, TrackingOrderActivity.class);
                            Common.currentRequest = model;
                            startActivity(tracking);

                        }
                        *//*
                        Comento esto porque si usamos longclick no podemos mostrar el contextmenu
                        Asi que intentare buscar otra forma para ver el detailorder *//*

                        else {

                            Intent orderDetail = new Intent(OrderStatusActivity.this, OrderDetailActivity.class);
                            Common.currentRequest = model;
                            orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                            startActivity(orderDetail);

                        }

                    }
                });*/

            }
        };

        adapter.notifyDataSetChanged();
        rcvListOrders.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){

            deleteOrder(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {

        requests.child(key).removeValue();

        adapter.notifyDataSetChanged();

    }

    private void showUpdateDialog(String key, final Request item) {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OrderStatusActivity.this);
        alertBuilder.setTitle("Actualizar Pedido");
        alertBuilder.setMessage("Por favor elija el estado");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spnStatus = (MaterialSpinner)view.findViewById(R.id.update_spnStatus);
        spnStatus.setItems("Pedido", "En el camino", "Enviado");

        alertBuilder.setView(view);

        final String localKey = key;

        alertBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                item.setStatus(String.valueOf(spnStatus.getSelectedIndex()));

                requests.child(localKey).setValue(item);

                //Lo añadimos para actualizar el tamaño del item
                adapter.notifyDataSetChanged();

                sendOrderStatusToUser(localKey, item);

            }
        });
        alertBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        alertBuilder.show();

    }

    private void sendOrderStatusToUser(final String key, final Request item) {

        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){

                    Token token = postSnapShot.getValue(Token.class);

                    //Make raw payload
                    Notification notification = new Notification("CarlosPrieto", "Tu pedido: " + key + " ha sido actualizado");

                    Sender content = new Sender(token.getToken(), notification);

                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            if (response.body().succes == 1){

                                Toast.makeText(OrderStatusActivity.this, "El pedido se ha actualizado", Toast.LENGTH_SHORT).show();

                            }else{

                                Toast.makeText(OrderStatusActivity.this, "El pedido se ha actualizado pero falló al enviar la notificación", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
