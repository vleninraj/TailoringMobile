package com.atlanta.tailoringmobile.Adapter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atlanta.tailoringmobile.Common;
import com.atlanta.tailoringmobile.Models.MeasurementDTL;
import com.atlanta.tailoringmobile.R;
import com.atlanta.tailoringmobile.ViewHolder.ViewHolderMeasurement;

import java.util.ArrayList;

public class MeasurementListAdapter extends BaseAdapter {
    private static final String TAG = "MeasurementListAdapter";
    Activity _context;
    ArrayList<MeasurementDTL> measurementDtls;

    public MeasurementListAdapter(Activity  context, ArrayList<MeasurementDTL> _measurementDtls) {
        //   super(c, R.layout.listview,workid);
        this._context=context;
        this.measurementDtls=_measurementDtls;
    }
    @Override
    public int getCount() {
        return measurementDtls.size();
    }

    @Override
    public Object getItem(int i) {
        return measurementDtls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vw=view;
        ViewHolderMeasurement _viewholder=null;
        if(vw==null)
        {
            LayoutInflater layoutInflater=_context.getLayoutInflater();
            vw=layoutInflater.inflate(R.layout.activity_measurement_list_adapter,viewGroup,false);
            _viewholder=new ViewHolderMeasurement(vw);
            vw.setTag(_viewholder);
        }
        else{
            _viewholder=(ViewHolderMeasurement) vw.getTag();
        }

        final MeasurementDTL _dtl = (MeasurementDTL) this.getItem(i);
        _viewholder.txtmeasurementname.setText(_dtl.getMeasurementName());
        _viewholder.txtmeasurementname.setTag(_dtl.getId());
        _viewholder.txtmeasurementValue.setText(String.format(Common.sformat,_dtl.getMeasurementValue()));
        _viewholder.txtmeasurementValue.setTag(_dtl.getMeasurementID());
        _viewholder.txtmeasurementValue.selectAll();
        _viewholder.txtmeasurementValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().equals(""))
                {
                    Double dblValue=Double.valueOf(charSequence.toString());
                    _dtl.setMeasurementValue(dblValue);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return vw;
    }
}