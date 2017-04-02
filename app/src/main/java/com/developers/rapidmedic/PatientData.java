package com.developers.rapidmedic;

import com.google.zxing.common.StringUtils;

/**
 * Created by Amanjeet Singh on 02-Apr-17.
 */

public class PatientData {
    String temp;
    String hearRate;
    String name;
    String age;
    String gender;
    String mob;
    public PatientData(){

    }
    public PatientData(String name,String temp,String heartRate,String age,String gender,String mob){
        this.name=name;
        this.age=age;
        this.mob=mob;
        this.hearRate=heartRate;
        this.gender=gender;
        this.temp=temp;
    }
}
