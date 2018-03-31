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
        digits = new int [MAX_LENGTH];
        length = 0;
    }

    public BigInteger(int digit)
    {
        digits = new int [MAX_LENGTH];
        digits[0] = digit;
        length = 1;
        sign = 1;
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
            digits[i] = Character.getNumericValue( num.charAt(length-i-1) );

    }

    private void checkCarry(boolean isMultiply) {

        // if addition or subtraction,
        // max length is bigger operand's length+1
        int max = this.length;
        // if multiplication,
        // max length is bigger operand's length*2
        if ( isMultiply )
            max = MAX_LENGTH;

        for (int i = 0; i < max; i++) {

            // carry for addition
            while ( this.digits[i] >= 10 ) {
                this.digits[i] -= 10;
                this.digits[i+1]++;
            }

            // carry for subtraction
            while ( this.digits[i] < 0 ) {
                this.digits[i] += 10;
                this.digits[i+1]--;
            }
        }
    }

    private int lastIndexOf(int[] val) {
        int i = MAX_LENGTH-1;
        while (i>0 && val[i] == 0) {
            i--;
        }
        return i;
    }

    public int compareTo(BigInteger big) {
        if (this.length > big.length)
            return 1;
        else if (this.length < big.length)
            return -1;
        else {
            for (int i = this.length-1; i >= 0; i--) {
                if (this.digits[i] > big.digits[i])
                    return 1;
                else if (this.digits[i] < big.digits[i])
                    return -1;
            }
        }
        return 0;

    }

    private boolean isZero() {
        if (this.compareTo( new BigInteger(0) ) == 0)
            return true;
        return false;
    }

    public BigInteger add(BigInteger big)
    {
        BigInteger result = new BigInteger();
        int sl = 1;
        int sr = 1;

        // sign assignment
        if (this.sign == big.sign) {
            // same sign holds
            result.sign = this.sign;
        }
        else {
            // bigger number holds
            int c = this.compareTo(big);
            if ( c > 0 ) {
                result.sign = this.sign;
                sr = -1;
            }
            else if ( c < 0 ) {
                result.sign = big.sign;
                sl = -1;
            }
            else {
                return new BigInteger(0);
            }
        }

        // store each digit
        for (int i = 0; i < this.length || i < big.length; i++) {
            result.digits[i] = sl * this.digits[i] + sr * big.digits[i];
            result.length++;
        }

        // check carry in and out
        result.checkCarry(false);

        // update length
        result.length = lastIndexOf(result.digits) + 1;

        return result;
    }

    public BigInteger subtract(BigInteger big)
    {
        // same with addition after negation of right operand
        big.sign *= -1;
        return this.add( big );
    }

    public BigInteger multiply(BigInteger big)
    {
        BigInteger result = new BigInteger();

        // sign assignment
        if (this.isZero() || big.isZero())
            // zero included => zero
            return new BigInteger(0);
        else if (this.sign == big.sign) {
            // same sign => positive
            result.sign = 1;
        }
        else {
            // diff sign => negative
            result.sign = -1;
        }

        // store each digit
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < big.length; j++)
                result.digits[i+j] += this.digits[i] * big.digits[j];
        }

        // check carry in and out
        result.checkCarry(true);

        // update length
        result.length = lastIndexOf(result.digits) + 1;

        return result;
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
        return Integer.toString( Math.abs(this.digits[i]) );
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
