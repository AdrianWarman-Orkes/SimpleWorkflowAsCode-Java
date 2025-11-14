package org.example;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.InputParam;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

/**
 * Run the worker that handles the insertUserData task.
 *  - Poll conductor for tasks
 *  - Run the business logic for any worker tasks
 *  - Send the results back to Conductor
 */

public class Workers {

    // Define the worker function with the @WorkerTask decorator
    // Set the task name in the @WorkerTask
    @WorkerTask(value="insertUserData")
    public TaskResult insertUserData(@InputParam("email") String email, @InputParam("planType") String planType) {

        // define the TaskResult object
        TaskResult result = new TaskResult();

        // Set a log, simulating the user insertion
        result.log("Inserting user...");

        // Set the output data for the task
        result.addOutputData(
                "statusMessage", "User " +
                        email + " has been inserted with plan type " +
                        planType);

        // Set the task status update
        result.setStatus(TaskResult.Status.COMPLETED);

        // return the TaskResult object back to Conductor
        return result;
    }
}


