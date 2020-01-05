package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int [] C=new int[A.length+B.length];
        int i=0;
        for(i=0;i<A.length+B.length;i++){
            if(i<A.length){
                C[i]=A[i];
            }
            else C[i]=B[i-A.length];
        }
        return C;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        if(A==null||start+len>A.length)
            return null;
        else{
            int[] temp=new int[A.length-len];
            for(int i=0;i<start;i++)
            temp[i]=A[i];
            for(int i=start+len;i<A.length;i++)
                temp[i-len]=A[i];
            return temp;
        }
    }


    static int[] getfrom(int []A,int init,int length){
        int i=0;
        int[] temp=new int[length];
        for(i=init;i<init+length;i++){
            temp[i-init]=A[i];
        }
        return temp;
    }
    static int[][] naturalRuns(int[] A) {
        if(A.length==0)
            return null;
        else{
            int i=0;
            int n=0;
            int re=0;
            int w=0;
            for(i=0;i<A.length-1;i++){
                if (A[i]>A[i+1])
                    n++;
            }
            w=n;
            int[][] temp=new int[n+1][];
            n=0;
            for (i=0;i<A.length-1;i++){
                if(A[i]>A[i+1]){
                temp[n]=getfrom(A,re,i-re+1);
                re=i+1;
                n=n+1;
                }
            }
            if(w>0){
                temp[w]=getfrom(A,re,A.length-re);
            }
            return temp;
        }

    }
    }