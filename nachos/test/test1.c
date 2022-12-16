#include "syscall.h"
int main(int argc,char * argv[]){
    char buf[10];
    exec("matmult.coff",0,buf);
    printf("######firsttime");
    exec("matmult.coff",0,buf);
    printf("######secondtime");
    exec("matmult.coff",0,buf);
    printf("######thirdtime");
    exit(-6666);

}