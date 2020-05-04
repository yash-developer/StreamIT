package com.example.streamitv3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {
    TextView signin, signup, signin_signup_txt, forgot_password;
    CircleImageView circleImageView;
    Button signin_signup_btn;
    EditText uname, fname, lname, email, password, confirmpassword;
    TextInputLayout unametxt, fnametxt, lnametxt, cpasstext;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;


    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // redirect if user is not null
        if(firebaseUser != null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        auth = FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin.setTextColor(Color.parseColor("#FFFFFF"));
                signin.setBackgroundColor(Color.parseColor("#FF2729C3"));
                signup.setTextColor(Color.parseColor("#FF2729C3"));
                signup.setBackgroundResource(R.drawable.bordershape);
                circleImageView.setImageResource(R.drawable.sigin_boy_img);
                signin_signup_txt.setText("Sign In");
                signin_signup_btn.setText("Sign In");
                forgot_password.setVisibility(View.VISIBLE);
                unametxt.setVisibility(View.GONE);
                fnametxt.setVisibility(View.GONE);
                lnametxt.setVisibility(View.GONE);
                cpasstext.setVisibility(View.GONE);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup.setTextColor(Color.parseColor("#FFFFFF"));
                signup.setBackgroundColor(Color.parseColor("#FF2729C3"));
                signin.setTextColor(Color.parseColor("#FF2729C3"));
                signin.setBackgroundResource(R.drawable.bordershape);
                circleImageView.setImageResource(R.drawable.sigup_boy_img);
                signin_signup_txt.setText("Sign Up");
                signin_signup_btn.setText("Sign Up");
                forgot_password.setVisibility(View.INVISIBLE);
                unametxt.setVisibility(View.VISIBLE);
                fnametxt.setVisibility(View.VISIBLE);
                lnametxt.setVisibility(View.VISIBLE);
                cpasstext.setVisibility(View.VISIBLE);
            }
        });
        signin_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signin_signup_btn.getText().toString().equals("Sign In")) {
                    progressDialog = new ProgressDialog(SignInActivity.this);
                    progressDialog.setMessage("Signing In...");
                    progressDialog.show();

                    String str_email = email.getText().toString();
                    String str_password = password.getText().toString();

                    if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                        Toast.makeText(SignInActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.signInWithEmailAndPassword(str_email, str_password)
                                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(
                                                    auth.getCurrentUser().getUid());

                                            databaseReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        } else{
                                            progressDialog.dismiss();
                                            Toast.makeText(SignInActivity.this, "Email or Password incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                } else if (signin_signup_btn.getText().toString().equals("Sign Up")) {
                    progressDialog = new ProgressDialog(SignInActivity.this);
                    progressDialog.setMessage("Signing Up...");
                    progressDialog.show();

                    String str_username = uname.getText().toString();
                    String str_firstname = fname.getText().toString();
                    String str_lastname = lname.getText().toString();
                    String str_emailid = email.getText().toString();
                    String str_password = password.getText().toString();
                    String str_cpassword = confirmpassword.getText().toString();

                    if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_firstname) || TextUtils.isEmpty(str_lastname) ||
                            TextUtils.isEmpty(str_emailid) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_cpassword)) {
                        Toast.makeText(SignInActivity.this, "All Fields are required", Toast.LENGTH_SHORT).show();
                    } else if (str_password.length() < 6) {
                        Toast.makeText(SignInActivity.this, "Password should atleast have 6 characters", Toast.LENGTH_SHORT).show();
                    } else if (!(str_password.equals(str_cpassword))) {
                        Toast.makeText(SignInActivity.this, "Password and Confirm Password Fields should have same text", Toast.LENGTH_SHORT).show();
                    } else {
                        SignUp(str_username, str_firstname, str_lastname, str_emailid, str_password);
                    }
                }
            }
        });
    }

    private void SignUp(String str_username, String str_firstname, String str_lastname, String str_emailid, String str_password) {
        auth.createUserWithEmailAndPassword(str_emailid, str_password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", str_username.toLowerCase());
                    hashMap.put("firstname", str_firstname);
                    hashMap.put("lastname", str_lastname);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/streamitv3.appspot.com/o/placeholder.png?alt=media&token=cc56ea21-73ba-4133-b186-42fc5ac10b5c");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "You cannot register with this email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        uname = findViewById(R.id.uname);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        email = findViewById(R.id.email);
        unametxt = findViewById(R.id.unametext);
        fnametxt = findViewById(R.id.fnametext);
        lnametxt = findViewById(R.id.lnametext);
        cpasstext = findViewById(R.id.cpasswordtext);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.cpassword);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        signin_signup_txt = findViewById(R.id.signin_signup_txt);
        forgot_password = findViewById(R.id.forgot_password);
        circleImageView = findViewById(R.id.circleImageView);
        signin_signup_btn = findViewById(R.id.signin_signup_btn);
        unametxt.setVisibility(View.GONE);
        fnametxt.setVisibility(View.GONE);
        lnametxt.setVisibility(View.GONE);
        cpasstext.setVisibility(View.GONE);
    }


}
