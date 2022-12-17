package com.atlanta.tailoringmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atlanta.tailoringmobile.Adapter.MeasurementListAdapter;
import com.atlanta.tailoringmobile.Adapter.OrderListAdapter;
import com.atlanta.tailoringmobile.Models.JobCardHDR;
import com.atlanta.tailoringmobile.Models.Measurement;
import com.atlanta.tailoringmobile.Models.MeasurementDTL;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeasurementActivity extends AppCompatActivity {

    GridView grdmeasurements;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot=firebaseDatabase.getReference();
    DatabaseReference rootReference;
    String sOrderNo;
    String sDtlID;
    String sCurrentDate;
    ArrayList<Measurement> measurements;
    ArrayList<MeasurementDTL> measurementDtls;
    String sProductName,sProductID,sSizeName,sSizeID;
    RelativeLayout btnsavemeasurement;
    TextView txtproductname,txtproductsize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        grdmeasurements=findViewById(R.id.grdmeasurements);
        btnsavemeasurement=findViewById(R.id.btnsavemeasurement);
        txtproductname=findViewById(R.id.txtproductname);
        txtproductsize=findViewById(R.id.txtproductsize);
        final SharedPreferences shApi =getApplicationContext().getSharedPreferences("Settings",MODE_PRIVATE);
        Common.sConnectionID=shApi.getString("ConnectionID","");
        rootReference=mroot.child("Tailoring").child(Common.sConnectionID);
        final Intent intent = getIntent();
        final Bundle bd = intent.getExtras();
        final String sCurrentUserID =bd.getString("LoginUserID","");
        if(sCurrentUserID.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Login user can't be blank!",Toast.LENGTH_LONG).show();
            finish();
        }
        sCurrentDate=bd.getString("CurrentDate");
        sOrderNo= bd.getString("OrderNo");
        sDtlID=bd.getString("DTLID");
        sProductName=bd.getString("ProductName");
        sProductID=bd.getString("ProductID");
        sSizeName=bd.getString("SizeName");
        sSizeID=bd.getString("SizeID");

        txtproductname.setText(sProductName);
        txtproductsize.setText(sSizeName);


      /*  btnAddMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtmeasurement.getText().toString().trim().equals(""))
                {
                    txtmeasurement.setError("Measurement must be selected!");
                    txtmeasurement.requestFocus();
                    return;
                }
                if(txtmeasuremtValue.getText().toString().equals("") || Double.valueOf(txtmeasuremtValue.getText().toString())<=0.0)
                {
                    txtmeasuremtValue.setError("Measurement value must be entered!");
                    txtmeasuremtValue.requestFocus();
                    return;
                }
                String sMid=Common.getToken();

                final Map<String, Object> data = new HashMap<>();
                data.put("id",sMid);
                data.put("Measurement",txtmeasurement.getText().toString());
                data.put("MeasurementID",txtmeasurement.getTag().toString());
                data.put("Value",Double.valueOf(txtmeasuremtValue.getText().toString()));
                rootReference.child("Order").child(sCurrentDate)
                        .child(sCurrentUserID).child(sOrderNo).child("Items")
                        .child(sDtlID).child("Measurements").child(sMid).updateChildren(data);
                Toast.makeText(MeasurementActivity.this,"Added",Toast.LENGTH_SHORT).show();
                txtmeasurement.setText("");
                txtmeasuremtValue.setText("0.0");
                txtmeasurement.requestFocus();

            }
        });

       */
/*
        txtmeasurement.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Measurement _m=(Measurement) adapterView.getItemAtPosition(i);
                if(_m!=null)
                {
                    txtmeasurement.setTag(_m.getId());
                }
            }
        });
*/





        rootReference.child("Order").child(sCurrentDate)
                .child(sCurrentUserID).child(sOrderNo).child("Items")
                .child(sDtlID).child("Measurements").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren())
                {
                    measurementDtls=new ArrayList<>();
                    for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        MeasurementDTL dtl=new MeasurementDTL();
                        dtl.setId(String.valueOf(ds.child("id").getValue()));
                        dtl.setMeasurementID(Integer.valueOf(String.valueOf(ds.child("MeasurementID").getValue())));
                        dtl.setMeasurementName(String.valueOf(ds.child("Measurement").getValue()));
                        dtl.setMeasurementValue(Double.valueOf(String.valueOf(ds.child("Value").getValue())));
                        measurementDtls.add(dtl);
                    }
                    MeasurementListAdapter adapter=new MeasurementListAdapter(MeasurementActivity.this,measurementDtls);
                    grdmeasurements.setAdapter(adapter);
                }
                else
                {
                    rootReference.child("ProductMeasurement").child(sProductID)
                            .child("sizedtls").child(sSizeID).child("measurements").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            measurementDtls=new ArrayList<>();
                            for(DataSnapshot ds:dataSnapshot.getChildren())
                            {
                                MeasurementDTL dtl=new MeasurementDTL();
                                dtl.setId("");
                                dtl.setMeasurementID(Integer.valueOf(String.valueOf(ds.child("MeasurementID").getValue())));
                                dtl.setMeasurementName(String.valueOf(ds.child("MeasurementName").getValue()));
                                dtl.setMeasurementValue(Double.valueOf(String.valueOf(ds.child("Value").getValue())));
                                measurementDtls.add(dtl);
                            }
                            MeasurementListAdapter adapter=new MeasurementListAdapter(MeasurementActivity.this,measurementDtls);
                            grdmeasurements.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnsavemeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder=new AlertDialog.Builder(MeasurementActivity.this);
                builder.setMessage("Are you sure to save?");
                builder.setTitle("Save");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        rootReference.child("Order").child(sCurrentDate)
                                .child(sCurrentUserID).child(sOrderNo).child("Items")
                                .child(sDtlID).child("Measurements").removeValue();
                        for(MeasurementDTL dtl:measurementDtls)
                        {
                            if(!dtl.getMeasurementName().equals("")) {
                                String sMid = Common.getToken();
                                final Map<String, Object> data = new HashMap<>();
                                data.put("id", sMid);
                                data.put("Measurement", dtl.getMeasurementName().toString());
                                data.put("MeasurementID", String.valueOf(dtl.getMeasurementID()));
                                data.put("Value", dtl.getMeasurementValue());

                                rootReference.child("Order").child(sCurrentDate)
                                        .child(sCurrentUserID).child(sOrderNo).child("Items")
                                        .child(sDtlID).child("Measurements").child(sMid).updateChildren(data);

                                rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID).child(sOrderNo)
                                        .child("OrderGuid").setValue(UUID.randomUUID().toString());
                            }
                        }
                        Toast.makeText(getApplicationContext(),"Measurements Added!",Toast.LENGTH_LONG).show();
                        finish();

                    }
                });
                builder.setNegativeButton("No",null);
                AlertDialog dialog=builder.create();
                dialog.show();





            }
        });

        /*
        grdmeasurements.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final MeasurementDTL mdtl=(MeasurementDTL)adapterView.getItemAtPosition(i);
                if(mdtl!=null)
                {
                    AlertDialog dialog=new AlertDialog.Builder(MeasurementActivity.this)
                            .setMessage("Confirm Deletion ?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String smid= mdtl.getId();
                                    rootReference.child("Order").child(sCurrentDate)
                                            .child(sCurrentUserID).child(sOrderNo).child("Items")
                                            .child(sDtlID).child("Measurements")
                                            .child(smid).removeValue();

                                }
                            })
                            .setNegativeButton("Cancel",null).create();
                    dialog.show();

                }

                return true;
            }
        });

         */

    }
}