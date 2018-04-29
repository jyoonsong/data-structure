import java.io.*;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorTest
{
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?<operator>[-+*/%^()])|(?<operand>\\d+)");

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
	    // split prefix expression
	    // convert prefix into postfix
	    String postfixExp = convert(input);
	    
	    // print postfix expression
        System.out.println( postfixExp );
        
        // evaluate postfix expression
        System.out.println( eval(postfixExp) );

	}

    private static String convert(String prefixExp) throws Exception {
        // Use pattern matcher to (1) split input and at the same time (2) remove whitespace from input
        Matcher matcher = EXPRESSION_PATTERN.matcher(prefixExp);

        // Use Stack & StringBuilder to (3) convert infix into postfix expression
        Stack<Character> operators = new Stack();
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
                    while ( operators.isEmpty() || operators.peek() == '(' || priorTo(c, operators.peek()) < 0 ) {
                        postfixExp.append(operators.pop() + " ");
                    }
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
                while ( operators.isEmpty() || operators.peek() == '(' || priorTo(c, operators.peek()) < 0 ) {
                    postfixExp.append(operators.pop() + " ");
                }
                operators.push(c);

                // next turn should be operand
                turnOfOperand = true;
            }

        }

        return postfixExp.toString();
    }

    private static int priorTo(char c, Character stackTop) {
        return Integer.compare(priorityOf(c), priorityOf(stackTop));
    }

    private static int priorityOf(char c) {
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

    private static long eval(String postfixExp) {
        return 1;
    }
}
