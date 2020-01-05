public class LeapYear{
   public static void main(String[] args){
     int year=2000;
     if (Isleapyear(year))
       System.out.println(year+" is a leap year");
     else
       System.out.println(year+" is not a leap year");
   }

   public static boolean Isleapyear(int year){
   if (year%400==0) 
     return true;
   else
   if (year%4==0&&year%100!=0)
     return true;
     else return false;
   }
 }