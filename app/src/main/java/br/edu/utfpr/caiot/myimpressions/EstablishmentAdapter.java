package br.edu.utfpr.caiot.myimpressions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.caiot.myimpressions.model.Establishment;

public class EstablishmentAdapter extends ArrayAdapter<Establishment>{

    public EstablishmentAdapter(Context context, List<Establishment> establishments){
        super(context, 0, establishments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Establishment establishment = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.establishment_item, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);

        tvName.setText(establishment.getName());
        tvAddress.setText(establishment.getAddress());

        return convertView;
    }

}
