package com.atlanta.tailoringmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {

    EditText txtregcdkey,txtregcustomername,txtregaddress,txtregmobile,txtregemail,txtregno;
    AutoCompleteTextView txtregcountry;
    TextView txtregproductid;
    Button btngetregno,btnregister;
    RequestQueue requestQueue;
    ArrayList<String> countries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        txtregcdkey=findViewById(R.id.txtregcdkey);
        txtregcustomername=findViewById(R.id.txtregcustomername);
        txtregaddress=findViewById(R.id.txtregaddress);
        txtregmobile =findViewById(R.id.txtregmobile);
        txtregemail=findViewById(R.id.txtregemail);
        txtregcountry=findViewById(R.id.txtregcountry);
        txtregproductid=findViewById(R.id.txtregproductid);
        txtregno=findViewById(R.id.txtregregno);
        btngetregno=findViewById(R.id.btngetregno);
        btnregister=findViewById(R.id.btnregregister);
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
       String sProductID= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        txtregproductid.setText(sProductID);
        LoadCountry();

        btngetregno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCDKeyDetails(txtregcdkey.getText().toString(),txtregproductid.getText().toString());
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterData();
            }
        });
        txtregcdkey.requestFocus();
    }
    public  void LoadCountry()
    {
        try {
            countries=new ArrayList<>();
            String sjson=loadJSONFromAsset();
            JSONObject obj = new JSONObject(sjson);
            JSONArray m_jArry = obj.getJSONArray("countries");
            String sCountry="";
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject joCountry = m_jArry.getJSONObject(i);
                sCountry=joCountry.getString("name");
                countries.add(sCountry);
            }
            String[] itemsArray = countries.toArray(new String[countries.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_list_item_1, itemsArray);
            txtregcountry.setAdapter(adapter);
            txtregcountry.setThreshold(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("CountryCodes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public void RegisterData()
    {

        if(txtregcdkey.getText().toString().equals(""))
        {
            txtregcdkey.setError("CD Key can't be blank!");
            txtregcdkey.requestFocus();
            return;
        }
        if(txtregproductid.getText().toString().equals(""))
        {
            txtregproductid.setError("Product ID can't be blank!");
            txtregproductid.requestFocus();
            return;
        }
        if(txtregcustomername.getText().toString().equals(""))
        {
            txtregcustomername.setError("Customer name can't be blank!");
            txtregcustomername.requestFocus();
            return;
        }
        if(txtregmobile.getText().toString().equals(""))
        {
            txtregmobile.setError("Mobile number can't be blank!");
            txtregmobile.requestFocus();
            return;
        }
        if(txtregcountry.getText().toString().equals(""))
        {
            txtregcountry.setError("Country must be selected!");
            txtregcountry.requestFocus();
            return;
        }
        btnregister.setEnabled(false);
        JSONArray jsnArray=new JSONArray();
        JSONObject obj=new JSONObject();
        try {
            obj.put("CdKey",txtregcdkey.getText().toString());
            obj.put("ProductID",txtregproductid.getText().toString());
            obj.put("CustomerName",txtregcustomername.getText().toString());
            obj.put("Address",txtregaddress.getText().toString());
            obj.put("RegNumber",txtregno.getText().toString());
            obj.put("Email",txtregemail.getText().toString());
            obj.put("Mobile",txtregmobile.getText().toString());
            obj.put("Country",txtregcountry.getText().toString());
            jsnArray.put(obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        String url="https://atlanta-it.com/TLP/registermobile.php";
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.POST, url, jsnArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null) {
                    try {
                        JSONObject _jsnresponse=response.getJSONObject(0);
                        String sStatus=_jsnresponse.getString("status");
                        String sRegNo=_jsnresponse.getString("regno");

                        if(sStatus.equals("1") || sStatus.equals("2"))
                        {
                            final SharedPreferences regpref = getApplicationContext().getSharedPreferences("regno", MODE_PRIVATE);
                            SharedPreferences.Editor ipAddressEditor = regpref.edit();
                            ipAddressEditor.putString("regno", sRegNo);
                            ipAddressEditor.apply();
                            Toast.makeText(RegistrationActivity.this,"Registration Success...",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else if(sStatus.equals("0"))
                        {
                            Toast.makeText(RegistrationActivity.this,"Invalid Registration...",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(RegistrationActivity.this,"Un Known error...",Toast.LENGTH_LONG).show();
                        }
                        btnregister.setEnabled(true);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, "Registration failed!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);

    }
    public void getCDKeyDetails(String scdKey,String sproductid)
    {

        if(txtregcdkey.getText().toString().equals(""))
        {
            txtregcdkey.setError("CD Key can't be blank!");
            txtregcdkey.requestFocus();
            return;
        }
        if(txtregproductid.getText().toString().equals(""))
        {
            txtregproductid.setError("Product ID can't be blank!");
            txtregproductid.requestFocus();
            return;
        }
        txtregno.setText("");
        btngetregno.setEnabled(false);
        String url = "https://atlanta-it.com/TLP/getcdkeydata?cdkey=" + scdKey + "&productid=" + sproductid;
        JsonRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray jsonArray = response;
                try {
                    if(jsonArray.length()>0)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String sregno=jsonObject.getString("regno");
                        String snolicense=jsonObject.getString("nolicense");
                        if(snolicense.equals("-1"))
                        {
                            Toast.makeText(RegistrationActivity.this,"Invalid CD Key!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        else if(snolicense.equals("0"))
                        {
                            Toast.makeText(RegistrationActivity.this,"License Limit Exceeded!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        txtregno.setText(sregno);
                        btngetregno.setEnabled(true);
                    }
                }
                catch (Exception w)
                {
                    Toast.makeText(RegistrationActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                    btngetregno.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                btngetregno.setEnabled(true);
            }
        });
        jsonArrayRequest.setShouldCache(false);

        requestQueue.add(jsonArrayRequest);
    }
}