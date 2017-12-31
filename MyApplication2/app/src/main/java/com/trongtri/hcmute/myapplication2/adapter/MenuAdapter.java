package com.trongtri.hcmute.myapplication2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.trongtri.hcmute.myapplication2.R;
import com.trongtri.hcmute.myapplication2.activity.HistoryActivity;
import com.trongtri.hcmute.myapplication2.activity.LoginActivity;
import com.trongtri.hcmute.myapplication2.activity.MainActivity;

/**
 * Created by Dell on 12/29/2017.
 */

public class MenuAdapter extends BaseAdapter {

    String[] result;
    Context context;
    int[] imageId;

    public MenuAdapter(String[] result, Context context, int[] imageId) {
        this.result = result;
        this.context = context;
        this.imageId = imageId;
    }

    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_menu, null);

        TextView tvNoiDung = (TextView) rowView.findViewById(R.id.txtMenu);
        ImageView imgAvatar = (ImageView) rowView.findViewById(R.id.imgAnhMenu);

        tvNoiDung.setText(result[i]);
        imgAvatar.setImageResource(imageId[i]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i == 0){
                    Intent intent = new Intent(context, HistoryActivity.class);
                    context.startActivity(intent);

                }
                else if(i == 2){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);


                }
               // Toast.makeText(context, "You Clicked " + result[i], Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }
}
