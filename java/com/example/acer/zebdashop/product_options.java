package com.example.acer.zebdashop;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class product_options extends Fragment {
    private AdView mAdView;
    ProgressDialog dialog;
    StorageReference storageReference;
    DatabaseReference databaseReference, databaseReferenceUpdate;
    Uri uri;
    Bitmap photo;

    public product_options() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_options, container, false);

        MobileAds.initialize(getActivity(), "ca-app-pub-1151520966182945/4105626545");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (check_connection()) {
            dialog = new ProgressDialog(getActivity());
            view.findViewById(R.id.add_product).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Add_Product_Or_Discount("إضافة سلعة جديدة", "product_class");
                }
            });
            view.findViewById(R.id.update_product).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Update_Product_Methode();
                }
            });
            view.findViewById(R.id.add_discount).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Add_Product_Or_Discount("إضافة عرض جديد", "discount_class");
                }
            });
            view.findViewById(R.id.update_discount).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Update_Discount_Methode();
                }
            });
            view.findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log_Out();
                }
            });
        } else {
            Toast.makeText(getActivity(), "الرجاء الاتصال بالانترنت.", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public boolean check_connection() {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                isConnected = true;
            }
        }
        return isConnected;
    }

    public void Update_Product_Methode() {
        final Dialog dilog = new Dialog(getActivity());
        dilog.setContentView(R.layout.update_product_dialog);
        dilog.setCancelable(true);
        dilog.setTitle("تعديل سلعة");
        dilog.show();
        if (MainActivity.arrayList_product.size() != 0) {
            String products[] = new String[MainActivity.arrayList_product.size()];
            for (int i = 0; i < MainActivity.arrayList_product.size(); ++i) {
                products[i] = MainActivity.arrayList_product.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, products);
            AutoCompleteTextView textView = dilog.findViewById(R.id.autoCompleteTextView);
            textView.setAdapter(adapter);
        }
        dilog.findViewById(R.id.Update_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_class updated_prooduct = null;
                String selected_product = ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).getText().toString();
                for (int i = 0; i < MainActivity.arrayList_product.size(); ++i) {
                    if (selected_product.equalsIgnoreCase(MainActivity.arrayList_product.get(i).getName())) {
                        updated_prooduct = MainActivity.arrayList_product.get(i);
                        break;
                    }
                }
                try {
                    databaseReferenceUpdate = FirebaseDatabase.getInstance().
                            getReference("product_class").child(updated_prooduct.getProduct_id() + "");
                    String new_name = ((EditText) dilog.findViewById(R.id.New_product_name)).getText().toString();
                    String new_price = ((EditText) dilog.findViewById(R.id.New_product_price)).getText().toString();
                    String new_desc = ((EditText) dilog.findViewById(R.id.New_product_desc)).getText().toString();
                    if (new_name.isEmpty() || new_desc.isEmpty() || new_price.isEmpty()) {
                        Toast.makeText(dilog.getContext(), "جميع الحقول مطلوبة.", Toast.LENGTH_SHORT).show();
                    } else {
                        product_class std = new product_class(new_name, new_desc, new_price, updated_prooduct.getImg_URI(), updated_prooduct.getProduct_id());
                        databaseReferenceUpdate.setValue(std);
                        Toast.makeText(dilog.getContext(), "تم التعديل بنجاح.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(dilog.getContext(), "السلعة غير موجودة.", Toast.LENGTH_SHORT).show();
                }
                ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).setText("");
                ((EditText) dilog.findViewById(R.id.New_product_price)).setText("");
                ((EditText) dilog.findViewById(R.id.New_product_desc)).setText("");
                ((EditText) dilog.findViewById(R.id.New_product_name)).setText("");
            }
        });
        dilog.findViewById(R.id.Delete_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_class updated_prooduct = null;
                String selected_product = ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).getText().toString();
                for (int i = 0; i < MainActivity.arrayList_product.size(); ++i) {
                    if (selected_product.equalsIgnoreCase(MainActivity.arrayList_product.get(i).getName())) {
                        updated_prooduct = MainActivity.arrayList_product.get(i);
                        break;
                    }
                }
                try {
                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance()
                            .getReference("product_class").child(updated_prooduct.getProduct_id());
                    databaseReference3.removeValue();
                    FirebaseStorage.getInstance().getReferenceFromUrl(updated_prooduct.getImg_URI()).delete();
                    Toast.makeText(dilog.getContext(), "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(dilog.getContext(), "السلعة غير موجودة.", Toast.LENGTH_SHORT).show();
                }
                ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).setText("");
            }
        });
    }

    public void Update_Discount_Methode() {

        final Dialog dilog = new Dialog(getActivity());
        dilog.setContentView(R.layout.update_product_dialog);
        dilog.setCancelable(true);
        dilog.setTitle("تعديل عرض");
        dilog.show();
        if (MainActivity.arrayList_discount.size() != 0) {
            String products[] = new String[MainActivity.arrayList_discount.size()];
            for (int i = 0; i < MainActivity.arrayList_discount.size(); ++i) {
                products[i] = MainActivity.arrayList_discount.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, products);
            AutoCompleteTextView textView = dilog.findViewById(R.id.autoCompleteTextView);
            textView.setAdapter(adapter);
        }
        dilog.findViewById(R.id.Update_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discount_class updated_prooduct = null;
                String selected_product = ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).getText().toString();
                for (int i = 0; i < MainActivity.arrayList_discount.size(); ++i) {
                    if (selected_product.equalsIgnoreCase(MainActivity.arrayList_discount.get(i).getName())) {
                        updated_prooduct = MainActivity.arrayList_discount.get(i);
                        break;
                    }
                }
                try {
                    databaseReferenceUpdate = FirebaseDatabase.getInstance().
                            getReference("discount_class").child(updated_prooduct.getDiscount_id() + "");
                    String new_name = ((EditText) dilog.findViewById(R.id.New_product_name)).getText().toString();
                    String new_price = ((EditText) dilog.findViewById(R.id.New_product_price)).getText().toString();
                    String new_desc = ((EditText) dilog.findViewById(R.id.New_product_desc)).getText().toString();
                    if (new_name.isEmpty() || new_desc.isEmpty() || new_price.isEmpty()) {
                        Toast.makeText(dilog.getContext(), "جميع الحقول مطلوبة.", Toast.LENGTH_SHORT).show();
                    } else {
                        discount_class std = new discount_class(new_name, new_desc, new_price, updated_prooduct.getImg_URI(), updated_prooduct.getDiscount_id());
                        databaseReferenceUpdate.setValue(std);
                        Toast.makeText(dilog.getContext(), "تم التعديل بنجاح.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(dilog.getContext(), "السلعة غير موجودة.", Toast.LENGTH_SHORT).show();
                }
                ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).setText("");
                ((EditText) dilog.findViewById(R.id.New_product_price)).setText("");
                ((EditText) dilog.findViewById(R.id.New_product_desc)).setText("");
                ((EditText) dilog.findViewById(R.id.New_product_name)).setText("");
            }
        });
        dilog.findViewById(R.id.Delete_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discount_class updated_prooduct = null;
                String selected_product = ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).getText().toString();
                for (int i = 0; i < MainActivity.arrayList_discount.size(); ++i) {
                    if (selected_product.equalsIgnoreCase(MainActivity.arrayList_discount.get(i).getName())) {
                        updated_prooduct = MainActivity.arrayList_discount.get(i);
                        break;
                    }
                }
                try {
                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance()
                            .getReference("discount_class").child(updated_prooduct.getDiscount_id());
                    databaseReference3.removeValue();
                    FirebaseStorage.getInstance().getReferenceFromUrl(updated_prooduct.getImg_URI()).delete();
                    Toast.makeText(dilog.getContext(), "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(dilog.getContext(), "السلعة غير موجودة.", Toast.LENGTH_SHORT).show();
                }
                ((AutoCompleteTextView) dilog.findViewById(R.id.autoCompleteTextView)).setText("");
            }
        });
    }

    public void Log_Out() {
        MainActivity.editor.clear().commit();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    ////////////////////////////////////////////////
    public void Add_Product_Or_Discount(String Dialog_Title, final String Class_Name) {
        final Dialog dilog = new Dialog(getActivity());
        dilog.setContentView(R.layout.add_product_dialog);
        dilog.setCancelable(true);
        dilog.setTitle(Dialog_Title);
        dilog.show();
        dilog.findViewById(R.id.add_New_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.setMessage("الرجاء الانتظار يتم الان رفع السلعة...");
                    databaseReference = FirebaseDatabase.getInstance().getReference(Class_Name);
                    storageReference = FirebaseStorage.getInstance().getReference();
                    final String id = databaseReference.push().getKey();
                    final String product_name = ((EditText) dilog.findViewById(R.id.product_name)).getText().toString();
                    final String product_price = ((EditText) dilog.findViewById(R.id.product_price)).getText().toString();
                    final String product_desc = ((EditText) dilog.findViewById(R.id.product_desc)).getText().toString();
                    if (uri == null || product_name.isEmpty() || product_desc.isEmpty() || product_price.isEmpty()) {
                        Toast.makeText(dilog.getContext(), "جميع الحقول مطلوبة مع صورة.", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.show();
                        StorageReference filepath = storageReference.child("images/" + UUID.randomUUID().toString());
                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                dialog.dismiss();
                                if (Class_Name.equals("product_class")) {
                                    product_class my_proudct_class = new product_class(product_name,
                                            product_desc, product_price, taskSnapshot.getDownloadUrl().toString(), id);
                                    databaseReference.child(id).setValue(my_proudct_class);
                                } else {
                                    discount_class my_proudct_class = new discount_class(product_name,
                                            product_desc, product_price, taskSnapshot.getDownloadUrl().toString(), id);
                                    databaseReference.child(id).setValue(my_proudct_class);
                                }
                                Toast.makeText(dilog.getContext(), "تم إضافة السلعة بنجاح.", Toast.LENGTH_SHORT).show();
                                ((EditText) dilog.findViewById(R.id.product_name)).setText("");
                                ((EditText) dilog.findViewById(R.id.product_price)).setText("");
                                ((EditText) dilog.findViewById(R.id.product_desc)).setText("");
                                uri = null;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(dilog.getContext(), "حدث خطأ يجب عليك تحديث play services", Toast.LENGTH_LONG).show();
                                dilog.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        });
        dilog.findViewById(R.id.capture_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 100);
            }
        });
        dilog.findViewById(R.id.upload_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 71);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 100) {
            photo = (Bitmap) data.getExtras().get("data");
            photo = resizeImage(photo);
            uri = getImageUri(getContext(), photo);
            Toast.makeText(getActivity(), "تم اختيار الصورة", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_OK && requestCode == 71) {
            uri = data.getData();
            Toast.makeText(getActivity(), "تم اختيار الصورة", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap resizeImage(Bitmap photo) {
        Bitmap bitmap = Bitmap.createScaledBitmap(photo, (int) (photo.getWidth() * 0.8), (int) (photo.getHeight() * 0.8), true);
        return bitmap;
    }
}

