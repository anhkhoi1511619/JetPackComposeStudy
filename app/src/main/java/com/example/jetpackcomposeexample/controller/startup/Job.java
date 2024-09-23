package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class Job {
    static boolean isMainController = true;
    public enum Status {
        QUEUED,         // job is determined to run
        PENDING,        // job is pending
        WORKING,        // job is running
        FAILED,         // job is finished and failed
        SKIPPED,        // job was not run but skipped instead
        DONE,           // job is done successfully
    }
    public enum ChainCondition {
        RUN_IF_STRICTLY_SUCCESS,
        RUN_IF_SUCCESS,
        RUN_IF_FAILED,
        RUN_ALWAYS,
    }
    Context context;

//    public Job(Context context) {
//        this.context = context;
//    }

    ChainCondition condition = ChainCondition.RUN_ALWAYS;
    public Status status = Status.PENDING;
    static final ScheduledThreadPoolExecutor executor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(30);
    List<Runnable> onFinished = new ArrayList<>();
    List<Runnable> onStarted = new ArrayList<>();

    static final String TAG = "UPDATE_PROCEDURE";

    Job next;
    Job previous;

    public void clear() {
    }

    public Job chain(Job other, ChainCondition condition) {
        this.next = other;
        other.previous = this;
        this.condition = condition;
        return other;
    }
//    public static Job runAll(Job... jobs) {
//        return new Job(null) {
//            @Override
//            protected void doRun() {
//                if(jobs.length == 0) {
//                    setStatus(Status.DONE);
//                    return;
//                }
//                setStatus(Status.WORKING);
//                Job first = null;
//                Job last = null;
//                for(var job: jobs) {
//                    if(last == null) {
//                        first = last = job;
//                        continue;
//                    }
//                    last = last.chain(job, ChainCondition.RUN_ALWAYS);
//                }
//                last.then(() -> {
//                    var success = Arrays.stream(jobs).allMatch(job -> job.done());
//                    setStatus(success?Status.DONE:Status.FAILED);
//                });
//                first.run();
//            }
//        };
//    }
    protected abstract void doRun();
    public void run() {
        Log.d(TAG, "Task "+this.getClass().getSimpleName()+" is starting...");
        Log.d(TAG, "Performing onStarted event for "+this.getClass().getSimpleName()+"...");
        for (Runnable callback: onStarted) {
            callback.run();
        }
        if(finished()) {
            Log.d(TAG, "Task "+this.getClass().getSimpleName()+" is prematurely finished, calling stop using thread pool "
                    +executor.getActiveCount()+"/"+executor.getCorePoolSize());
            executor.execute(() -> stop());
        } else {
            Log.d(TAG, "Start "+this.getClass().getSimpleName()+" using thread pool "+
                    executor.getActiveCount()+"/"+executor.getCorePoolSize());
            setStatus(Status.WORKING);
            executor.execute(() -> doRun());
        }
    }
    public Job then(Runnable runnable) {
        this.onFinished.add(runnable);
        return this;
    }
    public Job before(Runnable callback) {
        this.onStarted.add(callback);
        return this;
    }
    void setStatus(Status status) {
        this.status = status;
        if(finish(status)) {
            Log.d("Thread", "schedule task, thread pool active count = "+executor.getActiveCount()+"/"+executor.getPoolSize());
            executor.execute(()->stop());
        }
    }

    void stop() {
        if(!finished()) {
            //throw new RuntimeException();
            Log.e(TAG, "Task "+this.getClass().getSimpleName()+" finished with invalid result "+status);
            return;
        }
        for (Runnable callback: onFinished) {
            callback.run();
        }
        clear();
        if(!finished()) {
            return;
        }
        Log.d(TAG, "Task "+this.getClass().getSimpleName()+" finished with result "+status);
        boolean canRunNext = next != null;
        if(canRunNext) {
            boolean shouldRunNext =
                    (condition == ChainCondition.RUN_IF_SUCCESS && done()) ||
                    (condition == ChainCondition.RUN_IF_FAILED && !done()) ||
                    (condition == ChainCondition.RUN_IF_STRICTLY_SUCCESS && status == Status.DONE) ||
                    (condition == ChainCondition.RUN_ALWAYS && finished());
            Log.d("Thread", "schedule task at Job, thread pool active count = "+executor.getActiveCount()+"/"+executor.getPoolSize());
            if(shouldRunNext) {
                executor.execute(()->next.doRun());
            } else {
                executor.execute(() -> next.byPassWith(status));
            }
        }

    }

    public boolean done() {
        return done(status);
    }

    public static boolean done(Status status){
        return status == Status.DONE ||
                status == Status.SKIPPED;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    boolean done(List<Status> a) {
        return a.stream().allMatch(Job::done);
    }

    public void byPassWith(Status status) {
        this.status = status;
        stop();
    }

    boolean finished(){
        return finish(status);
    }

    boolean finish(Status status){
        return status == Status.DONE||
                status == Status.FAILED||
                status == Status.SKIPPED;
    }

    public void resetStatus() {
        setStatus(Status.PENDING);
    }



}
