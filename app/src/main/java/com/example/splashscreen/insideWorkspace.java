package com.example.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

public class insideWorkspace extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  RecyclerViewAdapterInsideWorkspaces recyclerViewAdapter;
    private FloatingActionButton fab;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;//
    private MaterialTimePicker picker;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    private ImageButton img_button_heading;
    String title ;
    String wID;
    int password;

    TextView tv_workspaceTitle;

    // Initialize elements for the members dropdown menu
    boolean [] selectedMember; // this will be 1, for if a member is selected and 0 if its not
    ArrayList<Integer> memberList = new ArrayList<>(); // holds the position of the days selected
    String [] membersArray = {}; // holds the members names that will appear in the drop box
    ArrayList<String> memberArrayList= new ArrayList<>(Arrays.asList(membersArray));
    ArrayList<String> memberIDList= new ArrayList<String>(); // holds the members IDs, so we can link an id and a name given their positions in the list.
    // e.g the id at position 1 and the name at position 1 of the memberArraylist are of the same person
    ArrayList<String> selectedMemberIDs = new ArrayList<String>(); // this will store the IDs of the members selected. The ones assigned to a task


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_inside_workspace);


        // Worspace title
        tv_workspaceTitle = findViewById(R.id.tv_workspaceTitle);
        img_button_heading = findViewById(R.id.img_button_heading);

