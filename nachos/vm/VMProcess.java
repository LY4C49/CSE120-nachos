package nachos.vm;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;
import nachos.vm.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * A <tt>UserProcess</tt> that supports demand-paging.
 */
public class VMProcess extends UserProcess {
	/**
	 * Allocate a new process.
	 */
	public VMProcess() {
		super();
	}

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
		super.saveState();
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		super.restoreState();
	}

		/**
	 * Transfer data from this process's virtual memory to all of the specified
	 * array. Same as <tt>readVirtualMemory(vaddr, data, 0, data.length)</tt>.
	 * 
	 * @param vaddr the first byte of virtual memory to read.
	 * @param data the array where the data will be stored.
	 * @return the number of bytes successfully transferred.
	 */
	public int readVirtualMemory(int vaddr, byte[] data) {
		return readVirtualMemory(vaddr, data, 0, data.length);
	}

	/**
	 * Transfer data from this process's virtual memory to the specified array.
	 * This method handles address translation details. This method must
	 * <i>not</i> destroy the current process if an error occurs, but instead
	 * should return the number of bytes successfully copied (or zero if no data
	 * could be copied).vpn
	 * 
	 * @param vaddr the first byte of virtual memory to read.
	 * @param data the array where the data will be stored.
	 * @param offset the first byte to write in the array.
	 * @param length the number of bytes to transfer from virtual memory to the
	 * array.
	 * @return the number of bytes successfully transferred.
	 */
	public int readVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		System.out.println("really read vm!!!####$$$");
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();
		int vpn = vaddr / pageSize;
		
		int page_offset = vaddr % pageSize;
		// for now, just assume that virtual addresses equal physical addresses
		//System.out.println(vpn);
		if (vpn >= this.numPages || vpn < 0){
			return -1;
		}
		int count_amount=0;
		int amount = Math.min(length, pageSize - page_offset);
		int ppn;
		while (length > 0){
			System.out.println("rVM #####");
			if (vpn>=this.numPages){
				return -1;
			}
			count_amount+=amount;
			//acqire lock here
			if(!VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.acquire();
			}
			if (this.pageTable[vpn].valid==false){
				//System.out.println("readVM "+vpn);
				handlePageFault(vpn*this.pageSize);
			}
			//pin here
			VMKernel.pinning_map.put(this.pageTable[vpn].ppn,true);
			if(VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.release();
			}
			//release lock
			ppn = this.pageTable[vpn].ppn;
			//this.pageTable[vpn].dirty=true;
			this.pageTable[vpn].used=true;
			System.arraycopy(memory,ppn*pageSize+page_offset,data,offset,amount);

			//acquire lock
			if(!VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.acquire();
			}
			VMKernel.pinning_map.put(this.pageTable[vpn].ppn,false);
			//unpin here
			//wake all
			VMKernel.cv.wakeAll();
			//release lock
			if(VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.release();
			}
			length = length - amount;
			offset += amount;
			vpn += 1;
			page_offset = 0;
			amount = Math.min(length,pageSize);
		}
		return count_amount;
	}
	/**
	 * Transfer all data from the specified array to this process's virtual
	 * memory. Same as <tt>writeVirtualMemory(vaddr, data, 0, data.length)</tt>.
	 * 
	 * @param vaddr the first byte of virtual memory to write.
	 * @param data the array containing the data to transfer.
	 * @return the number of bytes successfully transferred.
	 */
	public int writeVirtualMemory(int vaddr, byte[] data) {
		return writeVirtualMemory(vaddr, data, 0, data.length);
	}

	/**
	 * Transfer data from the specified array to this process's virtual memory.
	 * This method handles address translation details. This method must
	 * <i>not</i> destroy the current process if an error occurs, but instead
	 * should return the number of bytes successfully copied (or zero if no data
	 * could be copied).
	 * 
	 * @param vaddr the first byte of virtual memory to write.
	 * @param data the array containing the data to transfer.
	 * @param offset the first byte to transfer from the array.
	 * @param length the number of bytes to transfer from the array to virtual
	 * memory.
	 * @return the number of bytes successfully transferred.
	 */
	public int writeVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		System.out.println("really write vm!!!####");
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();
		int vpn = vaddr / pageSize;
		
		int page_offset = vaddr % pageSize;
		// for now, just assume that virtual addresses equal physical addresses
		if (vpn >= this.numPages || vpn < 0){
			return -1;
		}
		int ppn;
		int count_amount=0;
		int amount = Math.min(length, pageSize - page_offset);
		while (length > 0){
			System.out.println("wVM #####");
			if(vpn>=this.numPages){
				return -1;
			}
			count_amount+=amount;
			if(!VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.acquire();
			}
			if (this.pageTable[vpn].valid==false){
				//System.out.println("writeVM "+vpn);
				handlePageFault(vpn*this.pageSize);
			}
			VMKernel.pinning_map.put(this.pageTable[vpn].ppn,true);
			if(VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.release();
			}
			
			ppn = this.pageTable[vpn].ppn;
			//this.pageTable[vpn].dirty=true;
			this.pageTable[vpn].used=true;
			System.arraycopy(data,offset,memory,ppn*pageSize+page_offset,amount);
			if(!VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.acquire();
			}
			VMKernel.pinning_map.put(this.pageTable[vpn].ppn,false);
			//unpin here
			//wake all
			VMKernel.cv.wakeAll();
			//release lock
			if(VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.release();
			}
			length = length - amount;
			offset += amount;
			vpn += 1;
			page_offset = 0;
			amount = Math.min(length,pageSize);
		}
		return count_amount;
	}

	/**
	 * Initializes page tables for this process so that the executable can be
	 * demand-paged.
	 * 
	 * @return <tt>true</tt> if successful.
	 */
	protected boolean loadSections() {
		//return super.loadSections();
		this.pageTable = new TranslationEntry[this.numPages];

		//the first bit is vpn
		//the second bit is ppn
		//the third bit is valid or not
		//the fourth bit is read-only
		//the fifth bit is used or not
		//the sixth bit is dirty or not

		for (int i=0;i<numPages;i++){
			this.pageTable[i]=new TranslationEntry(i,-1,false,false,false,false);
		}

		return true;
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		// UserKernel.lock_phy.acquire();
		for (int i = 0;i < this.pageTable.length;i++){
			if (this.pageTable[i].ppn>=0){
				VMKernel.free_phy_mem_list[this.pageTable[i].ppn]=this.pageTable[i].ppn;

			}
		}
		
		for (int i = 0;i < file_table.length;i++){
			if (file_table[i] != null){
				file_table[i].close();
			}
		}
	}

	/*Check whether it exists a free physical memory*/ 
	private int check_free_memory(){
		for (int i=0;i<VMKernel.free_phy_mem_list.length;i++){
			if (VMKernel.free_phy_mem_list[i] != -1){
				int ppn = VMKernel.free_phy_mem_list[i];
				VMKernel.free_phy_mem_list[i]=-1;
				return ppn;
			}
		}
		return -1;
	}

	/*
	Swap in from swap file
	input: TranslationEntry, ppn you want to write to
	*/ 
	private void swap_in(TranslationEntry te, int ppn){
		System.out.println("Swap in begin.....");
		OpenFile openfile = VMKernel.openfile;

		//Utilize the vpn to store its offset in swap file
		int swap_offset = te.vpn;

		byte[] buf = new byte[this.pageSize];
		int res=openfile.read(swap_offset,Machine.processor().getMemory(),ppn*this.pageSize,this.pageSize);
		byte[] memory = Machine.processor().getMemory();
		if (te.readOnly==true){
			System.out.println("WTF ARE YOU DOING! TRY TO WRITE SHIT!!!!!!!!!========================================================");
		}
		//System.arraycopy(memory,ppn*this.pageSize,buf,0,pageSize);
	}

	/*
	Swap out to swap file
	input: TranslationEntry that choose to be evicted
	*/ 
	private int swap_out(TranslationEntry te){
		/*
		If there is a free offset in swap file, use it
		else write from the end of swap file
		*/
		int write_offset;
			
		write_offset = VMKernel.openfile.length();

		OpenFile openfile=VMKernel.openfile;
		byte[] buf = new byte[this.pageSize];
		byte[] memory = Machine.processor().getMemory();
		if (te.readOnly==true){
			System.out.println("WTF ARE YOU DOING! TRY TO WRITE SHIT!!!!!!!!!*********************************");
		}
		//System.arraycopy(buf,0,memory,te.ppn*this.pageSize,this.pageSize);
		
		int res=openfile.write(write_offset,Machine.processor().getMemory(),te.ppn*this.pageSize,this.pageSize);

		//load zero newly add
		// byte[] buf_1 = new byte[this.pageSize];
		// //byte[] memory = Machine.processor().getMemory();
		// System.arraycopy(memory,te.ppn*this.pageSize,buf_1,0,pageSize);
		// //
			
		//Set the bits in evicted te
		int swap_ppn=te.ppn;
		te.vpn=write_offset; //Utilize the vpn in te to store its offset
		//System.out.println("!!!the victim 's " +swap_ppn+" is and offset in swap is "+te.vpn+"@@@@@");
		te.valid=false;
		te.ppn=-1;
		return swap_ppn;
	}

	private TranslationEntry find_victim(){
		System.out.println("find victim in begin.....");
		boolean success=false;
		//If not all the pages are pinned, you must can find a victim in 2 loops
		for(int j= 0;j<2;j++){
			for (Integer i : VMKernel.process_map.keySet()){
				TranslationEntry cur_te=VMKernel.process_map.get(i);
				//Initialize the pinning_map in VMKernel
				//guarantee that a ppn must be in the pinning_map

				//Checke logics here!!!!   !!!!
				if (cur_te.used == true){
					cur_te.used = false;
					continue;
				}
				else if (VMKernel.pinning_map.get(i) == false){
					VMKernel.find_VMprocess(i, cur_te);
					System.out.println("find victim end.....");
					return cur_te;
				}
			}
		}
		System.out.println("find victim end with bad.....");
		return null;
	}


	public void handlePageFault(int vaddr){
		if(!VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.acquire();
			}
		int vpn = vaddr / this.pageSize; //Translate vaddr into vpn
		System.out.println("handlePageFault vpn is "+vpn);


		/* Detect whether it should call section.loadPage()*/ 
		CoffSection mysection=null;
		for (int s = 0; s < coff.getNumSections(); s++) {
			CoffSection section = coff.getSection(s);

			Lib.debug(dbgProcess, "\tinitializing " + section.getName()
					+ " section (" + section.getLength() + " pages)");

			for (int i = 0; i < section.getLength(); i++) {
				int cur_vpn = section.getFirstVPN() + i;

				if (cur_vpn==vpn){
					mysection=section;
					if (section.isReadOnly()==true){
			 			this.pageTable[vpn].readOnly = true;
					}
					break;
				}
			}
		}

		//acquire lock here!!!

		
		boolean can_break=false;
		int ppn=-1;
		while(!can_break){
			/*
			Check free memory list; if have one free page, 
			return its ppn
			or return -1
			*/ 
			ppn = check_free_memory();
			if (ppn != -1){
				// this.pageTable[vpn].valid=true;
				// this.pageTable[vpn].ppn=ppn;
				can_break=true;
			}
			else{
				//System.out.println("No free page now");
				TranslationEntry victim = find_victim();
				if (victim == null){
					System.out.println("No evicatable page, sleep");
					VMKernel.cv.sleep();
				}
				else{
					ppn=swap_out(victim);
					victim.valid=false;
					victim.ppn=-1;

					if (!victim.readOnly){
						byte[] buf = new byte[this.pageSize];
						byte[] memory = Machine.processor().getMemory();
						System.arraycopy(memory,ppn*this.pageSize,buf,0,pageSize);
						}
					
					}

					can_break=true;
				}
			}
		
		this.pageTable[vpn].ppn=ppn;
		VMKernel.process_map.put(ppn,this.pageTable[vpn]);
		VMKernel.Vprocess_map.put(ppn, this);
		this.pageTable[vpn].valid=true;
		this.pageTable[vpn].used=true;
		//this.pageTable[vpn].ppn=ppn;
		
		
		if (mysection!=null && mysection.isReadOnly()){
			int spn=vpn-mysection.getFirstVPN();
			this.pageTable[vpn].readOnly=true;
			mysection.loadPage(spn,ppn);
			VMKernel.process_map.put(ppn,this.pageTable[vpn]);
			VMKernel.Vprocess_map.put(ppn, this);
			this.pageTable[vpn].valid=true;
			this.pageTable[vpn].ppn=ppn;
			
			return ;
		}


		if (this.swap_map.containsKey(vpn) && this.swap_map.get(vpn)){
			swap_in(this.pageTable[vpn],ppn);
			this.swap_map.put(vpn,false);
			this.pageTable[vpn].valid=true;
		}
		else{
			if (mysection!=null){
				int spn=vpn-mysection.getFirstVPN();
				mysection.loadPage(spn,ppn);
			}
			else{
				byte[] buf = new byte[this.pageSize];
				byte[] memory = Machine.processor().getMemory();
				System.arraycopy(memory,ppn*this.pageSize,buf,0,pageSize);
			}
		}
		
		
	}

	/**
	 * Handle a user exception. Called by <tt>UserKernel.exceptionHandler()</tt>
	 * . The <i>cause</i> argument identifies which exception occurred; see the
	 * <tt>Processor.exceptionZZZ</tt> constants.
	 * 
	 * @param cause the user exception that occurred.
	 */
	public void handleException(int cause) {
		Processor processor = Machine.processor();

		switch (cause) {
		
		case exceptionPageFault:
			int vaddr = processor.readRegister(37);
			System.out.println("the origin vaddr is "+vaddr);
			handlePageFault(vaddr);
			//release lock here!!!
			if(VMKernel.lock_all.isHeldByCurrentThread()){
				VMKernel.lock_all.release();
			}
			break;

		default:
			super.handleException(cause);
			break;
		}
	}

	public void set_swap_map(int vpn){
		this.swap_map.put(vpn, true);
	}

	public int get_vpn(TranslationEntry te){
		for (int i=0;i<this.pageTable.length;i++){
			if (te == this.pageTable[i]){
				return i;
			}
		}
		return -1;
	}

	private static HashMap<Integer, Boolean> swap_map = new HashMap<Integer,Boolean>();

	private static final int exceptionPageFault = 1;

	private static final int pageSize = Processor.pageSize;

	private static final char dbgProcess = 'a';

	private static final char dbgVM = 'v';
}
