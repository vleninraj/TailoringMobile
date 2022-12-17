package com.atlanta.tailoringmobile.ViewHolder;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.atlanta.tailoringmobile.R;

public class ViewHolderMeasurement {

    public TextView txtmeasurementname;
    public EditText txtmeasurementValue;
    public ViewHolderMeasurement(View v)
    {
        txtmeasurementname=(TextView)v.findViewById(R.id.txtmeasurementname);
        txtmeasurementValue=(EditText) v.findViewById(R.id.txtmeasurementValue);
    }
}
