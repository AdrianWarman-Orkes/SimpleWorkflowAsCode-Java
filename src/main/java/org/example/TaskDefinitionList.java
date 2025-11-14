package org.example;

import com.netflix.conductor.common.metadata.tasks.TaskDef;
import java.util.List;

/**
 * Defines the task definitions Conductor needs to know about
 *  - Creates TaskDef objects describing task the workers execute
 *  - Returns as a list to Main.java
 *  - Main.java handles registering the tasks
 */


public class TaskDefinitionList {

    // Method called from Main.java to get the list of tasks that need to be registered.
    public static List<TaskDef> getAllTaskDefs() {

        // 'insertUserData' task definition
        TaskDef insertUserData = new TaskDef();

        // Set the task name
        insertUserData.setName("insertUserData");

        // Set the description
        insertUserData.setDescription("Insert user data");

        // Define the retry policy
        insertUserData.setRetryCount(3);
        insertUserData.setRetryLogic(TaskDef.RetryLogic.FIXED);

        // Return the list of tasks
        return List.of(insertUserData);
    }
}

