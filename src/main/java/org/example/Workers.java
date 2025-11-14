package org.example;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.InputParam;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

public class Workers {

    @WorkerTask(value="insertUserData")
    public TaskResult insertUserData(@InputParam("email") String email, @InputParam("planType") String planType) {
        TaskResult result = new TaskResult();

        result.log("Inserting user...");

        result.addOutputData(
                "statusMessage", "User " +
                        email + " has been inserted with plan type " +
                        planType);

        result.setStatus(TaskResult.Status.COMPLETED);
        return result;
    }
}


