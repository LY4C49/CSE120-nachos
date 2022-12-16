package nachos.threads;

import nachos.machine.*;

import java.util.HashMap;

import java.util.Queue;

import java.util.PriorityQueue;
/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */


	/* Add on 20221013*/ 
	public static HashMap<KThread,Long> sleepQueue=new HashMap<KThread,Long>();
	// public static Queue<KThread> orderWakeup = new PriorityQueue<>((t1,t2)->{
	// 	long t1_waketime=this.sleepQueue.get(t1);
	// 	long t2_waketime=this.sleepQueue.get(t2);
	// 	return t1_waketime-t2_waketime;
	// });
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		// KThread.currentThread().yield();
		long nowTime=Machine.timer().getTime();

		Queue<KThread> orderWakeup = new PriorityQueue<KThread>((t1,t2)->{
			long t1_waketime=this.sleepQueue.get(t1);
			long t2_waketime=this.sleepQueue.get(t2);
			if (t1_waketime-t2_waketime>=0){
				return 1;
			}
			else{
				return -1;
			}
			//return t1_waketime-t2_waketime;
		});

		for(KThread i:sleepQueue.keySet()){
				long t=sleepQueue.get(i);
				//System.out.println(i.toString());
				if(nowTime>=t){
					KThread wakeThread=i;
					orderWakeup.offer(wakeThread);
					//sleepQueue.remove(i);
					//orderWakeup.offer(i);
				}
			}
		while(orderWakeup.size()!=0){
			KThread t=orderWakeup.peek();
			sleepQueue.remove(t);
			orderWakeup.poll();
			if (cancel(t)){
				t.ready();
			}
		}

	}

	

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		//for now, cheat just to get something working (busy waiting is bad)
		if(x>0){
			long wakeTime = Machine.timer().getTime() + x;
			sleepQueue.put(KThread.currentThread(), wakeTime);
			KThread.sleep();
		}
		
		// long wakeTime = Machine.timer().getTime() + x;
		// while (wakeTime > Machine.timer().getTime())
		// 	KThread.yield();
	}

        /**
	 * Cancel any timer set by <i>thread</i>, effectively waking
	 * up the thread immediately (placing it in the scheduler
	 * ready set) and returning true.  If <i>thread</i> has no
	 * timer set, return false.
	 * 
	 * <p>
	 * @param thread the thread whose timer should be cancelled.
	 */
	 
    public boolean cancel(KThread thread) {
		if (thread.getStatus()==3){
			return true;
		}
		else{
			return false;
		}
	}

	// Add Alarm testing code to the Alarm class
    public static void alarmTest1() {
	int durations[] = {1000, 10*1000, 100*1000,10,-1};
	long t0, t1;

	for (int d : durations) {
	    t0 = Machine.timer().getTime();
	    ThreadedKernel.alarm.waitUntil (d);
	    t1 = Machine.timer().getTime();
	    System.out.println ("alarmTest1: waited for " + (t1 - t0) + " ticks");
	}
    }
}
