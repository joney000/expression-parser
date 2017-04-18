import java.io.*;
import java.util.*;
import java.lang.*;
import java.math.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/*
 * Author    : [jaswantsinghyadav007@gmail.com]-Jaswant Singh
 * Algorithm : Recursive Inorder Travarsal/ Constructive Implementation
 * Lang      : Java8
 * Date      : 12/Apr/2017
 */

class EquationSimplification {

    private InputStream inputStream ;
    private OutputStream outputStream ;
    private FastReader in ;
    private PrintWriter out ;

    /*
		Overhead [Additional Temporary Strorage] but provides memory reusability for multiple test cases.
	*/

    //Critical Size Limit : 10^5 + 4 : Input Json String Text
    private final int BUFFER = 200005;
    private int    tempints[] = new int[BUFFER];
    private long   templongs[] = new long[BUFFER];
    private double tempdoubles[] = new double[BUFFER];
    private char   tempchars[] = new char[BUFFER];
    private final long mod = 1000000000+7;
    private final int  INF  = Integer.MAX_VALUE / 10;
    private final long INF_L  = Long.MAX_VALUE / 10;

    public EquationSimplification(){}
    public EquationSimplification(boolean stdIO)throws FileNotFoundException{
        if(stdIO){
            inputStream = System.in;
            outputStream = System.out;
        }else{
            inputStream = new FileInputStream("test1.json");
            outputStream = new FileOutputStream("output.txt");
        }
        in = new FastReader(inputStream);
        out = new PrintWriter(outputStream);

    }


    private JSONObject readJson(String fileName)throws IOException{
        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject) obj;
            return  jsonObject;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    String rhs = "";
    long rhs_value = 0L;

    void run()throws IOException{

        JSONObject jsonObject = readJson("test0.json");
        out.write("\n\n Input : "+jsonObject+"\n");
        out.flush();

        // Solution - A

        String equation = printEquation(jsonObject);
        equation = formatEquation(equation.substring(1,equation.length()-1));
        out.write(equation);

        // Solution - B

        transform(jsonObject);
        out.write("\n x = "+rhs  +"");

        // Solution - C
        long calculated_rhs_value = calculate_rhs_value(rhs);
        out.write("\n value of x = "+calculated_rhs_value);

    }//end run

    private String formatEquation(String eq){
        String s[] = eq.split("=");
        rhs = s[1];
        rhs_value = Long.parseLong(rhs);
        return s[0].substring(1,s[0].length()-1) + "="+s[1];
    }

    private char operator(String op){
        if(op.equals("add")){
            return '+';
        }else if(op.equals("subtract")){
            return '-';
        }else if(op.equals("divide")){
            return '/';
        }else if(op.equals("multiply")){
            return '*';
        }else if(op.equals("equal")){
            return '=';
        }
        return '#';
    }
    // provide inv of an operation to move segment lhs to rhs
    private char inverse_operator(String op){
        if(op.equals("add")){
            return '-';
        }else if(op.equals("subtract")){
            return '+';
        }else if(op.equals("divide")){
            return '*';
        }else if(op.equals("multiply")){
            return '/';
        }
        return '#';
    }
    // Recursive Inorder Traversal : Returns the Experession
    private String printEquation(Object jsonObject){

        int type = type(jsonObject);
        String equation = "";
        out.flush();
        if(type==0) {     // 0 = jsonObj => call recursive
            JSONObject recJson = (JSONObject)jsonObject;
            equation = "("+printEquation(recJson.get("lhs")) + operator((String)recJson.get("op")) + printEquation(recJson.get("rhs"))+")";
        }else if(type==1){
            String recJson = (String)jsonObject;             // 1 = string i.e variable
            return recJson;
        }else if(type==2){
            Long recJson = (Long)jsonObject;             // 1 = Long/Integer i.e constant
            return  ""+recJson;
        }
        return equation;
    }

    int type(Object jsonObject){
        int type = -1;
        try{
            Long recJson = (Long) jsonObject;
            type = 2;
        }catch (Exception ex){

        }
        try{
            String recJson = (String)jsonObject;
            type = 1;
        }catch (Exception ex){

        }

        try{
            JSONObject recJson = (JSONObject)jsonObject;
            if(type==-1)type = 0;
        }catch (Exception ex){

        }
        return type;
    }
    // Level order traversal to move blocks to the right side : level by level with the help of inverse_operator function
    private void transform(Object jsonObject){

        int type = type(jsonObject);
        String equation = "";
        if(type==0) {               // 0 = jsonObj => call recursive
            JSONObject recJson = (JSONObject)jsonObject;
            if(hasX(recJson.get("lhs"))){
                if(operator((String)recJson.get("op"))=='='){
                    transform(recJson.get("lhs"));
                }else{
                    rhs = "("+rhs+inverse_operator((String)recJson.get("op")) + printEquation(recJson.get("rhs"))+")";
                    transform(recJson.get("lhs"));
                }
            }else {
                if(operator((String)recJson.get("op"))=='='){
                    transform(recJson.get("rhs"));
                }else{
                    rhs = "("+rhs+inverse_operator((String)recJson.get("op")) + printEquation(recJson.get("lhs"))+")";
                    transform(recJson.get("rhs"));
                }
            }
        }else if(type==1){
            String recJson = (String)jsonObject;                        // 1 = string i.e variable
        }else if(type==2){
            Long recJson = (Long)jsonObject;                              // 2 = Long/Integer i.e constant
        }
    }

