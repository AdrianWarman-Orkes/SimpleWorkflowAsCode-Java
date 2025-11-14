package org.example;

import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;
import io.orkes.conductor.client.ApiClient;
import io.orkes.conductor.client.OrkesClients;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {

    private static final String CONDUCTOR_SERVER = "https://adrian-demo.orkesconductor.io/api";
    private static final String KEY = "1f9192d8-c029-11f0-96dc-b64d3314ac5e";
    private static final String SECRET = "1LTq7ryMizPIratPA9dQ8Z4pvYC7HVZtFagfRTZsreQq6aFe";

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        //Initialize Conductor Client
        var apiClient = new ApiClient(CONDUCTOR_SERVER, KEY, SECRET);

        var orkesClient = new OrkesClients(apiClient);
        var metadataClient = orkesClient.getMetadataClient();
        List<TaskDef> defs = TaskDefinitionList.getAllTaskDefs();
        metadataClient.registerTaskDefs(defs);

        //Initialize WorkflowExecutor and Conductor Workers
        var workflowExecutor = new WorkflowExecutor(apiClient, 100);
        workflowExecutor.initWorkers("org.example");

        //Create workflow with input
        var workflowCreator = new NewUserOnboardingWorkflow(workflowExecutor);
        var workflow = workflowCreator.createWorkflow();
        workflow.registerWorkflow(true);

        var input = new WorkflowInput("sample@email.com", "silver");
        var workflowExecution = workflow.executeDynamic(input);
        var workflowRun = workflowExecution.get(100, TimeUnit.SECONDS);

        System.out.println("Started Workflow: " + workflowRun.getWorkflowId());

        workflowExecutor.shutdown();
    }
}



