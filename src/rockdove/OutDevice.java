package rockdove;

public class OutDevice {
    public static void printString(String msg){
        if (msg.length() > 0) {
            System.out.println(msg);
            printLineInfo();
        }
    }

    public static void printLineInfo(){
        System.out.print(" [");
        System.out.print(intToString(_line));
        System.out.print("] >> ");
    }

    public static String intToString(int i){   return intToString(i, 3); }
    public static String intToString(int i, int col){
        int size = (int) Math.log10(i) + 1;

        if (size > col)
            return makeLine('9', col);

        if (size == col)
            return Integer.toString(i);

        return makeLine(' ', col - size) + Integer.toString(i);
    }

    public static String makeLine(char a, int col){
        String s = new String(new char[col]);
        return s.replace('\0', a);
    }

    public static void line() { _line += 1;}

    private static int _line = 1;
}
