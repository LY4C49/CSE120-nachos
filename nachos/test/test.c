#include "syscall.h"
int main(){
    // int res=unlink("lejfjowiejf2w.txt");
    // printf("unlink result: %d\n",res);

    // int res1=unlink("1.txt");
    // printf("unlink result: %d\n",res1);
    //char buffer[10];
    // int childargc = 2;
    // char *childargv[2] = {
	// "rm.coff",
	// "test.py",
    // };
    char ** file_name = {"1111111.txt"};
    printf("ptr1%d,ptr2%d",&file_name,&file_name[0]);
    
    //exec("write10.coff",0,buffer);
    
    exec("test1.coff",1,file_name);
}