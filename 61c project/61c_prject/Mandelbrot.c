/*********************
**  Mandelbrot fractal
** clang -Xpreprocessor -fopenmp -lomp -o Mandelbrot Mandelbrot.c
** by Dan Garcia <ddgarcia@cs.berkeley.edu>
** Modified for this class by Justin Yokota and Chenyu Shi
**********************/

#include <stdio.h>
#include <stdlib.h>
#include "ComplexNumber.h"
#include "Mandelbrot.h"

/*
This function returns the number of iterations before the initial point >= the threshold.
If the threshold is not exceeded after maxiters, the function returns 0.
*/
u_int64_t MandelbrotIterations(u_int64_t maxiters, ComplexNumber * point, double threshold)
{
  u_int64_t num=0;
  u_int64_t i=0;
  u_int64_t k=0;
  double re1=0;
  double im1=0;
  ComplexNumber* temp;
  ComplexNumber* temp_1;
  ComplexNumber* temp_2;
  for(i=0;i<maxiters;i++){
         temp=newComplexNumber(re1,im1);
         temp_1=ComplexProduct(temp,temp);
         temp_2=ComplexSum(temp_1,point);
  	if(ComplexAbs(temp_2)>=threshold){
  		num=i+1;
                k=1;
         }
        re1=Re(temp_2);
        im1=Im(temp_2);
  	freeComplexNumber(temp);
        freeComplexNumber(temp_1);
        freeComplexNumber(temp_2);
        if(k==1) break;
}
  	return num;
   
}

/*
This function calculates the Mandelbrot plot and stores the result in output.
The number of pixels in the image is resolution * 2 + 1 in one row/column. It's a square image.
Scale is the the distance between center and the top pixel in one dimension.
*/
void Mandelbrot(double threshold, u_int64_t max_iterations, ComplexNumber* center, double scale, u_int64_t resolution, u_int64_t * output){
    u_int64_t i=0;
    u_int64_t num_grid=(2*resolution+1)*(2*resolution+1);
    u_int64_t num_row;
    u_int64_t num_line;
    double real_c,imagine_c;
    for(i=0;i<num_grid;i++)
    {
       num_row=i/(2*resolution+1);
       num_line=i%(2*resolution+1);
       imagine_c=Im(center)+scale-num_row*scale/resolution;
       real_c=Re(center)-scale+num_line*scale/resolution;
       ComplexNumber *temp=newComplexNumber(real_c,imagine_c);
       output[i]=MandelbrotIterations(max_iterations,temp,threshold);
       freeComplexNumber(temp);
    }
}
