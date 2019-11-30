package CatchMind;

public class Test {
    public static void main(String[] args) {
        String a = "개", b = "개";
        System.out.println(a + (a.equals(b) ? " == " : " != ") + b);
    }
}
