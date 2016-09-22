import java.util.ArrayList;

/**
 * Created by camillom on 21/09/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class TwoStepsProjectiveParser extends NivreProjectiveParser {

    public TwoStepsProjectiveParser(DependencyTree sent) {
        super(sent);
    }

    public DependencyTree execute() {

        sent_length = gold.getNodes().entrySet().size();
        DependencyTree builded = new DependencyTree(gold.getSent_number());

        ArrayList<Integer> sentence = new ArrayList<>();
        for (int i = 0; i < sent_length; i++)
            sentence.add(i);

        Node head_node,tail_node;
        int top_index = 1;
        n_shift = 2; n_op = 2;

        boolean first_swap = true, first_step = true;
        int swap_point = 0;
        while (sentence.size() > 1) {
            switch (predictAction(sentence, top_index)) {
                case LEFT_ARC:
                    head_node = builded.addNode(sentence.get(top_index));
                    tail_node = builded.addNode(sentence.get(top_index - 1));

                    System.out.println("LEFT_ARC " + tail_node.getId() + " <- " + head_node.getId());

                    head_node.addLeftSon(tail_node);
                    sentence.remove(top_index - 1);
                    top_index--;
                    break;

                case RIGHT_ARC:
                    head_node = builded.addNode(sentence.get(top_index - 1));
                    tail_node = builded.addNode(sentence.get(top_index));

                    System.out.println("RIGHT_ARC " + head_node.getId() + " -> " + tail_node.getId());

                    head_node.addRightSon(tail_node);
                    sentence.remove(top_index);
                    top_index--;
                    break;

                case SWAP:

                    if (first_step) {
                        if (first_swap) {
                            System.out.println("Suppressing SWAP of " + sentence.get(top_index-1));
                            swap_point = top_index;
                            first_swap = false;
                        }
                        top_index++;
                        if (top_index < sentence.size()) {          //nel caso l'ultimo confronto sia caso swap
                            n_shift++;
                            System.out.println("SHIFT (instead of SWAP)");
                        }
                        break;
                    } else {
                        int swap_id = sentence.get(top_index - 1);
                        sentence.set(top_index - 1, sentence.get(top_index));
                        sentence.set(top_index, swap_id);

                        System.out.println("SWAP");
                        top_index--;
                        n_swap++;
                        break;
                    }

                case SHIFT:
                    top_index++;
                    if (top_index < sentence.size()) {
                        System.out.println("SHIFT");
                        n_shift++;
                    }
                    break;
            } //end switch
            n_op++;
            if (first_step && top_index == sentence.size()) {
                System.out.println("Retracting TOP");
                top_index = swap_point;                 //TODO: va contata come operazione/n operazioni? Per ora conta uno
                first_step = false;
            }
        }
        printExecutionStats();
        return builded;
    }
}