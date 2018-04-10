package com.trabajo.carlos.somefood;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Database.Database;
import com.trabajo.carlos.somefood.Remote.APIService;
import com.trabajo.carlos.somefood.Utilidad.MyResponse;
import com.trabajo.carlos.somefood.Utilidad.Notification;
import com.trabajo.carlos.somefood.Utilidad.Order;
import com.trabajo.carlos.somefood.Utilidad.Request;
import com.trabajo.carlos.somefood.Utilidad.Sender;
import com.trabajo.carlos.somefood.Utilidad.Token;
import com.trabajo.carlos.somefood.ViewHolders.CartAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends AppCompatActivity {

    private RecyclerView rcvListCart;
    private RecyclerView.LayoutManager layoutManager;
    private TextView txvTotalPrice;
    private Button btnPlace;

    FirebaseDatabase database;
    DatabaseReference requests;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Iniciar servicio
        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        rcvListCart = (RecyclerView) findViewById(R.id.cart_rcvListCart);
        rcvListCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvListCart.setLayoutManager(layoutManager);
        txvTotalPrice = (TextView) findViewById(R.id.cart_txvTotal);
        btnPlace = (Button) findViewById(R.id.cart_btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cart.size() > 0){

                    showAlertDialog();

                }else {

                    Toast.makeText(CartActivity.this, "Tu carrito está vacio", Toast.LENGTH_SHORT).show();

                }

            }
        });

        loadListFood();

    }

    private void showAlertDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Un último paso!");
        alertDialog.setMessage("Introduzca su dirección: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        final MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.comment_edtAddress);
        final MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.comment_edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Creamos un nuevo pedido
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txvTotalPrice.getText().toString(),
                        "0", //Status
                        edtComment.getText().toString(),
                        cart
                );

                //Lo subimos a firebase
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);

                //Borramos el carrito
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);

//                Toast.makeText(CartActivity.this, "Gracias, Pedido Realizado", Toast.LENGTH_SHORT).show();
//                finish();

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        alertDialog.show();

    }

    private void sendNotificationOrder(final String order_number) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");

        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Token serverToken = postSnapshot.getValue(Token.class);

                    //Create raw payload to send
                    Notification notification = new Notification("CarlosPrieto", "Tienes un nuevo pedido " + order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            //Solo se ejecuta si funciona
                            if (response.code() == 200) {

                                if (response.body().succes == 1) {

                                    Toast.makeText(CartActivity.this, "Gracias! Pedido ordenado", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {

                                    Toast.makeText(CartActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Log.e("---------------ERROR", t.getMessage());

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood() {

        cart = new Database(this).getCarts();

        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        rcvListCart.setAdapter(adapter);

        //Calcular el precio total
        int total = 0;

        for (Order order:cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txvTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());

        return super.onContextItemSelected(item);
    }

    private void deleteCart(int position) {

        //Borrareos el item de la lista de orders segun la posicion
        cart.remove(position);
        //Despues de eso borramos todos los viejos datos de sqlite
        new Database(this).cleanCart();
        //Y al final actualizamos los nuevos datos
        for (Order item:cart)
            new Database(this).addToCart(item);
        //Refrescamos
        loadListFood();

    }
}
