/** Use while to determine the max number in a array.*/
  public class Max_while{
      public static int main (int[] a){
          int i=0;
          int temp=a[0];
	      while(i<a.length){
	        if (temp<=a[i])
		    temp=a[i];
	        i=i+1;
	    }	   
          return temp;
      }
  
      
  }
