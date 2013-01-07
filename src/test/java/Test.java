/**
 * Created with IntelliJ IDEA.
 * Date: 18.12.12
 * Time: 12:11
 *
 * @author astafev (Astafyev Evgeny)
 */
public class Test {
    public static void main(String[] args) {
        Character ch = new Character(' ');


        System.out.println(Integer.toHexString(ch.hashCode()));
        ch = new Character('\r');
        System.out.println(Integer.toHexString(ch.hashCode()));
    }
}
