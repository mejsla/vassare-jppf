package se.mejsla.vassare.jppf.client;

import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.Task;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleTaskIT {
    @Disabled
    @Test
    void shouldSubmitTaskAndGetResult() throws Exception {
        try (JPPFClient jppfClient = new JPPFClient()) {
            JPPFJob job = new JPPFJob();
            job.setName("Test job");

            String taskId = job.getName() + " - Test task";
            Task<?> task = job.add(new SimpleTask(taskId));
            task.setId(taskId);

            List<Task<?>> results = jppfClient.submit(job);

            for (Task<?> taskWithResult : results) {
                SimpleTask simpleTask = (SimpleTask) taskWithResult;
                assertTrue(simpleTask.getResult());
            }
        }
    }
}
