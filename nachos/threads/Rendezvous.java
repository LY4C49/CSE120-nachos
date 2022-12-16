package nachos.threads;

import java.util.HashMap;

import nachos.machine.*;

/**
 * A <i>Rendezvous</i> allows threads to synchronously exchange values.
 */
public class Rendezvous {
    /**
     * Allocate a new Rendezvous.
     */
    public Rendezvous () {
    }

    /**
     * Synchronously exchange a value with another thread.  The first
     * thread A (with value X) to exhange will block waiting for
     * another thread B (with value Y).  When thread B arrives, it
     * will unblock A and the threads will exchange values: value Y
     * will be returned to thread A, and value X will be returned to
     * thread B.
     *
     * Different integer tags are used as different, parallel
     * synchronization points (i.e., threads synchronizing at
     * different tags do not interact with each other).  The same tag
     * can also be used repeatedly for multiple exchanges.
     *
     * @param tag the synchronization tag.
     * @param value the integer to exchange.
     */
    private HashMap<Integer,Integer> counter=new HashMap<>();
    private HashMap<Integer,Condition> exchanging=new HashMap<>();
    private HashMap<Integer,Condition> waiting=new HashMap<>();
    private HashMap<Integer,Integer> valueContainer=new HashMap<>();
    private HashMap<Integer,Lock> Locks=new HashMap<>();
    private Lock lock=new Lock();

    // public int exchange (int tag, int value) {
    //     //lock.acquire();
    //     boolean intStatus=Machine.interrupt().disable();
    //     if(!waitThreads.containsKey(tag)){
    //         exchangeValues.put(tag, value);
    //         waitThreads.put(tag,KThread.currentThread());
    //         //lock.release();
    //         KThread.sleep();
    //         //Machine.interrupt.restore(intStatus);
    //         return exchangeValues.get(tag);
    //     }
    //     else{
    //         int tmp=exchangeValues.get(tag);
    //         exchangeValues.put(tag,value);
    //         KThread wakeup=waitThreads.get(tag);
    //         waitThreads.remove(tag);
    //         wakeup.ready();
    //         Machine.interrupt().restore(intStatus);
    //         return tmp;
    //     }
    // }

            // KThread t3 = new KThread( new Runnable () {
            //     public void run() {
            //         int tag = 0;
            //         int send = 1;
        
            //         System.out.println ("Thread " + KThread.currentThread().getName() + " exchanging " + send);
            //         int recv = r.exchange (tag, send);
            //         //Lib.assertTrue (recv == -1, KThread.currentThread().getName()+"Was expecting " + -1 + " but received " + recv);
            //         System.out.println ("Thread " + KThread.currentThread().getName() + " received " + recv);
            //     }
            //     });
            // t3.setName("t3");

            // KThread t4 = new KThread( new Runnable () {
            //     public void run() {
            //         int tag = 0;
            //         int send = -100;
        
            //         System.out.println ("Thread " + KThread.currentThread().getName() + " exchanging " + send);
            //         int recv = r.exchange (tag, send);
            //         //Lib.assertTrue (recv == 1, KThread.currentThread().getName()+"Was expecting " + 1 + " but received " + recv);
            //         System.out.println ("Thread " + KThread.currentThread().getName() + " received " + recv);
            //     }
            //     });
            // t4.setName("t4");

            // t1.fork();t2.fork();t3.fork();t4.fork();
            // t1.join();t2.join();t3.join();t4.join();
        
            // t1.fork(); t2.fork();
            // // assumes join is implemented correctly
            // t1.join(); t2.join();//t3.join();

            // t3.fork();t4.fork();
            // t3.join();t4.join();
            public static void selfTest() {
    // place calls to your Rendezvous tests that you implement here
    //rendezTest1();
    }
            }
        
            // Invoke Rendezvous.selfTest() from ThreadedKernel.selfTest()
        
