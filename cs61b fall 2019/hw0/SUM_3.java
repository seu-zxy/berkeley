public class SUM_3{
	public static boolean main(int[] a){
	  int i=0;
      int j=0;
      int k=0;
      int d=0;
	for (i=0;i<a.length;i++){
	  for (j=0;j<a.length;j++){
	    for (k=0;k<a.length;k++)
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