import java.util.*;
import java.io.*;
public class Main {
    static Stack<Character> opStk = new Stack<>(10);
    static Stack<Double> valStk = new Stack<>(10);

    public static void main(String[] args) {
        rwFiles("./src/files/input.txt");
    }

    public static void rwFiles(String filePath) {
        try {
            Scanner sf = new Scanner(new FileInputStream(filePath));
            PrintWriter pw = new PrintWriter(new FileOutputStream("./src/files/results.txt"));
            while (sf.hasNextLine()) {
                String line = sf.nextLine();
                double result = evaluateExpression(line);
                pw.println(line + "\n" + result);
            }
            sf.close();
            pw.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found"+filePath);
            e.printStackTrace();
        }
    }

    public static double evaluateExpression(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch)) {
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                i--;
                valStk.push((double) num);
            } else if (ch == '(') {
                opStk.push(ch);
            } else if (ch == ')') {
                while (opStk.peek() != '(') {
                    doOp();
                }
                opStk.pop();
            } else if (isOperator(ch)) {
                while (!opStk.isEmpty() && precedence(ch) <= precedence(opStk.peek())) {
                    doOp();
                }
                opStk.push(ch);
            }
        }

        while (!opStk.isEmpty()) {
            doOp();
        }

        return valStk.pop();
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '>' || ch == '<' || ch == '≥' || ch == '≤' || ch == '=' || ch == '!';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '^':
                return 3;
            case '*':
            case '/':
                return 2;
            case '+':
            case '-':
                return 1;
            case '>':
            case '<':
            case '≥':
            case '≤':
                return 0;
            case '=':
            case '!':
                return -1;
            default:
                return -1;
        }
    }

    private static void doOp() {
        double b = valStk.pop();
        double a = valStk.pop();
        char op = opStk.pop();
        double result = applyOperator(op, b, a);
        valStk.push(result);
    }

    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return a / b;
            case '^':
                return Math.pow(a, b);
            case '>':
                return a > b ? 1 : 0;
            case '<':
                return a < b ? 1 : 0;
            case '≥':
                return a >= b ? 1 : 0;
            case '≤':
                return a <= b ? 1 : 0;
            case '=':
                return a == b ? 1 : 0;
            case '!':
                return a != b ? 1 : 0;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}