# New User Onboarding Workflow – Java Example (Orkes Conductor)

This project demonstrates how to build, register, and execute a workflow in **Orkes Conductor**, using the **Orkes Java SDK**.  
It provides a complete example of:

- Defining a workflow in Java  
- Building workflow tasks (inline, switch, HTTP, and worker tasks)  
- Registering workflow and task definitions in Conductor  
- Running worker processes  
- Executing workflows with typed input objects  

This application mirrors an onboarding workflow previously built in the Conductor UI, but shows how to build the entire system programmatically.

---

## 📚 Table of Contents
1. [Overview](#overview)  
2. [Architecture](#architecture)  
3. [Workflow Logic](#workflow-logic)  
4. [Project Structure](#project-structure)  
5. [Components Explained](#components-explained)  
   - Main.java  
   - WorkflowInput.java  
   - NewUserOnboardingWorkflow.java  
   - TaskDefinitionList.java  
   - Workers.java  
6. [Prerequisites](#prerequisites)  
7. [Setup & Deployment](#setup--deployment)  
8. [Running the Example](#running-the-example)  
9. [Extending the Workflow](#extending-the-workflow)

---

## Overview

This Java application demonstrates an end-to-end onboarding workflow using Orkes Conductor.

The workflow performs:

1. Input validation  
2. Branching logic  
3. HTTP fallback when validation fails  
4. Optional termination  
5. Final worker-driven user data insertion  

It uses the **Orkes Java SDK** to connect to Conductor, register definitions, run workers, and start workflows asynchronously with strongly typed inputs.

---

## Architecture

The application consists of several major layers:

### **1. Workflow Definition Layer**
Defines *what* the workflow does and which tasks it executes and configures each task.

### **2. Task Definition Layer**
Defines work task definitons to be registered in Conductor.

### **3. Worker Execution Layer**
Implements long-running or custom business logic for worker tasks.

### **4. Runtime Layer**
Bootstraps the app, registers items with Conductor, and launches the workflow.

---

## Workflow Logic

The onboarding workflow contains:

1. **Inline Task – Validate Input**  
   Checks `email` and `planType`.

2. **Switch Task – Validate Result**  
   - If valid → continue  
   - If invalid → HTTP retry path

3. **HTTP Task – Retry with Valid Data**  
   Calls an external service to attempt recovery.

4. **Terminate Task**  
   Ends workflow if validation cannot be corrected.

5. **Worker Task – insertUserData**  
   Invokes a Java worker to insert user data (DB or mock logic).

---

## Project Structure

