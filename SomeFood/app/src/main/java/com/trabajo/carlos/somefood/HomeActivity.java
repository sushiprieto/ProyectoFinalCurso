package com.trabajo.carlos.somefood;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Interface.ItemClickListener;
import com.trabajo.carlos.somefood.Utilidad.Category;
import com.trabajo.carlos.somefood.Utilidad.Token;
import com.trabajo.carlos.somefood.ViewHolders.MenuViewHolder;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase mdatabase;
    private DatabaseReference mcategory;
    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    private TextView txvName;
    private RecyclerView rcvMenu;
    private RecyclerView.LayoutManager layoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Iniciar firebase
        mdatabase = FirebaseDatabase.getInstance();
        mcategory = mdatabase.getReference("Category");

        //iniciar paper
        Paper.init(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(cartIntent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Establecer el nombre para el usuario
        View headerView = navigationView.getHeaderView(0);
        txvName = (TextView) headerView.findViewById(R.id.home_txvName);
        txvName.setText(Common.currentUser.getName());

        //Cargar el menu
        rcvMenu = (RecyclerView) findViewById(R.id.home_rcvMenu);
        rcvMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvMenu.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(this))
            loadMenu();
        else {

            Toast.makeText(this, "Compruebe tu conexión", Toast.LENGTH_SHORT).show();
            return;

        }


        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false); //Falso porque este token se envia desde la app cliente
        tokens.child(Common.currentUser.getPhone()).setValue(data);

    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, mcategory) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.txvMenuName.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imvImage);

                final Category clickItem = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Cogemos el id de la categoria y lo mandamos a la actividad
                        Intent foodList = new Intent(HomeActivity.this, FoodListActivity.class);
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });

            }
        };

        rcvMenu.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Al refrescar volvemos a cargar el menu
        if (item.getItemId() == R.id.refresh)
            loadMenu();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {

            Intent cart = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cart);

        } else if (id == R.id.nav_orders) {

            Intent order = new Intent(HomeActivity.this, OrderStatusActivity.class);
            startActivity(order);

        } else if (id == R.id.nav_logout) {

            //Borramos usuario y pwd recordados
            Paper.book().destroy();

            Intent login = new Intent(HomeActivity.this, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
