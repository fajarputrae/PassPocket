package com.kripto.passpocket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kripto.passpocket.db.DaoSession;
import com.kripto.passpocket.db.PassStorage;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewAct extends AppCompatActivity {
    static String TAG = "data-firebase";

    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.etSocmed)
    EditText etSocmed;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;

    String outputString;

    PassPocketApp passPocketApp;
    DaoSession daoSession;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        ButterKnife.bind(this);

        passPocketApp = (PassPocketApp) getApplication();
        daoSession = passPocketApp.getDaoSession();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String socmedName = etSocmed.getText().toString();
                String userName = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                try {
                    outputString = encrypt(socmedName, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                PassStorage passStorage = new PassStorage();
                passStorage.setSocialMedia(socmedName);
                passStorage.setUsername(userName);
                passStorage.setPassword(password);

                daoSession.getPassStorageDao().insert(passStorage);


                Map<String, Object> passw = new HashMap<>();
                passw.put("socialMedia", socmedName);
                passw.put("username", userName);
                passw.put("password", outputString);

                db.collection("passwData").document(socmedName)
                        .set(passw)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(AddNewAct.this, MainAct.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        });


    }

    private String encrypt(String data, String password_text) throws Exception {
        SecretKeySpec key = generateKey(password_text);
        Log.d("NIKHIL", "encrypt key:" + key.toString());
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes("UTF-8"));
        String encryptedvalue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedvalue;

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
