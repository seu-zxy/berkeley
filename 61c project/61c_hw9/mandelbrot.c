// Include SSE intrinsics
#if defined(_MSC_VER)
#include <intrin.h>
#elif defined(__GNUC__) && (defined(__x86_64__) || defined(__i386__))
#include <immintrin.h>
#include <x86intrin.h>
#endif

// Include OpenMP
#include <omp.h>

#include "mandelbrot.h"
#include "parameters.h"

uint32_t iterations(struct parameters params, double complex point) {
    double complex z = 0;
    for (int i = 1; i <= params.maxiters; i++) {
        z = z * z + point;
        if (creal(z) * creal(z) + cimag(z) * cimag(z) >= params.threshold * params.threshold) {
            return i;
        }
    }
    return 0;
}

uint32_t iterations(struct parameters params, __m256 real, __m256 img) {
    __m256 first_real = _mm256_set_pd (0,0,0,0);
    __m256 first_img = _mm256_set_pd (0,0,0,0);
    for (int i = 1; i <= params.maxiters; i++) {
        __m256d temp_real = __mm256_sub_pd(_mm256_mul_pd (first_real,first_real),_mm256_mul_pd (first_img,first_img));
        __m256d temp_img = __m256_mul_pd(first_real,first_img);
        first_real = __mm256_add_pd(first_real,real);
        first_img = __m256_add_pd(first_img,img);
        __m256d res = __m256_add_pd(__m256_mul_pd(first_real,first_real),__m256_mul_pd(first_img,first_img));
        double ccomp = params.threshold * params.threshold;
        __m256d comp4 = _mm256_set_pd (ccomp,ccomp,ccomp,ccomp);
        __m256d comp_res = __mm256_set_pd(1,1,1,1);
        //compare if <   get 1, then &until the end;
        comp_res = _mm256_and_pd(comp_res, _mm256_cmp_pd(res, comp4, 1));
    }
    Uint32_t val = 0;
    double res[4];
    _mm256_store_pd (res, comp_res);
    val = res[0] + res[1] + res[2] + res[3];
    return val;
}

void mandelbrot(struct parameters params, double scale, int32_t *num_pixels_in_set) {
    int32_t num_zero_pixels = 0;
    for (int i = params.resolution; i >= -params.resolution; i--) {
        int temp_res = params.resolution;
        double else1 = scale / temp_res;
        double para_real = (double)creal(params.center);
        double para_imag = (double)cimag(params.center);
        __m256d pararr4 = _mm256_set_pd (para_real,para_real,para_real,para_real);
        __m256d paraimg4 = _mm256_set_pd (para_imag,para_imag,para_imag,para_imag);
        __m256d res4 = _mm256_set_pd (temp_res,temp_res,temp_res,temp_res);
        __m256d else4 = _mm256_set_pd (else1,else1,else1,else1);
        for (int j = 0; j < (2 *params.resolution + 1) / 4 * 4 ; j = j + 4) {
            double jr = (double) (j - temp_res);
            __m256d  real = _mm256_set_pd (jr,jr + 1,jr + 2,jr + 3);
            real = _mm256_mul_pd (real, else4);
            real = _mm256_add_pd (real,pararr4);
            __m256d img = _mm256_set_pd (i,i,i,i);
            img = _mm256_mul_pd(img,else4);
            img = __mm256_add_pd(img,paraimg4)
            num_zero_pixels += iterations(params,real,img);
        }
        for (int j = (2 *params.resolution + 1) / 4 * 4; j < 2 * params.resolution + 1; j++) {
            int k = j -params.resolution;
             double complex point = (params.center +
                    k * scale / params.resolution +
                    i * scale / params.resolution * I);
            if (iterations(params, point) == 0) {
                num_zero_pixels++;
            }
        }
    }
    *num_pixels_in_set = num_zero_pixels;
}