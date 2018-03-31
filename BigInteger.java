import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigInteger
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";
    public static final int MAX_LENGTH = 200;

    // regex for input
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?<signLeft>[[+][-]]?)(?<numLeft>[0-9]+)(?<operator>[[+][-][*]])(?<signRight>[[+][-]]?)(?<numRight>[0-9]+)");

    // data structure of BigInteger
    private int[] digits;
    private int length;
    private int sign;

    // Constructors
    public BigInteger()
    {
        for (int i = 0; i < MAX_LENGTH; i++)
            this.digits[i] = 0;
        length = 0;
    }

    public BigInteger(int sign)
    {
        for (int i = 0; i < MAX_LENGTH; i++)
            this.digits[i] = 0;
        length = 0;
        this.sign = sign;
    }

    public BigInteger(int[] digits)
    {
        this.digits = new int [MAX_LENGTH];
        length = lastIndexOf(digits) + 1;

        for (int i = 0; i < length; i++) {
            this.digits[i] = digits[i];
            System.out.println(i + " digit " + digits[i]);
        }
    }

    public BigInteger(String sign, String num)
    {
        digits = new int[MAX_LENGTH];
        length = num.length();

        // check if the number is minus
        if ( sign.equals("-") )
            this.sign = -1;
        else
            this.sign = 1;

        // i = 10^i place value
        for (int i = 0; i < length; i++)
            digits[i] = this.sign * Character.getNumericValue( num.charAt(length-i-1) );

    }

    private static int[] checkCarry(int[] val) {

        for (int i = 0; i < lastIndexOf(val)+1; i++) {

            // check if the digit is negative
            int s = 1;
            if (val[i] < 0)
                s = -1;

            // carry for addition
            while ( val[i] * s >= 10 ) {
                System.out.print(i + "번째 " + val[i]);
                val[i] -= (10 * s);
                val[i+1] += s;
                System.out.println(" => " + val[i]);
            }

            // carry for subtraction
            while ( val[i] * s < 0 ) {
                System.out.print(i + "th " + val[i]);
                val[i] += (10 * s);
                val[i+1] -= s;
                System.out.println(" => " + val[i]);
            }
        }

        return val;
    }

    private static int lastIndexOf(int[] val) {
        int i = MAX_LENGTH-1;
        while (i>0 && val[i] == 0) {
            i--;
        }
        return i;
    }

    public BigInteger negate(BigInteger big) {
        big.sign *= -1;
        for (int i = 0; i < big.length; i++)
            big.digits[i] *= -1;
        return big;
    }

    public BigInteger add(BigInteger big)
    {
        // add each digit
        int[] digits = new int[MAX_LENGTH];


        for (int i = 0; i < this.length || i < big.length; i++)
            digits[i] = this.digits[i] + big.digits[i];

        // check carry in and out
        digits = checkCarry(digits);

        // convert resulting array into BigInteger
        BigInteger result = new BigInteger(digits);

        return result;
    }

    public BigInteger subtract(BigInteger big)
    {
        // same with addition after negation of right operand
        return this.add( negate(big) );
    }

    public BigInteger multiply(BigInteger big)
    {
        return this;
    }

    @Override
    public String toString()
    {
        String result = "";

        // if zero
        if ( this.length == 1 && this.digits[0] == 0 )
            result = "0";

        // if not
        else {
            // attach sign for negative number
            if ( this.sign == -1 )
                result = "-";
            // attach each digit
            for (int i = this.length-1; i >= 0; i--)
                result += this.getDigit(i);
        }

        return result;
    }

    public String getDigit(int i)
    {
        return Integer.toString( (this.digits[i]) );
    }

    private static String removeSpace(String str)
    {
        return str.replaceAll("\\s","");
    }

    static BigInteger evaluate(String input) throws IllegalArgumentException
    {
        // remove space
        input = removeSpace(input);

        // declare variables
        String o = "";
        String sl = "";
        String sr = "";
        String nl = "";
        String nr = "";

        // parse input using regex
        Matcher matcher = EXPRESSION_PATTERN.matcher(input);
        while (matcher.find()) {
             o = matcher.group("operator");
             sl = matcher.group("signLeft");
             nl = matcher.group("numLeft");
             sr = matcher.group("signRight");
             nr = matcher.group("numRight");
        }

        // process big integer
        BigInteger num1 = new BigInteger(sl, nl);
        BigInteger num2 = new BigInteger(sr, nr);

        // perform calculation according to operator
        // with exception handling
        BigInteger result;
        if ( o.equals("+") )
            result = num1.add(num2);
        else if ( o.equals("-") )
            result = num1.subtract(num2);
        else if ( o.equals("*") )
            result = num1.multiply(num2);
        else
            throw new IllegalArgumentException();

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

    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
