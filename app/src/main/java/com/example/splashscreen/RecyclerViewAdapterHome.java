package com.example.splashscreen;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class RecyclerViewAdapterHome extends  RecyclerView.Adapter<RecyclerViewAdapterHome.ViewHolder> {
    private ArrayList<taskiHome> myList;
    Context context;

    public RecyclerViewAdapterHome(ArrayList myList,  Context context) {
        this.myList = myList;
        this.context = context;


    }
    @NonNull
    @Override
    public RecyclerViewAdapterHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myworkspacerows,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterHome.ViewHolder holder, int position) {


        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        holder.workspaceName.setText(myList.get(position).getWorkspaceName());
        holder.btnworkspace.setBackgroundColor(color);


        holder.itemView.setOnClickListener(new View.OnClickListener() {// onclick listener for any item on recycler
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();//gets current position of clicked item
                // check if item still exists
                if(pos != RecyclerView.NO_POSITION){//
                    taskiHome clickedDataItem = myList.get(pos);//creates object that holds data from list in specific pos
                    Intent intent = new Intent(context, insideWorkspace.class);
                    intent.putExtra("wID", clickedDataItem.getWorkspaceID()); //*********** We need to get the workspace ID so that it knows which workspaces tasks to show
//                    intent.putExtra("wName", clickedDataItem.getWorkspaceName());// sending workspace name for title display
                    context.startActivity(intent);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView workspaceName;
        public Button btnworkspace;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            workspaceName = itemView.findViewById(R.id.tv_workspace_name);
            btnworkspace = itemView.findViewById(R.id.btn_workspace);



        }
    }
}
