package com.salsaw.msalsa.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.salsaw.msalsa.algorithm.TerminalGAPsStrategy;

public class TerminalGAPsStrategyConverter implements IStringConverter<TerminalGAPsStrategy> {
	 
    @Override
    public TerminalGAPsStrategy convert(String value) {
    	TerminalGAPsStrategy convertedValue = TerminalGAPsStrategy.fromString(value);
 
        if(convertedValue == null) {
        	StringBuilder errorMessage = new StringBuilder();
        	errorMessage.append("Value ");
        	errorMessage.append(value);
        	errorMessage.append(" can not be converted to TerminalGAPsStrategy.");
        	errorMessage.append(" Available values are: ");
        	for(TerminalGAPsStrategy terminalGAPsStrategy : TerminalGAPsStrategy.values()) {
                errorMessage.append(terminalGAPsStrategy.toString());
                errorMessage.append(" ");
            }
            throw new ParameterException(errorMessage.toString());
        }
        return convertedValue;
    }
}