package flik;
import org.junit.Test;
import static org.junit.Assert.*;


public class filktest {
    @Test
    public void test1 (){
        int a = 128;
        int b = 128;
        assertTrue(Flik.isSameNumber(a,b));
    }
}
