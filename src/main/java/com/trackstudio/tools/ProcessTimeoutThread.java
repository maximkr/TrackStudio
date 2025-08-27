package com.trackstudio.tools;

public class ProcessTimeoutThread implements Runnable {
    private Process mProcess;
    private long mStartTimeInMiliseconds;
    private long mTimeoutValueInMiliseconds;

    public ProcessTimeoutThread(Process aProcess, long aStartTimeInMiliseconds, long aTimeoutValueInMiliseconds) {
        mProcess = aProcess;
        mTimeoutValueInMiliseconds = aTimeoutValueInMiliseconds;
        mStartTimeInMiliseconds = aStartTimeInMiliseconds;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(mTimeoutValueInMiliseconds);
            mProcess.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
