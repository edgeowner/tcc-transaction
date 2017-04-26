package org.mengyun.tcctransaction.spring.recover;

import org.mengyun.tcctransaction.recover.RecoverConfig;

import java.util.List;

/**
 * Created by changming.xie on 6/1/16.
 */
public class DefaultRecoverConfig implements RecoverConfig {

    public static final RecoverConfig INSTANCE = new DefaultRecoverConfig();

    private int maxRetryCount = 30;

    private int recoverDuration = 120; //120 seconds

    private String cronExpression = "0 */1 * * * ?";

    private List<Class<? extends Exception>> delayCancelExceptions;

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public int getRecoverDuration() {
        return recoverDuration;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setRecoverDuration(int recoverDuration) {
        this.recoverDuration = recoverDuration;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public List<Class<? extends Exception>> getDelayCancelExceptions() {
        return delayCancelExceptions;
    }

    public void setDelayCancelExceptions(List<Class<? extends Exception>> delayCancelExceptions) {
        this.delayCancelExceptions = delayCancelExceptions;
    }
}
