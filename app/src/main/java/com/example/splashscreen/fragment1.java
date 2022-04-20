package com.example.splashscreen;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.splashscreen.databinding.Fragment1Binding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class fragment1 extends Fragment{
    public fragment1(){}
    Fragment1Binding binding;
    ArrayList<taskiHome> list= new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Workspaces");
    private FirebaseAuth mAuth; // added Firebase Authentication object to access the user ids.
    String name; // variable to store the workspaces creators name
    private Button joinWorkspace;
    int myPass;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding= Fragment1Binding.inflate(inflater,container,false);
        FloatingActionButton fab = binding.fab;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.f1RecyclerView.setLayoutManager(layoutManager);
        joinWorkspace = binding.joinWorkspace;


        mAuth = FirebaseAuth.getInstance(); //
        // the reference is to the personal workspaces section within the user node
        DatabaseReference userRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Personal Workspaces");// ******** from small u to capital Users. Since that's the reference in the sign up. in mainActivity2



        // On change Listener FOR THE information in their personal workspaces
        userRef.addValueEventListener(new ValueEventListener() { // so now this is userRef instead of myRef so that it shows the information within the Personal workspaces not the entire workspace

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot wSpaceSnapshot: snapshot.getChildren()){
                    taskiHome workspaces = wSpaceSnapshot.getValue(taskiHome.class);
                    list.add(workspaces);
                    RecyclerViewAdapterHome adapter= new RecyclerViewAdapterHome(list, getContext());
                    binding.f1RecyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        joinWorkspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button WKCancel, WKCreate;
                EditText workSPID2, workSPpass;
                Dialog dialog = new Dialog(fragment1.this.getContext());
                dialog.setContentView(R.layout.joinwkspacedialog);
                dialog.show();
                WKCancel = dialog.findViewById(R.id.WKCancel);
                WKCreate = dialog.findViewById(R.id.WKCreate);
                workSPID2 = dialog.findViewById(R.id.workSPID2);
                workSPpass = dialog.findViewById(R.id.workSPpass);

                WKCancel.setOnClickListener(view1 -> {
                    dialog.dismiss();

                });

                WKCreate.setOnClickListener(view1 -> {
                    String workspace = workSPID2 .getText().toString();
                    int pass = Integer.parseInt(workSPpass.getText().toString());
                    DatabaseReference ref = database.getReference("Workspaces").child(workspace);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            taskiHome Myworkspace =snapshot.getValue(taskiHome.class);
                            int Password = Myworkspace.getPass();
                            if(Password  == pass){
                                //DatabaseReference ref2 = database.getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Personal Workspaces");
                               userRef.child(Myworkspace.getWorkspaceID()).setValue(Myworkspace);
                                //reference to members node to add the creator as the first member
                                DatabaseReference membersRef = database.getReference("Workspaces").child(Myworkspace.getWorkspaceID()).child("Members");
                                String creatorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference myRef2 = database.getReference("Users").child(creatorID).child("username"); // ******** from small u to capital Users. Since that's the reference in the sign up. in mainActivity2
                                myRef2 // get the creators name so that we can display it as one of the members names
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                name = snapshot.getValue(String.class);
                                                User creator= new User(name,creatorID); // create a user object with the name and id for the creator // ********
                                                membersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(creator); // Add creator as the first member
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }});


                                dialog.dismiss();

                                Toast.makeText(getContext(), " Joined workspace ", Toast.LENGTH_LONG).show();

                            }
                            else{
                                Toast.makeText(getContext(), " Incorrect Password ", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(),"Something wrong happened!",Toast.LENGTH_LONG).show();

                        }
                    });

                });

            }
        });

        fab.setOnClickListener(view -> {

            Button btnCancel, btnCreate;
            EditText eCreateWorkspace;
            Dialog dialog = new Dialog(fragment1.this.getContext());
            dialog.setContentView(R.layout.fragment1_dialog);
            dialog.show();
            btnCancel = dialog.findViewById(R.id.editCancel);
            btnCreate = dialog.findViewById(R.id.editCreate);
            eCreateWorkspace = dialog.findViewById(R.id.etCreateWorkspace);

            btnCancel.setOnClickListener(view1 -> {
                dialog.dismiss();

            });
            btnCreate.setOnClickListener(view1 -> {
                Random r = new Random();
                String myTask = eCreateWorkspace.getText().toString();
                myPass  = r.nextInt(999999 - 100000) + 100000;
                taskiHome workspace = new taskiHome(myTask, FirebaseAuth.getInstance().getCurrentUser().getUid(), "not set",myPass); // the workspace ID is not set yet because the uniqueID hasn't been generated yet
                // unique ID starts with the name then the random digits- unnecessary just thought it helped
                String uniqueID = workspace.getWorkspaceName().toString()+"-"+UUID.randomUUID().toString(); // need object workspace to exist so as to get workspace name on this line for the unique id
                workspace = new taskiHome(myTask, FirebaseAuth.getInstance().getCurrentUser().getUid(), uniqueID,myPass);
                myRef.child(uniqueID).setValue(workspace);
                userRef.child(uniqueID).setValue(workspace); // add the value of the created workspace to the users personal workspaces as well

                //reference to members node to add the creator as the first member
                DatabaseReference membersRef = database.getReference("Workspaces").child(uniqueID).child("Members");
                String creatorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference myRef2 = database.getReference("Users").child(creatorID).child("username"); // ******** from small u to capital Users. Since that's the reference in the sign up. in mainActivity2
                myRef2 // get the creators name so that we can display it as one of the members names
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                name = snapshot.getValue(String.class);
                                User creator= new User(name,creatorID); // create a user object with the name and id for the creator // ********
                                membersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(creator); // Add creator as the first member
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }});


                dialog.dismiss();


            });


        });


        return binding.getRoot();


    }


}
