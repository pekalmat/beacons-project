package ch.zhaw.integration.beacons.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;

@Component
public class Calculator {

    private final Integer precision;
    private final MathContext mc;

    public Calculator(@Value("${beacons.calculations.bigdecimal.rounding.precision}") Integer precision) {
        this.precision = precision;
        this.mc = new MathContext(precision);
    }

    public BigDecimal add(BigDecimal thisValue, BigDecimal augend) {
        return thisValue.add(augend, mc);
    }

    public BigDecimal subtract(BigDecimal thisValue, BigDecimal subtrahend) {
        return thisValue.subtract(subtrahend, mc);
    }

    public BigDecimal divide(BigDecimal thisValue, BigDecimal divisor) {
        return thisValue.divide(divisor, mc);
    }

    public BigDecimal multiply(BigDecimal thisValue, BigDecimal multiplicand) {
        return thisValue.multiply(multiplicand, mc);
    }

    public BigDecimal sqrt(BigDecimal thisValue) {
        return thisValue.sqrt(mc);
    }

    public BigDecimal power(BigDecimal thisValue, int exponent) {
        return thisValue.pow(exponent, mc);
    }

    public MathContext getMathContext() {
        return mc;
    }

}
