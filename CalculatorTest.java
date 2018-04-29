import java.io.*;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorTest
{
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?<operator>[-+*/%^()])|(?<operand>\\d+)");

    public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("q") == 0)
					break;

				command(input);
			}
			catch (Exception e)
			{
				System.out.println("ERROR");
			}
		}
	}

	private static void command(String input) throws Exception
	{
	    // convert prefix into postfix and calculate
	    String postfixExp = convert(input);
	    long result = eval(postfixExp);

	    // print postfix expression
        System.out.println( postfixExp );

        // print result
        System.out.println( result );

	}

    private static String convert(String prefixExp) throws Exception {
        // Use pattern matcher to (1) split input and at the same time (2) remove whitespace from input
        Matcher matcher = EXPRESSION_PATTERN.matcher(prefixExp);

        // Use Stack & StringBuilder to (3) convert infix into postfix expression
        Stack<Character> operators = new Stack<>();
        StringBuilder postfixExp =  new StringBuilder();

        boolean turnOfOperand = true;

        while (matcher.find()) {
            String s = matcher.group();
            char c = s.charAt(0);

            // case 0: number
            if (Character.isDigit(c)) {
                // build string only if right turn
                if (!turnOfOperand)
                    throw new Exception();
                postfixExp.append(s + " ");

                // next turn should be operator
                turnOfOperand = false;
            }

            // case 1: parentheses
            else if (c == '(') {
                if (!turnOfOperand)
                    throw new Exception();
                operators.push(c);

                // next turn should be operand
                turnOfOperand = true;
            }
            else if (c == ')') {
                if (turnOfOperand)
                    throw new Exception();

                // stack pop until open parenthesis
                while (operators.peek() != '(') {
                    postfixExp.append(operators.pop() + " ");
                    if (operators.isEmpty()) throw new Exception();
                }
                // throw away open parenthesis
                operators.pop();

                // next turn should be operator
                turnOfOperand = false;
            }

            // case 2: unary or binary minus
            else if (c == '-') {
                if (turnOfOperand) {
                    operators.push('~');
                }
                else {
                    // before push, check stack
                    postfixExp.append( checkStack(c, operators) );
                    operators.push(c);

                    // next turn should be operand
                    turnOfOperand = true;
                }
            }

            // case 3: binary operators
            else if (c == '+' || c == '*' || c == '/' || c == '%' || c == '^') {
                if (turnOfOperand)
                    throw new Exception();

                // before push, check stack
                postfixExp.append( checkStack(c, operators) );
                operators.push(c);

                // next turn should be operand
                turnOfOperand = true;
            }

        }

        // ends with operand (or closing parenthesis), thus turnOfOperand should be false
        if (turnOfOperand)
            throw new Exception();

        // add rest of the operators in the back
        while (!operators.isEmpty())
            postfixExp.append(operators.pop() + " ");

        return postfixExp.toString().trim();
    }

    private static String checkStack(char c, Stack<Character> operators) {
        String out = "";

        // check stack and pop anything prior to current operator
        while (!operators.isEmpty()) {
            char top = operators.peek();

            int cmp = Integer.compare(priorityOf(c), priorityOf(top));
            if (cmp > 0)
                break;
            else if (cmp == 0)
                if (c == '^' || c == '~') break;

            out += (operators.pop() + " ");
        }

        return out;
    }

    private static int priorityOf(char c) {
        // return priority of operators
        switch (c) {
            case '+': case '-':
                return 0;
            case '*': case '/': case '%':
                return 1;
            case '~':
                return 2;
            case '^':
                return 3;
        }
        return -1;
    }

    private static long eval(String postfixExp) throws Exception {
        String [] tokens = postfixExp.split(" ");
        Stack<Long> operands = new Stack<>();

        for (String token : tokens) {
            // push if number
            if (Character.isDigit(token.charAt(0)))
                operands.push(Long.parseLong(token));
                // unary operator
            else if (token.equals("~"))
                operands.push(-1 * operands.pop());
                // binary operator
            else {
                long right = operands.pop();
                long left = operands.pop();

                switch (token.charAt(0)) {
                    case '+':
                        operands.push(left + right);
                        break;
                    case '-':
                        operands.push(left - right);
                        break;
                    case '*':
                        operands.push(left * right);
                        break;
                    case '/':
                        if (right == 0)
                            throw new Exception();
                        operands.push(left / right);
                        break;
                    case '%':
                        if (right == 0)
                            throw new Exception();
                        operands.push(left % right);
                        break;
                    case '^':
                        if (left == 0 && right < 0)
                            throw new Exception();
                        operands.push((long) Math.pow(left, right));
                        break;
                    default:
                        throw new Exception();
                }
            }
        }

        long result = operands.pop();

        // after all operations, stack should be empty
        if (operands.isEmpty())
            return result;
        else
            throw new Exception();
    }
}
