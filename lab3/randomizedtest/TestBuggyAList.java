package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> test1 = new AListNoResizing<>();
        BuggyAList<Integer> test2 = new BuggyAList<>();
        for (int i = 0; i < 3; i++){
            test1.addLast(i+4);
            test2.addLast(i+4);
        }
        assertEquals(test1.size(),test2.size());
        for (int i = 0; i < 3; i++){
            assertEquals(test1.removeLast(),test2.removeLast());
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size1 = correct.size();
                int size2 = broken.size();
            } else if (operationNumber == 2) {
                // getLast
                if (correct.size() == 0 || broken.size() == 0){
                    continue;
                }
                int last1 = correct.getLast();
                int last2 = broken.getLast();
                assertEquals(last1,last2);
            } else if (operationNumber == 3) {
                //removeLast
                if (correct.size() == 0 || broken.size() == 0){
                    continue;
                }
                int last1 = correct.removeLast();
                int last2 = broken.removeLast();
                assertEquals(last1,last2);
            }
        }
    }
}
