package edu.uwp.appfactory.attendance;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    //TODO update to kotlin
    EditText mEmail, mPassword;
    Button mSignInBtn;
    TextView mCreateAccountBtn, toSignin;
    FirebaseAuth fAuth;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.editTextTextPersonName);
        mPassword = findViewById(R.id.editTextTextPersonName2);
        //mCreateAccountBtn = findViewById(R.id.button_signIn);
        mSignInBtn = findViewById(R.id.button_signIn);
        toSignin=findViewById(R.id.textView3);
        fAuth = FirebaseAuth.getInstance();
        toggleButton = findViewById(R.id.toggleButton);

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


        System.out.println(fAuth.getClass());
        toSignin.setOnClickListener(view -> {
            Intent intent= new Intent(this, Register.class);
            startActivity(intent);
        });
        mSignInBtn.setOnClickListener(view -> {
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

            if (!email.contains("rangers.uwp.edu") && !email.contains("uwp.edu")) {
                mPassword.setError("Email must be a UWP email");
                return;
            }

            // Login the user
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener
                    (new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {

                                Intent intent = new Intent(getApplicationContext(),Schedule.class);
                                startActivity(intent);
                                Toast.makeText(Login.this, "User Signed In Successfully",
                                        Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(Login.this, "Error"
                                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        });

    }


}