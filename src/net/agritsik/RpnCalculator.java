package net.agritsik;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;

public class RpnCalculator {

    public static final String INVALID_INPUT = "Invalid input.";
    public static final String RPN_CALCULATOR_STOPPED = "RPN calculator stopped.";
    public static final String RPN_CALCULATOR_STARTED_ONLY_NUMBERS_AND_ARITHMETIC_OPERATORS_ARE_ALLOWED_USE_Q_FOR_EXIT = "RPN calculator started. Only numbers and arithmetic operators are allowed. Use 'q' for exit.";
    public static final String CANNOT_PERFORM_OPERATION_WITH_LESS_THAN_2_OPERANDS = "Cannot perform operation with less than 2 operands";
    public static final String RESULT = "result: ";
    public static final String Q = "q";
    public static final String INPUT_PROMPT = ">";
    public static final String CALCULATION_ERROR = "Calculation error: ";

    private static Scanner input = new Scanner(System.in);
    private final Pattern inputPattern = Pattern.compile("^[0-9,+,/,.,*,\\-,\\s]++$");
    private final Pattern arithmeticPattern = Pattern.compile("[+,/,*,-]");
    private Map<String, ArithmeticOperation> operatorsMap = new HashMap() {{
        put("+", (ArithmeticOperation) (o1, o2) -> o1 + o2);
        put("-", (ArithmeticOperation) (o1, o2) -> o2 - o1);
        put("*", (ArithmeticOperation) (o1, o2) -> o1 * o2);
        put("/", (ArithmeticOperation) (o1, o2) -> o2 / o1);
    }};

    private Stack<Double> operandStack = new Stack<Double>();

    public static void main(String[] args) {
        doSanityTests();
        boolean isContinue = true;
        RpnCalculator rpnCalculator = new RpnCalculator();
        System.out.println(RPN_CALCULATOR_STARTED_ONLY_NUMBERS_AND_ARITHMETIC_OPERATORS_ARE_ALLOWED_USE_Q_FOR_EXIT);
        System.out.print(INPUT_PROMPT);
        while (input.hasNextLine() && isContinue) {
            String userInput = input.nextLine();
            if (Q.equalsIgnoreCase(userInput)) {
                isContinue = false;
                continue;
            }
            System.out.println(rpnCalculator.handleInput(userInput));
            System.out.print(INPUT_PROMPT);
        }
        System.out.println(RPN_CALCULATOR_STOPPED);
    }

    /**
     * Function to handle any user entered string
     * @param userInput
     * @return result of calculation or just last entered number if no calculation occurred
     */
    private String handleInput(String userInput) {
        if (!inputPattern.matcher(userInput).matches()) {
            return INVALID_INPUT;
        }
        List<String> parsedValues = Arrays.asList(userInput.split(" ")).stream().filter(s -> !s.isEmpty()).map(s -> s.trim()).collect(Collectors.toList());
        for (String inputItem : parsedValues) {
            if (arithmeticPattern.matcher(inputItem).matches()) {
                try {
                    performArithmeticOperation(inputItem);
                    continue;
                } catch (ArithmeticException ex) {
                    return CALCULATION_ERROR + ex.getMessage();
                }
            }
            try {
                operandStack.push(Double.parseDouble(inputItem));
            } catch (NumberFormatException ex) {
                return INVALID_INPUT + " " + ex.getMessage() + ". Last value in stack: " + operandStack.peek().toString();
            }
        }
        return operandStack.peek().toString();
    }


    /**
     * Perform Arithmetic operation over 2 last operands in stack. If no available operands - throws ArithmeticException
     * @param inputItem
     */
    private void performArithmeticOperation(String inputItem) {
        if (operandStack.empty() || operandStack.size() < 2) {
            throw new ArithmeticException(CANNOT_PERFORM_OPERATION_WITH_LESS_THAN_2_OPERANDS) ;
        }
        Double result = operatorsMap.get(inputItem).executeOperation(operandStack.pop(), operandStack.pop());
        operandStack.push(result);
    }

    /**
     *  Do sanity test before application run
     */
    private static void doSanityTests() {
        testNotAllowedInput();
        testAddition();
        testSubtraction();
        testDivision();
        testMultiplication();
    }

    private static void testMultiplication() {
        RpnCalculator calculator = new RpnCalculator();
        assertEquals(calculator.handleInput("7 8 *"), new Double(56).toString());
    }

    private static void testDivision() {
        RpnCalculator calculator = new RpnCalculator();
        assertEquals(calculator.handleInput("10 5 /"), new Double(2).toString());
    }

    private static void testSubtraction() {
        RpnCalculator calculator = new RpnCalculator();
        assertEquals(calculator.handleInput("10 5 -"), new Double(5).toString());
    }

    private static void testAddition() {
        RpnCalculator calculator = new RpnCalculator();
        assertEquals(calculator.handleInput("10 5 +"), new Double(15).toString());
    }

    private static void testNotAllowedInput() {
        RpnCalculator calculator = new RpnCalculator();
        assertEquals(calculator.handleInput("text"), INVALID_INPUT);
    }


}
