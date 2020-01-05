/*********************
**  Color Map generator
** Skeleton by Justin Yokota
**********************/

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <math.h>
#include <string.h>
#include "ColorMapInput.h"


/**************
**This function reads in a file name colorfile.
**It then uses the information in colorfile to create a color array, with each color represented by an int[3].
***************/
uint8_t** FileToColorMap(char* colorfile, int* colorcount)
{
	FILE* ptr = fopen(colorfile,"r");
	if (ptr == NULL) {
		printf("%s\n","no such file" );
		colorcount = NULL;
		fclose(ptr);
		return NULL;
	} 
	fscanf(ptr,"%d ",colorcount);
	int line = *colorcount;
    int isright = 1;
    if (line <= 0) {
    	colorcount = NULL;
		fclose(ptr);
    	return NULL;
    }
    int** res;
	res = (int**)malloc(sizeof(int*)*line); 
	if(res == NULL) {
		colorcount = NULL;
		fclose(ptr);
		return NULL;
	}
	int sit = 0;
    for (int i = 0; i < line; i++) {
    	res[i] =(int*)malloc(sizeof(int)*3);
        sit = fscanf(ptr,"%d %d %d ",&res[i][0],&res[i][1],&res[i][2]);
    	for (int j = 0; j < 3; j++) {
            if(res[i][j] < 0 || res[i][j] > 255 || sit!= 3) {
            	isright = 0;
            }
    	} 
    }
	if (isright == 0){
	    for(int i = 0; i < line;i++){
			free(res[i]);
		}
        free(res);
		fclose(ptr);
    	return NULL;
	}
    fclose(ptr);
	uint8_t** val =(uint8_t**) malloc(sizeof(uint8_t*)*line);
	for(int i = 0; i < line;i++){
		val[i] = (uint8_t*) malloc(sizeof(uint8_t)*3);
		for (int j=0;j < 3;j++){
          val[i][j] = res[i][j];
		}
	}

	for(int i = 0; i < line;i++){
			free(res[i]);
	}
    free(res);

    return val;
}


