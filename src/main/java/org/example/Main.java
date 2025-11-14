package org.example;

import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;
import io.orkes.conductor.client.ApiClient;
import io.orkes.conductor.client.OrkesClients;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Main launcher that wires everything together
 *  - Connects to Orkes Conductor cluster
 *  - Initializes Orkres SDK clients
 *  - Registers Task Definitions
 *  - Registers the workflow definition
 *  - Executes the workflow with input parameters
 */


public class Main {

    // in production, store credentials securely in environment variables
    private static final String CONDUCTOR_SERVER = "<url>>";
    private static final String KEY = "<key>>";
    private static final String SECRET = "<secret>";

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        //Initialize Conductor Client
        var apiClient = new ApiClient(CONDUCTOR_SERVER, KEY, SECRET);

        // Initialize MetadataClient to be able to register a new task definition
        var metadataClient = new OrkesClients(apiClient).getMetadataClient();

        // get the list of task definitions from TaskDefinitionsList.java
        List<TaskDef> defs = TaskDefinitionList.getAllTaskDefs();
        metadataClient.registerTaskDefs(defs);

        //Initialize WorkflowExecutor and Conductor Workers
        var workflowExecutor = new WorkflowExecutor(apiClient, 100);
        workflowExecutor.initWorkers("org.example");

        //Create and register the workflow. set overwrite to true to save over pre-existing version of workflow.
        var workflowCreator = new NewUserOnboardingWorkflow(workflowExecutor);
        var workflow = workflowCreator.createWorkflow();
        workflow.registerWorkflow(true);

        //Create the input for a workflow execution
        var input = new WorkflowInput("sample@email.com", "silver");

        //execute the workflow dynamically.
        var workflowExecution = workflow.executeDynamic(input);
        var workflowRun = workflowExecution.get(10, TimeUnit.SECONDS);

        //print the workflowId
        System.out.println("Started Workflow: " + workflowRun.getWorkflowId());


        //workflowExecutor.shutdown();
    }
}



