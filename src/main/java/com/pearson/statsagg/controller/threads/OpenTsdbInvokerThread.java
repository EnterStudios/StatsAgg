package com.pearson.statsagg.controller.threads;

import com.pearson.statsagg.utilities.InvokerThread;
import com.pearson.statsagg.globals.ApplicationConfiguration;
import com.pearson.statsagg.metric_aggregation.threads.OpenTsdbThread;
import com.pearson.statsagg.utilities.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeffrey Schmidt
 */
public class OpenTsdbInvokerThread extends InvokerThread {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenTsdbInvokerThread.class.getName());
    
    private final long threadExecutorShutdownWaitTime_;
    
    public OpenTsdbInvokerThread() {
        threadExecutorShutdownWaitTime_ = (2 * ApplicationConfiguration.getFlushTimeAgg()) + 3000;
    }
    
    @Override
    public void run() {

        synchronized (lockObject_) {
            while (continueRunning_) {
                long currentTimeInMilliseconds = System.currentTimeMillis();

                threadExecutor_.execute(new OpenTsdbThread(currentTimeInMilliseconds));

                try {
                    lockObject_.wait(ApplicationConfiguration.getFlushTimeAgg());
                }
                catch (Exception e) {}
            }
        }
        
        while (!threadExecutor_.isTerminated()) {
            Threads.sleepMilliseconds(100);
        }
        
        isShutdown_ = true;
    }
    
    @Override
    public long getThreadExecutorShutdownWaitTime() {
        return threadExecutorShutdownWaitTime_;
    }
    
}