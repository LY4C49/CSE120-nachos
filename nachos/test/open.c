#include "syscall.h"

int main (int argc, char *argv[])
{
    // char *str = "\nroses are red\nviolets are blue\nI love Nachos\nand so do you\n\n";
    
    // while (*str) {
	// int r = write (1, str, 1);
	// if (r != 1) {
	//     printf ("failed to write character (r = %d)\n", r);
	//     exit (-1);
	// }
	// str++;
    // }
    char *filename="test.txt";
    int des=creat(filename);
    //int des=open(filename);
    
    char *str = "roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n
    roses are red\nviolets are blue\nI love Nachos\nand so do you\n\n 0123456789\t9876543210hhh\n"; //86Bytes

    //only count=1 is ok; other cases will boom!
    int count=4096;
    int bytes_write=0;
    while (*str) {
	int r = write (des, str, count);
    bytes_write+=r;
    // printf("%d bytes write\n",r);
	// if (r != count) {
	//     printf ("failed to write character (r = %d)\n", r);
	//     exit (-1);
	// }
	str+=r;
    }
    printf("the des is :%d-------",des);
    printf("total %d bytes write\n",bytes_write);
    unsigned char buffer[4096];
    // char *ptr=buffer;
    
    int amount=read(des, buffer, 4096);
    
    printf("%d bytes are read\n",amount);
    for(int i=0;i<4096;i++){
        printf("%c",buffer[i]);
    }
    printf("\0");
    
    return 0;
}