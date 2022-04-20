package com.example.splashscreen;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Random;

public class RecyclerViewAdapterTasks extends  RecyclerView.Adapter<RecyclerViewAdapterTasks.ViewHolder> {
    private ArrayList<taskiHome> myList;
    Context context;
    private MaterialTimePicker picker;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    String myUserID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef2 = database.getReference("Users");//*** Also not being used. I forgot to remove this one as well

    public RecyclerViewAdapterTasks(ArrayList myList, Context context, String myUserID) {
        this.myList = myList;
        this.context = context;
        this.myUserID = myUserID;


    }
    @NonNull
    @Override
    public RecyclerViewAdapterTasks.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_tasks_rows,parent,false);
        return new ViewHolder(view).myDialogs(this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterTasks.ViewHolder holder, int position) {

        holder.taskName.setText(myList.get(position).getTaskName());
        holder.workspaceName.setText(myList.get(position).getWorkspaceName());
        holder.DueDate.setText(myList.get(position).getDueDate());
        holder.time.setText(myList.get(position).getTime());


    }

    @Override
    public int getItemCount() {
        return myList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskName, workspaceName, DueDate, Status, time;
        public Button button2;
        public ImageButton imageButton;
        private  RecyclerViewAdapterTasks adapter;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        private FirebaseAuth mAuth;//


        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            taskName = itemView.findViewById(R.id.textView5);
            workspaceName = itemView.findViewById(R.id.textView6);
            DueDate = itemView.findViewById(R.id.textView7);
//            Status = itemView.findViewById(R.id.textView8);
            time = itemView.findViewById(R.id.textView9);
//            button2 = itemView.findViewById(R.id.button2);
            imageButton = itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button btnEdit, btnDelete;
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.moreinfo_dialog);
                    dialog.show();

                    btnEdit = dialog.findViewById(R.id.btnEdit);
                    btnDelete = dialog.findViewById(R.id.btnDelete);

                    btnEdit.setOnClickListener(view1 -> {
                        final Calendar myCalendar = Calendar.getInstance();
                        Button editCancel, editCreate;
                        EditText editTask, editDueDate, editTime;
                        Dialog dialog2 = new Dialog(context);
                        dialog2.setContentView(R.layout.editdialog2);
                        dialog2.show();

                        editCancel = dialog2.findViewById(R.id.editCancel);
                        editCreate = dialog2.findViewById(R.id.editCreate);
                        editTask = dialog2.findViewById(R.id.editTask);
                        editDueDate = dialog2.findViewById(R.id.editDueDate);
                        editTime= dialog2.findViewById(R.id.editTime);

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
                            String myWorkspace = adapter.myList.get(getAdapterPosition()).getWorkspaceName();
                            taskiHome editMyTask = new taskiHome(myTask,myWorkspace,myDueDate,myTime);

                            DatabaseReference ref = database.getReference("Users").child(myUserID).child("My Tasks");
                            String tasks =  adapter.myList.get(getAdapterPosition()).getTaskID();
                            ref.child(tasks).setValue(editMyTask);

                            adapter.myList.set(getAdapterPosition(),editMyTask);
                            adapter.notifyItemChanged(getAdapterPosition());
                            dialog2.dismiss();


                        });
                        editCancel.setOnClickListener(view2 -> {
                            dialog2.dismiss();
                        });



                        dialog.dismiss();
                    });

                    btnDelete.setOnClickListener(view1 -> {
                        DatabaseReference ref = database.getReference("Users").child(myUserID).child("My Tasks");
                        String tasks =  adapter.myList.get(getAdapterPosition()).getTaskID();
                        ref.child(tasks).removeValue();
                        adapter.myList.remove(getAdapterPosition());
                        adapter.notifyItemRemoved(getAdapterPosition());
                        dialog.dismiss();
                    });
                }
            });



        }
        public ViewHolder myDialogs(RecyclerViewAdapterTasks adapter){
            this.adapter = adapter;
            return this;

        }

    }


}
