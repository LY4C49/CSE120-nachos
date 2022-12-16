package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;
import nachos.vm.*;

import java.io.EOFException;
//add this one to help us use hash map in this file
import java.util.HashMap;

/**
 * Encapsulates the state of a user process that is not contained in its user
 * thread (or threads). This includes its address translation state, a file
 * table, and information about the program being executed.
 * 
 * <p>
 * This class is extended by other classes to support additional functionality
 * (such as additional syscalls).
 * 
 * @see nachos.vm.VMProcess
 * @see nachos.network.NetProcess
 */
public class UserProcess {
	/**
	 * Allocate a new process.
	 */
	public static SynchConsole console;
	public UserProcess() {
		// int numPhysPages = Machine.processor().getNumPhysPages();
		// pageTable = new TranslationEntry[numPhysPages];
		// for (int i = 0; i < numPhysPages; i++)
		// 	pageTable[i] = new TranslationEntry(i, i, true, false, false, false);
		// //console = new SynchConsole(Machine.console());
		for (int i=0;i<16;i++){
			position_table[i] = 0;
		}
		file_table[0] = UserKernel.console.openForReading();
		file_table[1] = UserKernel.console.openForWriting();	
	}
	//private SynchConsole synchconsole = null;
	
	/**
	 * Allocate and return a new process of the correct class. The class name is
	 * specified by the <tt>nachos.conf</tt> key
	 * <tt>Kernel.processClassName</tt>.
	 * 
	 * @return a new process of the correct class.
	 */
	public static UserProcess newUserProcess() {
	        String name = Machine.getProcessClassName ();

		// If Lib.constructObject is used, it quickly runs out
		// of file descriptors and throws an exception in
		// createClassLoader.  Hack around it by hard-coding
		// creating new processes of the appropriate type.

		if (name.equals ("nachos.userprog.UserProcess")) {
		    return new UserProcess ();
		} else if (name.equals ("nachos.vm.VMProcess")) {
		    return new VMProcess ();
		} else {
		    return (UserProcess) Lib.constructObject(Machine.getProcessClassName());
		}
	}

