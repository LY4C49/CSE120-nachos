package nachos.vm;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;
import nachos.vm.*;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * A kernel that can support multiple demand-paging user processes.
 */
public class VMKernel extends UserKernel {
	/**
	 * Allocate a new VM kernel.
	 */
	public VMKernel() {
		super();
	}

	/**
	 * Initialize this kernel.
	 */
	public void initialize(String[] args) {
		super.initialize(args);
		this.openfile=ThreadedKernel.fileSystem.open("swap",true);
		for (int i=0;i<Machine.processor().getNumPhysPages();i++){
			VMKernel.pinning_map.put(i,false);
		}
		lock_all = new Lock();
		cv = new Condition(lock_all);
	}

	/**
	 * Test this kernel.
	 */
	public void selfTest() {
		super.selfTest();
	}

	/**
	 * Start running user programs.
	 */
	public void run() {
		super.run();
	}

	/**
	 * Terminate this kernel. Never returns.
	 */
	public void terminate() {
		super.terminate();
	}

	public static void find_VMprocess(int ppn, TranslationEntry te){
		VMProcess vp = VMKernel.Vprocess_map.get(ppn);
		int v = vp.get_vpn(te);
		vp.set_swap_map(v);
		System.out.println("find_VMprocess finish");
	}

	// dummy variables to make javac smarter
	private static VMProcess dummy1 = null;

	private static final char dbgVM = 'v';

	public static HashMap<Integer,TranslationEntry> process_map = new HashMap<Integer,TranslationEntry>();
	//store the evicted TranslationEntry : offset in swap file
	//public static HashMap<TranslationEntry,Integer> offset_map = new HashMap<TranslationEntry,Integer>();
	//public static ArrayList<Integer> free_swap = new ArrayList<Integer>();
	public static HashMap<Integer,Boolean> pinning_map = new HashMap<Integer,Boolean>();
	public static HashMap<Integer,VMProcess> Vprocess_map = new HashMap<Integer,VMProcess>();
	//public static int total_offset;
	public static OpenFile openfile;
	public static Lock lock_all;
	public static Condition cv;
}
