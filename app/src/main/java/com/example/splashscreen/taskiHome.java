package com.example.splashscreen;

public class taskiHome {

    private String  taskName;
    private String workspaceName;
    private String dueDate;
    private String status;
    private String members;
    private String time;
    private String CreatorID;
    private String workspaceID;

    public int getPass() {
        return Pass;
    }

    public void setPass(int pass) {
        Pass = pass;
    }

    private int Pass;

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    private String taskID;


    public taskiHome(String taskName, String workspaceName,String dueDate,String time) {

        this.taskName = taskName;
        this.workspaceName = workspaceName;
        this.dueDate = dueDate;
        this.time = time;
    }
    public taskiHome(String workspaceName, String creatorID, String workspaceID, int Pass) {
        this.workspaceName = workspaceName;
        this.CreatorID=creatorID;
        this.workspaceID=workspaceID;
        this. Pass = Pass;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public taskiHome(String taskName, String dueDate, String members, String workspaceName, String time,String taskID) {

        this.taskName = taskName;
        this.workspaceName = workspaceName;
        this.dueDate = dueDate;
        this.members = members;
        this.time = time;
        this.taskID = taskID;
    }
    public taskiHome(String taskName,String dueDate, String status, String assignedTo, String workspaceID,String time, String taskID) {

        this.taskName = taskName;
        this.dueDate = dueDate;
        this.members=assignedTo;
        this.workspaceID=workspaceID;
        this.status=status;
        this.time = time;
        this.taskID = taskID;

    }
    public  taskiHome(){}






    public String getTaskName() {
        return taskName;
    }

    @Override
    public String toString() {
        return "" + taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    //********* getter for workspace ID
    public String getWorkspaceID() {
        return workspaceID;
    }
}
