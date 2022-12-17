package com.atlanta.tailoringmobile.Adapter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.atlanta.tailoringmobile.Common;
import com.atlanta.tailoringmobile.Models.JobCardHDR;
import com.atlanta.tailoringmobile.R;
import com.atlanta.tailoringmobile.ViewHolder.ViewHolderOrderList;

import java.util.ArrayList;
import java.util.Date;

public class OrderListAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = "OrderListAdapter";
    Activity _context;
    ArrayList<JobCardHDR> _orders;
    ArrayList<JobCardHDR> _ordersCopy;

    public OrderListAdapter(Activity  context, ArrayList<JobCardHDR> orders) {
        //   super(c, R.layout.listview,workid);
        this._context=context;
        this._orders=orders;
    }
    @Override
    public int getCount() {
        return _orders.size();
    }

    @Override
    public Object getItem(int i) {
        return _orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vw=view;
        ViewHolderOrderList _viewholder=null;
        if(vw==null)
        {
            LayoutInflater layoutInflater=_context.getLayoutInflater();
            vw=layoutInflater.inflate(R.layout.activity_order_list_adapter,viewGroup,false);
            _viewholder=new ViewHolderOrderList(vw);
            vw.setTag(_viewholder);
        }
        else{
            _viewholder=(ViewHolderOrderList) vw.getTag();
        }

        final JobCardHDR _order = (JobCardHDR) this.getItem(i);
        _viewholder.txtvoucherno.setText(_order.getVoucherNo());
        _viewholder.txtpartyName.setText(_order.getPartyName());
        _viewholder.txtgrandamount.setText(String.format(Common.sformat,_order.getGrandAmount()));
        Date date = new Date(Long.valueOf(_order.getOrderDate()));
        _viewholder.txtvoucherdate.setText(Common.FormatDate(date));

        return vw;
    }

    @Override
    public Filter getFilter()
     {
         return new Filter() {
             @Override
             protected FilterResults performFiltering(CharSequence constraint) {

                 final FilterResults oReturn=new FilterResults();
                 final ArrayList<JobCardHDR> results=new ArrayList<>();
                 if(_ordersCopy==null){
                     _ordersCopy=_orders;
                 }
                if(constraint!=null)
                {
                    if(_ordersCopy!=null && _ordersCopy.size()>0)
                    {
                        for(final JobCardHDR hdr:_ordersCopy) {
                            if (hdr.getPartyName().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || hdr.getVoucherNo().toLowerCase().contains(constraint.toString().toLowerCase())
                            )
                            {
                                results.add(hdr);
                            }
                        }
                    }
                    oReturn.values=results;
                }
                 return oReturn;
             }

             @Override
             protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                 _orders= (ArrayList<JobCardHDR>) filterResults.values;
                 notifyDataSetChanged();
             }
         };

     }

}