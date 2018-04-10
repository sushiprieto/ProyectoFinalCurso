package com.trabajo.carlos.somefoodserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.trabajo.carlos.somefoodserver.Common.Common;
import com.trabajo.carlos.somefoodserver.Interface.ItemClickListener;
import com.trabajo.carlos.somefoodserver.Model.Category;
import com.trabajo.carlos.somefoodserver.Model.Food;
import com.trabajo.carlos.somefoodserver.ViewHolder.FoodViewHolder;
import com.trabajo.carlos.somefoodserver.ViewHolder.MenuViewHolder;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodListActivity extends AppCompatActivity {

    private RecyclerView rcvList;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton fabAdd;

    private RelativeLayout rootLayout;

    //Firebase
    private FirebaseDatabase db;
    private DatabaseReference foodList;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    String categoryId = "";

    private MaterialEditText edtName, edtDescription, edtPrize, edtDiscount;
    private Button btnUpload, btnSelect;

    Food newFood;

    private Uri saveUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Iniciar Firebase
        db = FirebaseDatabase.getInstance();
        foodList = db.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Iniciar vistas
        rcvList = (RecyclerView) findViewById(R.id.food_rcvList);
        rcvList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvList.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);

        fabAdd = (FloatingActionButton) findViewById(R.id.food_fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddFoodDialog();

            }
        });

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty())
            loadListFood(categoryId);

    }

    /**
     *
     */
    private void showAddFoodDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodListActivity.this);
        alertDialog.setTitle("Añade Nueva Comida");
        alertDialog.setMessage("Complete toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        edtName = add_menu_layout.findViewById(R.id.newfood_edtName);
        edtDescription = add_menu_layout.findViewById(R.id.newfood_edtDescription);
        edtPrize = add_menu_layout.findViewById(R.id.newfood_edtPrize);
        edtDiscount = add_menu_layout.findViewById(R.id.newfood_edtDiscount);
        btnUpload = add_menu_layout.findViewById(R.id.newfood_btnUpload);
        btnSelect = add_menu_layout.findViewById(R.id.newfood_btnSelect);

        //Evento del boton
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseImage();

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadImage();

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                //Creamos una nueva categoria
                if (newFood != null) {

                    foodList.push().setValue(newFood);

                    Snackbar.make(rootLayout, "Nueva Comida " + newFood.getName() + " ha sido añadida", Snackbar.LENGTH_SHORT).show();

                }

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

    /**
     * Metodo que selecciona una imagen de la galeria
     */
    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona la Imagen"), Common.PICK_IMAGE_REQUEST);

    }

    /**
     * Metodo que sube una imagen desde la galeria
     */
    private void uploadImage() {

        if (saveUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();

                    Toast.makeText(FoodListActivity.this, "Subida con éxito", Toast.LENGTH_SHORT).show();

                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //Establecemos el valor de la nueva categoria si la imagen se ha subido y podremos obtener el link de descarga
                            newFood = new Food();
                            newFood.setName(edtName.getText().toString());
                            newFood.setName(edtDescription.getText().toString());
                            newFood.setName(edtPrize.getText().toString());
                            newFood.setName(edtDiscount.getText().toString());
                            newFood.setMenuId(categoryId);
                            newFood.setImage(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mDialog.dismiss();

                    Toast.makeText(FoodListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()); //No preocuparse por este error
                    mDialog.setMessage("Subida " + progress + "%");

                }
            });

        }

    }

    private void loadListFood(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {

                viewHolder.txvFoodName.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imvFoodImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {


                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        rcvList.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            saveUri = data.getData();
            btnSelect.setText("Imagen Seleccionada!");

        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)) {

            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        } else if (item.getTitle().equals(Common.DELETE)) {

            deleteFood(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);

    }

    /**
     * Metodo que borra una comida
     * @param key
     */
    private void deleteFood(String key) {

        foodList.child(key).removeValue();

    }

    /**
     * Metodo que actualiza una comida
     * @param key
     * @param item
     */
    private void showUpdateFoodDialog(final String key, final Food item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodListActivity.this);
        alertDialog.setTitle("Editar Comida");
        alertDialog.setMessage("Complete toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        edtName = add_menu_layout.findViewById(R.id.newfood_edtName);
        edtDescription = add_menu_layout.findViewById(R.id.newfood_edtDescription);
        edtPrize = add_menu_layout.findViewById(R.id.newfood_edtPrize);
        edtDiscount = add_menu_layout.findViewById(R.id.newfood_edtDiscount);
        btnUpload = add_menu_layout.findViewById(R.id.newfood_btnUpload);
        btnSelect = add_menu_layout.findViewById(R.id.newfood_btnSelect);

        //Ponemos el nombre por defecto
        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrize.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());

        //Evento del boton
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseImage();

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeImage(item);

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                //Creamos una nueva comida

                //Actualizamos al info
                item.setName(edtName.getText().toString());
                item.setDescription(edtDescription.getText().toString());
                item.setPrice(edtPrize.getText().toString());
                item.setDiscount(edtDiscount.getText().toString());

                foodList.child(key).setValue(item);

                Snackbar.make(rootLayout, "La Comida " + item.getName() + " ha sido editada", Snackbar.LENGTH_SHORT).show();


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

    /**
     * @param item
     */
    private void changeImage(final Food item) {

        if (saveUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();

                    Toast.makeText(FoodListActivity.this, "Subida con éxito", Toast.LENGTH_SHORT).show();

                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mDialog.dismiss();

                    Toast.makeText(FoodListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()); //No preocuparse por este error
                    mDialog.setMessage("Subida " + progress + "%");

                }
            });

        }

    }
}
