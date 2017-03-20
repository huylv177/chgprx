package com.tamine.changeprefix;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Contact> contacts = new ArrayList<>();
    @BindView(R.id.contact_rv)
    RecyclerView contact_rv;
    ContactAdapter contactAdapter;
    @BindView(R.id.contact_pb)    ProgressBar contact_pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        contact_rv.setLayoutManager(llm);
        contactAdapter = new ContactAdapter(this, contacts);
        contact_rv.setAdapter(contactAdapter);


        new TedPermission(this).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                GetContactTask t = new GetContactTask(MainActivity.this,contact_pb,contactAdapter);
                t.execute();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(PickPhotoActivity.this,"STOP",Toast.LENGTH_SHORT).show();
                Utils.createAlertDialog(MainActivity.this,"STOPP");
            }
        }).setDeniedMessage(getString(R.string.deny_message))
                .setPermissions(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS).check();


    }

    @OnClick(R.id.btChange)
    void change() {
        startActivity(new Intent(this, ReviewActivity.class));
    }
}
