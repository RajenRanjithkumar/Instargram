package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginBt;
    private TextView registerUser, forgotPassword;
    private FirebaseAuth mAuth;
    private String m_Text = "";
    private DatabaseReference mRootref;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    public  String key = "842795394923-petko96m8vfev49uni0oaph5lvcu9f1s.apps.googleusercontent.com";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBt = findViewById(R.id.loginButton);
        registerUser = findViewById(R.id.registerUser);
        forgotPassword = findViewById(R.id.forgotPassword);
        signInButton = findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();
        mRootref = FirebaseDatabase.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(key)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }

            }
        });



        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){

                    Toast.makeText(getApplicationContext(), "Empty Credentials", Toast.LENGTH_SHORT).show();

                }else{

                        loginUser(txt_email,txt_password);
                }

            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Reset Password?");

                // Set up the input
                final EditText input = new EditText(LoginActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                input.setHint("Enter your email");
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        resetPassword(m_Text);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();



            }
        });





    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //handleSignInResult(task);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(), account);

            } catch (ApiException e) {
                e.printStackTrace();
            }


        }
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            FirebaseUser user = mAuth.getCurrentUser();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("username", account.getDisplayName());
                            map.put("name", account.getDisplayName());
                            map.put("email", account.getEmail());
                            map.put("id", user.getUid());
                            map.put("bio","Testing");
                            map.put("imageurl", "default");

                            Toast.makeText(getApplicationContext(), "Id:\n"+user.getUid(), Toast.LENGTH_SHORT).show();
                            com.restapi.insta.user.firebaseUser = user.getUid();

                            //add to DB
                            mRootref.child("Users").child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Login successfull!!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity( intent);
                                        finish();


                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });








                        }else {
                            Toast.makeText(getApplicationContext(), "New Error:\n"+task.getException() , Toast.LENGTH_SHORT).show();
                        }



                    }
                });





    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            HashMap<String, Object> map = new HashMap<>();
            map.put("username", account.getDisplayName());
            map.put("name", account.getDisplayName());
            map.put("email", account.getEmail());
            map.put("id", account.getId());
            map.put("bio","Testing");
            map.put("imageurl", "default");

            //add to DB
            mRootref.child("Users").child(account.getId()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Login successfull!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity( intent);
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });



            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void resetPassword(String email) {

       mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

               if (task.isSuccessful()){
                   Toast.makeText(LoginActivity.this, "Password reset email sent to \n"+email, Toast.LENGTH_SHORT).show();
               }

           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

           }
       });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getApplicationContext(), "Login Succesfull!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity( intent);
                    finish();


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), OpenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity( intent);
            finish();

        }
    }

}