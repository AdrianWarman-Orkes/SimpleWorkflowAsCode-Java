package org.example;

public class WorkflowInput {
    // user data parameters
    private String email;
    private String planType;

    //Constructor
    public WorkflowInput(String email, String planType) {
        this.email = email;
        this.planType = planType;
    }

    //Getters
    public String getEmail() {
        return email;
    }

    public String getPlanType() {
        return planType;
    }

    //Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }
}


