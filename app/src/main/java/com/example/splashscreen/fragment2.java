package com.example.splashscreen;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.splashscreen.databinding.Fragment1Binding;
import com.example.splashscreen.databinding.Fragment2Binding;
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
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class fragment2 extends Fragment{

    private MaterialTimePicker picker;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    public fragment2(){}
    Fragment2Binding binding;
    ArrayList<taskiHome> list= new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef2 = database.getReference("Tasks"); // ******** commented it out since its not used. I forgot to remove it from my file
    private FirebaseAuth mAuth;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding= Fragment2Binding.inflate(inflater,container,false);

        FloatingActionButton fab2 = binding.fab2;


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.f2RecyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance(); //
        String myUserID = mAuth.getCurrentUser().getUid();
        // ******** from small u to capital Users. Since that's the reference in the sign up. in mainActivity2
        DatabaseReference myRef3 = database.getReference("Users").child(myUserID).child("My Tasks"); // referencing to mytasks within user node

        // On change Listener for my tasks
        myRef3.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot TasksSnapshot: snapshot.getChildren()){
                    taskiHome tasks = TasksSnapshot.getValue(taskiHome.class);
                    list.add(tasks);
                    RecyclerViewAdapterTasks adapter= new RecyclerViewAdapterTasks(list, getContext(),myUserID);
                    binding.f2RecyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        fab2.setOnClickListener(view -> {

            final Calendar myCalendar = Calendar.getInstance();
            Button btnCancel, btnCreate;
            EditText etCreateTask, etWorkspace, etDueDate,etTime;
            Dialog dialog = new Dialog(fragment2.this.getContext());
            dialog.setContentView(R.layout.fragment2_dialog);
            dialog.show();
            btnCancel = dialog.findViewById(R.id.btnCancel2);
            btnCreate = dialog.findViewById(R.id.btnCreate2);
            etCreateTask = dialog.findViewById(R.id.etCreateTask);
            etWorkspace= dialog.findViewById(R.id.etWorkspace);
            etDueDate = dialog.findViewById(R.id.etDueDate);
            etTime = dialog.findViewById(R.id.etTime);

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

                    etDueDate.setText(sdf.format(myCalendar.getTime()));
                }

            };
            etDueDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            etTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    picker = new MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_12H)
                            .setHour(12)
                            .setMinute(0)
                            .setTitleText("Select Alarm Time")
                            .build();

                    picker.show(getFragmentManager(),"taski");

                    picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (picker.getHour() > 12){

                                etTime.setText(
                                        String.format("%02d",(picker.getHour()-12))+" : "+String.format("%02d",picker.getMinute())+" PM"
                                );

                            }else {

                                etTime.setText(picker.getHour()+" : " + picker.getMinute() + " AM");

                            }

                            calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                            calendar.set(Calendar.MINUTE,picker.getMinute());
                            calendar.set(Calendar.SECOND,0);
                            calendar.set(Calendar.MILLISECOND,0);

                            alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                            Intent intent = new Intent(getContext(),AlarmReceiver.class);


                            pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,0);

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY,pendingIntent);



                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                CharSequence name = "testing..";
                                String description = "testing..";
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel channel = new NotificationChannel("taski",name,importance);
                                channel.setDescription(description);

                                NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);

                            }

                            Toast.makeText(getContext(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();


                        }
                    });


                }
            });






            btnCancel.setOnClickListener(view1 -> {
                dialog.dismiss();

            });
            btnCreate.setOnClickListener(view1 -> {
                String myTask = etCreateTask.getText().toString();
                String myWorkspace= etWorkspace.getText().toString();
                String myDueDate = etDueDate .getText().toString();
                String myTime = etTime .getText().toString();
                taskiHome userTasks = new taskiHome(myTask,myDueDate, mAuth.getCurrentUser().getUid(),myWorkspace, myTime,"unique");

                // unique ID starts with the name then the random digits- unnecessary just thought it helped
                String uniqueID = userTasks.getTaskName().toString()+"-"+ UUID.randomUUID().toString();
                userTasks = new taskiHome(myTask,myDueDate, mAuth.getCurrentUser().getUid(),myWorkspace, myTime,uniqueID);
                myRef3.child(uniqueID).setValue(userTasks); // ******** from myRef2 to myRef3, since its the reference to the myTasks in the users node
                dialog.dismiss();



            });


        });


        return binding.getRoot();

    }

}
