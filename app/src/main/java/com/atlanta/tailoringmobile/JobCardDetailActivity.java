package com.atlanta.tailoringmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atlanta.tailoringmobile.Adapter.OrderItemAdapter;
import com.atlanta.tailoringmobile.Models.JobCardDTL;
import com.atlanta.tailoringmobile.Models.JobCardHDR;
import com.atlanta.tailoringmobile.Models.Product;
import com.atlanta.tailoringmobile.Models.Size;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobCardDetailActivity extends AppCompatActivity {

    ArrayList<Product> _products=new ArrayList<>();
    ArrayList<Size> _sizes=new ArrayList<>();
    ArrayList<JobCardDTL> _jobcarddtls=new ArrayList<>();
    GridView grdproducts;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot=firebaseDatabase.getReference();
    DatabaseReference rootReference;
    boolean blnNewOrder=false;
    String sOrderNo;
    String sCurrentUserID;
    String sCurrentDate;
    TextView txtgrandamount;
    RelativeLayout btnAddItem;
    SearchView search;
    Button btnSearch;
    Button btnMeasurement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_card_detail);
        grdproducts=findViewById(R.id.grdproducts);
        txtgrandamount=findViewById(R.id.txtgrandamount);
        btnAddItem=findViewById(R.id.btnAddItem);
        search=findViewById(R.id.searchitem);
        btnSearch=findViewById(R.id.btnsearchitem);

        final SharedPreferences shApi =getApplicationContext().getSharedPreferences("Settings",MODE_PRIVATE);
        Common.sConnectionID=shApi.getString("ConnectionID","");
        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        sCurrentUserID =bd.getString("LoginUserID","");
        if(sCurrentUserID.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Login user can't be blank!",Toast.LENGTH_LONG).show();
            finish();
        }
        blnNewOrder = bd.getBoolean("NewOrder");
        sOrderNo= bd.getString("OrderNo");
        rootReference=mroot.child("Tailoring").child(Common.sConnectionID);
        sCurrentDate=bd.getString("CurrentDate");
        if(blnNewOrder) {
            txtgrandamount.setText("Rs: 0.00");
            txtgrandamount.setTag(0.0);
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSearch.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                search.setIconified(false);
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                btnSearch.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                return true;
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)){
                    grdproducts.clearTextFilter();
                }else{
                    grdproducts.setFilterText(s);
                }
                return false;
            }
        });
        rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID)
                .child(sOrderNo)
                .child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                _jobcarddtls=new ArrayList<>();
                Double dblTotalGrandAmount=0.0;
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(!ds.child("ProductID").exists())
                    {
                        continue;
                    }
                    JobCardDTL dtl=new JobCardDTL();
                    dtl.setId(String.valueOf(ds.child("id").getValue()));
                    dtl.setProductID(Integer.valueOf(String.valueOf(ds.child("ProductID").getValue())));
                    dtl.setProductName(String.valueOf(ds.child("ProductName").getValue()));
                    dtl.setQty(Double.valueOf(String.valueOf(ds.child("Qty").getValue())));
                    dtl.setRate(Double.valueOf(String.valueOf(ds.child("Rate").getValue())));
                    dtl.setSubPartyCode(String.valueOf(ds.child("SubPartyCode").getValue()));
                    dtl.setSubPartyName(String.valueOf(ds.child("SubPartyName").getValue()));
                    dblTotalGrandAmount+=dtl.getQty()*dtl.getRate();
                    _jobcarddtls.add(dtl);
                }
                txtgrandamount.setText(String.format(Common.sformat,dblTotalGrandAmount));
                txtgrandamount.setTag(dblTotalGrandAmount);
                OrderItemAdapter adapter=new OrderItemAdapter(JobCardDetailActivity.this,_jobcarddtls);
                grdproducts.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        grdproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JobCardDTL _dtl=(JobCardDTL)adapterView.getItemAtPosition(i);
                if(_dtl!=null) {
                    AddItem(blnNewOrder,sOrderNo,_dtl.getId(),false);
                }
            }
        });
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sDtlID=Common.getToken();
                AddItem(blnNewOrder,sOrderNo,sDtlID,true);
            }
        });

    }
    private void AddItem(boolean blnNewOrder,String sVNo,String sDtlID,Boolean blnNewItem)
    {
        final String sCurrentVno=sVNo;
        final String sCurrentDTLID=sDtlID;
        final LayoutInflater inflater=getLayoutInflater();
        View dlgView=inflater.inflate(R.layout.activity_add_new_item,null);
        final RelativeLayout btnSaveItem=dlgView.findViewById(R.id.btnsaveitem);
        final AutoCompleteTextView txtproduct=dlgView.findViewById(R.id.txtproduct);
        final Spinner txtsize=dlgView.findViewById(R.id.txtsize);
        final EditText txtqty=dlgView.findViewById(R.id.txtqty);
        final EditText txtRate=dlgView.findViewById(R.id.txtrate);
        final TextView txtAmount=dlgView.findViewById(R.id.txtamount);
        final EditText txtmodelname=dlgView.findViewById(R.id.txtmodelname);
        final EditText txtsubpartycode=dlgView.findViewById(R.id.txtsubpartycode);
        final EditText txtsubpartyname=dlgView.findViewById(R.id.txtsubpartyname);
        final EditText txtsubpartyDepartment=dlgView.findViewById(R.id.txtsubpartydepartment);
        final EditText txtsubpartyDesignation=dlgView.findViewById(R.id.txtsubpartydesignation);
        final EditText txtfabricName =dlgView.findViewById(R.id.txtfabricname);
        final RelativeLayout btnMeasurement=dlgView.findViewById(R.id.btnmeasurements);
        final RelativeLayout btnattachimage=dlgView.findViewById(R.id.btnattachimage);
        if(blnNewOrder) {
            txtproduct.requestFocus();
        }
        if(!blnNewItem) {
            rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID)
                    .child(sOrderNo).child("Items").child(sDtlID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    if (ds.child("id").exists()) {

                        final String sSizeName=String.valueOf(ds.child("Size").getValue());

                        rootReference.child("Size").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotSize) {
                                _sizes = new ArrayList<>();
                                Size currentSize=null;
                                for (DataSnapshot dsSize : snapshotSize.getChildren()) {
                                    final Size p = new Size();
                                    p.setId(Integer.valueOf(String.valueOf(dsSize.child("id").getValue())));
                                    p.setName(String.valueOf(dsSize.child("Name").getValue()));
                                    if(p.getName().equals(sSizeName))
                                    {
                                        currentSize=p;
                                    }
                                    _sizes.add(p);
                                }
                                Size[] objArray = _sizes.toArray(new Size[_sizes.size()]);
                                ArrayAdapter<Size> adapter = new ArrayAdapter<Size>(JobCardDetailActivity.this, android.R.layout.simple_list_item_1, objArray);
                                txtsize.setAdapter(null);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                txtsize.setAdapter(adapter);
                                if(currentSize!=null) {
                                    txtsize.setSelection(_sizes.indexOf(currentSize));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        txtAmount.setText(String.format(Common.sformat, Double.valueOf(String.valueOf(ds.child("Amount").getValue()))));
                        txtfabricName.setText(String.valueOf(ds.child("FabricName").getValue()));
                        txtmodelname.setText(String.valueOf(ds.child("ModelName").getValue()));
                        txtproduct.setTag(String.valueOf(ds.child("ProductID").getValue()));
                        txtproduct.setText(String.valueOf(ds.child("ProductName").getValue()));
                        txtqty.setText(String.format(Common.sformat, Double.valueOf(String.valueOf(ds.child("Qty").getValue()))));
                        txtRate.setText(String.format(Common.sformat, Double.valueOf(String.valueOf(ds.child("Rate").getValue()))));
                        txtsubpartycode.setText(String.valueOf(ds.child("SubPartyCode").getValue()));
                        txtsubpartyname.setText(String.valueOf(ds.child("SubPartyName").getValue()));
                        txtsubpartyDepartment.setText(String.valueOf(ds.child("SubPartyDepartment").getValue()));
                        txtsubpartyDesignation.setText(String.valueOf(ds.child("SubPartyDesignation").getValue()));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            rootReference.child("Size").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshotSize) {
                    _sizes = new ArrayList<>();
                    for (DataSnapshot dsSize : snapshotSize.getChildren()) {
                        final Size p = new Size();
                        p.setId(Integer.valueOf(String.valueOf(dsSize.child("id").getValue())));
                        p.setName(String.valueOf(dsSize.child("Name").getValue()));
                        _sizes.add(p);
                    }
                    Size[] objArray = _sizes.toArray(new Size[_sizes.size()]);
                    ArrayAdapter<Size> adapter = new ArrayAdapter<Size>(JobCardDetailActivity.this, android.R.layout.simple_list_item_1, objArray);
                    txtsize.setAdapter(null);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    txtsize.setAdapter(adapter);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        rootReference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                _products=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Product p =new Product();
                    p.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                    p.setName(String.valueOf(ds.child("Name").getValue()));
                    _products.add(p);
                }
                Product[] objArray = _products.toArray(new Product[_products.size()]);
                ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(JobCardDetailActivity.this, android.R.layout.simple_list_item_1, objArray);
                txtproduct.setAdapter(adapter);
                txtproduct.setThreshold(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtproduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product _product=(Product)adapterView.getItemAtPosition(i);
                if(_product!=null)
                {
                    txtproduct.setTag(_product.getId());
                }
            }
        });


        txtqty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(txtRate.getText().toString().trim().equals(""))
                {
                    return;
                }
                if(txtqty.getText().toString().trim().equals(""))
                {
                    return;
                }
                Double dblRate=Double.valueOf(txtRate.getText().toString());
                Double dblQty=Double.valueOf(txtqty.getText().toString());
                Double dblAmount=dblRate * dblQty;
                txtAmount.setText(String.format(Common.sformat,dblAmount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        txtRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txtRate.getText().toString().trim().equals(""))
                {
                    return;
                }
                if(txtqty.getText().toString().trim().equals(""))
                {
                    return;
                }

                Double dblRate=Double.valueOf(txtRate.getText().toString());
                Double dblQty=Double.valueOf(txtqty.getText().toString());
                Double dblAmount=dblRate * dblQty;
                txtAmount.setText(String.format(Common.sformat,dblAmount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        grdproducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final JobCardDTL _dtl=(JobCardDTL)adapterView.getItemAtPosition(i);
                if(_dtl!=null)
                {
                    AlertDialog dialog=new AlertDialog.Builder(JobCardDetailActivity.this)
                            .setMessage("Confirm Deletion ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                rootReference.child("Order").child(sCurrentDate)
                                        .child(sCurrentUserID).child(sOrderNo)
                                        .child("Items").child(_dtl.getId()).removeValue();
                            }
                        })
                            .setNeutralButton("Cancel",null)
                            .create();
                    dialog.show();
                }
                return true;
            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(JobCardDetailActivity.this).create();
        alertDialog.setView(dlgView);
        alertDialog.show();

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtproduct.getText().toString().trim().equals(""))
                {
                    txtproduct.setError("Product must be selected!");
                    txtproduct.requestFocus();
                    return;
                }
                if(txtsize.getSelectedItem()==null && txtsize.getSelectedItem().toString().trim().equals(""))
                {
                    TextView tv=(TextView)txtsize.getSelectedView();
                    tv.setError("Size must be selected!");
                    txtsize.requestFocus();
                    return;
                }
                if(txtqty.getText().toString().equals("") || Double.valueOf(txtqty.getText().toString())==0.0)
                {
                    txtqty.setError("Qty must be Entered!");
                    txtqty.requestFocus();
                    return;
                }
                if(txtRate.getText().toString().equals("") || Double.valueOf(txtRate.getText().toString())==0.0)
                {
                    txtRate.setError("Rate must be Entered!");
                    txtRate.requestFocus();
                    return;
                }
                if(txtsubpartycode.getText().toString().trim().equals(""))
                {
                    txtsubpartycode.setError("Sub party code can't be blank!");
                    txtsubpartycode.requestFocus();
                    return;
                }
                if(txtsubpartyname.getText().toString().trim().equals(""))
                {
                    txtsubpartyname.setError("Sub party name can't be blank!");
                    txtsubpartyname.requestFocus();
                    return;
                }
                if(txtfabricName.getText().toString().trim().equals(""))
                {
                    txtfabricName.setError("Fabric name can't be blank!");
                    txtfabricName.requestFocus();
                    return;
                }

                Size _size=(Size)txtsize.getSelectedItem();
                final Map<String, Object> data = new HashMap<>();
                data.put("id",sCurrentDTLID);
                data.put("ProductID",txtproduct.getTag().toString());
                data.put("ProductName",txtproduct.getText().toString());
                data.put("SizeID",_size.getId());
                data.put("Size",_size.getName());
                data.put("Qty",Double.valueOf(txtqty.getText().toString()));
                data.put("Rate",Double.valueOf(txtRate.getText().toString()));
                data.put("Amount",Double.valueOf(txtAmount.getText().toString()));
                data.put("ModelName",txtmodelname.getText().toString());
                data.put("SubPartyCode", txtsubpartycode.getText().toString().trim());
                data.put("SubPartyName", txtsubpartyname.getText().toString().trim());
                data.put("SubPartyDepartment", txtsubpartyDepartment.getText().toString().trim());
                data.put("SubPartyDesignation", txtsubpartyDesignation.getText().toString().trim());
                data.put("FabricName", txtfabricName.getText().toString().trim());

                rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID).child(sOrderNo)
                        .child("Items").child(sCurrentDTLID).updateChildren(data);

                rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID).child(sOrderNo)
                        .child("OrderGuid").setValue(UUID.randomUUID().toString());

                Toast.makeText(getApplicationContext(),"New Item Added!",Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
                AddItem(true,sCurrentVno,"",true);

            }
        });

        btnMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtproduct.getText().toString().trim().equals(""))
                {
                    txtproduct.setError("Product must be selected!");
                    txtproduct.requestFocus();
                    return;
                }
                if(txtsize.getSelectedItem().equals(null))
                {
                    Toast.makeText(getApplicationContext(),"Size Must be selected!",Toast.LENGTH_LONG).show();
                    txtsize.requestFocus();
                    return;
                }

                Size _size=(Size)txtsize.getSelectedItem();
                Intent intMeasurements=new Intent(JobCardDetailActivity.this,MeasurementActivity.class);
                intMeasurements.putExtra("OrderNo",sOrderNo);
                intMeasurements.putExtra("CurrentDate", sCurrentDate);
                intMeasurements.putExtra("DTLID",sCurrentDTLID);
                intMeasurements.putExtra("ProductName",txtproduct.getText().toString());
                intMeasurements.putExtra("ProductID",txtproduct.getTag().toString());
                intMeasurements.putExtra("SizeName",_size.getName());
                intMeasurements.putExtra("SizeID",String.valueOf(_size.getId()));
                intMeasurements.putExtra("LoginUserID",sCurrentUserID);
                startActivity(intMeasurements);
            }
        });
        btnattachimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intAttach=new Intent(JobCardDetailActivity.this,ImageViewerActivity.class);
                intAttach.putExtra("OrderNo",sOrderNo);
                intAttach.putExtra("CurrentDate", sCurrentDate);
                intAttach.putExtra("DTLID",sCurrentDTLID);
                intAttach.putExtra("LoginUserID",sCurrentUserID);
                startActivity(intAttach);
            }
        });

    }
}