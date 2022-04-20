package com.example.splashscreen;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
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

public class RecyclerViewAdapterInsideWorkspaces extends  RecyclerView.Adapter<RecyclerViewAdapterInsideWorkspaces.ViewHolder> {
    private ArrayList<taskiHome> myList;
    Context context;
    private MaterialTimePicker picker;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    String wID;
    String status = "Not Done";
    String title;
    int flag = 0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    // Initialize elements for the members dropdown menu
    boolean [] selectedMember; // this will be 1, for if a member is selected and 0 if its not
    ArrayList<Integer> memberList = new ArrayList<>(); // holds the position of the days selected
    String [] membersArray = {}; // holds the members names that will appear in the drop box
    ArrayList<String> memberArrayList= new ArrayList<>(Arrays.asList(membersArray));
    ArrayList<String> memberIDList= new ArrayList<String>(); // holds the members IDs, so we can link an id and a name given their positions in the list.
    // e.g the id at position 1 and the name at position 1 of the memberArraylist are of the same person
    ArrayList<String> selectedMemberIDs = new ArrayList<String>(); // this will store the IDs of the members selected. The ones assigned to a task



    public RecyclerViewAdapterInsideWorkspaces(ArrayList myList, Context context, String wID, String title) {
        this.myList = myList;
        this.context = context;
        this.wID = wID;
        this.title = title;



    }

