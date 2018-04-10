package com.trabajo.carlos.somefood;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.trabajo.carlos.somefood.Common.Common;
import com.trabajo.carlos.somefood.Database.Database;
import com.trabajo.carlos.somefood.Utilidad.Food;
import com.trabajo.carlos.somefood.Utilidad.Order;
import com.trabajo.carlos.somefood.Utilidad.Rating;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetailActivity extends AppCompatActivity implements View.OnClickListener, RatingDialogListener{

    private TextView txvFood, txvPrice, txvDescription;
    private ImageView imvImage;
    private CollapsingToolbarLayout ctlLayout;
    private FloatingActionButton fabCart, fabRating;
    private ElegantNumberButton enbButton;
    private RatingBar rtbRating;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTbl = database.getReference("Rating");

        txvFood = (TextView) findViewById(R.id.detail_txvFood);
        txvPrice = (TextView) findViewById(R.id.detail_txvPrice);
        txvDescription = (TextView) findViewById(R.id.detail_txvDescription);
        ctlLayout = (CollapsingToolbarLayout) findViewById(R.id.detail_ctlCollapsing);
        imvImage = (ImageView) findViewById(R.id.detail_imvComida);
        fabCart = (FloatingActionButton) findViewById(R.id.detail_fabCart);
        enbButton = (ElegantNumberButton) findViewById(R.id.detail_enbCant);
        rtbRating = (RatingBar) findViewById(R.id.detail_rtbRating);
        fabRating = (FloatingActionButton) findViewById(R.id.detail_fabRating);

        fabCart.setOnClickListener(this);
        enbButton.setOnClickListener(this);
        fabRating.setOnClickListener(this);

        ctlLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        ctlLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Recoger el id de la comida
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()){

            if (Common.isConnectedToInternet(getBaseContext())){

                getDetailFood(foodId);
                getRatingFood(foodId);

            } else{

                Toast.makeText(FoodDetailActivity.this, "Compruebe tu conexión", Toast.LENGTH_SHORT).show();
                return;

            }

        }

    }

    private void getRatingFood(String foodId) {

        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {

            int count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;

                }

                if (count != 0){

                    float average = sum / count;
                    rtbRating.setRating(average);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getDetailFood(final String foodId) {

        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(imvImage);

                ctlLayout.setTitle(currentFood.getName());

                txvPrice.setText(currentFood.getPrice());
                txvFood.setText(currentFood.getName());
                txvDescription.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.detail_enbCant:


                break;
            case R.id.detail_fabCart:

                new Database (getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        enbButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));

                Toast.makeText(FoodDetailActivity.this, "Añadido al Carrito", Toast.LENGTH_SHORT).show();
                break;
            case R.id.detail_fabRating:

                showRatingDialog();
                break;

        }

    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Enviar")
                .setNegativeButtonText("Cancelar")
                .setNoteDescriptions(Arrays.asList("Muy Mala", "No Muy Buena", "Buena", "Muy Buena", "Buenísima"))
                .setDefaultRating(1)
                .setTitle("Valora esta comida")
                .setDescription("Por favor danos tu opinión")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Por favor escribe tu comentario")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetailActivity.this)
                .show();

    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {

        //Cogemos el rating  y lo subimos a firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(), foodId, String.valueOf(value), comments);

        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()){

                    //Borramos el valor antiguo
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //Actualizamos el nuevo valor
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }else{

                    //Actualizamos el nuevo valor
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }

                Toast.makeText(FoodDetailActivity.this, "Gracias por valorarla!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
