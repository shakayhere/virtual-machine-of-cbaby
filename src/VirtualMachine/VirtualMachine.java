package VirtualMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class VirtualMachine
{
    static List<List> machineCode = new ArrayList<List>();  // Contains lists of machine codes for each line in Three Address Code

    public void setMachineCode(List mcList)
    {
        VirtualMachine.machineCode = mcList;
    }

    public VirtualMachine()
    { }

    ////////////////////////////////////////////////////////////
    //     Virtual Machine functions and execution of code    //
    ////////////////////////////////////////////////////////////
    
    static String getRelativeAddress(String var) throws FileNotFoundException
    {
        File myObj = new File("translator-symboltable.txt");
        Scanner myReader = new Scanner(myObj);
        String getValue = "";
        while(myReader.hasNext())
        {
            String tempString = myReader.nextLine();
            
            String[] finalString = tempString.split("\t");
            if(finalString[0].contentEquals(var))
            {
                getValue = finalString[2];
                break;
            }
        }
        myReader.close();
        return getValue;
    }

    static String getRelativeAddressOfFunction(String var) throws FileNotFoundException
    {
        File tempObj = new File("function-mapping.txt");
        Scanner tempReader = new Scanner(tempObj);
        String getValue = "";
        while(tempReader.hasNext())
        {
            String tempString = tempReader.nextLine();
            String[] finalString = tempString.split("\t");
            if(finalString[0].contentEquals(var))
            {
                getValue = finalString[1];
                break;
            }
        }
        tempReader.close();
        return getValue;
    }

    static String getDataType(String x) throws FileNotFoundException
    {
        File myObj = new File("translator-symboltable.txt");
        Scanner myReader = new Scanner(myObj);
        String getValue = "";
        while(myReader.hasNext())
        {
            String tempString = myReader.nextLine();
            
            String[] finalString = tempString.split("\t");
            if(finalString[2].contentEquals(x))
            {
                getValue = finalString[1];
                break;
            }
        }
        myReader.close();
        return getValue;        
    }
    
    static String getFunctionName(String x) throws FileNotFoundException
    {
        File myObj = new File("function-mapping.txt");
        Scanner myReader = new Scanner(myObj);
        String getValue = "";
        while(myReader.hasNext())
        {
            String tempString = myReader.nextLine();
            
            String[] finalString = tempString.split("\t");
            if(finalString[1].contentEquals(x))
            {
                getValue = finalString[0];
                break;
            }
        }
        myReader.close();
        return getValue;        
    }
    
    static String getInitialValue(String x) throws FileNotFoundException
    {
        File myObj = new File("translator-symboltable.txt");
        Scanner myReader = new Scanner(myObj);
        String getValue = "";
        while(myReader.hasNext())
        {
            String tempString = myReader.nextLine();
            
            String[] finalString = tempString.split("\t");
            if(finalString[2].contentEquals(x))
            {
                for(int i = 3; i < finalString.length; i++)
                {
                    getValue += finalString[i];
                }
            }
        }
        
        if(getValue.startsWith("\"") || getValue.startsWith("'"))
        {
            getValue = getValue.substring(1, getValue.length() - 1);
        }
        myReader.close();
        return getValue;        
    }
    
    static boolean checkInitialValue(String x) throws FileNotFoundException
    {
        File myObj = new File("translator-symboltable.txt");
        Scanner myReader = new Scanner(myObj);
        boolean getValue = false;
        while(myReader.hasNext())
        {
            String tempString = myReader.nextLine();
            
            String[] finalString = tempString.split("\t");
            if(finalString[2].contentEquals(x))
            {
                if (finalString.length > 3)
                {
                    return true;
                }
            }
        }
        myReader.close();
        return getValue;        
    }
    
    static boolean checkInStack(List <List> stack, String check) throws FileNotFoundException
    {
        for(int i = 0; i < stack.size(); i++)
        {
            List <String> tempLine = stack.get(i);
            if(tempLine.size() > 0)
            {
                if(tempLine.get(0).contentEquals(check))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    static boolean checkValueInStack(List <List> stack, String check) throws FileNotFoundException
    {
        for(int i = 0; i < stack.size(); i++)
        {
            List <String> tempLine = stack.get(i);
            if(tempLine.size() > 0)
            {
                if(tempLine.get(0).contentEquals(check))
                {
                    if(tempLine.size() > 2)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    static void updateVariable(List <List> stack, String address, String value) throws FileNotFoundException
    {
        for(int i = 0; i < stack.size(); i++)
        {
            List <String> tempLine = stack.get(i);
            
            if(tempLine.get(0).contentEquals(address))
            {
                if(tempLine.size() > 2)
                {
                    tempLine.set(2, value);
                }
                else
                {
                    tempLine.add(value);
                }
                stack.set(i, tempLine);
                break;
            }
        }
    }
    
    static void updateVariableDataType(List <List> stack, String address, String dataType) throws FileNotFoundException
    {
        for(int i = 0; i < stack.size(); i++)
        {
            List <String> tempLine = stack.get(i);
            
            if(tempLine.get(0).contentEquals(address))
            {                
                tempLine.set(1, dataType);
                stack.set(i, tempLine);
                break;
            }
        }
    }
    
    static String getDataTypeFromStack(List <List> stack, String address) throws FileNotFoundException
    {
        String getValue = "";
        for(int i = 0; i < stack.size(); i++)
        {
            List <String> tempLine = stack.get(i);
            if(tempLine.size() > 0)
            {
                if(tempLine.get(0).contentEquals(address))
                {
                    getValue = tempLine.get(1);
                }
            }
        }
        return getValue;
    }
    
    static String getValueFromStack(List <List> stack, String address) throws FileNotFoundException
    {
        String getValue = "";
        for(int i = 0; i < stack.size(); i++)
        {
            List <String> tempLine = stack.get(i);
            if(tempLine.size() > 0)
            {
                if(tempLine.get(0).contentEquals(address))
                {
                    getValue = tempLine.get(2);
                }
            }
        }
        return getValue;
    }
    
    static List executeCode(List<List> stack, String funcName) throws FileNotFoundException
    {
        
        List <List> tempStackForFunctionCall = new ArrayList<List>();
        String funcRa = getRelativeAddressOfFunction(funcName);
        
        int index = -1;
        
        // Getting index of first statement of given function
        for(int i = 0; i < machineCode.size(); i++)
        {
            List <String> tempLine = machineCode.get(i);
            
            if(tempLine.get(0).contentEquals(funcRa))
            {
                index = i + 1;
                break;
            }
        }
        
        // Updating parameters passed in function
        for(int i = 0; i < stack.size(); i++)
        {
            List <String> tempLine = machineCode.get(index);
            
            String value = (String) stack.get(i).get(2);
            if(tempLine.get(0).contentEquals("30") && stack.get(i).get(1).toString().equalsIgnoreCase("char"))
            {
                char tempChar = value.charAt(0);
                int tempInt = tempChar;
                value = Integer.toString(tempInt);
            }
            else if(tempLine.get(0).contentEquals("31") && stack.get(i).get(1).toString().equalsIgnoreCase("integer"))
            {
                int tempInt = Integer.parseInt(value);
                char tempChar = (char) tempInt;
                value = Character.toString(tempChar);
            }
            stack.get(i).set(2, value);
            stack.get(i).set(0, tempLine.get(1));
            index++;
        }
        
        
        while(index < machineCode.size())
        {
            List <String> varData = new ArrayList <String> ();
            List <String> machineCodeLine = machineCode.get(index);
            
            if(machineCodeLine.get(0).contentEquals("1"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0, value;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        String tempSTR = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = Integer.parseInt(tempSTR);
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    value = op1 + op2;

                    if(checkInStack(stack, machineCodeLine.get(3)))
                    { }
                    else
                    {
                        varData = new ArrayList <String> ();
                        varData.add(machineCodeLine.get(3));
                        varData.add(getDataType(machineCodeLine.get(3)));
                        stack.add(varData);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("integer"))
                    {
                        String finalValue = Integer.toString(value);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("char"))
                    {
                        char charTemp = (char) value;
                        String finalValue = Character.toString(charTemp);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            else if(machineCodeLine.get(0).contentEquals("2"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0, value;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    value = op1 - op2;

                    if(checkInStack(stack, machineCodeLine.get(3)))
                    { }
                    else
                    {
                        varData = new ArrayList <String> ();
                        varData.add(machineCodeLine.get(3));
                        varData.add(getDataType(machineCodeLine.get(3)));
                        stack.add(varData);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("integer"))
                    {
                        String finalValue = Integer.toString(value);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("char"))
                    {
                        char charTemp = (char) value;
                        String finalValue = Character.toString(charTemp);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("3"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0, value;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    value = op1 * op2;

                    if(checkInStack(stack, machineCodeLine.get(3)))
                    { }
                    else
                    {
                        varData = new ArrayList <String> ();
                        varData.add(machineCodeLine.get(3));
                        varData.add(getDataType(machineCodeLine.get(3)));
                        stack.add(varData);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("integer"))
                    {
                        String finalValue = Integer.toString(value);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("char"))
                    {
                        char charTemp = (char) value;
                        String finalValue = Character.toString(charTemp);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            if(machineCodeLine.get(0).contentEquals("4"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0, value;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    value = op1 / op2;

                    if(checkInStack(stack, machineCodeLine.get(3)))
                    { }
                    else
                    {
                        varData = new ArrayList <String> ();
                        varData.add(machineCodeLine.get(3));
                        varData.add(getDataType(machineCodeLine.get(3)));
                        stack.add(varData);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("integer"))
                    {
                        String finalValue = Integer.toString(value);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("char"))
                    {
                        char charTemp = (char) value;
                        String finalValue = Character.toString(charTemp);
                        updateVariable(stack, machineCodeLine.get(3), finalValue);
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("5"))
            {
                if(machineCodeLine.get(3).contentEquals("-1") && machineCodeLine.get(2).contentEquals("-1"))
                {
                    if(checkInStack(stack, machineCodeLine.get(1)))
                    { }
                    else
                    {
                        varData = new ArrayList <String> ();
                        varData.add(machineCodeLine.get(1));
                        varData.add(getDataType(machineCodeLine.get(1)));
                        
                        if(checkInitialValue(machineCodeLine.get(1)))
                        {
                            varData.add(getInitialValue(machineCodeLine.get(1)));
                        }
                        stack.add(varData);
                    }
                }
                else
                {
                    if(checkInStack(stack, machineCodeLine.get(1)))
                    { }
                    else
                    {
                        varData = new ArrayList <String> ();
                        varData.add(machineCodeLine.get(1));
                        varData.add(getDataType(machineCodeLine.get(1)));
                        
                        if(checkInitialValue(machineCodeLine.get(1)))
                        {
                            varData.add(getInitialValue(machineCodeLine.get(1)));
                        }
                        stack.add(varData);
                    }
                    
                    if(checkValueInStack(stack, machineCodeLine.get(1)))
                    {
                        String newValue = getValueFromStack(stack, machineCodeLine.get(1));
                        if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char") && getDataType(machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                        {
                            int tempInt = Integer.parseInt(newValue);
                            char tempChar = (char) tempInt;
                            newValue = Character.toString(tempChar);
                        }
                        else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer") && getDataType(machineCodeLine.get(1)).equalsIgnoreCase("char"))
                        {
                            char tempChar = newValue.charAt(0);
                            int tempInt = tempChar;
                            newValue = Integer.toString(tempInt);
                        }

                        updateVariable(stack, machineCodeLine.get(2), newValue);
                    }
                    else
                    {
                        System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                        System.exit(0);
                    }
                }
                
            }
            
            else if(machineCodeLine.get(0).contentEquals("10"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));
                        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));
                        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    if (op1 < op2)
                    {
                        index = Integer.parseInt(machineCodeLine.get(3)) - 2;
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("11"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));
                        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));
                        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    if (op1 <= op2)
                    {
                        index = Integer.parseInt(machineCodeLine.get(3)) - 2;
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("12"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));
                        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));
                        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    if (op1 > op2)
                    {
                        index = Integer.parseInt(machineCodeLine.get(3)) - 2;
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("13"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));
                        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));
                        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    if (op1 >= op2)
                    {
                        index = Integer.parseInt(machineCodeLine.get(3)) - 2;
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("14"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));
                        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));
                        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    if (op1 == op2)
                    {
                        index = Integer.parseInt(machineCodeLine.get(3)) - 2;
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("15"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataType(machineCodeLine.get(1)));
                        
                    if(checkInitialValue(machineCodeLine.get(1)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(1)));
                    }
                    stack.add(varData);
                }
                
                if(checkInStack(stack, machineCodeLine.get(2)))
                { }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(2));
                    varData.add(getDataType(machineCodeLine.get(2)));
                        
                    if(checkInitialValue(machineCodeLine.get(2)))
                    {
                        varData.add(getInitialValue(machineCodeLine.get(2)));
                    }
                    stack.add(varData);
                }
                
                int op1 = 0, op2 = 0;
                if(checkValueInStack(stack, machineCodeLine.get(1)) && checkValueInStack(stack, machineCodeLine.get(2)))
                {
                    if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                    {
                        op1 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(1));
                        op1 = strTemp.charAt(0);
                    }

                    if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("integer"))
                    {
                        op2 = Integer.parseInt(getValueFromStack(stack, machineCodeLine.get(2)));
                    }
                    else if(getDataTypeFromStack(stack, machineCodeLine.get(2)).equalsIgnoreCase("char"))
                    {
                        String strTemp = getValueFromStack(stack, machineCodeLine.get(2));
                        op2 = strTemp.charAt(0);
                    }

                    if (op1 != op2)
                    {
                        index = Integer.parseInt(machineCodeLine.get(3)) - 2;
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("20"))
            {
                Scanner userInput = new Scanner(System.in);
                String tempData = "";
                if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("integer"))
                {
                    int temp = userInput.nextInt();
                    tempData = Integer.toString(temp);
                }
                else if(getDataTypeFromStack(stack, machineCodeLine.get(1)).equalsIgnoreCase("char"))
                {
                    char temp = userInput.next().charAt(0);
                    tempData = Character.toString(temp);
                }
                
                updateVariable(stack, machineCodeLine.get(1), tempData);
            }
            
            else if(machineCodeLine.get(0).contentEquals("21"))
            {
                if(checkValueInStack(stack, machineCodeLine.get(1)))
                {
                    if(getValueFromStack(stack, machineCodeLine.get(1)).contentEquals("\\n") || machineCodeLine.get(1).contentEquals("-1"))
                    {
                        System.out.println();
                    }
                    else
                    {
                        System.out.print(getValueFromStack(stack, machineCodeLine.get(1)));
                    }
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("22"))
            {
                if(machineCodeLine.get(1).contentEquals("-1"))
                {
                    return null;
                }
                else
                {
                    if(checkValueInStack(stack, machineCodeLine.get(1)))
                    {
                        varData = new ArrayList <String> ();
                        varData.add(machineCodeLine.get(1));
                        varData.add(getDataTypeFromStack(stack, machineCodeLine.get(1)));
                        varData.add(getValueFromStack(stack, machineCodeLine.get(1)));
                        return varData;
                    }
                    else
                    {
                        System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                        System.exit(0);
                    }
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("23"))
            {
                index = Integer.parseInt(machineCodeLine.get(1)) - 2;
            }
            
            else if(machineCodeLine.get(0).contentEquals("24"))
            {
                if(checkValueInStack(stack, machineCodeLine.get(1)))
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add(getDataTypeFromStack(stack, machineCodeLine.get(1)));

                    varData.add(getValueFromStack(stack, machineCodeLine.get(1)));

                    tempStackForFunctionCall.add(varData);
                }
                else
                {
                    System.out.println("\nVariable not initialised at line " + (index + 1) + " of Three Address Code");
                    System.exit(0);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("25"))
            {
                if(machineCodeLine.get(3).contentEquals("-1"))
                {
                    String tempName = getFunctionName(machineCodeLine.get(1));
                    executeCode(tempStackForFunctionCall, tempName);
                    tempStackForFunctionCall.clear();
                }
                else
                {
                    String tempName = getFunctionName(machineCodeLine.get(1));
                    List <String> returnValue = executeCode(tempStackForFunctionCall, tempName);
                    String value = returnValue.get(2);
                    if(value.equalsIgnoreCase("integer") && getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("char"))
                    {
                        int tempInt = Integer.parseInt(value);
                        char tempChar = (char)tempInt;
                        value = Character.toString(tempChar);
                    }
                    else if(value.equalsIgnoreCase("char") && getDataTypeFromStack(stack, machineCodeLine.get(3)).equalsIgnoreCase("integer"))
                    {
                        char tempChar = value.charAt(0);
                        int tempInt = tempChar;
                        value = Integer.toString(tempInt);
                    }
                    updateVariable(stack, machineCodeLine.get(3), value);
                }
            }
            
            else if(machineCodeLine.get(0).contentEquals("30"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                {
                    updateVariableDataType(stack, machineCodeLine.get(1), "INTEGER");
                }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add("INTEGER");
                    stack.add(varData);
                }
            }
            else if(machineCodeLine.get(0).contentEquals("31"))
            {
                if(checkInStack(stack, machineCodeLine.get(1)))
                {
                    updateVariableDataType(stack, machineCodeLine.get(1), "CHAR");
                }
                else
                {
                    varData = new ArrayList <String> ();
                    varData.add(machineCodeLine.get(1));
                    varData.add("CHAR");
                    stack.add(varData);
                };
            }
            
            index++;
        }
        return null;
    }
    
    //////////////////////////
    //  ------------------  //
    //////////////////////////

    public void startVirtualMachine() throws FileNotFoundException
    {
        System.out.println("Executing code...");
        List <List> emptyStack = new ArrayList();
        executeCode(emptyStack, "main");
        System.out.println();
    }

}