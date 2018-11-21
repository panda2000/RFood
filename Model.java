package ru.seminma.rfood;


import android.content.Intent;

public class Model {
    public Intent share (String message, String intentFilter){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plant");
        intent.setPackage(intentFilter);
        return intent;
    }

}
