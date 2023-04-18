import java.util.concurrent.atomic.AtomicIntegerArray;

public class SavagesFair {
















    public static void main(String[] args) {
        //check that inputs are 2
        if (args.length != 2) {
            System.out.println("The arguments provided are not 2!");
            return;
        }

        //parse input arguments
        int Savages = Integer.parseInt(args[0]);
        int PotLength = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (Savages <= 0 || PotLength <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        AtomicIntegerArray Pot = SavagesBasic.initializePot(PotLength);

        AtomicIntegerArray portionsEaten = SavagesBasic.eatCounter(Savages);

        SavagesBasic.Savage[] savages = new SavagesBasic.Savage[Savages];
        for (int i = 0; i < Savages; i++) {
            savages[i] = new SavagesBasic.Savage(i, portionsEaten, Pot);
            savages[i].start();
        }
        SavagesBasic.Cooker cooker = new SavagesBasic.Cooker(Pot, portionsEaten);
        cooker.start();

        // Wait for threads completion
        for (int i = 0; i < Savages; i++) {
            try {
                savages[i].join();
                cooker.join();
            } catch (InterruptedException e) {
            }
        }
    }
}
