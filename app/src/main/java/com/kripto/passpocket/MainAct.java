package com.kripto.passpocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.kripto.passpocket.adapter.PasswResponse;
import com.kripto.passpocket.db.DaoSession;
import com.kripto.passpocket.db.PassStorage;
import com.kripto.passpocket.db.PassStorageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.security.MessageDigest;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAct extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.pass_list)
    RecyclerView passList;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    String outputString, outputStringFinal;

    PassStorageDao passStorageDao;
    PassPocketApp passPocketApp;
    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        getPassList();

        passPocketApp = (PassPocketApp) getApplication();
        daoSession = passPocketApp.getDaoSession();
        passStorageDao = this.daoSession.getPassStorageDao();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAct.this, AddNewAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        passList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getPassList(){
        Query query = db.collection("passwData");

        FirestoreRecyclerOptions<PasswResponse> response = new FirestoreRecyclerOptions.Builder<PasswResponse>()
                .setQuery(query, PasswResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PasswResponse, FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(FriendsHolder holder, int position, final PasswResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.tvSocialMediaName.setText(model.getSocialMedia());
                holder.tvUsername.setText(model.getUserName());
                holder.tvPassword.setText(model.getPassword());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<PassStorage> passStorages = passStorageDao.queryBuilder()
                                .where(PassStorageDao.Properties.SocialMedia.like(model.getSocialMedia()))
                                .list();

                        outputString = model.getPassword();

                        try {
                            outputStringFinal = decrypt(outputString, passStorages.get(0).getPassword());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Snackbar.make(passList, model.getSocialMedia() + ", " + model.getUserName() + ", " + outputStringFinal, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                });
            }

            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);

                return new FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        passList.setAdapter(adapter);
    }

    public class FriendsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.socialMediaName)
        TextView tvSocialMediaName;
        @BindView(R.id.username)
        TextView tvUsername;
        @BindView(R.id.password)
        TextView tvPassword;

        public FriendsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private String decrypt(String data, String password_text) throws Exception {
        SecretKeySpec key = generateKey(password_text);
        Log.d("NIKHIL", "encrypt key:" + key.toString());
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedvalue = Base64.decode(data, Base64.DEFAULT);
        byte[] decvalue = c.doFinal(decodedvalue);
        String decryptedvalue = new String(decvalue, "UTF-8");
        return decryptedvalue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

}
