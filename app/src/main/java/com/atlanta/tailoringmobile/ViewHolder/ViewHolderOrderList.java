package com.atlanta.tailoringmobile.ViewHolder;

import android.view.View;
import android.widget.TextView;
import com.atlanta.tailoringmobile.R;

public class ViewHolderOrderList {
    public TextView txtvoucherno,txtvoucherdate,txtpartyName,txtgrandamount;
    public ViewHolderOrderList(View v)
    {
        txtvoucherno=(TextView)v.findViewById(R.id.txtvoucherno);
        txtpartyName=(TextView)v.findViewById(R.id.txtpartyName);
        txtvoucherdate=(TextView)v.findViewById(R.id.txtvoucherdate);
        txtgrandamount=(TextView)v.findViewById(R.id.txtgrandamount);

    }
}
