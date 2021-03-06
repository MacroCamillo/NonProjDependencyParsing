import java.util.ArrayList;

/**
 * Created by camillom on 19/09/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class TestParse {

    public static void main(String[] args){

        if (args.length!=1) {
            System.out.println(args.length);
            System.out.println("Usage: TestRead <filename>");
            return;
        }

        String file_path = args[0];

        if (file_path.substring(file_path.length()-7 ,file_path.length()).equals(".conll")) {
            System.out.println("File must be a .conll file");
            return;
        }

        ArrayList<DependencyTree> corpus = CorpusReader.read(file_path);
        NonProjectiveParsingSystem parser;

        int countBetter = 0, countWorse = 0;
        int countSwapped = 0;
        int totalOpNivre = 0, totalOpTwoStep = 0;
        int countWords = 0;
        int countSwapsNivre = 0, countSwapsTwoStep = 0;

        ArrayList<Integer> TwoStepWins = new ArrayList<>();
        ArrayList<Integer> NivreWins = new ArrayList<>();

        for (DependencyTree sent : corpus) {
            System.out.println("Parsing sentence n." + sent.getSent_number());

            System.out.println("Parsing with 2-steps...");
            parser = new TwoStepParsingSystem(sent);
            parser.execute();
            System.out.println("... done\n");

            int twoStepOp = parser.getN_op();
            if (parser.n_swap > 0)
                totalOpTwoStep += twoStepOp;
            countSwapsTwoStep+=parser.n_swap;
            sent.resetAdded();

            System.out.println("Parsing with original Nivre algorithm...");
            parser = new NivreParsingSystem(sent);
            parser.execute();
            System.out.println("... done");

            int nivreOp = parser.getN_op();
            if (parser.n_swap > 0) {
                totalOpNivre += nivreOp;
                countSwapped++;
            }
            countSwapsNivre+=parser.n_swap;
            System.out.println("\n-----------------------------------------");


            //Compute statistics over whole sentences
            if (twoStepOp < nivreOp) {
                countBetter++;
  //              TwoStepWins.add(sent.getSent_number());
            }

            if (twoStepOp > nivreOp) {
                countWorse++;
//              NivreWins.add(sent.getSent_number());
            }

            countWords += sent.getNodes().size();
        }

        System.out.println("###################################################\n");
        System.out.println("Total number of sentences: " + corpus.size());
        System.out.println("Non-projective ones: " + countSwapped + "\n");

        System.out.println("TwoStep better than Nivre: " + countBetter + " times");
//        for (int i : TwoStepWins)
//            System.out.print(i + " ");

        System.out.println("TwoStep worse than Nivre: " + countWorse + " times");

        System.out.println("\nSwaps using TwoStep: "+ countSwapsTwoStep);
        System.out.println("Swaps using Nivre: "+ countSwapsNivre);

        System.out.println("\n\nTotal word count: " + countWords);
        System.out.println("Total operations in non-projective sentences with TwoStep: " + totalOpTwoStep);
        System.out.println("Total operations in non-projective sentences with Nivre: " + totalOpNivre);

    }
}
