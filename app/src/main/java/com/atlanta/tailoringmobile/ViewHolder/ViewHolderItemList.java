package com.atlanta.tailoringmobile.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.atlanta.tailoringmobile.R;

import org.w3c.dom.Text;

public class ViewHolderItemList {

    public TextView txtdtlproductname,txtdtlamount,txtdtlQty,txtdtlrate,txtdtlsubpartyName;
    public ViewHolderItemList(View v)
    {
        txtdtlproductname=(TextView)v.findViewById(R.id.txtdtlproductname);
        txtdtlamount=(TextView)v.findViewById(R.id.txtdtlamount);
        txtdtlQty=(TextView)v.findViewById(R.id.txtdtlQty);
        txtdtlrate=(TextView)v.findViewById(R.id.txtdtlrate);
        txtdtlsubpartyName=(TextView)v.findViewById(R.id.txtdtlsubpartyName);

    }
}
