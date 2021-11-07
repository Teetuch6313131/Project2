import java.util.*;
import java.io.File;
import java.util.ArrayList;

class Main {
  public static void main(String[] args) {
     int check = 1;
     ArrayList<OneShareMaterial> matList = new ArrayList<OneShareMaterial>();
     ArrayList<Factory> factList = new ArrayList<Factory>();
     Scanner input = new Scanner(System.in);  
        
     while(check == 1) {
          System.out.println("Enter product specification file =  ");
          String fileName = input.nextLine();
            
          try {
            Scanner scan  = new Scanner(new File(fileName));
            System.out.println("");
            //Get material name;
            String line = scan.nextLine();
            String[] buf = line.split(",");
            for(int i = 0; i < buf.length; i++){
              OneShareMaterial temp = new OneShareMaterial(buf[i].trim());
              matList.add(temp);
            }
            //////////////////////////////
            //get factory
            while(scan.hasNext()){
              line = scan.nextLine();
              buf = line.split(",");
              ArrayList<Integer> arrT = new ArrayList<Integer>();
              int id = Integer.parseInt(buf[0].trim());
              int lotSize = Integer.parseInt(buf[2].trim());
              String product = buf[1].trim();
              for(int i = 3; i < buf.length; i++){
                arrT.add(Integer.parseInt(buf[i].trim()));
              }
              Factory temp = new Factory(id, product, lotSize, arrT);
              factList.add(temp);
              System.out.printf("Thread %-7s >> %-7s factory %5d units per lot   materials per lot =",Thread.currentThread().getName(), product, lotSize);
              for(int i = 0; i < matList.size; i++){
                System.out.printf(" %4d %s",arrT.get(i), matList.get(i).getName()+"s");
                if(i==(matList.size)-1){System.out.printf("\n");}
                else{System.out.printf(",");}
              }
            }
            ///////////////////////////////materials per lot = %4d buttons,%5d zippers
            scan.close();
            check = 0;
          }
            
          catch(Exception e) {
            System.out.println(e);
          }
     }

    System.out.printf("Thread %-7s >>",Thread.currentThread().getName());
    //System.out.printf("Enter amount of material per days = ");
    
    //System.out.printf("Enter number of days = ");
  }
}

class OneShareMaterial{
  private String name;
  private int Balance;
  public OneShareMaterial(String n)
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

  public String getName(){return name;}
}

class Factory extends Thread{
  private int id, lotSize;
  private String product;
  private ArrayList<Integer> material = new ArrayList<Integer>();
  private boolean on = true;

  public Factory(int x, String s, int y, ArrayList<Integer> arr){
    id = x;
    product = s;
    lotSize = y;
    material = arr;
  }

  public void run(){
    while(on){
      
      
    }
  }
}