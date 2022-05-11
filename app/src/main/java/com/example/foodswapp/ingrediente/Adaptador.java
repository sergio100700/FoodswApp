package com.example.foodswapp.ingrediente;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.foodswapp.R;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter {

    private Context context;
    private ArrayList<IngredienteLista> ingrediente;

    public Adaptador(Context context, ArrayList<IngredienteLista> ingrediente) {
        this.context = context;
        this.ingrediente = ingrediente;
    }

    @Override
    public int getCount() {
        return ingrediente.size();
    }

    @Override
    public Object getItem(int i) {
        return ingrediente.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        IngredienteLista ingrediente = (IngredienteLista) getItem(i);

        view = LayoutInflater.from(context).inflate(R.layout.ingrediente,null);

        TextView texto = view.findViewById(R.id.tvIngrediente);
        TextView fecha = view.findViewById(R.id.tvFecha);

        fecha.setText(ingrediente.getDate().getDayOfMonth()+ "/"+ ingrediente.getDate().getMonthValue());
        texto.setText(ingrediente.getNombre());

        if(ingrediente.isDone()){
            texto.setPaintFlags(texto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            texto.setPaintFlags(texto.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return view;

    }
}

