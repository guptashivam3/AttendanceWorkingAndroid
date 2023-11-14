package edu.uwp.appfactory.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    //TODO update to kotlin
    EditText mFirstName, mLastName, mEmail, mPassword, mConfirmPassword;
    Button mCreateAccountBtn;
    TextView mSignInBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ToggleButton toggleButton, toggleButtonConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        mFirstName = findViewById(R.id.firstNameField);
        mLastName = findViewById(R.id.lastNameField);
        mEmail = findViewById(R.id.editTextTextPersonName3);
        mPassword = findViewById(R.id.editTextTextPersonName6);
        mConfirmPassword = findViewById(R.id.editTextTextPersonName5);
        mCreateAccountBtn = findViewById(R.id.button_Register);
        mSignInBtn = findViewById(R.id.text_SignIn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        toggleButton = findViewById(R.id.toggleButton);
        toggleButtonConfirmPassword = findViewById(R.id.toggleButtonConfirmPassword);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    toggleButton.setButtonDrawable(R.drawable.eye_show_small);
                } else {
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    toggleButton.setButtonDrawable(R.drawable.hidden);
                }
            }
        });
        toggleButtonConfirmPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    toggleButtonConfirmPassword.setButtonDrawable(R.drawable.eye_show_small);
                } else {
                    mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    toggleButtonConfirmPassword.setButtonDrawable(R.drawable.hidden);
                }
            }
        });


        mSignInBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });

        mCreateAccountBtn.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is Required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required");
                return;
            }

            if(!email.contains("rangers.uwp.edu") && !email.contains("uwp.edu") ){
                mPassword.setError("Email must be a UWP email");
                return;
            }

            // Register the user in Firebase
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                    (new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                Toast.makeText(Register.this, "User Created",
                                        Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(Register.this, "Error"
                                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        });

    }

    public void onClick(View view)
    {

    }
}






