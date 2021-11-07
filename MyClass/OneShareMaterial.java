package MyClass;
import java.util.ArrayList;
public class OneShareMaterial{
  private String name;
  private int Balance;
  public void OneShareMaterial(){}
  public void OneShareMaterial(String n)
  {
    name=n;
    Balance=0;
  }
  synchronized public void put(int x)
  {
    Balance+=x;
  }
  synchronized public int get(int y)
  {
    int temp;
    if(y>=Balance)
    {
      Balance-=y;
      return y;
    }
    else{
      temp = Balance;
      Balance=0;
      return temp; 
    }
  }


}