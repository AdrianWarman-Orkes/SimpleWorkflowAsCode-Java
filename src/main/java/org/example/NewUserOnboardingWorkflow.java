package org.example;

import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.tasks.*;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;

import java.util.HashMap;
import java.util.Map;

public class NewUserOnboardingWorkflow {

    private final WorkflowExecutor executor;

    public NewUserOnboardingWorkflow(WorkflowExecutor executor) { this.executor = executor; }

    public ConductorWorkflow<WorkflowInput> createWorkflow() {
        var workflow = new ConductorWorkflow<WorkflowInput>(executor);
        workflow.setName("newUserOnboarding_2");
        workflow.setDescription("New User Onboarding Workflow");
        workflow.setVersion(1);

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

        var validateUserDataTask = new Javascript("validate_user_data_ref", validationScript);
        validateUserDataTask.setName("validate_user_data");
        validateUserDataTask.input("email", "${workflow.input.email}");
        validateUserDataTask.input("planType", "${workflow.input.planType}");
        validateUserDataTask.input("evaluatorType", "graaljs");
        workflow.add(validateUserDataTask);

        var retryHttp = new Http("notify_and_try_again_ref");
        retryHttp.setName("notify_and_try_again");
        retryHttp.method(Http.Input.HttpMethod.POST);
        retryHttp.url("https://orkes-api-tester.orkesconductor.com/api");
        Map<String, Object> body = new HashMap<>();
        body.put("valid", false);
        body.put("message", "${validate_user_data_ref.output.result.message}");
        retryHttp.body(body);

        var terminateTask = new Terminate("terminate_ref", Workflow.WorkflowStatus.TERMINATED, "${validate_user_data_ref.output.result.message}");
        terminateTask.setName("terminate");

        var switchTask = new Switch("switch_ref", "${validate_user_data_ref.output.result.valid}");
        switchTask.switchCase("true", new Task[] {});
        switchTask.switchCase("false", retryHttp, terminateTask);
        workflow.add(switchTask);

        var insertUserTask = new SimpleTask("insertUserData", "insertUserData_ref");
        insertUserTask.input("email", "${workflow.input.email}");
        insertUserTask.input("planType", "${workflow.input.planType}");
        workflow.add(insertUserTask);

        return workflow;
    }

}
