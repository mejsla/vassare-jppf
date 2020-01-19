/*
 * JPPF.
 * Copyright (C) 2005-2019 JPPF Team.
 * http://www.jppf.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.mejsla.vassare.jppf.client;

import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFConnectionPool;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.Task;
import org.jppf.utils.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * This is a template JPPF application runner.
 * It is fully commented and is designed to be used as a starting point
 * to write an application using JPPF.
 *
 * This is a slightly modified version of the template application runner.
 *
 * @author Laurent Cohen
 * @author Johan Dykstrom
 */
public class TemplateApplicationRunner {

    private static final double NANOS_IN_SECOND = 1_000_000_000.0;

    private final Logger logger = LoggerFactory.getLogger(TemplateApplicationRunner.class);

    /**
     * The entry point for this application runner to be run from a Java command line.
     *
     * @param args by default, we do not use the command line arguments,
     *             however nothing prevents us from using them if need be.
     */
    public static void main(final String... args) {

        // create the JPPFClient. This constructor call causes JPPF to read the configuration file
        // and connect with one or multiple JPPF drivers.
        try (final JPPFClient jppfClient = new JPPFClient()) {

            // create a runner instance.
            final TemplateApplicationRunner runner = new TemplateApplicationRunner();

            // create and execute a blocking job
            //runner.executeBlockingJob(jppfClient);

            // create and execute a non-blocking job
            //runner.executeNonBlockingJob(jppfClient);

            // create and execute 3 jobs concurrently
            runner.executeMultipleConcurrentJobs(jppfClient, 3);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a JPPF job that can be submitted for execution.
     *
     * @param jobName an arbitrary, human-readable name given to the job.
     * @return an instance of the {@link org.jppf.client.JPPFJob JPPFJob} class.
     * @throws Exception if an error occurs while creating the job or adding tasks.
     */
    public JPPFJob createJob(final String jobName) throws Exception {
        // create a JPPF job
        final JPPFJob job = new JPPFJob();
        // give this job a readable name that we can use to monitor and manage it.
        job.setName(jobName);

        // add a task to the job.
        String taskId = jobName + " - Template task";
        final Task<?> task = job.add(new ListFilesTask(taskId, "."));
        // provide a user-defined name for the task
        task.setId(taskId);

        // add more tasks here ...

        // there is no guarantee on the order of execution of the tasks,
        // however the results are guaranteed to be returned in the same order as the tasks.
        return job;
    }

    /**
     * Times the execution of a number of jobs, and returns a list of task results.
     */
    private List<Task<?>> timeExecution(Supplier<List<Task<?>>> jobsToTime) {
        logger.info("Waiting for job(s) to execute");
        long start = System.nanoTime();
        try {
            return jobsToTime.get();
        } finally {
            long stop = System.nanoTime();
            double durationInSeconds = (stop - start) / NANOS_IN_SECOND;
            logger.info("Job(s) executed in {} seconds", String.format("%.3f", durationInSeconds));
        }
    }

    /**
     * Execute a job in blocking mode. The application will be blocked until the job execution is complete.
     *
     * @param jppfClient the {@link JPPFClient} instance which submits the job for execution.
     * @throws Exception if an error occurs while executing the job.
     */
    public void executeBlockingJob(final JPPFClient jppfClient) throws Exception {
        // ensure that the client connection pool has as many connections
        // as the number of jobs to execute
        ensureNumberOfConnections(jppfClient, 1);

        // Create a job
        final JPPFJob job = createJob("Template blocking job");

        // Submit the job and wait until the results are returned.
        // The results are returned as a list of Task<?> instances,
        // in the same order as the one in which the tasks where initially added to the job.
        logger.info("Submitting job: " + job.getName());
        final List<Task<?>> results = timeExecution(() -> jppfClient.submit(job));

        // process the results
        processExecutionResults(results);
    }

    /**
     * Execute a job in non-blocking mode. The application has the responsibility
     * for handling the notification of job completion and collecting the results.
     *
     * @param jppfClient the {@link JPPFClient} instance which submits the job for execution.
     * @throws Exception if an error occurs while executing the job.
     */
    public void executeNonBlockingJob(final JPPFClient jppfClient) throws Exception {
        // ensure that the client connection pool has as many connections
        // as the number of jobs to execute
        ensureNumberOfConnections(jppfClient, 1);

        // Create a job
        final JPPFJob job = createJob("Template non-blocking job");

        // Submit the job. This call returns immediately without waiting for the execution of
        // the job to complete. As a consequence, the object returned for a non-blocking job is
        // always null. Note that we are calling the exact same method as in the blocking case.
        logger.info("Submitting job: " + job.getName());
        jppfClient.submitAsync(job);

        // the non-blocking job execution is asynchronous, we can do anything else in the meantime
        logger.info("Doing something while the job is executing ...");
        // ...

        // We are now ready to get the results of the job execution.
        // We use JPPFJob.awaitResults() for this. This method returns immediately with
        // the results if the job has completed, otherwise it waits until the job execution is complete.
        final List<Task<?>> results = timeExecution(job::awaitResults);

        // process the results
        processExecutionResults(results);
    }

    /**
     * Execute multiple jobs in parallel from the same JPPFClient.
     * <p>This is an extension of the {@code executeNonBlockingJob()} method, with one additional step:
     * to ensure that a sufficient number of connections to the server are present, so that jobs can be submitted concurrently.
     * The number of connections determines the number of jobs that can be submitted in parallel.
     * It can be set in the JPPF configuration or dynamically with the {@link JPPFConnectionPool} API.
     * <p>As a result, the call to {@code executeNonBlockingJob(jppfClient)} is effectively
     * equivalent to {@code executeMultipleConccurentJobs(jppfClient, 1)}.
     * <p>There are many patterns that can be applied to parallel job execution, you are encouraged to read
     * the <a href="http://www.jppf.org/doc/v4/index.php?title=Submitting_multiple_jobs_concurrently">dedicated section</a>
     * of the JPPF documentation for details and code samples.
     *
     * @param jppfClient   the JPPF client which submits the jobs.
     * @param numberOfJobs the number of jobs to execute.
     * @throws Exception if any error occurs.
     */
    public void executeMultipleConcurrentJobs(final JPPFClient jppfClient, final int numberOfJobs) throws Exception {
        // ensure that the client connection pool has as many connections
        // as the number of jobs to execute
        ensureNumberOfConnections(jppfClient, numberOfJobs);

        // this list will hold all the jobs submitted for execution,
        // so we can later collect and process their results
        final List<JPPFJob> jobList = new ArrayList<>(numberOfJobs);

        // create and submit all the jobs
        for (int i = 1; i <= numberOfJobs; i++) {
            // create a job with a distinct name
            final JPPFJob job = createJob("Template concurrent job " + i);

            // submit the job for execution, without blocking the current thread
            logger.info("Submitting job: " + job.getName());
            jppfClient.submitAsync(job);

            // add this job to the list
            jobList.add(job);
        }

        // the non-blocking jobs are submitted asynchronously, we can do anything else in the meantime
        logger.info("Doing something while the jobs are executing ...");
        // ...

        // wait until the jobs are finished and process their results.
        final List<Task<?>> results = timeExecution(() -> jobList.stream()
                .map(JPPFJob::awaitResults)
                .flatMap(Collection::stream)
                .collect(toList())
        );

        // process the job results
        processExecutionResults(results);
    }

    /**
     * Ensure that the JPPF client has the desired number of connections.
     *
     * @param jppfClient          the JPPF client which submits the jobs.
     * @param numberOfConnections the desired number of connections.
     */
    public void ensureNumberOfConnections(final JPPFClient jppfClient, final int numberOfConnections) {
        logger.info("Checking connections");
        // wait until the client has at least one connection pool with at least one available connection
        final JPPFConnectionPool pool = jppfClient.awaitActiveConnectionPool();

        // if the pool doesn't have the expected number of connections, change its size
        if (pool.getConnections().size() != numberOfConnections) {
            // set the pool size to the desired number of connections
            pool.setSize(numberOfConnections);
        }

        // wait until all desired connections are available (ACTIVE status)
        pool.awaitActiveConnections(Operator.AT_LEAST, numberOfConnections);
    }

    /**
     * Process the execution results of each submitted task.
     *
     * @param results the tasks results after execution on the grid.
     */
    public synchronized void processExecutionResults(final List<Task<?>> results) {
        for (final Task<?> task : results) {
            final String taskName = task.getId();
            // if the task execution resulted in an exception
            if (task.getThrowable() != null) {
                // process the exception here ...
                System.out.println(taskName + ", an exception was raised: " + task.getThrowable().getMessage());
            } else {
                // process the result here ...
                System.out.println(taskName + ", execution result: " + task.getResult());
            }
        }
    }
}
