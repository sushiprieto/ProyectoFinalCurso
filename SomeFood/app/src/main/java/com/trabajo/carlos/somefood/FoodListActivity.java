package com.trabajo.carlos.somefood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Database.Database;
import com.trabajo.carlos.somefood.Interface.ItemClickListener;
import com.trabajo.carlos.somefood.Utilidad.Food;
import com.trabajo.carlos.somefood.ViewHolders.FoodViewHolder;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    private RecyclerView rcvLista;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseDatabase mdatabase;
    DatabaseReference mfoodList;

    String categoryId = "";

    //Funcionalidad de busqueda
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar msbSearchBar;

    //Favoritos
    Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        mdatabase = FirebaseDatabase.getInstance();
        mfoodList = mdatabase.getReference("Foods");

        //Local DB
        localDB = new Database(this);

        rcvLista = (RecyclerView) findViewById(R.id.food_rcvList);
        rcvLista.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rcvLista.setLayoutManager(layoutManager);

        //Recogemos el id
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null){

            if (Common.isConnectedToInternet(getBaseContext()))
                loadListFood(categoryId);
            else{

                Toast.makeText(FoodListActivity.this, "Compruebe tu conexión", Toast.LENGTH_SHORT).show();
                return;

            }

        }

        //Busqueda
        msbSearchBar = (MaterialSearchBar) findViewById(R.id.food_msbSearchBar);
        msbSearchBar.setHint("Introduzca la comida");

        loadSuggest();

        //Le indicamos cual es la lista de sugeridos
        msbSearchBar.setLastSuggestions(suggestList);
        msbSearchBar.setCardViewElevation(10);
        msbSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //Cuando el usuario introduzca el texto cambiaremos la lista
                List<String> suggest = new ArrayList<String>();

                for (String search:suggestList){

                    if (search.toLowerCase().contains(msbSearchBar.getText().toLowerCase()))
                        suggest.add(search);

                }

                msbSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        msbSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                //Cuando la barra de busqueda este cerrada restauramos el adaptador original
                if (!enabled)
                    rcvLista.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                //Cuando la busqueda termine mostramos el resultado del adaptador de busqueda
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text){

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                mfoodList.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {

                viewHolder.txvName.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imvImage);

                final Food local = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent foodDetail = new Intent(FoodListActivity.this, FoodDetailActivity.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);

                    }
                });

            }
        };

        rcvLista.setAdapter(searchAdapter);

    }

    private void loadSuggest() {

        mfoodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Food item = postSnapshot.getValue(Food.class);

                    //Añadimos el nombre de la comida a la lista de sugeridos
                    suggestList.add(item.getName());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class, mfoodList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.txvName.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imvImage);

                //Añadir a favoritos
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.imvFav.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click para cambiar el estado de favorito
                viewHolder.imvFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!localDB.isFavorite(adapter.getRef(position).getKey())){

                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.imvFav.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodListActivity.this, "" + model.getName() + " ha sido añadida a FAVORITOS", Toast.LENGTH_SHORT).show();

                        }else {

                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.imvFav.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodListActivity.this, "" + model.getName() + " ha sido eliminada de FAVORITOS", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                final Food local = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent foodDetail = new Intent(FoodListActivity.this, FoodDetailActivity.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);

                    }
                });

            }
        };

        rcvLista.setAdapter(adapter);

    }
}
