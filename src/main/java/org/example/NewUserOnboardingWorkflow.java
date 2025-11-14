package org.example;

import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.tasks.*;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Define the structure and logic of the workflow
 *  - Create and configure each workflow step (task)
 *  - Main.java registers and executes the workflow
 */

public class NewUserOnboardingWorkflow {

    // The executor used to build and register workflow definitions
    private final WorkflowExecutor executor;

    // The workflow receives its WorkflowExecutor from Main.java
    // This separates definition from application bootstrapping.
    public NewUserOnboardingWorkflow(WorkflowExecutor executor) { this.executor = executor; }

    // Creates the workflow definition.
    // Main.java will register the workflow.
    public ConductorWorkflow<WorkflowInput> createWorkflow() {

        // Create a new workflow object using the executor
        var workflow = new ConductorWorkflow<WorkflowInput>(executor);

        // Basic workflow metadata
        workflow.setName("newUserOnboarding_2");
        workflow.setDescription("New User Onboarding Workflow");
        workflow.setVersion(1);


        // Step 1. Inline Task
        // Validate the workflow input parameters


        // Define the script.
        // The script validates that email is in the correct format,
        // and that planType matches one of the three defined types.
        var validationScript =
                """
                        (function () {
                        
                          const emailFormat = /^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$/;
                          const validPlans = ["bronze", "silver", "gold"];
                        
                          if (!$.email || !emailFormat.test($.email)) {
                            return {
                              valid: false,
                              message: "Invalid or missing email"
                            };
                          }
                        
                          if (!$.planType || !validPlans.includes($.planType)) {
                            return {
                              valid: false,
                              message: "Invalid or missing planType. Must be bronze, silver, or gold"
                            };
                          }
                        
                          return {
                            valid: true,
                            message: "Validated user"
                          };
                        })();
                       
                        """;


        // JavaScript task = Inline task
        // JavaScript(taskReferenceName, script)
        var validateUserDataTask = new Javascript("validate_user_data_ref", validationScript);

        // Set the task name
        validateUserDataTask.setName("validate_user_data");

        // Define the input parameters referencing the workflow input parameters
        validateUserDataTask.input("email", "${workflow.input.email}");
        validateUserDataTask.input("planType", "${workflow.input.planType}");

        // Set the evaluator type to graaljs
        validateUserDataTask.input("evaluatorType", "graaljs");

        // Add the task to the workflow
        workflow.add(validateUserDataTask);


        // Step 2. Switch task
        // Before defining the Switch task, define all the tasks
        // that will exist in any of the switchCases


        // Step 2.1 HTTP task
        // If validation fails, notify an external service with the reason
        // Lets assume for this example the external service also allows the
        // user to try again with valid data.
        // Http(taskReferenceName)
        var retryHttp = new Http("notify_and_try_again_ref");

        // Set the task name
        retryHttp.setName("notify_and_try_again");

        // Define the HTTP method to POST
        retryHttp.method(Http.Input.HttpMethod.POST);

        // Set the URL. For this example, use the orkes api tester
        retryHttp.url("https://orkes-api-tester.orkesconductor.com/api");

        // Define the body of the request to be the error
        // message generated from the Inline task
        Map<String, Object> body = new HashMap<>();
        body.put("valid", false);
        body.put("message", "${validate_user_data_ref.output.result.message}");
        retryHttp.body(body);
        // Don't add the task to the workflow.
        // Instead, add the task to the Switch task's switch case




        // Step 2.2 Terminate task
        // End the workflow if the validation fails.
        // The termination reason is the validation error message generated in the Inline task.
        // Terminate(taskReferenceName, WorkflowStatus, terminationReason)
        var terminateTask = new Terminate(
                "terminate_ref",
                Workflow.WorkflowStatus.TERMINATED,
                "${validate_user_data_ref.output.result.message}");

        // Set the task name
        terminateTask.setName("terminate");


        // Step 2.3 Switch task
        // After all tasks that will exist in the switchCases have been defined,
        // now define the Switch task and its switchCases
        // Evaluate using the Value-Param
        // set the switchCaseValue to reference the output from the Inline task.
        // Switch(taskReferenceName, switchCaseValue value)
        var switchTask = new Switch("switch_ref", "${validate_user_data_ref.output.result.valid}");

        // Define the switchCases
        // switchCase(Key, List of tasks to be executed)
        switchTask.switchCase("true", new Task[] {});
        switchTask.switchCase("false", retryHttp, terminateTask);

        // Add Switch task to the workflow
        // This will also add any tasks set in the switchCases
        workflow.add(switchTask);


        // Step 3. Simple task
        // Worker task that will take in the validated user data
        // and insert into a database of users.
        // For this example, the database insertion will be simulated.
        // SimpleTask(taskName, taskReferenceName)
        var insertUserTask = new SimpleTask("insertUserData", "insertUserData_ref");

        // Define the input parameters.
        // Reference the workflow input parameters.
        insertUserTask.input("email", "${workflow.input.email}");
        insertUserTask.input("planType", "${workflow.input.planType}");

        // Add Simple task to the workflow
        workflow.add(insertUserTask);

        // return the workflow object.
        return workflow;
    }

}
