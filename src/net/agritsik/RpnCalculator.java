package net.agritsik;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RpnCalculator {
    private static Scanner input = new Scanner(System.in);
    private final Pattern inputPattern = Pattern.compile("^[0-9,+,/,.,*,-]++$");
    private final Pattern arithmeticPattern = Pattern.compile("[+,/,.,*,-]");
    private Map<String, ArithmeticOperation> operatorsMap = new HashMap() {{
        put("+", (ArithmeticOperation) (o1, o2) -> o1 + o2);
        put("-", (ArithmeticOperation) (o1, o2) -> o1 - o2);
        put("*", (ArithmeticOperation) (o1, o2) -> o1 * o2);
        put("/", (ArithmeticOperation) (o1, o2) -> o1 / o2);
    }};

    private Stack<Double> operandStack = new Stack<Double>() {{
        push(0d);
    }};

    public static void main(String[] args) {
        boolean isContinue = true;
        RpnCalculator rpnCalculator = new RpnCalculator();
        System.out.println("RPN calculator started. Only numbers and arithmetic operators are allowed. Use 'q' for exit.");
        System.out.println(">");
        while (input.hasNextLine() && isContinue) {
            String userInput = input.next();
            if ("q".equalsIgnoreCase(userInput)) {
                isContinue = false;
                continue;
            }
            System.out.println(">" + rpnCalculator.handleInput(userInput));
        }
        System.out.println("RPN calculator stopped.");
    }

    public String handleInput(String userInput) {
        if (!inputPattern.matcher(userInput).matches()) {
            return ("Invalid input!!!");
        }
        List<String> parsedValues = Arrays.asList(userInput.split(" ")).stream().map(s -> s.trim()).collect(Collectors.toList());
        for (String inputItem : parsedValues) {
            if (arithmeticPattern.matcher(inputItem).matches()) {
                return performCalculation(inputItem);
            }
            operandStack.push(Double.parseDouble(inputItem));
        }
        return userInput;
    }

    private String performCalculation(String inputItem) {
        if (operandStack.empty() || operandStack.size() < 2) {
            return "Cannot perform operation with less than 2 operands";
        }
        Double result = operatorsMap.get(inputItem).executeOperation(operandStack.pop(), operandStack.pop());
        operandStack.clear();
        operandStack.push(result);
        return "result: " + result.toString();
    }

}
