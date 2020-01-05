public class SUM_distinct{
	public static boolean main(int[] a){
	int i=0;
	int d=0;
	int j,k;
	for (i=0;i<a.length;i++){
	  for (j=i+1;j<a.length;j++){
	    for (k=i+2;k<a.length;k++)
	    {
	     int s3=0;
	     s3=a[i]+a[j]+a[k];
	     if (s3==0)
	     d=1;
	    }
	  }
	}
	if (d==1)
	return true;
	else return false;
	}
}