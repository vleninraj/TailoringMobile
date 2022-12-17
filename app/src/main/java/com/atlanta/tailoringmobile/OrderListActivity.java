package com.atlanta.tailoringmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.atlanta.tailoringmobile.Adapter.OrderListAdapter;
import com.atlanta.tailoringmobile.Models.JobCardHDR;
import com.atlanta.tailoringmobile.Models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot=firebaseDatabase.getReference();
    DatabaseReference rootReference;
    ArrayList<JobCardHDR> _orders;
    GridView grdview;
    SearchView search;
    Button btnSearch;
    Button btnneworder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        grdview=findViewById(R.id.gridView);
        search=findViewById(R.id.Search);
        btnSearch=findViewById(R.id.btnsearch);
        btnneworder=findViewById(R.id.btnneworder);
        final SharedPreferences shApi =getApplicationContext().getSharedPreferences("Settings",MODE_PRIVATE);
        Common.sConnectionID=shApi.getString("ConnectionID","");
        rootReference=mroot.child("Tailoring").child(Common.sConnectionID);
        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        final String sCurrentUserID =bd.getString("LoginUserID","");
        if(sCurrentUserID.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Login user can't be blank!",Toast.LENGTH_LONG).show();
            finish();
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


        Date d = new Date();
        d= Calendar.getInstance().getTime();
        final String sCurrentDate= Common.FormatDate(d);
        rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                _orders=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    JobCardHDR _order=new JobCardHDR();
                    if(!ds.child("VoucherNo").exists())
                    {
                        continue;
                    }
                    _order.setVoucherNo(String.valueOf(ds.child("VoucherNo").getValue()));
                    _order.setOrderDate(String.valueOf(ds.child("OrderDate").getValue()));
                    _order.setStatus(String.valueOf(ds.child("Status").getValue()));
                    _order.setGrandAmount(Double.valueOf(String.valueOf(ds.child("GrandAmount").getValue())));
                    _order.setPartyName(String.valueOf(ds.child("Party").getValue()));
                    _orders.add(_order);
                }
                OrderListAdapter adapter=new OrderListAdapter(OrderListActivity.this,_orders);
                grdview.setAdapter(adapter);
                grdview.setTextFilterEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    grdview.clearTextFilter();
                }else{
                    grdview.setFilterText(s);
                }
                return false;
            }
        });
        btnneworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(OrderListActivity.this,NewOrderActivity.class);
                intent.putExtra("NewOrder", true);
                intent.putExtra("OrderNo","");
                intent.putExtra("LoginUserID",sCurrentUserID);
                startActivity(intent);

            }
        });
        grdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JobCardHDR _hdr=(JobCardHDR)adapterView.getItemAtPosition(i);
                if(_hdr!=null) {
                   Intent intent = new Intent(OrderListActivity.this, NewOrderActivity.class);
                   intent.putExtra("NewOrder", false);
                   intent.putExtra("OrderNo", _hdr.getVoucherNo());
                    intent.putExtra("LoginUserID",sCurrentUserID);
                   startActivity(intent);
               }
            }
        });
        grdview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final JobCardHDR _hdr=(JobCardHDR)adapterView.getItemAtPosition(i);
                if(_hdr!=null)
                {
                    AlertDialog dialog=new AlertDialog.Builder(OrderListActivity.this)
                            .setMessage("Confirm Deletion ?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String _orderno= _hdr.getVoucherNo();
                                    rootReference.child("Order").child(sCurrentDate)
                                            .child(sCurrentUserID).child(_orderno).removeValue();
                                }
                            })
                            .setNegativeButton("Cancel",null).create();
                    dialog.show();

                }
                return true;
            }

        });


    }
}