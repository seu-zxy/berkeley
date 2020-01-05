/*********************
**  Color Palette generator
** Skeleton by Justin Yokota
**********************/

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <math.h>
#include <string.h>
#include "ColorMapInput.h"

//You don't need to call this function but it helps you understand how the arguments are passed in 
void usage(char* argv[])
{
	printf("Incorrect usage: Expected arguments are %s <inputfile> <outputfolder> <width> <heightpercolor>", argv[0]);
}

//Creates a color palette image for the given colorfile in outputfile. Width and heightpercolor dictates the dimensions of each color. Output should be in P3 format
int P3colorpalette(char* colorfile, int width, int heightpercolor, char* outputfile)
{
    int* num = malloc(sizeof(int));
	FILE *save = fopen(outputfile,"w+");
	if (width < 1 || heightpercolor < 1) {
		fclose(save);
		free(num);
		return 1;
	}
	uint8_t** source_arr = FileToColorMap(colorfile,num);
	fprintf(save,"%s %d %d %d\n","P3",width,(*num)*heightpercolor,255);
	if (source_arr != NULL){
		for (int i = 0;i < *num; i++){
	        for (int j = 0; j < heightpercolor; j++){
			   for (int k = 0; k < width; k++){
			   	  int x0 = source_arr[i][0];
                  int x1 = source_arr[i][1];
                  int x2 = source_arr[i][2];
                  if(k < width - 1){
				    fprintf(save, "%d %d %d ",x0,x1,x2);
                  } else {
                    fprintf(save,"%d %d %d\n", x0, x1, x2);
                  }
			   }  
		    }
	    }
		for(int i = 0; i< *num; i++){
			free(source_arr[i]);
		}
		free(source_arr);
		free(num);
		fclose(save);
	    return 0;
	} else {
		free(num);
		fclose(save);
		return 1;
	}
}

//Same as above, but with P6 format
int P6colorpalette(char* colorfile, int width, int heightpercolor, char* outputfile)
{
	int* num = malloc(sizeof(int));
	FILE *save = fopen(outputfile,"w+");
	if (width < 1 || heightpercolor < 1) {
		fclose(save);
		free(num);
		return 1;
	}
	uint8_t** source_arr = FileToColorMap(colorfile,num);
	fprintf(save,"%s %d %d %d\n","P6",width,(*num)*heightpercolor,255);
	if (source_arr != NULL){
		for (int i = 0;i < *num; i++){
	        for (int j = 0; j < heightpercolor; j++){
			   for (int k = 0; k < width; k++){
                  fwrite(source_arr[i],sizeof(uint8_t),3,save);
			   } 
		    }
	    }
		for(int i = 0; i< *num; i++){
			free(source_arr[i]);
		}
		free(source_arr);
		free(num);
		fclose(save);
	    return 0;
	} else {
		free(num);
		fclose(save);
		return 1;
	}
}


//The one piece of c code you don't have to read or understand. Still, might as well read it, if you have time.
int main(int argc, char* argv[])
{
	if (argc != 5)
	{
		usage(argv);
		return 1;
	}
	int width = atoi(argv[3]);
	int height = atoi(argv[4]);
	char* P3end = "/colorpaletteP3.ppm";
	char* P6end = "/colorpaletteP6.ppm";
	char buffer[strlen(argv[2]) + strlen(P3end)+1];
	sprintf(buffer, "%s%s", argv[2], P3end);
	int failed = P3colorpalette(argv[1], width, height, buffer);
	if (failed)
	{
		printf("Error in making P3colorpalette");
		return 1;
	}
	sprintf(buffer, "%s%s", argv[2], P6end);
	failed = P6colorpalette(argv[1], width, height, buffer);
	if (failed)
	{
		printf("Error in making P6colorpalette");
		return 1;
	}
	printf("P3colorpalette and P6colorpalette ran without error, output should be stored in %s%s, %s%s", argv[2], P3end, argv[2], P6end);
	return 0;
}




