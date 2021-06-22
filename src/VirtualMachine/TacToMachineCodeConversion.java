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

public class TacToMachineCodeConversion
{
    static int relativeAddressNumber = 0;                   // Relative Address number for translator symboltable
    static int t = 1;                                       // Number for temp variable generation
    static List<List> machineCode = new ArrayList<List>();  // Contains lists of machine codes for each line in Three Address Code

    public void setRelativeAddressNumber(int raNum)
    {
        TacToMachineCodeConversion.relativeAddressNumber = raNum;
    }

    public void setTempVarCount(int tCount)
    {
        TacToMachineCodeConversion.t = tCount;
    }

    public List getMachineCode()
    {
        return TacToMachineCodeConversion.machineCode;
    }

    
    ////////////////////////////////////////////////////////////////////
    //     Three Address Code to Machine Code Conversion Functions    //
    ////////////////////////////////////////////////////////////////////
    
    static String genTemp()
    {
        String temp = "t" + t;
        t++;
        return temp;
    }

    static boolean checkArithmeticOperator(String x)
    {
        if(x.contentEquals("+") || x.contentEquals("-") || x.contentEquals("*") || x.contentEquals("/"))
        {
            return true;
        }
        return false;
    }

    static boolean checkNumericConstant(String x)
    {
        if (x.charAt(0) >= '0' && x.charAt(0) <= '9')
        {
            return true;
        }
        
        return false;
    }

    static boolean checkLiteralConstant(String x)
    {
        if(x.startsWith("'") && x.endsWith("'"))
        {
            return true;
        }
        return false;
    }

    static void writeToTranslatorSymbolTable(String x, String typeName, String initialValue) throws IOException
    {
        FileWriter tempWriter = new FileWriter("translator-symboltable.txt", true);
        
        if(initialValue.length() > 0)
        {
            tempWriter.append(x + "\t" + typeName + "\t" + Integer.toString(relativeAddressNumber) + "\t" + initialValue);
        }
        else
        {
            tempWriter.append(x + "\t" + typeName + "\t" + Integer.toString(relativeAddressNumber));
        }
        
        
        tempWriter.append("\n");
        tempWriter.close();
        
        if(typeName.equalsIgnoreCase("integer"))
        {
            relativeAddressNumber += 4;
        }
        else if(typeName.equalsIgnoreCase("char"))
        {
            relativeAddressNumber += 1;
        }
        else if(typeName.equalsIgnoreCase("string"))
        {
            relativeAddressNumber += initialValue.length() - 2;
        }
    }

    static void addInMachineCode(String opCode, String firstOp, String secondOp, String thirdOp)
    {
        List <String> temp = new ArrayList<String>();
        temp.add(opCode);
        temp.add(firstOp);
        temp.add(secondOp);
        temp.add(thirdOp);
        
        machineCode.add(temp);
    }

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
    
    static void addInFunctionMapping(String funcName, String opCode) throws IOException
    {
        FileWriter tempWriter = new FileWriter("function-mapping.txt", true);
        tempWriter.write(funcName + "\t" + opCode + "\n");
        tempWriter.close();
        
    }
    
    static String getOpcodeForArithmeticOperator(String x)
    {
        String getValue = "";
        if(x.equalsIgnoreCase("+"))
        {
            return "1";
        }
        if(x.equalsIgnoreCase("-"))
        {
            return "2";
        }
        if(x.equalsIgnoreCase("*"))
        {
            return "3";
        }
        if(x.equalsIgnoreCase("/"))
        {
            return "4";
        }
        return getValue;
    }
    
    static String getOpcodeForRelationalOperator(String x)
    {
        String getValue = "";
        if(x.equalsIgnoreCase("LT"))
        {
            return "10";
        }
        if(x.equalsIgnoreCase("LE"))
        {
            return "11";
        }
        if(x.equalsIgnoreCase("GT"))
        {
            return "12";
        }
        if(x.equalsIgnoreCase("GE"))
        {
            return "13";
        }
        if(x.equalsIgnoreCase("EQ"))
        {
            return "14";
        }
        if(x.equalsIgnoreCase("NE"))
        {
            return "15";
        }
        return getValue;
    }
    
    static String checkAndGetOperandAddress(String x) throws IOException
    {
        String getValue = "";
        if(checkNumericConstant(x))
        {
            String tempVar = genTemp();
            writeToTranslatorSymbolTable(tempVar, "INTEGER", x);
            getValue = getRelativeAddress(tempVar);
        }
        else if(checkLiteralConstant(x))
        {
            String tempVar = genTemp();
            writeToTranslatorSymbolTable(tempVar, "CHAR", x);
            getValue = getRelativeAddress(tempVar);
        }
        else
        {
            getValue = getRelativeAddress(x);
        }
        
        return getValue;
    }
    
