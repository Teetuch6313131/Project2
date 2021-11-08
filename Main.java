import java.util.*;
import java.io.File;
import java.util.ArrayList;

class Main {
  public static void main(String[] args) {
     int check = 1;
     int dayMax, matPerDay;
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
              for(int i = 0; i < matList.size(); i++){
                System.out.printf(" %4d %s",arrT.get(i), matList.get(i).getName());
                if(i==(matList.size())-1){System.out.printf("\n");}
                else{System.out.printf(",");}
              }
            }
            //End of get Factory
            scan.close();
            check = 0;
          }
            
          catch(Exception e) {
            System.out.println(e);
          }
     }

    System.out.printf("\nThread %-7s >>",Thread.currentThread().getName());
    System.out.printf(" Enter amount of material per days = ");
    matPerDay = input.nextInt();
    System.out.printf("\nThread %-7s >>",Thread.currentThread().getName());
    System.out.printf(" Enter number of days = ");
    dayMax = input.nextInt();
    for(int day = 1; day <= dayMax; day++){
      System.out.printf("\n\nThread %-7s >> Day %3d\n",Thread.currentThread().getName(),day);
      for(int i = 0; i < matList.size(); i++){
        matList.get(i).put(matPerDay);
        System.out.printf("Thread %-7s >> Put %5d %s   balance = %5d %s\n",Thread.currentThread().getName(),matPerDay,matList.get(i).getName(),matList.get(i).getBalance(),matList.get(i).getName());
      }
      for(int i = 0; i < factList.size(); i++){
        factList.get(i).updateMatList(matList);
      }
      for(int i = 0; i < factList.size(); i++){
        factList.get(i).run();
      }
    }
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
  public int getBalance(){return Balance;}
}

class Factory extends Thread{
  private int id, lotSize;
  private String product;
  private ArrayList<Integer> material = new ArrayList<Integer>();
  private ArrayList<OneShareMaterial> materialList = new ArrayList<OneShareMaterial>();
  private boolean on;

  public Factory(int x, String s, int y, ArrayList<Integer> arr){
    id = x;
    product = s;
    this.setName(s);
    lotSize = y;
    material = arr;
  }

  public void updateMatList(ArrayList<OneShareMaterial> x){materialList = x;}

  private void getMat(){
    for(int i = 0; i < materialList.size(); i++){
      int mat = materialList.get(i).get(material.get(i));
      System.out.printf("Thread %-7s >> Get %5d %s   balance = %5d %s\n",this.getName(),mat,materialList.get(i).getName(),materialList.get(i).getBalance(),materialList.get(i).getName());
    }
  }

  public void run(){
    on = true;
    while(on){
      getMat();
      on = false;
    }
  }
}