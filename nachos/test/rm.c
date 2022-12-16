#include "syscall.h"
#include "stdio.h"
#include "stdlib.h"

int main(int argc, char** argv)
{
  printf("argc%d\n",argc);
  for (int i=0;i<argc;i++){
    printf("argv%c\n",argv[i]);
  }
  if (argc!=2) {
    printf("Usage: rm <file>\n");
    return 1;
  }

  if (unlink(argv[1]) != 0) {
      printf("Unable to remove %s\n", argv[1]);
      return 1;
  }

  return 0;
}