    // Check Recursively if x variable is present in one block or not
    private boolean hasX(Object jsonObject){

        int type = type(jsonObject);
        boolean hasX = false;
        if(type==0) {                                           // 0 = jsonObj => call recursive
            JSONObject recJson = (JSONObject)jsonObject;
            hasX |= hasX(recJson.get("lhs")) || hasX(recJson.get("rhs"));
        }else if(type==1){
            String recJson = (String)jsonObject;
            hasX |= recJson.equals("x");
        }
        return hasX;
    }
    /*
     *  @params:
     *      expression: to evalute and represent the rhs of x (after making the transformation)
     *  @returns:
     *      evaluate the expression.
     */
    private long calculate_rhs_value(String expression) {
        LinkedList<Character> operator_stack = new LinkedList<Character>();
        LinkedList<Long> operand_stack = new LinkedList<Long>();

        int n = expression.length();
        long operand = 0L;
        for(int i = 0; i <= n - 1; i++){
            if(expression.charAt(i)=='(')continue;
            else if(isDigit(expression.charAt(i))){    // Check The Digit
                operand = operand * 10 + expression.charAt(i)-'0';  // parsing the operator to push into stack
                if(i < n - 1 && !isDigit(expression.charAt(i+1))){
                    operand_stack.addLast(operand);
                    operand = 0;
                }
            }else if(expression.charAt(i)!=')' && !isDigit(expression.charAt(i))){
                operator_stack.add(expression.charAt(i));
            }else if(expression.charAt(i)==')'){
                Character operator = (Character)operator_stack.removeLast();
                Long op1 = (Long)operand_stack.removeLast();
                Long op2 = (Long)operand_stack.removeLast();
                long new_calculated_value = perform_operataion(operator, op2, op1);
                operand_stack.addLast(new_calculated_value);
            }
        }
        return operand_stack.removeLast();
    }

    private long perform_operataion(char op, long op1, long op2){
        if(op == '+')return op1 + op2;
        else if(op == '-')return op1 - op2;
        else if(op == '/')return op1 / op2;
        else return op1 * op2;
    }
    boolean isDigit(char ch){
        return  ch >= '0' && ch <= '9';
    }
    int hash(String s){
        int base = 31;
        int a = 31;//base = a multiplier
        int mod = 100005;//range [0..100004]
        long val = 0;
        for(int i =  1 ; i<= s.length() ;i++){
            val += base * s.charAt(i-1);
            base = ( a * base ) % 100005;
        }
        return (int)(val % 100005) ;
    }

    long gcd(long a , long b){
        if(b==0)return a;
        return gcd(b , a%b);
    }
    long lcm(long a , long b){
        if(a==0||b==0)return 0;
        return (a*b)/gcd(a,b);
    }
    int i()throws Exception{
        //return Integer.parseInt(br.readLine().trim());
        return in.nextInt();
    }
    int[] is(int n)throws Exception{
        for(int i=1 ; i <= n ;i++)tempints[i] = in.nextInt();
        return tempints;
    }
    long l()throws Exception{
        return in.nextLong();
    }
    long[] ls(int n)throws Exception{
        for(int i=1 ; i <= n ;i++)templongs[i] = in.nextLong();
        return templongs;
    }

    double d()throws Exception{
        return in.nextDouble();
    }
    double[] ds(int n)throws Exception{
        for(int i=1 ; i <= n ;i++)tempdoubles[i] = in.nextDouble();
        return tempdoubles;
    }
    char c()throws Exception{
        return in.nextCharacter();
    }
    char[] cs(int n)throws Exception{
        for(int i=1 ; i <= n ;i++)tempchars[i] = in.nextCharacter();
        return tempchars;
    }
    String s()throws Exception{
        return in.nextLine();
    }
    BigInteger bi()throws Exception{
        return in.nextBigInteger();
    }
    private void closeResources(){
        out.flush();
        out.close();
        return;
    }
    public static void main(String[] args) throws java.lang.Exception{

        EquationSimplification driver = new EquationSimplification(true);
        long start =  System.currentTimeMillis();
        driver.run();
        long end =  System.currentTimeMillis();
        //out.write(" Total Time : "+(end - start)+"\n");
        driver.closeResources();
        return ;

    }

}

class FastReader{

    private boolean finished = false;

    private InputStream stream;
    private byte[] buf = new byte[4*1024];
    private int curChar;
    private int numChars;
    private SpaceCharFilter filter;

    public FastReader(InputStream stream){
        this.stream = stream;
    }

