package uk.co.whatsa;

public class Helper {

	private Helper() {
	}
	
	public static void waitForOrKill(Process process, long numberOfMillis) {
	    ProcessRunner runnable = new ProcessRunner(process);
	    Thread thread = new Thread(runnable);
	    thread.start();
	    runnable.waitForOrKill(numberOfMillis);
	}

	private static class ProcessRunner implements Runnable {
	    private Process process;
	    private boolean finished;

	    public ProcessRunner(Process process) {
	        this.process = process;
	    }

	    public void run() {
	        try {
	            process.waitFor();
	        } catch (InterruptedException e) {
	            // Ignore
	        }
	        synchronized (this) {
	            notifyAll();
	            finished = true;
	        }
	    }

	    public synchronized void waitForOrKill(long millis) {
	        if (!finished) {
	            try {
	                wait(millis);
	            } catch (InterruptedException e) {
	                // Ignore
	            }
	            if (!finished) {
	                process.destroy();
	            }
	        }
	    }
	}
}
