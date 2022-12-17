package com.atlanta.tailoringmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atlanta.tailoringmobile.Models.LoginUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnSettings;
    EditText txtpinnumber;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot=firebaseDatabase.getReference();
    DatabaseReference rootReference;
    String sRegNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin=findViewById(R.id.btnLogin);
        btnSettings=findViewById(R.id.btnSettings);
        txtpinnumber=findViewById(R.id.txtpinnumber);
        final SharedPreferences shApi =getApplicationContext().getSharedPreferences("Settings",MODE_PRIVATE);
        Common.sConnectionID=shApi.getString("ConnectionID","");

        final SharedPreferences regpref = getApplicationContext().getSharedPreferences("regno", MODE_PRIVATE);
        sRegNo=regpref.getString("regno", "");
        if(sRegNo.equals("")) // Not Registered
        {
            btnLogin.setVisibility(View.INVISIBLE);
            btnSettings.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);

        }
        else
        {
            btnLogin.setVisibility(View.VISIBLE);
            btnSettings.setVisibility(View.VISIBLE);
        }


        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intSettings=new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intSettings);

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtpinnumber.setError(null);
                if(Common.sConnectionID.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Must set connection id before login!",Toast.LENGTH_LONG).show();
                    txtpinnumber.requestFocus();
                    return;
                }
                if(txtpinnumber.getText().toString().trim().equals(""))
                {
                    txtpinnumber.setError("PIN Number must be entered!");
                    Toast.makeText(getApplicationContext(),"PIN Number must be entered!",Toast.LENGTH_LONG).show();
                    txtpinnumber.requestFocus();
                    return;
                }

                rootReference=mroot.child("Tailoring").child(Common.sConnectionID);
                String sPinNumber=txtpinnumber.getText().toString();
                rootReference.child("LoginUsers").child(sPinNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot!=null && snapshot.child("id").exists())
                        {
                            LoginUser _user = new LoginUser();
                            _user.setId(Integer.valueOf(snapshot.child("id").getValue().toString()));
                            _user.setUserName(String.valueOf(snapshot.child("UserName").getValue()));
                            _user.setPassword(String.valueOf(snapshot.child("Password").getValue()));
                            _user.setPinNumber(String.valueOf(snapshot.child("PinNumber").getValue()));
                              if (_user.getPinNumber().trim().equals(txtpinnumber.getText().toString().trim())) {

                                  Toast.makeText(getApplicationContext(),"Login Success...",Toast.LENGTH_LONG).show();
                                  Intent intentOrderList=new Intent(getApplicationContext(),OrderListActivity.class);
                                  intentOrderList.putExtra("LoginUserID",String.valueOf(_user.getId()));
                                  startActivity(intentOrderList);
                            }
                              else
                              {
                                  Toast.makeText(getApplicationContext(),"Login Failed...",Toast.LENGTH_LONG).show();
                              }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

}