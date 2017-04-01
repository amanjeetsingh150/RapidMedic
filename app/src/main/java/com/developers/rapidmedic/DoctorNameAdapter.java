package com.developers.rapidmedic;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gagan on 1/4/17.
 */

public class DoctorNameAdapter extends ArrayAdapter<String> {
    private ArrayList<String> doctorNames;
    private String name;
    public DoctorNameAdapter(Context context, ArrayList<String> doctorNames) {
        super(context, 0, doctorNames);
        this.doctorNames = doctorNames;
    }

    private TextView doctorNameTV, doctorNameHindiTV, doctorTypeTV, doctorTypeHindiTV;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.doctor_item, parent, false);
        }
        name = doctorNames.get(position);
        doctorNameTV = (TextView) listItemView.findViewById(R.id.doc_name_eng);
        doctorNameHindiTV = (TextView) listItemView.findViewById(R.id.doc_name_hin);
        doctorTypeTV = (TextView) listItemView.findViewById(R.id.doc_type_eng);
        doctorTypeHindiTV = (TextView) listItemView.findViewById(R.id.doc_type_hin);
        switch (name){
            case "Dr. Shastri" :
                doctorNameTV.setText("Dr. Shastri");
                doctorNameHindiTV.setText("डॉ शास्त्री");
                doctorTypeTV.setText("Cardiologist");
                doctorTypeHindiTV.setText("हृदय रोग विशेषज्ञ");
                break;
            case "Dr. Reema" :
                doctorNameTV.setText("Dr. Reema");
                doctorNameHindiTV.setText("डॉ रीमा");
                doctorTypeTV.setText("Allergist");
                doctorTypeHindiTV.setText("एलर्जिस्ट");
                break;
            case "Dr. Pooja" :
                doctorNameTV.setText("Dr. Pooja");
                doctorNameHindiTV.setText("डॉ पूजा");
                doctorTypeTV.setText("Dermatologist");
                doctorTypeHindiTV.setText("त्वचा विशेषज्ञ");
                break;
            case "Dr. Venkat" :
                doctorNameTV.setText("Dr. Venkat");
                doctorNameHindiTV.setText("डॉ वेंकट");
                doctorTypeTV.setText("Endocrinologist");
                doctorTypeHindiTV.setText("एंडोक्राइनोलॉजिस्ट");
                break;
            case "Dr. Reddy" :
                doctorNameTV.setText("Dr. Reddy");
                doctorNameHindiTV.setText("डॉ रेड्डी");
                doctorTypeTV.setText("Hepatologist");
                doctorTypeHindiTV.setText("हेपेटोलॉजिस्ट");
                break;
            case "Dr. Singh" :
                doctorNameTV.setText("Dr. Singh");
                doctorNameHindiTV.setText("डॉ सिंह");
                doctorTypeTV.setText("Physiatrist");
                doctorTypeHindiTV.setText("फ़िज़ियाट्रिस्ट");
                break;
            case "Dr. Jain" :
                doctorNameTV.setText("Dr. Jain");
                doctorNameHindiTV.setText("डॉ जैन");
                doctorTypeTV.setText("Pediatrician");
                doctorTypeHindiTV.setText("बाल रोग विशेषज्ञ");
                break;
        }
        return listItemView;
    }
}
