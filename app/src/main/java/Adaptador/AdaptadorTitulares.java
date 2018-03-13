package Adaptador;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.progland.labuna.R;

import Personal.Titular;

/**
 * Created by DeeJa on 13/3/2018.
 */

public class AdaptadorTitulares extends ArrayAdapter {

    Activity context;
    Titular[] datos;

    public AdaptadorTitulares(Activity context,Titular[] datos)
    {
        super(context, R.layout.adaptador_personal_lista_1, datos);
        this.datos = datos;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.adaptador_personal_lista_1, null);

        TextView titulo = (TextView)item.findViewById(R.id.titulo);
        titulo.setText(datos[position].getTitulo());
        TextView desc = (TextView)item.findViewById(R.id.desc);
        desc.setText(datos[position].getDescripcion());
        ImageView imagen = (ImageView)item.findViewById(R.id.imageView2);
        imagen.setImageResource(datos[position].getImg());

        return item;

    }


}