    @NonNull
    @Override
    public RecyclerViewAdapterInsideWorkspaces.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        DatabaseReference membersRef = database.getReference("Workspaces").child(wID).child("Members"); // reference to
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

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inside_workspace_rows, parent, false);
        return new ViewHolder(view).myDialogs(this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterInsideWorkspaces.ViewHolder holder, int position) {


        holder.tv_taskTitle.setText(myList.get(position).getTaskName());
        holder.tv_dueDate.setText(myList.get(position).getDueDate());
        holder.tv_members.setText(myList.get(position).getMembers());
        holder.tv_time.setText(myList.get(position).getTime());


        holder.tv_status.setText(status);
        DatabaseReference checkStatus = database.getReference("Workspaces").child(wID).child("Tasks").
                child(myList.get(holder.getAdapterPosition()).getTaskID());

        checkStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskiHome tasksStatus = snapshot.getValue(taskiHome.class);

                status = tasksStatus.getStatus();
                if(status.equals("Done")){
                    flag = 1;
                }
                else {
                    flag = 0;
                }
                if(flag == 1){

                    holder.tv_status.setText(status);
                    holder.tv_taskTitle.setPaintFlags(holder.tv_taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_dueDate.setPaintFlags(holder.tv_dueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_members.setPaintFlags(holder.tv_members.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_time.setPaintFlags(holder.tv_time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.statusWSbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag == 0){

                    checkStatus.child("status").setValue("Done");
                    holder.tv_status.setText(status);
                    holder.tv_taskTitle.setPaintFlags(holder.tv_taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_dueDate.setPaintFlags(holder.tv_dueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_members.setPaintFlags(holder.tv_members.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_time.setPaintFlags(holder.tv_time.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    flag = 1;
                }
                else if(flag == 1){
                    checkStatus.child("status").setValue("Not Done");
                    holder.tv_status.setText(status);
                    holder.tv_taskTitle.setPaintFlags(holder.tv_taskTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_dueDate.setPaintFlags(holder.tv_dueDate.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_members.setPaintFlags(holder.tv_members.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tv_time.setPaintFlags(holder.tv_time.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    flag = 0;
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return myList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_taskTitle, tv_dueDate, tv_status, tv_members, tv_time;
        public ImageButton ib_moreInfo;
        private Button statusWSbtn;
        private  RecyclerViewAdapterInsideWorkspaces adapter;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            tv_taskTitle = itemView.findViewById(R.id.tv_taskTitle);
            tv_dueDate = itemView.findViewById(R.id.tv_dueDate);
             tv_status = itemView.findViewById(R.id.tv_status);
            tv_members = itemView.findViewById(R.id.tv_members);
            ib_moreInfo = itemView.findViewById(R.id.ib_moreInfo);
            tv_time = itemView.findViewById(R.id.tv_time);
            statusWSbtn = itemView.findViewById(R.id.statusWSbtn);

            ib_moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button btnEdit, btnDelete;
                    Dialog dialog3 = new Dialog(context);
                    dialog3.setContentView(R.layout.moreinfo_dialog);
                    dialog3.show();

                    btnEdit = dialog3.findViewById(R.id.btnEdit);
                    btnDelete = dialog3.findViewById(R.id.btnDelete);


                    btnEdit.setOnClickListener(view1 -> {
                        final Calendar myCalendar = Calendar.getInstance();
                        Button editCancel, editCreate;
                        EditText editTask, editDueDate, editTime;
                        TextView tv_members;
                        Dialog dialog2 = new Dialog(context);
                        dialog2.setContentView(R.layout.editdialog);
                        dialog2.show();

                        editCancel = dialog2.findViewById(R.id.editCancel);
                        editCreate = dialog2.findViewById(R.id.editCreate);
                        editTask = dialog2.findViewById(R.id.editTask);
                        editDueDate = dialog2.findViewById(R.id.editDueDate);
                        editTime = dialog2.findViewById(R.id.editTime);
                        tv_members = dialog2.findViewById(R.id.tv_dropDownmembers2);


                        // ***** left these two out
                        membersArray = memberArrayList.toArray(membersArray); // change the arraylist back to an array cause the drop down only works with arrays
                        // Initialize selected member array
                        // this holds all the members who've been selected
                        selectedMember= new boolean[membersArray.length];

                        tv_members.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Initialize the alert dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        context
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



                        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateLabel();
                            }

                            private void updateLabel() {
                                String myFormat = "MM/dd/yy"; //In which you need put here
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                                editDueDate.setText(sdf.format(myCalendar.getTime()));
                            }

                        };
                        editDueDate.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                new DatePickerDialog(context, date, myCalendar
                                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });
                        editTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                picker = new MaterialTimePicker.Builder()
                                        .setTimeFormat(TimeFormat.CLOCK_12H)
                                        .setHour(12)
                                        .setMinute(0)
                                        .setTitleText("Select Alarm Time")
                                        .build();

                                picker.show(((FragmentActivity)context).getSupportFragmentManager(),"taski");

                                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (picker.getHour() > 12){

                                            editTime.setText(
                                                    String.format("%02d",(picker.getHour()-12))+" : "+String.format("%02d",picker.getMinute())+" PM"
                                            );

                                        }else {

                                            editTime.setText(picker.getHour()+" : " + picker.getMinute() + " AM");

                                        }

                                        calendar = Calendar.getInstance();
                                        calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                                        calendar.set(Calendar.MINUTE,picker.getMinute());
                                        calendar.set(Calendar.SECOND,0);
                                        calendar.set(Calendar.MILLISECOND,0);




                                    }
                                });


                            }
                        });

                        editCreate.setOnClickListener(view2 -> {



                            String myTask = editTask.getText().toString();
                            String myDueDate = editDueDate.getText().toString();
                            String myTime = editTime.getText().toString();
//                            String myWorkspace = adapter.myList.get(getAdapterPosition()).getWorkspaceName();
                            String members = tv_members.getText().toString();
                            taskiHome editMyTask = new taskiHome(myTask,myDueDate,"incomplete",members, wID,myTime,myList.get(getAdapterPosition()).getTaskID());
                            DatabaseReference ref = database.getReference("Workspaces").child(wID).child("Tasks");
                            String tasks =  adapter.myList.get(getAdapterPosition()).getTaskID();
                            ref.child(tasks).setValue(editMyTask);
                            adapter.myList.set(getAdapterPosition(),editMyTask);
                            adapter.notifyItemChanged(getAdapterPosition());

                            //assign it to the members so that it shows up on their my tasks section
                            for (int i = 0; i < selectedMemberIDs.size(); i++) {
                                String assignedTo= selectedMemberIDs.get(i);
                                DatabaseReference myRef3 = database.getReference("Users").child(assignedTo).child("My Tasks");
                                taskiHome personalTask = new taskiHome(editMyTask.getTaskName(),editMyTask.getDueDate(),assignedTo, title,editMyTask.getTime(),editMyTask.getTaskID());
                                myRef3.child(tasks).setValue(personalTask);

                            }

                            dialog2.dismiss();


                        });
                        editCancel.setOnClickListener(view2 -> {
                            dialog2.dismiss();
                        });



                        dialog3.dismiss();
                    });
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatabaseReference ref = database.getReference("Workspaces").child(wID).child("Tasks");
                            String tasks =  adapter.myList.get(getAdapterPosition()).getTaskID();
                            ref.child(tasks).removeValue();
                            adapter.myList.remove(getAdapterPosition());
                            adapter.notifyItemRemoved(getAdapterPosition());
                            dialog3.dismiss();
                        }
                    });


                }
            });
        }
        public RecyclerViewAdapterInsideWorkspaces.ViewHolder myDialogs(RecyclerViewAdapterInsideWorkspaces adapter){
            this.adapter = adapter;
            return this;

        }
    }
}