	/**
	 * Execute the specified program with the specified arguments. Attempts to
	 * load the program, and then forks a thread to run it.
	 * 
	 * @param name the name of the file containing the executable.
	 * @param args the arguments to pass to the executable.
	 * @return <tt>true</tt> if the program was successfully executed.
	 */
	public boolean execute(String name, String[] args) {
		if (!load(name, args)){
			System.out.println("execute fail");
			return false;
		}
		UserKernel.lock_num.acquire();
		
		UserKernel.cur_process_num += 1;
		if (UserKernel.lock_num.isHeldByCurrentThread()){
			UserKernel.lock_num.release();
		}
		System.out.println("thread name at execute"+name);
		thread = new UThread(this);
		thread.setName(name).fork();

		return true;
	}

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		Machine.processor().setPageTable(pageTable);
	}

	/**
	 * Read a null-terminated string from this process's virtual memory. Read at
	 * most <tt>maxLength + 1</tt> bytes from the specified address, search for
	 * the null terminator, and convert it to a <tt>java.lang.String</tt>,
	 * without including the null terminator. If no null terminator is found,
	 * returns <tt>null</tt>.
	 * 
	 * @param vaddr the starting virtual address of the null-terminated string.
	 * @param maxLength the maximum number of characters in the string, not
	 * including the null terminator.
	 * @return the string read, or <tt>null</tt> if no null terminator was
	 * found.
	 */
	public String readVirtualMemoryString(int vaddr, int maxLength) {
		Lib.assertTrue(maxLength >= 0);

		byte[] bytes = new byte[maxLength + 1];

		int bytesRead = readVirtualMemory(vaddr, bytes);
		//System.out.println("this is byte read"+bytesRead+'\n');
		//System.out.println("this is byte read"+bytes+'\n');

		for (int length = 0; length < bytesRead; length++) {
			if (bytes[length] == 0){
				//System.out.println("lenerhrfegoirvklevgoelgh"+length+'\n');
				return new String(bytes, 0, length);
			}
		}

		return null;
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
	 * could be copied).
	 * 
	 * @param vaddr the first byte of virtual memory to read.
	 * @param data the array where the data will be stored.
	 * @param offset the first byte to write in the array.
	 * @param length the number of bytes to transfer from virtual memory to the
	 * array.
	 * @return the number of bytes successfully transferred.
	 */
	public int readVirtualMemory(int vaddr, byte[] data, int offset, int length) {
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();
		int vpn = vaddr / pageSize;
		
		int page_offset = vaddr % pageSize;
		// for now, just assume that virtual addresses equal physical addresses
		//System.out.println(vpn);
		if (phy_page_list[vpn]*pageSize + page_offset < 0 || phy_page_list[vpn]*pageSize + page_offset >= memory.length)
			return 0;
		int count_amount=0;
		int amount = Math.min(length, pageSize - page_offset);
		int ppn;
		while (length > 0){
			if (vpn>=this.numPages){
				return -1;
			}
			count_amount+=amount;
			ppn = phy_page_list[vpn];
			System.arraycopy(memory,ppn*pageSize+page_offset,data,offset,amount);
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
		Lib.assertTrue(offset >= 0 && length >= 0
				&& offset + length <= data.length);

		byte[] memory = Machine.processor().getMemory();
		int vpn = vaddr / pageSize;
		
		int page_offset = vaddr % pageSize;
		// for now, just assume that virtual addresses equal physical addresses
		if (phy_page_list[vpn]*pageSize + page_offset < 0 || phy_page_list[vpn]*pageSize + page_offset >= memory.length)
			return 0;
		int ppn = phy_page_list[vpn];
		int count_amount=0;
		int amount = Math.min(length, pageSize - page_offset);
		while (length > 0){
			if(vpn>=this.numPages){
				return -1;
			}
			count_amount+=amount;
			ppn = phy_page_list[vpn];
			System.arraycopy(data,offset,memory,ppn*pageSize+page_offset,amount);
			length = length - amount;
			offset += amount;
			vpn += 1;
			page_offset = 0;
			amount = Math.min(length,pageSize);
		}
		return count_amount;
	}


	/**
	 * Load the executable with the specified name into this process, and
	 * prepare to pass it the specified arguments. Opens the executable, reads
	 * its header information, and copies sections and arguments into this
	 * process's virtual memory.
	 * 
	 * @param name the name of the file containing the executable.
	 * @param args the arguments to pass to the executable.
	 * @return <tt>true</tt> if the executable was successfully loaded.
	 */
	private boolean load(String name, String[] args) {
		Lib.debug(dbgProcess, "UserProcess.load(\"" + name + "\")");

		OpenFile executable = ThreadedKernel.fileSystem.open(name, false);
		if (executable == null) {
			Lib.debug(dbgProcess, "\topen failed");
			System.out.println("1001");
			return false;
		}

		try {
			coff = new Coff(executable);
		}
		catch (EOFException e) {
			executable.close();
			Lib.debug(dbgProcess, "\tcoff load failed");
			System.out.println("1002");
			return false;
		}

		// make sure the sections are contiguous and start at page 0
		numPages = 0;
		for (int s = 0; s < coff.getNumSections(); s++) {
			CoffSection section = coff.getSection(s);
			if (section.getFirstVPN() != numPages) {
				coff.close();
				Lib.debug(dbgProcess, "\tfragmented executable");
				System.out.println("1003");
				return false;
			}
			numPages += section.getLength();
		}

		// make sure the argv array will fit in one page
		byte[][] argv = new byte[args.length][];
		int argsSize = 0;
		for (int i = 0; i < args.length; i++) {
			argv[i] = args[i].getBytes();
			// 4 bytes for argv[] pointer; then string plus one for null byte
			argsSize += 4 + argv[i].length + 1;
		}
		if (argsSize > pageSize) {
			coff.close();
			Lib.debug(dbgProcess, "\targuments too long");
			System.out.println("1004");
			return false;
		}

		// program counter initially points at the program entry point
		initialPC = coff.getEntryPoint();

		// next comes the stack; stack pointer initially points to top of it
		numPages += stackPages;
		initialSP = numPages * pageSize;

		// and finally reserve 1 page for arguments
		numPages++;

		if (!loadSections()){
			System.out.println("1005");
			return false;
		}

		// store arguments in last page
		int entryOffset = (numPages - 1) * pageSize;
		int stringOffset = entryOffset + args.length * 4;

		this.argc = args.length;
		this.argv = entryOffset;

		for (int i = 0; i < argv.length; i++) {
			byte[] stringOffsetBytes = Lib.bytesFromInt(stringOffset);
			Lib.assertTrue(writeVirtualMemory(entryOffset, stringOffsetBytes) == 4);
			entryOffset += 4;
			Lib.assertTrue(writeVirtualMemory(stringOffset, argv[i]) == argv[i].length);
			stringOffset += argv[i].length;
			Lib.assertTrue(writeVirtualMemory(stringOffset, new byte[] { 0 }) == 1);
			stringOffset += 1;
		}

		return true;
	}

	/**
	 * Allocates memory for this process, and loads the COFF sections into
	 * memory. If this returns successfully, the process will definitely be run
	 * (this is the last step in process initialization that can fail).
	 * 
	 * @return <tt>true</tt> if the sections were successfully loaded.
	 */
		protected boolean loadSections() {
		int free_phy_num = 0;

		UserKernel.lock_phy.acquire();
		System.out.println("!!!All physical num="+Machine.processor().getNumPhysPages()+"\n");
		for (int i = 0;i<Machine.processor().getNumPhysPages();i++){
			if (UserKernel.free_phy_mem_list[i] != -1){
				free_phy_num += 1;
			}
		}
		System.out.println("!!!free_phy_num="+free_phy_num+"\n");
		System.out.println("!!!numPages="+numPages+"\n");
		if (numPages > free_phy_num) {
			coff.close();
			Lib.debug(dbgProcess, "\tinsufficient physical memory");
			System.out.println("1006");
			if (UserKernel.lock_phy.isHeldByCurrentThread()){
			UserKernel.lock_phy.release();
		}
			return false;
		}
		pageTable = new TranslationEntry[numPages];
		System.out.println(numPages);
		phy_page_list = new int[numPages];
		int count = 0;

		
		//UserKernel.lock_phy.acquire();
		for (int i = 0;i<UserKernel.free_phy_mem_list.length;i++){
			if(count==numPages){
				break;
			}
			if (UserKernel.free_phy_mem_list[i] != -1){
				phy_page_list[count] = UserKernel.free_phy_mem_list[i];
				UserKernel.free_phy_mem_list[i] = -1;
				count += 1;
			}
		}
		if (UserKernel.lock_phy.isHeldByCurrentThread()){
			UserKernel.lock_phy.release();
		}

		for (int i = 0; i < numPages; i++){
			pageTable[i] = new TranslationEntry(i, phy_page_list[i], true, false, false, false);	
		}
		// load sections
		for (int s = 0; s < coff.getNumSections(); s++) {
			CoffSection section = coff.getSection(s);

			Lib.debug(dbgProcess, "\tinitializing " + section.getName()
					+ " section (" + section.getLength() + " pages)");

			for (int i = 0; i < section.getLength(); i++) {
				int vpn = section.getFirstVPN() + i;
				if (section.isReadOnly()==true){
					pageTable[vpn].readOnly = true;
				}
				int spn = i;
				int ppn = phy_page_list[vpn];
				// for now, just assume virtual addresses=physical addresses
				section.loadPage(spn, ppn);
			}
		}

		return true;
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		UserKernel.lock_phy.acquire();
		for (int i = 0;i < phy_page_list.length;i++){
			UserKernel.free_phy_mem_list[phy_page_list[i]] = phy_page_list[i];
		}
		if (UserKernel.lock_phy.isHeldByCurrentThread()){
			UserKernel.lock_phy.release();
		}
		for (int i = 0;i < file_table.length;i++){
			if (file_table[i] != null){
				file_table[i].close();
			}
		}
	}

	/**
	 * Initialize the processor's registers in preparation for running the
	 * program loaded into this process. Set the PC register to point at the
	 * start function, set the stack pointer register to point at the top of the
	 * stack, set the A0 and A1 registers to argc and argv, respectively, and
	 * initialize all other registers to 0.
	 */
	public void initRegisters() {
		Processor processor = Machine.processor();

		// by default, everything's 0
		for (int i = 0; i < processor.numUserRegisters; i++)
			processor.writeRegister(i, 0);

		// initialize PC and SP according
		processor.writeRegister(Processor.regPC, initialPC);
		processor.writeRegister(Processor.regSP, initialSP);

		// initialize the first two argument registers to argc and argv
		processor.writeRegister(Processor.regA0, argc);
		processor.writeRegister(Processor.regA1, argv);
	}


	/**
	 * Handle the halt() system call.
	 */
	private int handleHalt() {
		System.out.println("Halt!!!");
		Machine.halt();

		Lib.assertNotReached("Machine.halt() did not halt machine!");
		return 0;
	}

	//check if vaddr is valid; valid return 1 else 0
	private boolean isValidadd(int vaddr){
		if (vaddr<0){
			return false;
		}

		int vpn=vaddr / this.pageSize;
		if (vpn>=this.numPages){
			return false;
		}
		else{
			return true;
		}
		
	}

    private int handleOpen(int vaddr){
		System.out.println("Open......");
		if (!isValidadd(vaddr)){
			return -1;
		}

        String file_name = readVirtualMemoryString(vaddr,256);
		//corner case if!
		System.out.println(file_name);
        OpenFile file = ThreadedKernel.fileSystem.open(file_name,false);
        if (file == null){
			return -1;
		}
		int file_descriptor = -1;
        for (int i = 0;i < file_table.length; i ++){
            if (file_table[i] == null){
                file_descriptor = i;
                file_table[i] = file;
				position_table[i] = 0;
				break;
            }
        }
		System.out.println(file_descriptor);
        return file_descriptor;
    }

    private int handleClose(int file_descriptor){
		//file = ThreadedKernel.fileSystem.open(file_name,false);
		//file.close();
		System.out.println("Closse");
		if (file_descriptor>15 || file_descriptor<0){
			return -1;
		}
		if (file_table[file_descriptor] == null){
			return -1;
		}
		file_table[file_descriptor].close();
        file_table[file_descriptor] = null;
		position_table[file_descriptor] = 0;
        return 0;
    }

    private int handleCreate(int vaddr){
		System.out.println("Create");
		if (!isValidadd(vaddr)){
			return -1;
		}

        String file_name = readVirtualMemoryString(vaddr,256);
		System.out.println(file_name);
		//corner case! if
        OpenFile file = ThreadedKernel.fileSystem.open(file_name,false);
		if (file != null){
			//if full, return -1?
			int file_descriptor = -1;
			for (int i = 0;i < file_table.length; i ++){
				if (file_table[i] == null){
					file_descriptor = i;
					file_table[i] = file;
					break;
				}
			}
			return file_descriptor;
		}
		else{
			file = ThreadedKernel.fileSystem.open(file_name,true);
			if (file != null){
				//if full, return -1?
				int file_descriptor = -1;
				for (int i = 0;i < file_table.length; i ++){
					if (file_table[i] == null){
						file_descriptor = i;
						file_table[i] = file;
						break;
					}
				}
				return file_descriptor;
			}
			else{
				return -1;
			}
		}
    }

    private int handleUnlink(int vaddr){
		System.out.println("Unlink");
		if (!isValidadd(vaddr)){
			return -1;
		}

        String file_name = readVirtualMemoryString(vaddr,256);
		System.out.println("handleUnlink:"+file_name);
		if (file_name==null){
			return -1;
		}
        boolean result=ThreadedKernel.fileSystem.remove(file_name);
		if (result){
			return 0;
		}
		else{
			return -1;
		}
        
    }
    private int handleRead(int file_descriptor,int buf_address,int count){
		System.out.println("CallHandleRead ........");
		if (file_descriptor>15 || file_descriptor<0){
			return -1;
		}

		if (!isValidadd(buf_address)){
			return -1;
		}


		if (count<0){
			return -1;
		}

        OpenFile file = file_table[file_descriptor];

		if (file == null){
			return -1;
		}
		
		//count>page_size
		byte[] buf = new byte[count];
		int amount=-1;
		if(file_descriptor==0 || file_descriptor==1){
			amount = file.read(buf,0,count);
		}
		else{
			int length=file.length();
			amount = file.read(position_table[file_descriptor],buf,0,count);
			position_table[file_descriptor] += amount;
		}

		int status=writeVirtualMemory(buf_address,buf);
		if (status==-1){
			return -1;
		}
		System.out.println("CallHandleRead Finish soon........");
        return amount;
    }

    private int handleWrite(int file_descriptor,int buf_address,int count){
		System.out.println("CallHandleWrite ........");
		if (file_descriptor>15 || file_descriptor<0){
			return -1;
		}

		if (!isValidadd(buf_address)){
			return -1;
		}

		//System.out.println("???");
		int buffer_length=0;

		if(count<0){
			return -1;
		}

        OpenFile file = file_table[file_descriptor];
		if (file == null){
			return -1;
		}
		byte[] buf = new byte[count];
		
		int status=readVirtualMemory(buf_address,buf);
		//System.out.println("status is "+status+"!!!");
		if (status==-1){
			return -1;
		}
		int real_count=count;
		int amount=-1;
		if(file_descriptor==0 || file_descriptor==1){
			amount = file.write(buf,0,Math.min(real_count,count));
		}
		else{
			int length=file.length();
			amount = file.write(length,buf,0,Math.min(real_count,count));
		}
		//System.out.println("amount is "+amount);
		System.out.println("CallHandleWrite finish........");
		return amount;
		
    }

	public int handleExec(int a0,int a1,int a2){
		System.out.println("Exec");
			if (!isValidadd(a0)){
				return -1;
			}
			if (!isValidadd(a2)){
				return -1;
			}
			if(a1<0){
				return -1;
			}	
			UserProcess child = newUserProcess();
			//System.out.println("I want to exec "+"thread pid:"+pid_count+"thread:"+child.thread);
			
			child.my_pid = pid_count;
			
			child.parent = this;

			String file_name = readVirtualMemoryString(a0,256);
			//System.out.println("file name:"+file_name);
			String [] buf = new String[a1];
			//byte[] buf = new byte[count];
			for (int i = 0; i < a1; i++){
				byte ptr[] = new byte[4];
				readVirtualMemory(a2 + i * 4,ptr);
				int str_vaddr = Lib.bytesToInt(ptr,0);
				String part_argument = readVirtualMemoryString(str_vaddr,256);
				//System.out.println("now position"+a2+"\n"+part_argument+"part_argument");
				buf[i] = part_argument;
			}
			boolean exec_res=child.execute(file_name,buf);
			if (exec_res){
				child_process_map.put(pid_count,child);
				pid_count += 1;
				return child.my_pid;
			}
			else{
				return -1;
			}
			//System.out.println("After,execute, I want to exec "+"thread pid:"+pid_count+"thread:"+child.thread);
			
	}

	public int handleJoin(int a0,int a1){
		System.out.println("Join");
		if (!isValidadd(a1)){
			return -1;
		}
		System.out.println("I want to join Pid:"+a0);
		System.out.println("I want to join Process:"+child_process_map.get(a0));
		System.out.println("I want to join Thread:"+child_process_map.get(a0).thread);
		if (child_process_map.containsKey(a0)){
			System.out.println("pid"+a0);
			if(child_process_map.get(a0).thread==null){
				return 1;
			}
			child_process_map.get(a0).thread.join();
			byte[] buf = Lib.bytesFromInt(child_exit_status_map.get(a0));
			writeVirtualMemory(a1,buf);
			if (child_exit_status_map.containsKey(a0)){
				return 1;
			}
			else{
				return 0;
			}
		}
		else{
			return -1;
		}
	}

	//initial the file table and put the stdin and stdout into the file table.
    protected OpenFile[] file_table = new OpenFile[16];
	protected int[] position_table = new int[16];

	/**
	 * Handle the exit() system call.
	 */
	private int handleExit(int status) {
		System.out.println("Exit");

	        // Do not remove this call to the autoGrader...
		Machine.autoGrader().finishingCurrentProcess(status);
		// ...and leave it as the top of handleExit so that we
		// can grade your implementation.

		Lib.debug(dbgProcess, "UserProcess.handleExit (" + status + ")");
		// for now, unconditionally terminate with just one process
		if (exit_noraml){
			if (parent != null){
				parent.child_exit_status_map.put(my_pid,status);
			}		
		}

		for(Integer i:child_process_map.keySet()){
			child_process_map.get(i).thread.join();
		}
		unloadSections();
		System.out.println("process_num"+UserKernel.cur_process_num);
		if (UserKernel.cur_process_num <= 1){

			UserKernel.lock_num.acquire();
			UserKernel.cur_process_num -= 1;
			if (UserKernel.lock_num.isHeldByCurrentThread()){
				UserKernel.lock_num.release();
			}

			//this place should be cleared. Should we consider this case, when there is a join, the parent thread must terminate until the child terminate.
			//However, when there is no join, what will happen if the parent is willing to terminate while the child is still running.
			
			Kernel.kernel.terminate();
		}
		else{
			UserKernel.lock_num.acquire();
			UserKernel.cur_process_num -= 1;
			if (UserKernel.lock_num.isHeldByCurrentThread()){
				UserKernel.lock_num.release();
			}
			thread.finish();
		}

		return 0;
	}

	private static final int syscallHalt = 0, syscallExit = 1, syscallExec = 2,
			syscallJoin = 3, syscallCreate = 4, syscallOpen = 5,
			syscallRead = 6, syscallWrite = 7, syscallClose = 8,
			syscallUnlink = 9;

	/**
	 * Handle a syscall exception. Called by <tt>handleException()</tt>. The
	 * <i>syscall</i> argument identifies which syscall the user executed:
	 * 
	 * <table>
	 * <tr>
	 * <td>syscall#</td>
	 * <td>syscall prototype</td>
	 * </tr>
	 * <tr>
	 * <td>0</td>
	 * <td><tt>void halt();</tt></td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td><tt>void exit(int status);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td><tt>int  exec(char *name, int argc, char **argv);
	 * 								</tt></td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td><tt>int  join(int pid, int *status);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td><tt>int  creat(char *name);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>5</td>
	 * <td><tt>int  open(char *name);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>6</td>
	 * <td><tt>int  read(int fd, char *buffer, int size);
	 * 								</tt></td>
	 * </tr>
	 * <tr>
	 * <td>7</td>
	 * <td><tt>int  write(int fd, char *buffer, int size);
	 * 								</tt></td>
	 * </tr>
	 * <tr>
	 * <td>8</td>
	 * <td><tt>int  close(int fd);</tt></td>
	 * </tr>
	 * <tr>
	 * <td>9</td>
	 * <td><tt>int  unlink(char *name);</tt></td>
	 * </tr>
	 * </table>
	 * 
	 * @param syscall the syscall number.
	 * @param a0 the first syscall argument.
	 * @param a1 the second syscall argument.
	 * @param a2 the third syscall argument.
	 * @param a3 the fourth syscall argument.
	 * @return the value to be returned to the user.
	 */
	public int handleSyscall(int syscall, int a0, int a1, int a2, int a3) {
		//file_table[0] = openForWriting();
		//file_table[1] = openForReading();
		switch (syscall) {
		case syscallHalt:
			return handleHalt();
		case syscallExit:
			return handleExit(a0);
		case syscallCreate:
			return handleCreate(a0);
		case syscallOpen:
			return handleOpen(a0);
		case syscallRead:
			return handleRead(a0,a1,a2);
		case syscallWrite:
			return handleWrite(a0,a1,a2);
		case syscallClose:
			return handleClose(a0);
		case syscallUnlink:
			return handleUnlink(a0);
		//here is what I add into the handleSyscall function:
		case syscallExec:
			return handleExec(a0,a1,a2);
		case syscallJoin:
			return handleJoin(a0,a1);

		default:
			Lib.debug(dbgProcess, "Unknown syscall " + syscall);
			//return -1;
			Lib.assertNotReached("Unknown system call!");
		}
		return 0;
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
		case Processor.exceptionSyscall:
			int result = handleSyscall(processor.readRegister(Processor.regV0),
					processor.readRegister(Processor.regA0),
					processor.readRegister(Processor.regA1),
					processor.readRegister(Processor.regA2),
					processor.readRegister(Processor.regA3));
			processor.writeRegister(Processor.regV0, result);
			processor.advancePC();
			break;

		default:
			exit_noraml = false;
			handleExit(-1000);
			Lib.debug(dbgProcess, "Unexpected exception: "
					+ Processor.exceptionNames[cause]);
			Lib.assertNotReached("Unexpected exception");
		}
	}

	/** The program being run by this process. */
	protected Coff coff;

	/** This process's page table. */
	protected TranslationEntry[] pageTable;
	protected int[] phy_page_list;

	/** The number of contiguous pages occupied by the program. */
	protected int numPages;

	/** The number of pages in the program's stack. */
	protected final int stackPages = 8;

	/** The thread that executes the user-level program. */
	protected UThread thread;
    
	private int initialPC, initialSP;

	private boolean exit_noraml = true;
	

	private int argc, argv;

	private static final int pageSize = Processor.pageSize;

	//this variables are for the part3 of proje2
	protected HashMap<Integer,UserProcess> child_process_map = new HashMap<Integer,UserProcess>();
	protected UserProcess parent;
	protected HashMap<Integer,Integer> child_exit_status_map = new HashMap<Integer,Integer>();
	protected int pid_count = 5;
	protected int my_pid = -1;

	private static final char dbgProcess = 'a';

}
