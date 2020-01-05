public class Max_for{
	public static int main(int[] a){
	 int temp=a[0];
      int i=0;
      for (i=0;i<a.length;i++){
         if (temp<=a[i])
           temp=a[i];
      }
      return temp;
	}
   
}

	