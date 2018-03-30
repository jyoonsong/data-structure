import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigInteger
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";

    // implement this
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("^()$");

    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();

                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }

    public BigInteger(int i)
    {
    }

    public BigInteger(int[] num1)
    {
    }

    public BigInteger(String s)
    {
    }

    public BigInteger add(BigInteger big)
    {
    }

    public BigInteger subtract(BigInteger big)
    {
    }

    public BigInteger multiply(BigInteger big)
    {
    }

    @Override
    public String toString()
    {
    }

    static String removeSpace(String str)
    {
        return str.replaceAll("\\s","");
    }

    static BigInteger evaluate(String input) throws IllegalArgumentException
    {
        // implement here
        // remove space
        input = removeSpace(input);

        // parse input using regex

        // process big integer
         BigInteger num1 = new BigInteger(arg1);
         BigInteger num2 = new BigInteger(arg2);

         // calculation
         BigInteger result = num1.add(num2);

         return result;
    }

    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);

        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());

            return false;
        }
    }

    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
