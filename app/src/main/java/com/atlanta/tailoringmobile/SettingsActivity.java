package com.atlanta.tailoringmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    EditText txtconnectionID;
    Button btnSave;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot=firebaseDatabase.getReference();
    DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        txtconnectionID=findViewById(R.id.txtconnectionID);
        btnSave=findViewById(R.id.btnsavesettings);
        final SharedPreferences shApi =getApplicationContext().getSharedPreferences("Settings",MODE_PRIVATE);

        Common.sConnectionID=shApi.getString("ConnectionID","");
        txtconnectionID.setText(Common.sConnectionID);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtconnectionID.setError(null);
                if(txtconnectionID.getText().toString().trim().equals(""))
                {
                    txtconnectionID.setError("Connection ID can't be blank!");
                    txtconnectionID.requestFocus();
                }



                SharedPreferences.Editor shEditor=shApi.edit();
                shEditor.putString("ConnectionID",txtconnectionID.getText().toString());
                shEditor.apply();
                Toast.makeText(getApplicationContext(),"Settings Saved!",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

}