    public int read(){
        if (numChars == -1){
            throw new InputMismatchException ();
        }
        if (curChar >= numChars){
            curChar = 0;
            try{
                numChars = stream.read (buf);
            } catch (IOException e){
                throw new InputMismatchException ();
            }
            if (numChars <= 0){
                return -1;
            }
        }
        return buf[curChar++];
    }

    public int peek(){
        if (numChars == -1){
            return -1;
        }
        if (curChar >= numChars){
            curChar = 0;
            try{
                numChars = stream.read (buf);
            } catch (IOException e){
                return -1;
            }
            if (numChars <= 0){
                return -1;
            }
        }
        return buf[curChar];
    }

    public int nextInt(){
        int c = read ();
        while (isSpaceChar (c))
            c = read ();
        int sgn = 1;
        if (c == '-'){
            sgn = -1;
            c = read ();
        }
        int res = 0;
        do{
            if(c==','){
                c = read();
            }
            if (c < '0' || c > '9'){
                throw new InputMismatchException ();
            }
            res *= 10;
            res += c - '0';
            c = read ();
        } while (!isSpaceChar (c));
        return res * sgn;
    }

    public long nextLong(){
        int c = read ();
        while (isSpaceChar (c))
            c = read ();
        int sgn = 1;
        if (c == '-'){
            sgn = -1;
            c = read ();
        }
        long res = 0;
        do{
            if (c < '0' || c > '9'){
                throw new InputMismatchException ();
            }
            res *= 10;
            res += c - '0';
            c = read ();
        } while (!isSpaceChar (c));
        return res * sgn;
    }

    public String nextString(){
        int c = read ();
        while (isSpaceChar (c))
            c = read ();
        StringBuilder res = new StringBuilder ();
        do{
            res.appendCodePoint (c);
            c = read ();
        } while (!isSpaceChar (c));
        return res.toString ();
    }

    public boolean isSpaceChar(int c){
        if (filter != null){
            return filter.isSpaceChar (c);
        }
        return isWhitespace (c);
    }

    public static boolean isWhitespace(int c){
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
    }

    private String readLine0(){
        StringBuilder buf = new StringBuilder ();
        int c = read ();
        while (c != '\n' && c != -1){
            if (c != '\r'){
                buf.appendCodePoint (c);
            }
            c = read ();
        }
        return buf.toString ();
    }

    public String nextLine(){
        String s = readLine0 ();
        while (s.trim ().length () == 0)
            s = readLine0 ();
        return s;
    }

    public String nextLine(boolean ignoreEmptyLines){
        if (ignoreEmptyLines){
            return nextLine ();
        }else{
            return readLine0 ();
        }
    }

    public BigInteger nextBigInteger(){
        try{
            return new BigInteger (nextString ());
        } catch (NumberFormatException e){
            throw new InputMismatchException ();
        }
    }

    public char nextCharacter(){
        int c = read ();
        while (isSpaceChar (c))
            c = read ();
        return (char) c;
    }

    public double nextDouble(){
        int c = read ();
        while (isSpaceChar (c))
            c = read ();
        int sgn = 1;
        if (c == '-'){
            sgn = -1;
            c = read ();
        }
        double res = 0;
        while (!isSpaceChar (c) && c != '.'){
            if (c == 'e' || c == 'E'){
                return res * Math.pow (10, nextInt ());
            }
            if (c < '0' || c > '9'){
                throw new InputMismatchException ();
            }
            res *= 10;
            res += c - '0';
            c = read ();
        }
        if (c == '.'){
            c = read ();
            double m = 1;
            while (!isSpaceChar (c)){
                if (c == 'e' || c == 'E'){
                    return res * Math.pow (10, nextInt ());
                }
                if (c < '0' || c > '9'){
                    throw new InputMismatchException ();
                }
                m /= 10;
                res += (c - '0') * m;
                c = read ();
            }
        }
        return res * sgn;
    }

    public boolean isExhausted(){
        int value;
        while (isSpaceChar (value = peek ()) && value != -1)
            read ();
        return value == -1;
    }

    public String next(){
        return nextString ();
    }

    public SpaceCharFilter getFilter(){
        return filter;
    }

    public void setFilter(SpaceCharFilter filter){
        this.filter = filter;
    }

    public interface SpaceCharFilter{
        public boolean isSpaceChar(int ch);
    }
}

/******************** Pair class ***********************/

class Pair implements Comparable<Pair>{
    public int id;
    public long b;
    public long a;
    public long c;
    public Pair(){
        this.id = 1000;
        this.a = 0;
        this.b = 0;
        this.c = 0;
    }
    public Pair(int id , long a,long b , long c ){
        this.id = id;
        this.a = a;
        this.b = b;
        this.c = c;
    }
    public int compareTo(Pair p){
        if(this.a < p.a)return -1;
        else if(this.a > p.a )return 1;
        else {
            if(this.b < p.b)return -1;
            else if(this.b > p.b )return 1;
            else return 0;

        }
    }
    public String toString(){
        return "a="+this.a+" b="+this.b;
    }

}
