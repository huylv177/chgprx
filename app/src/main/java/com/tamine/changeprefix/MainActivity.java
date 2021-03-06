package com.tamine.changeprefix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tamine.changeprefix.Utils.contacts;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.contact_rv)
    RecyclerView contact_rv;
    @BindView(R.id.contact_pb)    ProgressBar contact_pb;
    ContactAdapter contactAdapter;

    private Comparator<? super Contact> Comparator_NAME = new Comparator<Contact>() {

        @Override
        public int compare(Contact arg0, Contact arg1) {
            return arg0.getName().toLowerCase().compareTo(arg1.getName().toLowerCase());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        contactAdapter = new ContactAdapter(this, contacts);
//        contact_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        contact_rv.setLayoutManager(new LayoutManager(this));
        contact_rv.setAdapter(contactAdapter);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED) {
            getContacts();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }




    }

    @OnClick(R.id.btScan)
    void scan(){
        startActivity(new Intent(MainActivity.this,ReviewActivity.class));
    }

    void getContacts(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                contact_pb.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                contacts = new ArrayList<Contact>();
                Cursor cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
                while (cursor.moveToNext()) {
                    String name =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contacts.add(new Contact(name, phoneNumber));
                }
                cursor.close();
//                String[] as = getResources().getStringArray(R.array.country_names);
//                for(String a : as){
//                    contacts.add(new Contact(a,"111111"));
//                }
//
                Collections.sort(contacts, Comparator_NAME);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                contact_pb.setVisibility(View.GONE);
                contactAdapter.redrawList(contacts);
            }
        }.execute();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContacts();
                }
            }
        }
    }

}
