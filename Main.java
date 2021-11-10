
import java.util.*;
import java.util.concurrent.*;
import java.io.File;
import java.util.ArrayList;

class FactorySimulation {

    public static void main(String[] args) {
        int check = 1;
        int dayMax, matPerDay;
        ArrayList<OneShareMaterial> matList = new ArrayList<OneShareMaterial>();
        ArrayList<Factory> factList = new ArrayList<Factory>();
        Scanner input = new Scanner(System.in);

        while (check == 1) {
            System.out.printf("\nThread %-7s >> Enter product specification file =  ", Thread.currentThread().getName());
            System.out.println("");
            String fileName = input.nextLine();

            try {
                Scanner scan = new Scanner(new File(fileName));
                System.out.println("");
                //Get material name;
                String line = scan.nextLine();
                String[] buf = line.split(",");
                for (int i = 0; i < buf.length; i++) {
                    OneShareMaterial temp = new OneShareMaterial(buf[i].trim());
                    matList.add(temp);
                }
                //////////////////////////////
                //get factory
                while (scan.hasNext()) {
                    line = scan.nextLine();
                    buf = line.split(",");
                    ArrayList<Integer> arrT = new ArrayList<Integer>();
                    int id = Integer.parseInt(buf[0].trim());
                    int lotSize = Integer.parseInt(buf[2].trim());
                    String product = buf[1].trim();
                    for (int i = 3; i < buf.length; i++) {
                        arrT.add(Integer.parseInt(buf[i].trim()));
                    }
                    Factory temp = new Factory(id, product, lotSize, arrT);
                    factList.add(temp);
                    System.out.printf("Thread %-7s >> %-7s factory %5d units per lot   materials per lot =", Thread.currentThread().getName(), product, lotSize);
                    for (int i = 0; i < matList.size(); i++) {
                        System.out.printf(" %4d %s", arrT.get(i), matList.get(i).getName());
                        if (i == (matList.size()) - 1) {
                            System.out.printf("\n");
                        } else {
                            System.out.printf(",");
                        }
                    }
                }
                //End of get Factory
                scan.close();
                check = 0;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("");
        System.out.printf("Thread %-7s >> Enter amount of material per days = ", Thread.currentThread().getName());
        System.out.println("");
        matPerDay = input.nextInt();
        System.out.printf("\nThread %-7s >> Enter number of days = ", Thread.currentThread().getName());
        System.out.println("");
        dayMax = input.nextInt();


        for (int day = 1; day <= dayMax; day++) {
            System.out.printf("\n\nThread %-7s >> Day %3d\n", Thread.currentThread().getName(), day);
            for (int i = 0; i < matList.size(); i++) {
                matList.get(i).put(matPerDay);
                System.out.printf("Thread %-7s >> Put %5d %s   balance = %5d %s\n", Thread.currentThread().getName(), matPerDay, matList.get(i).getName(), matList.get(i).getBalance(), matList.get(i).getName());
            }
            System.out.println("");
            for (int i = 0; i < factList.size(); i++) {
                factList.get(i).updateMatList(matList);
            }
            ///////////////////////////////////////////////
            if (day > 1) {
                for (int i = 0; i < factList.size(); i++) {
                    int id = factList.get(0).getID();
                    int lotSize = factList.get(0).getlotSize();
                    int complete = factList.get(0).getComplete();
                    String product = factList.get(0).getProduct();
                    ArrayList<Integer> material = factList.get(0).getmaterial();
                    ArrayList<OneShareMaterial> materialList = factList.get(0).getmaterialList();
                    ArrayList<Integer> matInstock = factList.get(0).getmatInstock();
                    Factory temp = new Factory(id, lotSize, complete, product, material, materialList, matInstock);
                    factList.remove(0);
                    factList.add(temp);
                }
            }

            for (int i = factList.size() - 1; i >= 0; i--) {
                factList.get(i).start();
            }
            for (int i = factList.size() - 1; i >= 0; i--) {
                try {
                    factList.get(i).join();
                } catch (InterruptedException e) {
                }
            }

        }
        /////////////////////////////////////////////////
        //print in the end
        System.out.printf("\nThread %-7s >> Summary\n", Thread.currentThread().getName());
        ArrayList<Integer> index = new ArrayList<Integer>();
        ArrayList<Integer> Sum = new ArrayList<Integer>();
        for (int i = 0; i < factList.size(); i++) {
            index.add(i);
            Sum.add(factList.get(i).getComplete());
        }
        for (int i = 0; i < Sum.size() - 1; i++) {
            for (int j = 0; j > Sum.size() - 1 - i; j++) {
                if (Sum.get(j) < Sum.get(j + 1)) {
                    int temp = Sum.get(j+1);
                    Sum.set(j+1, Sum.get(j));
                    Sum.set(j, temp);
                    int temp2 = index.get(j+1);
                    index.set(j+1 , index.get(j));
                    index.set(j, temp2);
                }
            }
        }
        for (int i = index.size()-1; i >= 0; i--) {
            System.out.printf("Thread %-7s >> Total %-8s Lots = %3d\n", Thread.currentThread().getName(), factList.get(index.get(i)).getProduct(), Sum.get(i));
        }
    }
}

class OneShareMaterial {

    private String name;
    private int Balance;

    public OneShareMaterial(String n) {
        name = n;
        Balance = 0;
    }

    synchronized public void put(int x) {
        Balance += x;
    }

    synchronized public int get(int y) {
        int temp;
        if (y <= Balance) {
            Balance -= y;
            return y;
        } else {

            temp = Balance;
            Balance = 0;
            return temp;
        }
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return Balance;
    }
}

class Factory extends Thread {

    private int id, lotSize, complete;
    private String product;
    private ArrayList<Integer> material = new ArrayList<Integer>();
    private ArrayList<OneShareMaterial> materialList = new ArrayList<OneShareMaterial>();
    private ArrayList<Integer> matInstock = new ArrayList<Integer>();

    public Factory(int a, int b, int c, String d, ArrayList<Integer> e, ArrayList<OneShareMaterial> f, ArrayList<Integer> g) {
        id = a;
        lotSize = b;
        complete = c;
        product = d;
        material = e;
        materialList = f;
        matInstock = g;
        this.setName(d);
    }

    public Factory(int x, String s, int y, ArrayList<Integer> arr) {
        id = x;
        product = s;
        this.setName(s);
        lotSize = y;
        material = arr;
        for (int i = 0; i < arr.size(); i++) {
            matInstock.add(0);
        }
        complete = 0;
    }

    public int getID() {
        return id;
    }

    public int getlotSize() {
        return lotSize;
    }

    public int getComplete() {
        return complete;
    }

    public String getProduct() {
        return product;
    }

    public ArrayList<Integer> getmaterial() {
        return material;
    }

    public ArrayList<OneShareMaterial> getmaterialList() {
        return materialList;
    }

    public ArrayList<Integer> getmatInstock() {
        return matInstock;
    }

    public void updateMatList(ArrayList<OneShareMaterial> x) {
        materialList = x;
    }

    synchronized private void getMat() {
        for (int i = 0; i < materialList.size(); i++) {
            int mat = materialList.get(i).get(((material.get(i)) * lotSize) - matInstock.get(i));
            if (mat > 0) {
                System.out.printf("Thread %-7s >> Get %5d %s   balance = %5d %s\n", this.getName(), mat, materialList.get(i).getName(), materialList.get(i).getBalance(), materialList.get(i).getName());
                matInstock.set(i, matInstock.get(i) + mat);
            }
        }

        for (int i = 0; i < materialList.size(); i++) {
            if ((material.get(i) * lotSize) != matInstock.get(i)) {
                System.out.printf("Thread %-7s >> -----Fail\n", this.getName());
                return;
            }
        }
        complete += 1;
        System.out.printf("Thread %-7s >> +++++Complete Lot %2d\n", this.getName(), complete);

        for (int i = 0; i < matInstock.size(); i++) {
            matInstock.set(i, 0);
        }
        return;
    }

    public void run() {
        Random rnd = new Random();
        int r = rnd.nextInt(1000);
        try{this.sleep(r);}catch(Exception e){}
        getMat();
    }
}