    static void tacToMCConversion() throws IOException
    {
        FileWriter mcWriter = new FileWriter("machine-code.txt");    // Creates machine-code.txt
        
        // Opening file tac.txt
        File myObj = new File("tac.txt");
        
        Scanner myReader = new Scanner(myObj);
        int counter = 1;
        
        int funcMap = 40;
        while(myReader.hasNext())
        {
            String opCode = "-1", firstOp = "-1", secondOp = "-1", thirdOp = "-1";
            
            String forSubString = Integer.toString(counter);
            String tempString = myReader.nextLine();
            tempString = tempString.substring(forSubString.length(), tempString.length());
            tempString = tempString.trim();
            
            // Function Mapping
            if(tempString.endsWith(":"))
            {
                tempString = tempString.substring(0, tempString.length() - 1);
                opCode = Integer.toString(funcMap);
                addInFunctionMapping(tempString, opCode);
                
                mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                funcMap++;
            }
            else
            {
                String[] finalString = tempString.split(" ");
                
                // Variable declaration statements
                
                if(finalString[0].equalsIgnoreCase("integer") || finalString[0].equalsIgnoreCase("char"))
                {
                    if(finalString[0].equalsIgnoreCase("integer"))
                    {
                        opCode = "30";
                    }
                    else
                    {
                        opCode = "31";
                    }
                    firstOp = getRelativeAddress(finalString[1]);
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                
                // Function call statements
                else if(finalString[0].equalsIgnoreCase("call"))
                {
                    opCode = "25";
                    firstOp = getRelativeAddressOfFunction(finalString[1]);
                    secondOp = finalString[2];
                    if(finalString.length > 3)
                    {
                        thirdOp = getRelativeAddress(finalString[3]);
                    }
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                
                // Parameter statement for function call
                else if(finalString[0].equalsIgnoreCase("param"))
                {
                    opCode = "24";
                    firstOp = getRelativeAddress(finalString[1]);
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                else if(finalString[0].equalsIgnoreCase("goto"))
                {
                    opCode = "23";
                    firstOp = finalString[1];
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                
                // Return statement
                else if(finalString[0].equalsIgnoreCase("ret"))
                {
                    opCode = "22";
                    if(finalString.length > 1)
                    {
                        firstOp = getRelativeAddress(finalString[1]);
                    }
                    
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                
                // Output Statement
                else if(finalString[0].equalsIgnoreCase("out"))
                {
                    opCode = "21";
                    firstOp = getRelativeAddress(finalString[1]);
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                
                // Input Statements
                else if(finalString[0].equalsIgnoreCase("in"))
                {
                    opCode = "20";
                    firstOp = getRelativeAddress(finalString[1]);
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                
                // If and While Statement
                else if(finalString[0].equalsIgnoreCase("if") || finalString[0].equalsIgnoreCase("while"))
                {
                    opCode = getOpcodeForRelationalOperator(finalString[2]);
                    
                    String temp1 = finalString[1].substring(1, finalString[1].length());
                    firstOp = checkAndGetOperandAddress(temp1);
                    
                    String temp2 = finalString[3].substring(0, finalString[3].length() - 1);
                    secondOp = checkAndGetOperandAddress(temp2);
                    
                    thirdOp = finalString[finalString.length - 1];
                    mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                    addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                }
                
                // Assignment and Arithmetic statements
                else if(finalString.length >= 3)
                {
                    if(finalString[1].contentEquals("="))
                    {
                        if(finalString[2].startsWith("\""))
                        {
                            opCode = "5";
                            firstOp = getRelativeAddress(finalString[0]);
                        }
                        else
                        {
                            if(finalString.length > 3)
                            {
                                if(checkArithmeticOperator(finalString[3]))
                                {
                                    opCode = getOpcodeForArithmeticOperator(finalString[3]);
                                    firstOp = checkAndGetOperandAddress(finalString[2]);
                                    secondOp = checkAndGetOperandAddress(finalString[4]);
                                    thirdOp = getRelativeAddress(finalString[0]);
                                }
                            }
                            else
                            {
                               opCode = "5";
                               firstOp = checkAndGetOperandAddress(finalString[2]);
                               secondOp = getRelativeAddress(finalString[0]);
                            }
                        }
                        mcWriter.write(opCode + " " + firstOp + " " + secondOp + " " + thirdOp + "\n");
                        addInMachineCode(opCode, firstOp, secondOp, thirdOp);
                    }
                }
            }
            
            counter++;
        }
        
        mcWriter.close();
    }
    
    //////////////////////////
    //  ------------------  //
    //////////////////////////


    public void startTacToMachineCodeConversion() throws IOException
    {
        FileWriter tempWriter = new FileWriter("function-mapping.txt");
        tempWriter.close();
        
        tacToMCConversion();
//        outputTacOnConsole();
    }
}