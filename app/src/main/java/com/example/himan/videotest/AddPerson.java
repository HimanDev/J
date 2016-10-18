package com.example.himan.videotest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.himan.videotest.domains.PersonDto;
import com.example.himan.videotest.repository.PersonDatabaseRepo;

/**
 * Created by himan on 27/7/16.
 */
public class AddPerson extends Activity {

    private ImageView imageViewClose,imageViewSave;
    private EditText  editTextName,editTextPhone,editTextCode,editTextEmail;
    private PersonDatabaseRepo personDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_person);
        personDatabaseHandler=new PersonDatabaseRepo();
        imageViewClose=(ImageView)findViewById(R.id.imageViewClose);
        imageViewSave=(ImageView)findViewById(R.id.imageViewSave);
        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextPhone=(EditText)findViewById(R.id.editTextPhoneNo);
        editTextCode=(EditText)findViewById(R.id.editTextCode);
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);

        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(personDatabaseHandler.getContactsCount()>=4){
                    Toast.makeText(AddPerson.this,"you can not add more than 4 users",Toast.LENGTH_LONG).show();
                }else {
                    PersonDto person=new PersonDto(editTextName.getText().toString(),
                            editTextPhone.getText().toString(),editTextCode.getText().toString(),
                            editTextEmail.getText().toString());
                    personDatabaseHandler.addContact(person);
                    Toast.makeText(AddPerson.this,"Saved",Toast.LENGTH_LONG).show();


                }

            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPerson.this.finish();
//                overridePendingTransition(R.anim.push_left, R.anim.push_right);


            }
        });
    }

}