//        String title=intent.getStringExtra("wName");
        // Workspace ID
        Intent intent= getIntent();
        wID=intent.getStringExtra("wID"); // Receive the workspace ID
        DatabaseReference myRef2 = database.getReference("Workspaces").child(wID).child("Tasks");// Put the tasks inside the workspace node, because tasks are specific to their workspaces, so that the recycler view shows the relevant tasks for a certain workspace

        // Add to the members Array. This displays the members to choose from
        DatabaseReference membersRef = database.getReference("Workspaces").child(wID).child("Members"); // reference to the members node. This is where we will get the list of members to chose from
        DatabaseReference ref = database.getReference("Workspaces").child(wID).child("workspaceName");
        DatabaseReference passref = database.getReference("Workspaces").child(wID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                title = snapshot.getValue(String.class);
//                if(title.isEmpty() == true) {
//                   title = "workspace does not exist";
//                }

                tv_workspaceTitle.setText(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        membersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear these so we can refresh them
                memberArrayList.clear();
                memberIDList.clear();
                for(DataSnapshot membersSnapshot: snapshot.getChildren()){
                    User member = membersSnapshot.getValue(User.class);// ******** from Users class to User
                    String memberName= member.getUsername(); // get the name of the member// ********
                    String memberID= member.getUserID(); // get the id of the member// ********
                    memberArrayList.add(memberName); // add the name to the memberArrayList for display
                    memberIDList.add(memberID); // add the ID
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        fab = findViewById(R.id.fabInWorkspace);
        recyclerView = findViewById(R.id.insideWspaceRecy);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<taskiHome> list= new ArrayList<>();


        // On change Listener
        myRef2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot TasksSnapshot: snapshot.getChildren()){
                    taskiHome tasks = TasksSnapshot.getValue(taskiHome.class);
                    list.add(tasks);
                    // update recycler view on changes
                    recyclerViewAdapter = new RecyclerViewAdapterInsideWorkspaces (list,getApplicationContext(),wID,title);
                    recyclerView.setAdapter(recyclerViewAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

       passref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               taskiHome workspaceInfo = snapshot.getValue(taskiHome.class);
               password = workspaceInfo.getPass();

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
        img_button_heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Button btnEdit, btnDelete,btnShare;
                Dialog dialog = new Dialog(insideWorkspace.this); // insideWorkspace
                dialog.setContentView(R.layout.moreinfo2);
                dialog.show();

                btnEdit = dialog.findViewById(R.id.shareEdit);
                btnDelete = dialog.findViewById(R.id.shareDelete);
                btnShare = dialog.findViewById(R.id.sharebutton);

                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String message = "Workspace Details \n" +
                                "Workspace ID:" + wID + "\n"+
                                "Workspace Password: " + password + "\n";
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Join Taski Workspace");// doesnt show up
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        startActivity(Intent.createChooser(intent, "Share Via")); // doesnt show up
                    }
                });

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Button renameCancel, renameCreate;
                        EditText renameWorkspace;
                        Dialog dialog2 = new Dialog(insideWorkspace.this); // insideWorkspace
                        dialog2.setContentView(R.layout.rename_workspace_dialog);
                        dialog2.show();

                        renameCancel = dialog2.findViewById(R.id.renameCancel);
                        renameCreate = dialog2.findViewById(R.id.renameCreate);
                        renameWorkspace = dialog2.findViewById(R.id.RenameWorkspace);

                        renameCreate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String newWorkspacename = renameWorkspace.getText().toString();
                                tv_workspaceTitle.setText(newWorkspacename);
                                ref.setValue(newWorkspacename);
                                dialog2.dismiss();

                            }
                        });
                        renameCancel.setOnClickListener(view1 -> {
                            dialog2.dismiss();
                        });

                        dialog.dismiss();
                    }


                });
                btnDelete.setOnClickListener(view1 -> {
                    DatabaseReference ref3 = database.getReference("Workspaces").child(wID);
                    ref3.removeValue();
                    list.clear();
                    wID = "";
                    recyclerViewAdapter = new RecyclerViewAdapterInsideWorkspaces (list,getApplicationContext(),wID,title);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    dialog.dismiss();

                });


            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar myCalendar = Calendar.getInstance();
                Button btnTask, createTaskCancel;
                EditText Tasks, createDueDate,  time;
                TextView tv_members;


                Dialog dialog = new Dialog(insideWorkspace.this); // insideWorkspace
                dialog.setContentView(R.layout.inworkspacedialog);
                dialog.show();
                btnTask = dialog.findViewById(R.id.BtnTasks);
                createTaskCancel = dialog.findViewById(R.id.createTaskCancel);
                Tasks = dialog.findViewById(R.id.Tasks);
                createDueDate = dialog.findViewById(R.id.createDueDate);
                time = dialog.findViewById(R.id.time);
                tv_members = dialog.findViewById(R.id.tv_dropDownmembers);

                // ***** left these two out
                membersArray = memberArrayList.toArray(membersArray); // change the arraylist back to an array cause the drop down only works with arrays
                // Initialize selected member array
                // this holds all the members who've been selected
                selectedMember= new boolean[membersArray.length];

                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                    private void updateLabel(){
                        String myFormat = "MM/dd/yy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        createDueDate.setText(sdf.format(myCalendar.getTime()));
                    }

                };
                createDueDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(insideWorkspace.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                time .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        picker = new MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_12H)
                                .setHour(12)
                                .setMinute(0)
                                .setTitleText("Select Alarm Time")
                                .build();

                        picker.show(getSupportFragmentManager(),"taski");

                        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (picker.getHour() > 12){

                                    time .setText(
                                            String.format("%02d",(picker.getHour()-12))+" : "+String.format("%02d",picker.getMinute())+" PM"
                                    );

                                }else {

                                    time .setText(picker.getHour()+" : " + picker.getMinute() + " AM");

                                }

                                calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                                calendar.set(Calendar.MINUTE,picker.getMinute());
                                calendar.set(Calendar.SECOND,0);
                                calendar.set(Calendar.MILLISECOND,0);

                                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                                Intent intent = new Intent(insideWorkspace.this,AlarmReceiver.class);


                                pendingIntent = PendingIntent.getBroadcast(insideWorkspace.this,0,intent,0);

                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                                        AlarmManager.INTERVAL_DAY,pendingIntent);



                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    CharSequence name = "testing..";
                                    String description = "testing..";
                                    int importance = NotificationManager.IMPORTANCE_HIGH;
                                    NotificationChannel channel = new NotificationChannel("taski",name,importance);
                                    channel.setDescription(description);

                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);

                                }

                                Toast.makeText(insideWorkspace.this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();


                            }
                        });


                    }
                });


                tv_members.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Initialize the alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                insideWorkspace.this
                        );

                        // Set the title of the Alert dialog
                        builder.setTitle("Select Members");

                        // set dialog non cancelable
                        builder.setCancelable(false);


                        builder.setMultiChoiceItems(membersArray, selectedMember, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                // check condition
                                // This part is creating a list of all the positions selected
                                if(b){
                                    // when the check box is selected
                                    // add position to the member list
                                    memberList.add(i);

                                    // sort the members list - not sure this is necessary
                                    Collections.sort(memberList);

                                }else{
                                    // when the checkbox is unselected
                                    // remove the position from the list
                                    memberList.remove(i);

                                }
                            }
                        });

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // initialize string builder
                                StringBuilder stringBuilder = new StringBuilder();
                                selectedMemberIDs.clear();// refresh this so we can have a new list of members selected



                                // use for loop
                                // loop through the members list
                                for(int j=0; j<memberList.size();j++){
                                    // Concatenate array value
                                    stringBuilder.append(membersArray[memberList.get(j)]); // Add the names in the positions selected to the string buffer
                                    selectedMemberIDs.add(memberIDList.get(memberList.get(j))); // Add the ID in the positions selected to the list.


                                    // check condition
                                    if(j!= memberList.size()-1){
                                        // when j value not equal to memebrs list size-1
                                        // Add comma
                                        stringBuilder.append(", "); // for formating


                                    }


                                }
                                // set text on text view
                                tv_members.setText(stringBuilder.toString());


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Dismiss dialog
                                dialogInterface.dismiss();
                            }
                        });

                        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // user for loop
                                for(int j=0; j<selectedMember.length;j++){
                                    // Remove all selection
                                    selectedMember[j]=false;
                                    // clear memeber from list
                                    memberList.clear();
                                    // clear text view value
                                    tv_members.setText("");
                                    //selectedMemberIDs.clear();


                                }
                            }

                        });
                        // show dialog
                        builder.show();


                    }
                });
                createTaskCancel.setOnClickListener(view1 -> {
                    dialog.dismiss();

                });
                btnTask.setOnClickListener(view1 -> {
                    String myTask = Tasks.getText().toString();
                    String myDueDate = createDueDate .getText().toString();
                    String myMembers= tv_members.getText().toString();
                    String myTime= time.getText().toString();
                    String myStatus = "Not Done";

                    taskiHome groupTasks = new taskiHome(myTask,myDueDate, myStatus, myMembers, wID, myTime,"uniqueID");

                    // unique ID starts with the name then the random digits- unnecessary just thought it helped
                    String uniqueID = groupTasks.getTaskName().toString()+"-"+ UUID.randomUUID().toString();
                    groupTasks = new taskiHome(myTask,myDueDate, myStatus, myMembers, wID, myTime,uniqueID);

                    myRef2.child(uniqueID).setValue(groupTasks);

                    //****** left out this part
                    //assign it to the members so that it shows up on their my tasks section
                    for (int i = 0; i < selectedMemberIDs.size(); i++) {
                        String assignedTo= selectedMemberIDs.get(i);
                        DatabaseReference myRef3 = database.getReference("Users").child(assignedTo).child("My Tasks");
                        taskiHome personalTask = new taskiHome(groupTasks.getTaskName(),groupTasks.getDueDate(),assignedTo, title,groupTasks.getTime(),groupTasks.getTaskID());

                        myRef3.child(uniqueID).setValue(personalTask);
                    }

                    dialog.dismiss();

                });
            }
        });



    }
}