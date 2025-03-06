import java.util.*;
import java.io.*;
public class Main {
    static Stack<Character> opStk = new Stack<>(10);
    static Stack<Double> valStk = new Stack<>(10);

    public static void main(String[] args) {
        rwFiles("./src/files/input.txt");
    }
    public static void runCLI() {
        Scanner scanner = new Scanner(System.in);
        List<String> history = new ArrayList<>();  

        while (true) {
            System.out.print("Enter expression (or 'exit' to quit): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;
            history.add(input);
            try {
                double result = evaluateExpression(input);
                System.out.println("Result: " + result);
            } catch (ArithmeticException e) {
                System.out.println("Math Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid Expression: " + input);
            }
        }
        scanner.close();
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
        opStk = new Stack<>(10);
        valStk = new Stack<>(10);
        expression = expression.replaceAll("\\s+", "");
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch)) {
                StringBuilder number = new StringBuilder();
                while (i < expression.length() &&
                      (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i));
                    i++;
                }
                i--;  
                valStk.push(Double.parseDouble(number.toString()));
            } else if (Character.isLetter(ch)) {
                StringBuilder func = new StringBuilder();
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    func.append(expression.charAt(i));
                    i++;
                }
                if (i < expression.length() && expression.charAt(i) == '(') {
                    String funcName = func.toString();
                    switch (funcName) {
                        case "sin":
                            opStk.push('s');
                            break;
                        case "cos":
                            opStk.push('c');
                            break;
                        case "tan":
                            opStk.push('t');
                            break;
                        case "log":
                            opStk.push('l');
                            break;
                        case "exp":
                            opStk.push('e');
                            break;
                        case "sqrt":
                            opStk.push('q');
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown function: " + funcName);
                    }    opStk.push('(');
                } else {
                    throw new IllegalArgumentException("Expected '(' after function name: " + func.toString());
                }
            } else if (isOperator(ch)) {
                while (!opStk.isEmpty() && precedence(ch) <= precedence(opStk.peek())) {
                    doOp();
                }
                opStk.push(ch);
            } else if (ch == '(') {
                opStk.push(ch);
            } else if (ch == ')') {
                while (!opStk.isEmpty() && opStk.peek() != '(') {
                    doOp();
                }
                if (opStk.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                opStk.pop(); 
                if (!opStk.isEmpty() && isFunction(opStk.peek())) {
                    char funcOp = opStk.pop();
                    double operand = valStk.pop();
                    double result = applyFunction(funcOp, operand);
                    valStk.push(result);
                }
            } else {
                throw new IllegalArgumentException("Invalid character encountered: " + ch);
            }
        }

        while (!opStk.isEmpty()) {
            doOp();
        }
        if (valStk.isEmpty()) {
            throw new IllegalArgumentException("Invalid Expression");
        }
        return valStk.pop();
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '>' || ch == '<' || ch == '≥' || ch == '≤' || ch == '=' || ch == '!';
    }
    private static boolean isFunction(char ch) {
        return ch == 's' || ch == 'c' || ch == 't' ||
               ch == 'l' || ch == 'e' || ch == 'q';
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
            if (isFunction(operator))
                    return 4;
                return -1;
        }
    }

    private static void doOp() {
        char op = opStk.pop();
        if (isFunction(op)) {
            double operand = valStk.pop();
            double result = applyFunction(op, operand);
            valStk.push(result);
        } else {double b = valStk.pop();
            double a = valStk.pop();
            double result = applyOperator(op, a, b);
            valStk.push(result);
        }
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
    private static double applyFunction(char function, double operand) {
        switch (function) {
            case 's': 
                return Math.sin(Math.toRadians(operand));
            case 'c': 
                return Math.cos(Math.toRadians(operand));
            case 't': 
                return Math.tan(Math.toRadians(operand));
            case 'l':
                if (operand <= 0) {
                    throw new ArithmeticException("Logarithm operand must be positive");
                }
                return Math.log(operand);
            case 'e': 
                return Math.exp(operand);
            case 'q': 
                if (operand < 0) {
                    throw new ArithmeticException("Square root operand must be non-negative");
                }
                return Math.sqrt(operand);
            default:
                throw new IllegalArgumentException("Unknown function: " + function);
        }
    }
}