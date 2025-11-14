package org.example;

import com.netflix.conductor.common.metadata.tasks.TaskDef;
import java.util.List;

public class TaskDefinitionList {
    public static List<TaskDef> getAllTaskDefs() {

        TaskDef insertUserData = new TaskDef();
        insertUserData.setName("insertUserData");
        insertUserData.setDescription("Insert user data");
        insertUserData.setRetryCount(3);
        insertUserData.setRetryLogic(TaskDef.RetryLogic.FIXED);
        return List.of(insertUserData);
    }
}

