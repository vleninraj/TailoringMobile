package com.atlanta.tailoringmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.atlanta.tailoringmobile.Adapter.OrderItemAdapter;
import com.atlanta.tailoringmobile.Adapter.OrderListAdapter;
import com.atlanta.tailoringmobile.Models.Depo;
import com.atlanta.tailoringmobile.Models.Employee;
import com.atlanta.tailoringmobile.Models.JobCardDTL;
import com.atlanta.tailoringmobile.Models.Party;
import com.atlanta.tailoringmobile.Models.Priority;
import com.atlanta.tailoringmobile.Models.Product;
import com.atlanta.tailoringmobile.Models.Size;
import com.atlanta.tailoringmobile.Models.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NewOrderActivity extends AppCompatActivity {


    Button btnpickdate;
    RelativeLayout btnSave,btnPrint;

    AutoCompleteTextView txtdepo,txtparty,txtregisteredby;
    Spinner txtpriority,txtstatus;
    EditText txtCompanyName,txtMobileNumber,txtEmail,txtDueDays,txtRemarks;
    TextView txtorderdate,txtorderno;
    String sCurrentDate;
    String sCurrentUserID;
    boolean blnNewOrder=false;
    String sOrderNo;
    String sOrderDate;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mroot=firebaseDatabase.getReference();
    DatabaseReference rootReference;
    ArrayList<Depo> depos;
    ArrayList<Party> parties;
    ArrayList<Priority> priorities;
    ArrayList<Status> statuses;
    ArrayList<Employee> employees;
    TextView txtgrandamount;
    RelativeLayout btnjobcarddetails;
    ImageView imgnewparty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        btnSave=findViewById(R.id.btnsave);
        btnPrint=findViewById(R.id.btnprint);
        btnpickdate=findViewById(R.id.btnpickdate);
        btnjobcarddetails=findViewById(R.id.btnjobcarddetails);
        txtdepo=findViewById(R.id.txtdepo);
        txtparty=findViewById(R.id.txtparty);
        txtpriority=findViewById(R.id.txtpriority);
        txtstatus=findViewById(R.id.txtstatus);
        txtregisteredby=findViewById(R.id.txtregisteredby);
        txtCompanyName=findViewById(R.id.txtcompanyname);
        txtMobileNumber=findViewById(R.id.txtmobileno);
        txtEmail=findViewById(R.id.txtemail);
        txtDueDays=findViewById(R.id.txtduedays);
        txtRemarks=findViewById(R.id.txtremarks);
        txtorderdate=findViewById(R.id.txtorderdate);
        txtorderno=findViewById(R.id.txtorderno);
        txtgrandamount=findViewById(R.id.txtnewordergrandamount);
        imgnewparty=findViewById(R.id.imgnewparty);
        final SharedPreferences shApi =getApplicationContext().getSharedPreferences("Settings",MODE_PRIVATE);
        Common.sConnectionID=shApi.getString("ConnectionID","");
        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        blnNewOrder = bd.getBoolean("NewOrder");
        sOrderNo= bd.getString("OrderNo");
        sCurrentUserID =bd.getString("LoginUserID","");
        rootReference=mroot.child("Tailoring").child(Common.sConnectionID);
        if(sCurrentUserID.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Login user can't be blank!",Toast.LENGTH_LONG).show();
            finish();
        }

        Date d = new Date();
        final Calendar[] cal = {Calendar.getInstance()};
        d = Calendar.getInstance().getTime();
        cal[0].setTime(d);
        sCurrentDate = Common.FormatDate(d);

        if(blnNewOrder) {
          sOrderNo=Common.getToken();
          txtorderno.setText(sOrderNo);
          txtorderdate.setText(sCurrentDate);
            txtgrandamount.setText("Rs: 0.00");
            txtgrandamount.setTag(0.0);
            txtdepo.requestFocus();
            PopulatePriorities();
            PopulateStatuses();
        }
        else
        {
            rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID)
                    .child(sOrderNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child("DepoName").exists()) {
                        Date date = new Date(Long.valueOf(String.valueOf(snapshot.child("OrderDate").getValue())));
                        String svdate = svdate = Common.FormatDate(date);
                        cal[0].setTime(date);
                        txtorderno.setText(sOrderNo);
                        txtorderdate.setText(svdate);
                        txtdepo.setText(String.valueOf(snapshot.child("DepoName").getValue()));
                        txtdepo.setTag(String.valueOf(snapshot.child("DepoID").getValue()));
                        txtparty.setText(String.valueOf(snapshot.child("Party").getValue()));
                        txtparty.setTag(String.valueOf(snapshot.child("PartyID").getValue()));
                        txtCompanyName.setText(String.valueOf(snapshot.child("CompanyName").getValue()));
                        txtMobileNumber.setText(String.valueOf(snapshot.child("MobileNumber").getValue()));
                        txtEmail.setText(String.valueOf(snapshot.child("Email").getValue()));
                        txtregisteredby.setText(String.valueOf(snapshot.child("RegisteredBy").getValue()));
                        txtregisteredby.setTag(String.valueOf(snapshot.child("RegisteredByID").getValue()));
                        txtDueDays.setText(String.valueOf(snapshot.child("DueDays").getValue()));
                        txtRemarks.setText(String.valueOf(snapshot.child("Remarks").getValue()));
                        Double dblGrandAmount = Double.valueOf(String.valueOf(snapshot.child("GrandAmount").getValue()));
                        txtgrandamount.setText(String.format(Common.sformat, dblGrandAmount));
                        txtgrandamount.setTag(dblGrandAmount);

                    }
                    final String sPriority=String.valueOf(snapshot.child("Priority").getValue());
                    final String sStatus=String.valueOf(snapshot.child("Status").getValue());

                    priorities=new ArrayList<>();
                    rootReference.child("Priority").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            priorities=new ArrayList<>();
                            Priority currentPriority=null;
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                Priority _priority=new Priority();
                                _priority.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                                _priority.setName(String.valueOf(ds.child("Name").getValue()));
                                priorities.add(_priority);
                                if(sPriority.equals(_priority.getName()))
                                {
                                    currentPriority=_priority;
                                }

                            }
                            Priority[] objArray = priorities.toArray(new Priority[priorities.size()]);
                            ArrayAdapter<Priority> adapter = new ArrayAdapter<Priority>(NewOrderActivity.this, android.R.layout.simple_list_item_1, objArray);
                            txtpriority.setAdapter(null);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            txtpriority.setAdapter(adapter);

                            txtpriority.setSelection(priorities.indexOf(currentPriority));
                            txtpriority.setTag(currentPriority.getId());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    statuses=new ArrayList<>();
                    rootReference.child("Status").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            statuses=new ArrayList<>();
                            Status _currentStatus=null;
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                Status status=new Status();
                                status.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                                status.setName(String.valueOf(ds.child("Name").getValue()));
                                statuses.add(status);
                                if(sStatus.equals(status.getName()))
                                {
                                    _currentStatus=status;
                                }
                            }
                            Status[] objArray = statuses.toArray(new Status[statuses.size()]);
                            ArrayAdapter<Status> adapter = new ArrayAdapter<Status>(NewOrderActivity.this, android.R.layout.simple_list_item_1, objArray);
                            txtstatus.setAdapter(null);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            txtstatus.setAdapter(adapter);

                            txtstatus.setSelection(statuses.indexOf(_currentStatus));
                            txtstatus.setTag(_currentStatus.getId());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        imgnewparty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater inflater=getLayoutInflater();
                View dlgView=inflater.inflate(R.layout.activity_new_customer,null);
                final AlertDialog alertDialog = new AlertDialog.Builder(NewOrderActivity.this).create();
                EditText txtcustomername=dlgView.findViewById(R.id.txtcustomername);
                EditText txtcontactperson=dlgView.findViewById(R.id.txtcontactperson);
                EditText txtaddress1=dlgView.findViewById(R.id.txtaddress1);
                EditText txtaddress2=dlgView.findViewById(R.id.txtaddress2);
                EditText txtmobileno=dlgView.findViewById(R.id.txtmobileno);
                Button btnsavecustomer=dlgView.findViewById(R.id.btnsavecustomer);
                Button btncancel=dlgView.findViewById(R.id.btncancel);
                txtcustomername.requestFocus();
                btnsavecustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(txtcustomername.getText().toString().equals(""))
                        {
                            txtcustomername.setError("Customer name must be entered!");
                            txtcustomername.requestFocus();
                            return;
                        }
                        if(txtcontactperson.getText().toString().equals(""))
                        {
                            txtcontactperson.setError("Contact person must be entered!");
                            txtcontactperson.requestFocus();
                            return;
                        }
                        if(txtaddress1.getText().toString().equals(""))
                        {
                            txtaddress1.setError("Address 1 must be entered!");
                            txtaddress1.requestFocus();
                            return;
                        }
                        if(txtmobileno.getText().toString().equals(""))
                        {
                            txtmobileno.setError("Mobile Number must be entered!");
                            txtmobileno.requestFocus();
                            return;
                        }
                        String sID=Common.getToken();
                        final Map<String, Object> data = new HashMap<>();
                        data.put("id",sID);
                        data.put("CustomerName",txtcustomername.getText().toString());
                        data.put("ContactPerson",txtcontactperson.getText().toString());
                        data.put("Address1",txtaddress1.getText().toString());
                        data.put("Address2",txtaddress2.getText().toString());
                        data.put("MobileNumber",txtmobileno.getText().toString());
                        data.put("UpdateTime",new Date().getTime());
                        rootReference.child("NewCustomer").child(sID)
                                .updateChildren(data);
                        rootReference.child("NewCustomer").child(sID).child("UpdateTime").setValue(new Date().getTime());

                        Toast.makeText(getApplicationContext(),"New Customer Created!",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();


                    }
                });
                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });



                alertDialog.setView(dlgView);
                alertDialog.show();

            }
        });
        btnpickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSelectDateListener listener = new OnSelectDateListener() {
                    @Override
                    public void onSelect(final List<Calendar> calendars) {

                        Calendar calendar = Calendar.getInstance();
                         cal[0] = calendars.get(0);
                         txtorderdate.setText(new SimpleDateFormat("dd-MM-yyyy").format(calendars.get(0).getTime()));
                    }
                };
                DatePickerBuilder builder = new DatePickerBuilder(NewOrderActivity.this, listener)
                        .pickerType(CalendarView.ONE_DAY_PICKER).date(cal[0]);

                com.applandeo.materialcalendarview.DatePicker datePicker = builder.build();
                datePicker.show();
            }
        });

        rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID)
                .child(sOrderNo)
                .child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double dblTotalGrandAmount=0.0,dblQty=0.0,dblRate=0.0;
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("Qty").exists()) {
                        dblQty = Double.valueOf(String.valueOf(ds.child("Qty").getValue()));
                        dblRate = Double.valueOf(String.valueOf(ds.child("Rate").getValue()));
                        dblTotalGrandAmount += dblQty * dblRate;
                    }

                }
                txtgrandamount.setText(String.format(Common.sformat,dblTotalGrandAmount));
                txtgrandamount.setTag(dblTotalGrandAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtdepo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Depo objDepo =(Depo)adapterView.getItemAtPosition(i);
                if(objDepo!=null)
                {
                    txtdepo.setTag(objDepo.getId());
                }
            }
        });
        txtparty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Party _party=(Party)adapterView.getItemAtPosition(i);
                if(_party!=null)
                {
                    txtparty.setTag(_party.getId());
                }
            }
        });
        txtregisteredby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Employee _employee=(Employee)adapterView.getItemAtPosition(i);
                if(_employee!=null)
                {
                    txtregisteredby.setTag(_employee.getId());
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtdepo.getTag()==null || txtdepo.getText().toString().trim().equals(""))
                {
                    txtdepo.setError("Depo must be selected!");
                    txtdepo.requestFocus();
                    return;
                }
                if(txtparty.getTag()==null || txtparty.getText().toString().trim().equals(""))
                {
                    txtparty.setError("Party must be selected!");
                    txtparty.requestFocus();
                    return;
                }
                if(txtpriority.getSelectedItem()==null || txtpriority.getSelectedItem().toString().trim().equals(""))
                {
                    TextView txterrorview1=(TextView)txtpriority.getSelectedView();
                    txterrorview1.setError("Priority must be selected!");
                    txtpriority.requestFocus();
                    return;
                }
                if(txtstatus.getSelectedItem()==null || txtstatus.getSelectedItem().toString().trim().equals(""))
                {
                    TextView txterrorview2=(TextView)txtstatus.getSelectedView();
                    txterrorview2.setError("Status must be selected!");
                    txtstatus.requestFocus();
                    return;
                }
                if(txtregisteredby.getTag()==null || txtregisteredby.getText().toString().trim().equals(""))
                {
                    txtregisteredby.setError("Regisdtered by must be selected!");
                    txtregisteredby.requestFocus();
                    return;
                }
                if(txtgrandamount.getTag()==null || Double.valueOf(txtgrandamount.getTag().toString())<=0)
                {
                    Toast.makeText(getApplicationContext(),"Atleast one item must be added!",Toast.LENGTH_LONG).show();
                    return;
                }

                final Map<String, Object> data = new HashMap<>();
                data.put("VoucherNo",sOrderNo);
                data.put("UserID",sCurrentUserID);
                data.put("OrderDate",cal[0].getTimeInMillis());
                data.put("DepoName",txtdepo.getText().toString());
                data.put("DepoID",txtdepo.getTag().toString());
                data.put("Party",txtparty.getText().toString());
                data.put("PartyID",txtparty.getTag().toString());
                data.put("CompanyName",txtCompanyName.getText().toString());
                data.put("MobileNumber",txtMobileNumber.getText().toString());
                data.put("Email",txtEmail.getText().toString());
                data.put("Priority",txtpriority.getSelectedItem().toString());
                String sPriorityID=String.valueOf(((Priority)txtpriority.getSelectedItem()).getId());
                data.put("PriorityID",sPriorityID);
                String sStatus=String.valueOf(((Status)txtstatus.getSelectedItem()).getId());
                data.put("Status",txtstatus.getSelectedItem().toString());
                data.put("StatusID",sStatus);
                data.put("RegisteredBy",txtregisteredby.getText().toString());
                data.put("RegisteredByID",txtregisteredby.getTag().toString());
                data.put("DueDays",txtDueDays.getText().toString());
                data.put("Remarks",txtRemarks.getText().toString());
                data.put("UpdateTime",new Date().getTime());
                data.put("GrandAmount",Double.valueOf(txtgrandamount.getTag().toString()));
                data.put("OrderGuid", UUID.randomUUID().toString());
                rootReference.child("Order").child(sCurrentDate).child(sCurrentUserID).child(sOrderNo)
                     .updateChildren(data);

                Toast.makeText(getApplicationContext(),"Order Saved!",Toast.LENGTH_LONG).show();
                finish();

            }
        });

        PopulateDepo();
        PopulateParties();
        PopulateEmployees();

        btnjobcarddetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intJobCardDetails=new Intent(NewOrderActivity.this,JobCardDetailActivity.class);
                intJobCardDetails.putExtra("NewOrder", blnNewOrder);
                intJobCardDetails.putExtra("OrderNo",sOrderNo);
                intJobCardDetails.putExtra("CurrentDate", sCurrentDate);
                intJobCardDetails.putExtra("LoginUserID",sCurrentUserID);
                startActivity(intJobCardDetails);
            }
        });





    }


    private void PopulateStatuses()
    {
        statuses=new ArrayList<>();
        rootReference.child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statuses=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Status status=new Status();
                    status.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                    status.setName(String.valueOf(ds.child("Name").getValue()));
                    statuses.add(status);
                }
                Status[] objArray = statuses.toArray(new Status[statuses.size()]);
                ArrayAdapter<Status> adapter = new ArrayAdapter<Status>(NewOrderActivity.this, android.R.layout.simple_list_item_1, objArray);
                txtstatus.setAdapter(null);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtstatus.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void PopulateEmployees()
    {
        employees=new ArrayList<>();
        rootReference.child("Employee").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employees=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Employee emp=new Employee();
                    emp.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                    emp.setName(String.valueOf(ds.child("Name").getValue()));
                    employees.add(emp);
                }
                Employee[] objArray = employees.toArray(new Employee[employees.size()]);
                ArrayAdapter<Employee> adapter = new ArrayAdapter<Employee>(NewOrderActivity.this, android.R.layout.simple_list_item_1, objArray);
                txtregisteredby.setAdapter(adapter);
                txtregisteredby.setThreshold(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void PopulateDepo()
    {

        depos=new ArrayList<>();
        rootReference.child("Depos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                depos=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Depo _depo=new Depo();
                    _depo.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                    _depo.setCode(String.valueOf(ds.child("Code").getValue()));
                    _depo.setName(String.valueOf(ds.child("Name").getValue()));
                    depos.add(_depo);
                }
                Depo[] deposArray = depos.toArray(new Depo[depos.size()]);
                ArrayAdapter<Depo> adapter = new ArrayAdapter<Depo>(NewOrderActivity.this, android.R.layout.simple_list_item_1, deposArray);
                txtdepo.setAdapter(adapter);
                txtdepo.setThreshold(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void PopulatePriorities()
    {
        priorities=new ArrayList<>();
        rootReference.child("Priority").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                priorities=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Priority _priority=new Priority();
                    _priority.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                    _priority.setName(String.valueOf(ds.child("Name").getValue()));
                    priorities.add(_priority);
                }
                Priority[] objArray = priorities.toArray(new Priority[priorities.size()]);
                ArrayAdapter<Priority> adapter = new ArrayAdapter<Priority>(NewOrderActivity.this, android.R.layout.simple_list_item_1, objArray);
                txtpriority.setAdapter(null);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                txtpriority.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void PopulateParties()
    {
        parties=new ArrayList<>();
        rootReference.child("Party").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parties=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Party _party=new Party();
                    _party.setId(Integer.valueOf(String.valueOf(ds.child("id").getValue())));
                    _party.setName(String.valueOf(ds.child("Name").getValue()));
                    parties.add(_party);
                }
                Party[] objArrays = parties.toArray(new Party[parties.size()]);
                ArrayAdapter<Party> adapter = new ArrayAdapter<Party>(NewOrderActivity.this, android.R.layout.simple_list_item_1, objArrays);
                txtparty.setAdapter(adapter);
                txtparty.setThreshold(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}