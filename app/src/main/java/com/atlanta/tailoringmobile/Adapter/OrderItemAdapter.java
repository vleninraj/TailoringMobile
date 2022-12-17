package com.atlanta.tailoringmobile.Adapter;

import androidx.appcompat.app.AppCompatActivity;

import com.atlanta.tailoringmobile.Common;
import com.atlanta.tailoringmobile.Models.JobCardDTL;
import com.atlanta.tailoringmobile.Models.JobCardHDR;
import com.atlanta.tailoringmobile.R;
import com.atlanta.tailoringmobile.ViewHolder.ViewHolderItemList;
import com.atlanta.tailoringmobile.ViewHolder.ViewHolderOrderList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class OrderItemAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = "OrderItemAdapter";
    Activity _context;
    ArrayList<JobCardDTL> _orderdtls;
    ArrayList<JobCardDTL> _orderdtlsCopy;


    public OrderItemAdapter(Activity  context, ArrayList<JobCardDTL> orderdtls) {
        //   super(c, R.layout.listview,workid);
        this._context=context;
        this._orderdtls=orderdtls;
    }
    @Override
    public int getCount() {
        return _orderdtls.size();
    }

    @Override
    public Object getItem(int i) {
        return _orderdtls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vw=view;
        ViewHolderItemList _viewholder=null;
        if(vw==null)
        {
            LayoutInflater layoutInflater=_context.getLayoutInflater();
            vw=layoutInflater.inflate(R.layout.activity_order_item_adapter,viewGroup,false);
            _viewholder=new ViewHolderItemList(vw);
            vw.setTag(_viewholder);
        }
        else{
            _viewholder=(ViewHolderItemList) vw.getTag();
        }

        final JobCardDTL _dtl = (JobCardDTL) this.getItem(i);
        _viewholder.txtdtlproductname.setText(_dtl.getProductName());
        Double dblRate=Double.valueOf(_dtl.getRate());
        Double dblQty=Double.valueOf(_dtl.getQty());
        Double dblAmount=dblQty * dblRate;
        _viewholder.txtdtlamount.setText(String.format(Common.sformat,dblAmount));
        _viewholder.txtdtlQty.setText(String.format(Common.sformat,dblQty));
        _viewholder.txtdtlrate.setText(String.format(Common.sformat,dblRate));
        _viewholder.txtdtlproductname.setTag(_dtl.getProductID());
        _viewholder.txtdtlamount.setTag(_dtl.getId());
        _viewholder.txtdtlsubpartyName.setText(_dtl.getSubPartyName());
        _viewholder.txtdtlsubpartyName.setTag(_dtl.getSubPartyCode());

        return vw;
    }
    @Override
    public Filter getFilter()
    {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final FilterResults oReturn=new FilterResults();
                final ArrayList<JobCardDTL> results=new ArrayList<>();
                if(_orderdtlsCopy==null){
                    _orderdtlsCopy=_orderdtls;
                }
                if(constraint!=null)
                {
                    if(_orderdtlsCopy!=null && _orderdtlsCopy.size()>0)
                    {
                        for(final JobCardDTL dtl:_orderdtlsCopy) {
                            if (dtl.getId().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || dtl.getProductName().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || dtl.getSubPartyName().toLowerCase().contains(constraint.toString().toLowerCase())
                            )
                            {
                                results.add(dtl);
                            }
                        }
                    }
                    oReturn.values=results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                _orderdtls= (ArrayList<JobCardDTL>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }
}