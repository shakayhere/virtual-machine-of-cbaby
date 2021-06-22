package VirtualMachine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseAndTranslate
{
    static int relativeAddressNumber = 0;                   // Relative Address number for translator symboltable
    static int n = 1;                                       // Line number for Three Address Code
    static int t = 1;                                       // Number for temp variable generation
    static int arrow = 0;                                   // Used for printing parse tree
    static int lineIndex = 0;                               // Used in parse tree implementation
    static List <Integer> tokensInLine = new ArrayList();   // To check how many tokens are there in 1 line
    static int tokenCounter = 0;                            // For updating 'lineIndex'
    static String fileName;                                 // file/path name entered by user // file/path name entered by user

    public void setFileName(String file)
    {
        ParseAndTranslate.fileName = file;
    }
    
    public void setTokensInLine(List <Integer> tokensList)
    {
        ParseAndTranslate.tokensInLine = tokensList;
    }

    public int getRelativeAddressCount()
    {
        return relativeAddressNumber;
    }
    
    public int getTempVarCount()
    {
        return t;
    }

    public ParseAndTranslate()
    {
        
    }

    ///////////////////////////////////////////////////
    //   Parser Functions (with Three Adress Code)   //
    ///////////////////////////////////////////////////
    
    static boolean checkArgumentsInFunctionCall(String idName) throws FileNotFoundException
    {
        int vdoCount = 0;
        int argCallCount = 0;
        File tempObj = new File("tokens.txt");
        Scanner tempReader = new Scanner(tempObj);
        String tempToken;
        
        while(tempReader.hasNext())
        {
            tempToken = tempReader.nextLine();
            
            if(tempToken.equalsIgnoreCase("(FUNC,^)"))
            {
                tempToken = tempReader.nextLine();
                tempToken = tempReader.nextLine();
                tempToken = tempReader.nextLine();
                if(tempToken.equalsIgnoreCase("(ID,\"" +idName + "\")"))
                {
                    while(!tempToken.contentEquals("(')',^)"))
                    {
                        tempToken = tempReader.nextLine();
                        if(tempToken.contentEquals("(':',^)"))
                        {
                            vdoCount++;
                        }
                   }
                   
                    break;
                }
            }
            
        }
        
        tempReader.close();
        
        tempReader = new Scanner(tempObj);
        for(int i = 0; i <= lineIndex; i++)
        {
            for(int j = 0; j < tokensInLine.get(i); j++)
            {
                tempToken = tempReader.nextLine();
                if(i == lineIndex)
                {
                    if(tempToken.contentEquals("(ID,\"" + idName + "\")"))
                    {
                        while(!tempToken.contentEquals("(')',^)"))
                        {
                            tempToken = tempReader.nextLine();
                            j++;
                            if(tempToken.startsWith("(ID") || tempToken.startsWith("(NUM") || tempToken.startsWith("(LC"))
                            {
                                argCallCount++;
                            }
                        }
                    }
                }
            }
        }
        
        return vdoCount == argCallCount;
    }
    
    static int returnFunctionCallArgumentsCount(String idName) throws FileNotFoundException
    {
        File tempReadObj = new File("tokens.txt");
        Scanner tempReader = new Scanner(tempReadObj);
        String tempToken;
        int argCallCount = 0;
        boolean hasArguments = false;
        for(int i = 0; i <= lineIndex; i++)
        {
            for(int j = 0; j < tokensInLine.get(i); j++)
            {
                tempToken = tempReader.nextLine();
                if(i == lineIndex)
                {
                    if(tempToken.contentEquals("(ID,\"" + idName + "\")"))
                    {
                        while(!tempToken.contentEquals("(')',^)"))
                        {
                            tempToken = tempReader.nextLine();
                            j++;
                            if(!hasArguments)
                            {
                                if(tempToken.startsWith("(ID") || tempToken.startsWith("(NUM") || tempToken.startsWith("(LC"))
                                {
                                    argCallCount++;
                                    hasArguments = true;
                                }
                            }
                            if(hasArguments)
                            {
                                if(tempToken.startsWith("(','"))
                                {
                                    argCallCount++;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return argCallCount;
    }
    
    static void checkReturnInFunction(String idname, String typeName) throws FileNotFoundException
    {
        boolean retFlag = false;
        int inBracket = 0;
        File tempObj = new File("tokens.txt");
        Scanner tempReader = new Scanner(tempObj);
        String tempToken;
        
        if(typeName.equalsIgnoreCase("void") && (!(idname.equalsIgnoreCase("\"main\""))))
        {
            retFlag = true;
        }
        
        while(tempReader.hasNext())
        {
            if(idname.equalsIgnoreCase("\"main\"") && (typeName.equalsIgnoreCase("char") || typeName.equalsIgnoreCase("integer")))
            {
                retFlag = false;
                break;
            }
            
            tempToken = tempReader.nextLine();
            if(tempToken.contentEquals("(FUNC,^)"))
            {
                tempToken = tempReader.nextLine();
                tempToken = tempReader.nextLine();
                tempToken = tempReader.nextLine();
                if(tempToken.contentEquals("(ID," + idname + ")"))
                {
                    while(tempReader.hasNext())
                    {
                        tempToken = tempReader.nextLine();
                        if(tempToken.contentEquals("(RET,^)"))
                        {
                            if(idname.equalsIgnoreCase("\"main\"") && typeName.equalsIgnoreCase("void"))
                            {
                                
                                if(tempReader.hasNext("\\(ID.*") || tempReader.hasNext("\\(NUM.*") || tempReader.hasNext("\\(LC.*"))
                                {
                                    retFlag = false;
                                }
                                else
                                {
                                    retFlag = true;
                                }
                            }
                            else if(tempReader.hasNext("\\(ID.*") || tempReader.hasNext("\\(NUM.*") || tempReader.hasNext("\\(LC.*"))
                            {
                                retFlag = true;
                            }
                        }
                        if (tempToken.contentEquals("('{',^)"))
                        {
                            inBracket++;
                        }
                        else if (tempToken.contentEquals("('}',^)"))
                        {
                            inBracket--;
                            if(inBracket == 0)
                            {
                                break;
                            }
                        }

                    }
                    break;
                }
            }
        }
        
        
        if (retFlag)
        { }
        else
        {
            String errorDetail = "";
            if(idname.equalsIgnoreCase("\"main\"") && (typeName.equalsIgnoreCase("char") || typeName.equalsIgnoreCase("integer")))
            {
                errorDetail = "Return type of main function is invalid. Only use 'void'";
            }
            else if(idname.equalsIgnoreCase("\"main\""))
            {
                errorDetail = "Cannot return value from 'main' function";
            }
            else
            {
                errorDetail = "No return argument in function or invalid assignment of function call";
            }
            String errorMessage = "Syntax error at line " + (lineIndex + 1);
            File tempReadObj = new File(fileName);
            Scanner tempReadFile = new Scanner(tempReadObj);

            String tempSTR = "";
            for (int i = 0; i <= lineIndex; i++)
            {
                tempSTR = tempReadFile.nextLine();
            }
            tempSTR = tempSTR.trim();
            System.out.println(errorMessage + "\n" + (lineIndex + 1) + "\t" + tempSTR + "\n" + errorDetail);
            
            tempReadFile.close();
            System.exit(0);
        }
    }
    
    static void checkFunctionCallArguments(String idname) throws FileNotFoundException
    {
        int funcArg = 0;
        int callArg = 0;
        File tempObj = new File("tokens.txt");
        Scanner tempReader = new Scanner(tempObj);
        boolean hasArguments = false;
        
        for(int i = 0; i <= lineIndex; i++)
        {
            for(int j = 0; j < tokensInLine.get(i); j++)
            {
                String tempToken = tempReader.nextLine();
                if(tempToken.contentEquals("(ID," + idname + ")"))
                {
                    if(i == lineIndex)
                    {
                        while(!tempToken.contains("(')',^)"))
                        {
                            tempToken = tempReader.nextLine();
                            j++;
                            if(j == tokensInLine.get(i))
                            {
                                j = 0;
                                i++;
                            }
                            if(!hasArguments)
                            {
                                if (tempToken.startsWith("(ID") || tempToken.startsWith("(LC") || tempToken.startsWith("(NUM"))
                                {
                                    callArg++;
                                    hasArguments = true;
                                }
                            }
                            if(hasArguments)
                            {
                                if (tempToken.startsWith("(','"))
                                {
                                    callArg++;
                                }
                            }
                        }
                        break;
                    }
                    
                }
                if(tempToken.contentEquals("(FUNC,^)"))
                {
                    tempToken = tempReader.nextLine();
                    tempToken = tempReader.nextLine();
                    tempToken = tempReader.nextLine();
                    j = j + 3;
                    if(tempToken.contentEquals("(ID," + idname + ")"))
                    {
                        while(!tempToken.contains("('{',^)"))
                        {
                            tempToken = tempReader.nextLine();
                            j++;
                            if(j == tokensInLine.get(i))
                            {
                                j = 0;
                                i++;
                            }
                            if (tempToken.contentEquals("(':',^)"))
                            {
                                funcArg++;
                            }
                        }
                    }
                }
            }
        }
        
        tempReader.close();
        
        if(funcArg == callArg)
        { }
        else
        {
            String errorMessage = "Syntax error at line " + (lineIndex + 1);
            File tempReadObj = new File(fileName);
            Scanner tempReadFile = new Scanner(tempReadObj);

            String tempSTR = "";
            for (int i = 0; i <= lineIndex; i++)
            {
                tempSTR = tempReadFile.nextLine();
            }
            tempSTR = tempSTR.trim();
            System.out.println(errorMessage + "\n" + (lineIndex + 1) + "\t" + tempSTR + "\nFunction arguments are invalid");
            
            tempReadFile.close();
            System.exit(0);
        }
    }
    
    static void writeToSymbolTable(String x) throws IOException
    {
        FileWriter tempWriter = new FileWriter("parser-symboltable.txt", true);
        
        tempWriter.append(x);
        tempWriter.append("\n");
        
        tempWriter.close();
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
    
    static boolean checkIDinFunctionScope(String idname) throws IOException
    {
        boolean scopeFlag = false;
        File tempObj = new File("tokens.txt");
        Scanner tempReader = new Scanner(tempObj);
            
        for(int i = 0; i <= lineIndex; i++)
        {
            for(int j = 0; j < tokensInLine.get(i); j++)
            {
                String tempToken = tempReader.nextLine();
                if(tempToken.contentEquals("(FUNC,^)"))
                {
                    while(!tempToken.contains("('{',^)"))
                    {
                        tempToken = tempReader.nextLine();
                        j++;
                        if(j == tokensInLine.get(i))
                        {
                            j = 0;
                            i++;
                        }
                        if (i == lineIndex)
                        {
                            if(tempToken.contentEquals("(ID," + idname + ")"))
                            {
                                scopeFlag = true;
                                break;
                            }
                        }
                    }
//                    if(tempToken.contains("('{',^)"))
//                    {
//                        tempToken = tempReader.nextLine();
//                        j++;
//                        if(j == tokensInLine.get(i))
//                        {
//                            j = 0;
//                            i++;
//                        }
//                    }
                }
                else if(tempToken.contentEquals("(ELIF,^)") || tempToken.contentEquals("(ELSE,^)") || tempToken.contentEquals("(IF,^)") || tempToken.contentEquals("(WHILE,^)"))
                {
                    int bracketCount = 0;
                    while(!tempToken.contains("('}',^)") && !(bracketCount == 0))
                    {
                        tempToken = tempReader.nextLine();
                        j++;
                        if(j == tokensInLine.get(i))
                        {
                            j = 0;
                            i++;
                        }
                        
                        if(tempToken.contains("('{',^)"))
                        {
                            bracketCount++;
                        }
                        if(tempToken.contains("('}',^)"))
                        {
                            bracketCount++;
                        }
                        
                        if (i == lineIndex)
                        {
                            if(tempToken.contentEquals("(ID," + idname + ")"))
                            {
                                scopeFlag = true;
                                break;
                            }
                        }
                    }
//                    if(tempToken.contains("('}',^)"))
//                    {
//                        tempToken = tempReader.nextLine();
//                        j++;
//                        if(j == tokensInLine.get(i))
//                        {
//                            j = 0;
//                            i++;
//                        }
//                    }
                }
                else if(tempToken.contentEquals("('}',^)"))
                {
                    scopeFlag = false;
//                    tempToken = tempReader.nextLine();
//                    j++;
//                    if(j == tokensInLine.get(i))
//                    {
//                        j = 0;
//                        i++;
//                    }
                }
                
                if (i == lineIndex)
                {
                    if(tempToken.contentEquals("(ID," + idname + ")"))
                    {
                        scopeFlag = true;
                        break;
                    }
                }
            }
        }
            
        tempReader.close();
        return scopeFlag;
    }
    
    static int checkIDScope(String idname) throws IOException
    {
        int scopeFlag = 1;
        File tempObj = new File("tokens.txt");
        Scanner tempReader = new Scanner(tempObj);
            
        for(int i = 0; i <= lineIndex; i++)
        {
            for(int j = 0; j < tokensInLine.get(i); j++)
            {
                String tempToken = tempReader.nextLine();
                if(tempToken.contentEquals("(FUNC,^)"))
                {
                    scopeFlag++;
                    while(!tempToken.contains("('{',^)"))
                    {
                        tempToken = tempReader.nextLine();
                        j++;
                        if(j == tokensInLine.get(i))
                        {
                            j = 0;
                            i++;
                        }
                        if (i == lineIndex)
                        {
                            if(tempToken.contentEquals("(ID," + idname + ")"))
                            {
                                break;
                            }
                        }
                    }
                    if(tempToken.contains("('{',^)"))
                    {
                        tempToken = tempReader.nextLine();
                        j++;
                        if(j == tokensInLine.get(i))
                        {
                            j = 0;
                            i++;
                        }
                    }
                }
                else if(tempToken.contentEquals("('{',^)"))
                {
                    scopeFlag++;
                }
                else if(tempToken.contentEquals("('}',^)"))
                {
                    scopeFlag--;
                }
                
                if (i == lineIndex)
                {
                    if(tempToken.contentEquals("(ID," + idname + ")"))
                    {
                        break;
                    }
                }
            }
        }
            
        tempReader.close();
        return scopeFlag;
    }
    
    static void checkFunctionCallID(String idname) throws IOException
    {
        String tempName = idname.substring(1, idname.length() - 1);
        File tempObj = new File("parser-symboltable.txt");
        Scanner tempReader = new Scanner(tempObj);
        boolean checkFlag = false;
        
        while (tempReader.hasNext())
        {
            String getString = tempReader.nextLine();
            
            String[] tempString = getString.split("\t");
            
            if(tempString[0].contentEquals(tempName))
            {
                if(tempString[1].equalsIgnoreCase("func"))
                {
                    checkFlag = true;   
                }
            }   
        }
        tempReader.close();
        
        if (checkFlag)
        { }
        else
        {
            String errorMessage = "Syntax error at line " + (lineIndex + 1);
            File tempReadObj = new File(fileName);
            Scanner tempReadFile = new Scanner(tempReadObj);

            String tempSTR = "";
            for (int i = 0; i <= lineIndex; i++)
            {
                tempSTR = tempReadFile.nextLine();
            }
            tempSTR = tempSTR.trim();
            System.out.println(errorMessage + "\n" + (lineIndex + 1) + "\t" + tempSTR + "\nFunction not defined or out of scope");
            
            tempReadFile.close();
            System.exit(0);
        }
    }
    
    static void checkFunctionIDError(String idname) throws IOException
    {
        String tempName = idname.substring(1, idname.length() - 1);
        File tempObj = new File("parser-symboltable.txt");
        Scanner tempReader = new Scanner(tempObj);
        boolean checkFlag = false;
        
        while (tempReader.hasNext())
        {
            String getString = tempReader.nextLine();
            
            String[] tempString = getString.split("\t");
            
            if(tempString[0].contentEquals(tempName))
            {
                if(tempString[1].equalsIgnoreCase("func"))
                {
                    checkFlag = true;   
                }
            }   
        }
        tempReader.close();
        
        if (!checkFlag)
        { }
        else
        {
            String errorMessage = "Syntax error at line " + (lineIndex + 1);
            File tempReadObj = new File(fileName);
            Scanner tempReadFile = new Scanner(tempReadObj);

            String tempSTR = "";
            for (int i = 0; i <= lineIndex; i++)
            {
                tempSTR = tempReadFile.nextLine();
            }
            tempSTR = tempSTR.trim();
            System.out.println(errorMessage + "\n" + (lineIndex + 1) + "\t" + tempSTR + "\nID already defined as function");
            
            tempReadFile.close();
            System.exit(0);
        }
    }
    
    static void checkIDError(String idname) throws IOException
    {
        String tempName = idname.substring(1, idname.length() - 1);
        File tempObj = new File("parser-symboltable.txt");
        Scanner tempReader = new Scanner(tempObj);
        
        boolean checkFlag = false;
        
        int scope = checkIDScope(idname);
        int countScope = 0;
        int funcScope = 0;
        while (tempReader.hasNext())
        {
            
            String getString = tempReader.nextLine();
            if(getString.equalsIgnoreCase("SCOPE START"))
            {
                countScope++;
            }
            else if(getString.equalsIgnoreCase("SCOPE END"))
            {
                if(funcScope == countScope)
                {
                    funcScope--;
                }
                countScope--;
            }
            String[] tempString = getString.split("\t");
            if(tempString.length > 1)
            {
                if(tempString[1].equalsIgnoreCase("FUNC"))
                {
                    funcScope = countScope;
                }
            }
            if((tempString[0].contentEquals(tempName) && ((scope >= funcScope) && (funcScope > 0))) || (tempString[0].contentEquals(tempName) && (scope == countScope)))
            {
                checkFlag = true;   
            }   
        }
        tempReader.close();
        
        if (checkFlag && checkIDinFunctionScope(idname))
        { }
        else
        {
            String errorMessage = "Syntax error at line " + (lineIndex + 1);
            File tempReadObj = new File(fileName);
            Scanner tempReadFile = new Scanner(tempReadObj);

            String tempSTR = "";
            for (int i = 0; i <= lineIndex; i++)
            {
                tempSTR = tempReadFile.nextLine();
            }
            tempSTR = tempSTR.trim();
            System.out.println(errorMessage + "\n" + (lineIndex + 1) + "\t" + tempSTR + "\nID not defined or out of scope");
            
            tempReadFile.close();
            System.exit(0);
        }
    }
    
    static void updateParserSymbolTable(String idname, String type) throws IOException
    {
        String tempName = idname.substring(1, idname.length() - 1);
        
        File tempObj = new File("parser-symboltable.txt");
        Scanner tempReader = new Scanner(tempObj);
        boolean checkFlag = false;
        int indexFlag = -1;
        int lineNum = 0;
        
        int scope = checkIDScope(idname);
        int countScope = 0;
        int funcScope = 0;
        while (tempReader.hasNext())
        {
            
            String getString = tempReader.nextLine();
            
            if(getString.equalsIgnoreCase("SCOPE START"))
            {
                countScope++;
            }
            else if(getString.equalsIgnoreCase("SCOPE END"))
            {
                if(funcScope == countScope)
                {
                    funcScope--;
                }
                countScope--;
            }
            
            String[] tempString = getString.split("\t");
            
            if(tempString.length > 1)
            {
                if(tempString[1].equalsIgnoreCase("FUNC"))
                {
                    funcScope = countScope;
                }
            }
            
            
            lineNum++;
            
            if((tempString[0].contentEquals(tempName) && ((scope >= funcScope) && (funcScope > 0))) || (tempString[0].contentEquals(tempName) && (scope == countScope)))
            {
                checkFlag = true;
                indexFlag = lineNum - 1;
                if(tempString[1].equalsIgnoreCase("func"))
                {
                    String errorMessage = "Syntax error at line " + (lineIndex + 1);
                    File tempReadObj = new File(fileName);
                    Scanner tempReadFile = new Scanner(tempReadObj);

                    String tempSTR = "";
                    for (int i = 0; i <= lineIndex; i++)
                    {
                        tempSTR = tempReadFile.nextLine();
                    }
                    tempSTR = tempSTR.trim();
                    System.out.println(errorMessage + "\n" + (lineIndex + 1) + "\t" + tempSTR + "\nID already defined as function");

                    tempReadFile.close();
                    System.exit(0);
                }
            }   
        }
        
        if (checkFlag && checkIDinFunctionScope(idname))
        {
            String tempFileName = "parser-symboltable.txt";
            BufferedReader file = new BufferedReader(new FileReader(tempFileName));
            Path path = Paths.get(tempFileName);
            List<String> lines = Files.readAllLines(path);
            lines.set(indexFlag, tempName + "\t" + type);
            Files.write(path, lines);
        }
        else
        {
            writeToSymbolTable(tempName + "\t" + type);
        }
    }
    
    static void submitSyntaxError() throws FileNotFoundException{
        String errorMessage = "Syntax error at line " + (lineIndex + 1);
        File tempObj = new File(fileName);
        Scanner tempReader = new Scanner(tempObj);
        
        String tempString = "";
        for (int i = 0; i <= lineIndex; i++)
        {
            tempString = tempReader.nextLine();
        }
        tempString = tempString.trim();
        System.out.println(errorMessage + "\n" + (lineIndex + 1) + "\t" + tempString + "\nReview your code and try again");
        tempReader.close();
        System.exit(0);
    }
    
    static String getROAttachString(String[] token)
    {
        if(token[1].equalsIgnoreCase("LT"))
        {
            return "LT";
        }
        if(token[1].equalsIgnoreCase("LE"))
        {
            return "LE";
        }
        if(token[1].equalsIgnoreCase("GT"))
        {
            return "GT";
        }
        if(token[1].equalsIgnoreCase("GE"))
        {
            return "GE";
        }
        if(token[1].equalsIgnoreCase("EQ"))
        {
            return "EQ";
        }
        if(token[1].equalsIgnoreCase("NE"))
        {
            return "NE";
        }
        return null;
    }

    static boolean checkThisInLine(String x) throws FileNotFoundException
    {
        boolean asFlag = false;
        File tempObj = new File("tokens.txt");
        Scanner tempReader = new Scanner(tempObj);
            
        for(int i = 0; i <= lineIndex; i++)
        {
            for(int j = 0; j < tokensInLine.get(i); j++)
            {
                String tempToken = tempReader.nextLine();
                if(i == lineIndex)
                {
                    if(tempToken.contentEquals(x))
                    {
                        asFlag = true;
                    }
                }
                
            }
        }
            
        tempReader.close();
        return asFlag;
    }
    
    static void outputTac(String x) throws IOException
    {
        FileWriter tempWriter = new FileWriter("tac.txt", true);
        tempWriter.append(n + "\t" + x);
        tempWriter.append("\n");
        n++;
        tempWriter.close();
    }
    
    static void outputTacOnConsole() throws FileNotFoundException
    {
        File tempObj = new File("tac.txt");
        Scanner temp = new Scanner(tempObj);
        while(temp.hasNext())
        {
            System.out.println(temp.nextLine());
        }
        temp.close();
    }
    
    static void fillGotoPatch(int line, int fill) throws IOException
    {
        String tempFileName = "tac.txt";
        Path path = Paths.get(tempFileName);
        List<String> lines = Files.readAllLines(path);
        String temp = lines.get(line - 1);
        Integer tempFill = fill;
        temp = temp + tempFill.toString();
        lines.set(line - 1, temp);
        Files.write(path, lines);
    }
    
    static void fillFunctionCallPatch(int line, String fill) throws IOException
    {
        String tempFileName = "tac.txt";
        Path path = Paths.get(tempFileName);
        List<String> lines = Files.readAllLines(path);
        String temp = lines.get(line - 1);
        temp = temp + fill;
        lines.set(line - 1, temp);
        Files.write(path, lines);
    }
    
    static void outputParser(FileWriter parserWriter, String attachString) throws IOException
    {
        String arrowForOutput = "";
        for(int i = 1; i <= arrow; i++)
        {
            arrowForOutput += "==>";
        }
        
        String outputString = arrowForOutput + attachString;     // Output string for parser tree
        
        parserWriter.write(outputString);
        parserWriter.write("\n");                                   // Adding new line for readability
        
    }
    
    static String[] extractToken(Scanner newReader) throws FileNotFoundException
    {
        String[] token = new String[2];
        String data = "";
        if(newReader.hasNext())
        {
            data = newReader.nextLine();      // Getting token from file   
        }
        else
        {
                submitSyntaxError();
        }
        // Skipping lines that have no tokens
        while(tokensInLine.get(lineIndex) == 0)
        {
            lineIndex++;
        }
        
        tokenCounter++;
        String tempToken = data.substring(1, data.length() - 1);
           
        // Extracting token info
            
        if(tempToken.startsWith("'"))
        {
            String temp = "" + tempToken.charAt(0);
            for (int i = 1 ; i < tempToken.length() - 1; i++)
            {
                if(tempToken.charAt(i) == '\'')
                {
                    temp = temp + tempToken.charAt(i);
                    temp += '\0';
                    token[0] = "" + temp;
                    break;
                }
                else
                {
                    temp += tempToken.charAt(i);                          
                }   
            }
            token[1] = "" + tempToken.charAt(tempToken.length()- 1);
        }
        else
        {
            String[] tempTokenList = tempToken.split(",");
            token[0] = tempTokenList[0];
            token[1] = tempTokenList[1];
        }
        
        // Updating tokenCounter and lineIndex
        if(tokenCounter > tokensInLine.get(lineIndex))
        {
            tokenCounter = 1;
            lineIndex++;
            while(tokensInLine.get(lineIndex) == 0)
            {
                lineIndex++;
            }
        }
        
        return token;
    }
    
    static void Start(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        
        if(token[0].equalsIgnoreCase("func"))
        {
            attachString = " Function";
            outputParser(parserWriter, attachString);
            Function(token, parserWriter, newReader);
        }
        else if(token[0].equalsIgnoreCase("if") || token[0].equalsIgnoreCase("while") || token[0].equalsIgnoreCase("print") || token[0].equalsIgnoreCase("println") || token[0].equalsIgnoreCase("integer") || token[0].equalsIgnoreCase("char") || token[0].equalsIgnoreCase("SLC") || token[0].equalsIgnoreCase("MLC") || token[0].equalsIgnoreCase("ret") || token[0].equalsIgnoreCase("in") || token[0].equalsIgnoreCase("id"))
        {
            attachString = " forStatement";
            outputParser(parserWriter, attachString);
            forStatement(token, parserWriter, newReader);
        }
        else
        {
            submitSyntaxError();
        }
        
        attachString = " Start";
        outputParser(parserWriter, attachString);
            
        if(newReader.hasNext())
        {
            token = extractToken(newReader);
            Start(token, parserWriter, newReader);
        }
        else
        {
            arrow++;
            attachString = " ^";
            outputParser(parserWriter, attachString);
            arrow--;
        }
        arrow--;
    }
    
    static void forStatement(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString = " Statement";
        outputParser(parserWriter, attachString);
        Statement(token, parserWriter, newReader);
        
        attachString = " forStatement";
        outputParser(parserWriter, attachString);
        
        if(newReader.hasNext(Pattern.quote("(IF,^)")) || newReader.hasNext(Pattern.quote("(WHILE,^)")) || newReader.hasNext(Pattern.quote("(PRINT,^)")) || newReader.hasNext(Pattern.quote("(PRINTLN,^)")) || newReader.hasNext(Pattern.quote("(IN,^)")) || newReader.hasNext(Pattern.quote("(RET,^)")) || newReader.hasNext(Pattern.quote("(INTEGER,^)")) || newReader.hasNext(Pattern.quote("(CHAR,^)")) || newReader.hasNext(Pattern.quote("(SLC,^)")) || newReader.hasNext(Pattern.quote("(MLC,^)")) || newReader.hasNext("\\(ID.*"))
        {
            token = extractToken(newReader);
            forStatement(token, parserWriter, newReader);
        }
        else
        {
            arrow++;
            attachString = " ^";
            outputParser(parserWriter, attachString);
            arrow--;
        }
        arrow--;
    }
    
    static void Statement(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String tacString;
        
        // For IF or WHILE Statement
        if(token[0].equalsIgnoreCase("while") || token[0].equalsIgnoreCase("if"))
        {
            boolean ifFlag = false;
            if (token[0].equalsIgnoreCase("if"))
            {
                ifFlag = true;
            }
            attachString = " " + token[0];
            outputParser(parserWriter, attachString);
            
            tacString = token[0].toLowerCase();
            
            token = extractToken(newReader);
            
            // Optional Bracket
            if(token[0].contains("'('"))
            {
                attachString = " (";
                outputParser(parserWriter, attachString);
                
                token = extractToken(newReader);
            }
            
            attachString = " Condition";
            outputParser(parserWriter, attachString);
            
            tacString = tacString + " (" + Condition(token, parserWriter, newReader) + ") goto ";
            int save1n = n;
            outputTac(tacString);
            
            tacString = "goto ";
            int save2n = n;
            outputTac(tacString);
            
            token = extractToken(newReader);
            
            // Optional Bracket
            if(token[0].contains("')'"))
            {
                attachString = " )";
                outputParser(parserWriter, attachString);
                token = extractToken(newReader);
            }
            
            if(token[0].contains("':'"))
            {
                attachString = " VDO";
                outputParser(parserWriter, attachString);
                VDO(token, parserWriter, newReader);
            }
            else
            {
                submitSyntaxError();
            }
            
            fillGotoPatch(save1n, n);
            
            token = extractToken(newReader);
            if(token[0].contains("'{'"))
            {
                attachString = " {";
                outputParser(parserWriter, attachString);
                writeToSymbolTable("SCOPE START");
            }
            else
            {
                submitSyntaxError();
            }

            token = extractToken(newReader);

            attachString = " forStatement";
            outputParser(parserWriter, attachString);
            forStatement(token, parserWriter, newReader);
            
            tacString = "goto ";
            int save3n = n;
            outputTac(tacString);

            token = extractToken(newReader);
            if(token[0].contains("'}'"))
            {
                attachString = " }";
                outputParser(parserWriter, attachString);
                
                writeToSymbolTable("SCOPE END");
                
            }
            else
            {
                submitSyntaxError();
            }
            
            fillGotoPatch(save2n, n);
            
            if (ifFlag == true)
            {
                
                if(newReader.hasNext(Pattern.quote("(ELIF,^)")) || newReader.hasNext(Pattern.quote("(ELSE,^)")))
                {
                    token = extractToken(newReader);
                }
                attachString = " ElifOrElse";
                outputParser(parserWriter, attachString);
                ElifOrElse(token, parserWriter, newReader);
                
                fillGotoPatch(save3n, n);
            }
            else
            {
                fillGotoPatch(save3n, save1n);
            }
            
        }
        
        // For PRINT and PRINTLN
        else if(token[0].equalsIgnoreCase("print") || token[0].equalsIgnoreCase("println"))
        {
            boolean forLN = false;
            attachString = " " + token[0];
            outputParser(parserWriter, attachString);
            
            if(token[0].equalsIgnoreCase("println"))
            {
                forLN = true;
            }
            token = extractToken(newReader);
            if(token[0].contains("'('"))
            {
                attachString = " (";
                outputParser(parserWriter, attachString);
            }
            else
            {
                submitSyntaxError();
            }
            
            attachString = " OutputOptions";
            outputParser(parserWriter, attachString);
            String returnOutput = OutputOptions(token, parserWriter, newReader); 
            if(returnOutput.length() == 0)
            {
                String tempVar = genTemp();
                
                tacString = tempVar + " = \"\\n\"";
                outputTac(tacString);
                tacString = "out " + tempVar;
                outputTac(tacString);
                
                writeToTranslatorSymbolTable(tempVar, "STRING", "\"\\n\"");
            }
            else
            {
                tacString = "out " + returnOutput;
                outputTac(tacString);
            }
            
            if(returnOutput.length() > 0)
            {
                token = extractToken(newReader);
            }
            if(token[0].contains("')'") || returnOutput.length() == 0)
            {
                attachString = " )";
                outputParser(parserWriter, attachString);
            }
            else
            {
                submitSyntaxError();
            }

            token = extractToken(newReader);
            if(token[0].contains("';'"))
            {
                attachString = " ;";
                outputParser(parserWriter, attachString);

            }
            else
            {
                submitSyntaxError();
            }
            
            if(forLN == true)
            {
                String tempVar = genTemp();
                
                tacString = tempVar + " = \"\\n\"";
                outputTac(tacString);
                tacString = "out " + tempVar;
                outputTac(tacString);
                
                writeToTranslatorSymbolTable(tempVar, "STRING", "\"\\n\"");
            }
        }
        
        // For Input 'In'
        else if(token[0].equalsIgnoreCase("in"))
        {
            attachString = " " + token[0];
            outputParser(parserWriter, attachString);
            
            tacString = "In ";
            
            token = extractToken(newReader);
            if(token[0].contains("IO"))
            {
                attachString = " IO";
                outputParser(parserWriter, attachString);
                IO(token, parserWriter, newReader);
            }
            else
            {
                submitSyntaxError();
            }
            
            token = extractToken(newReader);
            if(token[0].contains("ID"))
            {
                checkFunctionIDError(token[1]);
                checkIDError(token[1]);
                attachString = " ID(" + token[1] + ")";
                outputParser(parserWriter, attachString);
                
                tacString = tacString + token[1].substring(1, token[1].length() - 1);
                outputTac(tacString);
            }
            else
            {
                submitSyntaxError();
            }
            
            token = extractToken(newReader);
            attachString = " InputDelimiter";
            outputParser(parserWriter, attachString);
            InputDelimiter(token, parserWriter, newReader);
            
        }
        
        // Variable or Assignment Statement
        else if(token[0].equalsIgnoreCase("integer") || token[0].equalsIgnoreCase("char"))
        {
            if(checkThisInLine("(AO,^)") == true)
            {
                attachString = " AssignmentStatement";
                outputParser(parserWriter, attachString);
                AssignmentStatement(token, parserWriter, newReader);
            }
            else
            {
                attachString = " Variable";
                outputParser(parserWriter, attachString);
                Variable(token, parserWriter, newReader);
            }

        }
        
        // Return Statement
        else if(token[0].equalsIgnoreCase("ret"))
        {
            attachString = " " + token[0];
            outputParser(parserWriter, attachString);
            
            tacString = "ret ";
            
            token = extractToken(newReader);
            if (token[0].contains("';'"))
            {
                attachString = " ;";
                outputParser(parserWriter, attachString);
                outputTac(tacString);
            }
            else
            {
                attachString = " FCParam";
                outputParser(parserWriter, attachString);
                String temp = FCParam(token, parserWriter, newReader);

                tacString = tacString + temp;
                outputTac(tacString);
                
                token = extractToken(newReader);
                if (token[0].contains("';'"))
                {
                    attachString = " ;";
                    outputParser(parserWriter, attachString);
                }
                else
                {
                    submitSyntaxError();
                }
            }
            
        }
        
        // Function Call or Assignment Statement
        else if(token[0].equalsIgnoreCase("id"))
        {
            if(checkThisInLine("(AO,^)") == true)
            {
                attachString = " AssignmentStatement";
                outputParser(parserWriter, attachString);
                AssignmentStatement(token, parserWriter, newReader);
            }
            else if(newReader.hasNext(Pattern.quote("('(',^)")))
            {
                attachString = " FunctionCall";
                outputParser(parserWriter, attachString);
                FunctionCall(token, parserWriter, newReader);
                
                token = extractToken(newReader);
                if (token[0].contains("';'"))
                {
                    attachString = " ;";
                    outputParser(parserWriter, attachString);
                }
                else
                {
                    submitSyntaxError();
                }
            }
            else
            {
                submitSyntaxError();
            }

        }
        
        // For Single Line Comment
        else if(token[0].equalsIgnoreCase("slc"))
        {
            attachString = " " + token[0];
            outputParser(parserWriter, attachString);
            
        }
        // For Single Line Comment
        else if(token[0].equalsIgnoreCase("mlc"))
        {
            attachString = " " + token[0];
            outputParser(parserWriter, attachString);
            
        }
        else
        {
            submitSyntaxError();
        }
        
        arrow--;
    }
    
    static void ElifOrElse(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String tacString = "";
        if(token[0].equalsIgnoreCase("elif"))
        {
            attachString = " elif";
            outputParser(parserWriter, attachString);
            
            tacString = "if";
            
            token = extractToken(newReader);
            
            // Optional Bracket
            if(token[0].contains("'('"))
            {
                attachString = " (";
                outputParser(parserWriter, attachString);
                
                token = extractToken(newReader);
            }
            
            attachString = " Condition";
            outputParser(parserWriter, attachString);
//            Condition(token, parserWriter, newReader);
            
            tacString = tacString + " (" + Condition(token, parserWriter, newReader) + ") goto ";
            int save1n = n;
            outputTac(tacString);
            
            tacString = "goto ";
            int save2n = n;
            outputTac(tacString);
            
            token = extractToken(newReader);
            
            // Optional Bracket
            if(token[0].contains("')'"))
            {
                attachString = " )";
                outputParser(parserWriter, attachString);
                
                token = extractToken(newReader);
            }
            
            if(token[0].contains("':'"))
            {
                attachString = " VDO";
                outputParser(parserWriter, attachString);
                VDO(token, parserWriter, newReader);
            }
            else
            {
                submitSyntaxError();
            }
            
            fillGotoPatch(save1n, n);
            
            token = extractToken(newReader);
            if(token[0].contains("'{'"))
            {
                attachString = " {";
                outputParser(parserWriter, attachString);
                writeToSymbolTable("SCOPE START");
            }
            else
            {
                submitSyntaxError();
            }

            token = extractToken(newReader);

            attachString = " forStatement";
            outputParser(parserWriter, attachString);
            forStatement(token, parserWriter, newReader);

            tacString = "goto ";
            int save3n = n;
            outputTac(tacString);

            token = extractToken(newReader);
            if(token[0].contains("'}'"))
            {
                attachString = " }";
                outputParser(parserWriter, attachString);
                writeToSymbolTable("SCOPE END");
            }
            else
            {
                submitSyntaxError();
            }
            
            fillGotoPatch(save2n, n);
            
            if(newReader.hasNext(Pattern.quote("(ELIF,^)")) || newReader.hasNext(Pattern.quote("(ELSE,^)")))
            {
                token = extractToken(newReader);
            }
            attachString = " ElifOrElse";
            outputParser(parserWriter, attachString);
            ElifOrElse(token, parserWriter, newReader);
            
            fillGotoPatch(save3n, n);
        }
        else
        {
            attachString = " goElse";
            outputParser(parserWriter, attachString);
            goElse(token, parserWriter, newReader);
        }
        arrow--;
    }
    
    static void goElse(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String tacString = "";
        
        if(token[0].equalsIgnoreCase("else"))
        {
            attachString = " else";
            outputParser(parserWriter, attachString);
            
            token = extractToken(newReader);
            if(token[0].contains("'{'"))
            {
                attachString = " {";
                outputParser(parserWriter, attachString);
                writeToSymbolTable("SCOPE START");
            }
            else
            {
                submitSyntaxError();
            }

            token = extractToken(newReader);

            attachString = " forStatement";
            outputParser(parserWriter, attachString);
            forStatement(token, parserWriter, newReader);

            token = extractToken(newReader);
            if(token[0].contains("'}'"))
            {
                attachString = " }";
                outputParser(parserWriter, attachString);
                writeToSymbolTable("SCOPE END");
            }
            else
            {
                submitSyntaxError();
            }
            
        }
        else
        {
            attachString = " ^";
            outputParser(parserWriter, attachString);
        }
        arrow--;
    }
    
    static void ReOp(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        attachString = "RO(" + getROAttachString(token) + ")";
        outputParser(parserWriter, attachString);
        arrow--;
    }
    
    static String Condition(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String getString;
        String tempValue = "";
        if(token[0].contentEquals("ID") || token[0].contentEquals("NUM") || token[0].contentEquals("LC"))
        {
            attachString = " Expression";
            outputParser(parserWriter, attachString);
            tempValue = Expression(token, parserWriter, newReader);
        }
        else if(token[0].equalsIgnoreCase("true") || token[0].equalsIgnoreCase("false") || token[1].contentEquals("\"1\"") || token[1].contentEquals("\"0\""))
        {
            attachString = " Boolean";
            outputParser(parserWriter, attachString);
            tempValue = Boolean(token, parserWriter, newReader);
            arrow--;
            return tempValue;
        }
        else
        {
            submitSyntaxError();
        }
        
        getString = tempValue;
        
        token = extractToken(newReader);
        if(token[0].equalsIgnoreCase("RO") && (newReader.hasNext(Pattern.quote("(TRUE,^)")) || newReader.hasNext(Pattern.quote("(FALSE,^)"))))
        {
            attachString = " RO(" + getROAttachString(token) + ")";
            outputParser(parserWriter, attachString);
            
            getString = getString + " " + getROAttachString(token);
            
            token = extractToken(newReader);
            attachString = " Boolean";
            outputParser(parserWriter, attachString);
            tempValue = Boolean(token, parserWriter, newReader);
            
            getString = getString + " " + tempValue;
        }
        else if(token[0].equalsIgnoreCase("RO"))
        {
            attachString = " ReOp";
            outputParser(parserWriter, attachString);
            ReOp(token, parserWriter, newReader);
            
            getString = getString + " " + getROAttachString(token);
            
            token = extractToken(newReader);
            if(token[0].contentEquals("ID") || token[0].contentEquals("NUM") || token[0].contentEquals("LC"))
            {
                attachString = " Expression";
                outputParser(parserWriter, attachString);
                tempValue = Expression(token, parserWriter, newReader);
                getString = getString + " " + tempValue;
            }
            else
            {
                submitSyntaxError();
            }
        }
        else
        {
            submitSyntaxError();
        }
        arrow--;
        return getString;
    }
    
    static String Boolean(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String getValue = "";
        if(token[0].equalsIgnoreCase("true"))
        {
            attachString = " true";
            outputParser(parserWriter, attachString);
            getValue = "1";
        }
           
        else if (token[0].equalsIgnoreCase("false"))
        {
            attachString = " false";
            outputParser(parserWriter, attachString);
            getValue = "0";
        }
        
        else if(token[1].contentEquals("\"1\""))
        {
            attachString = " 1";
            outputParser(parserWriter, attachString);
            getValue = "1";
        }
        
        else if(token[1].contentEquals("\"0\""))
        {
            attachString = " 0";
            outputParser(parserWriter, attachString);
            getValue = "0";
        }
        else
        {
            submitSyntaxError();
        }
        arrow--;
        return getValue;
    }
    
    
    static void Function(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String tacString;
        String attachString = " func";
        outputParser(parserWriter, attachString);
        
        writeToSymbolTable("SCOPE START");
                
        
        token = extractToken(newReader);
        if(token[0].equalsIgnoreCase("integer") || token[0].equalsIgnoreCase("char"))
        {
            attachString = " TD";
            outputParser(parserWriter, attachString);
            TD(token, parserWriter, newReader);
        }
        else if(token[0].equalsIgnoreCase("void"))
        {
            attachString = " void";
            outputParser(parserWriter, attachString);
        }
        else
        {
            submitSyntaxError();
        }
        
        String typeName = token[0];
        
        token = extractToken(newReader);
        if(token[0].contains("':'"))
        {
            attachString = " VDO";
            outputParser(parserWriter, attachString);
            VDO(token, parserWriter, newReader);
        }
        else
        {
            submitSyntaxError();
        }
        
        token = extractToken(newReader);
        if (token[0].equalsIgnoreCase("ID"))
        {
            updateParserSymbolTable(token[1], "FUNC");
            checkReturnInFunction(token[1], typeName);
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            
            tacString = token[1].substring(1, token[1].length() - 1) + ":";
            outputTac(tacString);
        }
        else
        {
            submitSyntaxError();
        }
        
        token = extractToken(newReader);
        if(token[0].contains("'('"))
        {
            attachString = " (";
            outputParser(parserWriter, attachString);
        }
        else
        {
            submitSyntaxError();
        }
        
        attachString = " forParam";
        outputParser(parserWriter, attachString);
        forParam(token, parserWriter, newReader);
        
        token = extractToken(newReader);
        if(token[0].contains("')'"))
        {
            attachString = " )";
            outputParser(parserWriter, attachString);
        }
        else
        {
            submitSyntaxError();
        }
        
        token = extractToken(newReader);
        if(token[0].contains("'{'"))
        {
            attachString = " {";
            outputParser(parserWriter, attachString);
        }
        else
        {
            submitSyntaxError();
        }
        
        token = extractToken(newReader);
        
        attachString = " forStatement";
        outputParser(parserWriter, attachString);
        forStatement(token, parserWriter, newReader);
        
        
        token = extractToken(newReader);
        if(token[0].contains("'}'"))
        {
            attachString = " }";
            outputParser(parserWriter, attachString);
            
            writeToSymbolTable("SCOPE END");
            
//            if(newReader.hasNext())
//            {
//                outputTac("");
//            }
        }
        else
        {
            submitSyntaxError();
        }
        arrow--;
    }
    
    static void forParam(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        if(newReader.hasNext(Pattern.quote("(')',^)")))
        {
            attachString = " ^";
            outputParser(parserWriter, attachString);   
        }
        else
        {
            token = extractToken(newReader);
            attachString = " sendParam";
            outputParser(parserWriter, attachString);
            sendParam(token, parserWriter, newReader);
        }
        arrow--;
    }
    
    static void sendParam(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString = " Param";
        outputParser(parserWriter, attachString);
        Param(token, parserWriter, newReader);
        
        attachString = " nextParam";
        outputParser(parserWriter, attachString);
        nextParam(token, parserWriter, newReader);
        
        arrow--;
    }
    
    static void nextParam(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        if(newReader.hasNext(Pattern.quote("(',',^)")))
        {
            token = extractToken(newReader);
            attachString = " ,";
            outputParser(parserWriter, attachString);
            
            token = extractToken(newReader);
            attachString = " Param";
            outputParser(parserWriter, attachString);
            Param(token, parserWriter, newReader);
        
            attachString = " nextParam";
            outputParser(parserWriter, attachString);
            nextParam(token, parserWriter, newReader);
        }
        else
        {
            attachString = " ^";
            outputParser(parserWriter, attachString);
        }
        
        arrow--;
    }
    
    static String Param(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String getValue = "";
        String tacString = "";
        
        String attachString = " TD";
        outputParser(parserWriter, attachString);
        TD(token, parserWriter, newReader);
        String dataType = token[0];
        
        tacString = tacString + dataType.toLowerCase() + " ";
        
        token = extractToken(newReader);
        if(token[0].contains("':'"))
        {
            attachString = " VDO";
            outputParser(parserWriter, attachString);
            VDO(token, parserWriter, newReader);
        }
        else
        {
            submitSyntaxError();
        }
           
        token = extractToken(newReader);
        if (token[0].equalsIgnoreCase("ID"))
        {
            checkFunctionIDError(token[1]);
            updateParserSymbolTable(token[1], dataType);
            
            String tacVarString = token[1].substring(1,token[1].length() - 1);
            writeToTranslatorSymbolTable(tacVarString, dataType, "");
            
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1].substring(1, token[1].length() - 1);
            
            tacString = tacString + getValue;
            outputTac(tacString);
        }
        else
        {
            submitSyntaxError();
        }
        arrow--;
        
        return getValue;
    }
    
    static String FunctionCall(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String tacString = "";
        String getValueOfID = "";
        if(token[0].equalsIgnoreCase("ID"))
        {
            checkFunctionCallID(token[1]);
            checkFunctionCallArguments(token[1]);
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValueOfID = token[1];
        }
        else
        {
            submitSyntaxError();
        }
        
        String tempName = token[1].substring(1, token[1].length() - 1);
        int argCount = returnFunctionCallArgumentsCount(token[1].substring(1, token[1].length() - 1));
        
        token = extractToken(newReader);
        if(token[0].contains("'('"))
        {
            attachString = " (";
            outputParser(parserWriter, attachString);
        }
        else
        {
            submitSyntaxError();
        }
        
        
        
        attachString = " forFCParam";
        outputParser(parserWriter, attachString);
        String temp = forFCParam(token, parserWriter, newReader);
        
        if(argCount > 0)
        {
            String[] argTemp = temp.split(",");
            for (String i : argTemp)
            {
                tacString = "param " + i;
                outputTac(tacString);
            }
            
        }
        tacString = "call " + tempName + " " + argCount;
        outputTac(tacString);
        
        token = extractToken(newReader);
        if(token[0].contains("')'"))
        {
            attachString = " )";
            outputParser(parserWriter, attachString);
        }
        else
        {
            submitSyntaxError();
        }
        arrow--;
        
        return getValueOfID;
    }
    
    static void AssignmentStatement(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String tacString;
        
        String attachString;
        if (token[0].equalsIgnoreCase("ID"))
        {
            checkIDError(token[1]);
            tacString = token[1].substring(1, token[1].length() - 1);
            
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            
            String tempName = " " + token[1].substring(1, token[1].length() - 1);
            
            token = extractToken(newReader);
            if (token[0].equalsIgnoreCase("AO"))
            {
                attachString = " AO";
                outputParser(parserWriter, attachString);
                AO(token, parserWriter, newReader);
                
                token = extractToken(newReader);
                attachString = " SelectOption";
                outputParser(parserWriter, attachString);
                
                String temp = SelectOption(token, parserWriter, newReader);
                if(temp.startsWith("funcCall"))
                {
                    temp = temp.substring(8, temp.length());
                    checkReturnInFunction(temp, "");
                    fillFunctionCallPatch(n-1, tempName);        
                }
                else
                {
                    tacString = tacString + " = " + temp;
                    outputTac(tacString);
                }
                
                token = extractToken(newReader);
                if (token[0].contains("';'"))
                {
                    attachString = " ;";
                    outputParser(parserWriter, attachString);
                }
                else
                {
                    submitSyntaxError();
                }
            
            }
            else
            {
                submitSyntaxError();
            }
            
        }
        
        else if (token[0].equalsIgnoreCase("integer") || token[0].equalsIgnoreCase("char"))
        {
            String dataType = token[0].toLowerCase();
            attachString = " Param";
            outputParser(parserWriter, attachString);
            tacString = Param(token, parserWriter, newReader); 
            String tempName = " " + tacString;
            token = extractToken(newReader);
            if (token[0].equalsIgnoreCase("AO"))
            {
                attachString = " AO";
                outputParser(parserWriter, attachString);
                AO(token, parserWriter, newReader);
                
                token = extractToken(newReader);
                attachString = " SelectOption";
                outputParser(parserWriter, attachString);
                
                String temp = SelectOption(token, parserWriter, newReader);
                
                if(temp.startsWith("funcCall"))
                {
                    temp = temp.substring(8, temp.length());
                    checkReturnInFunction(temp, dataType);
                    fillFunctionCallPatch(n-1, tempName);
                                        
                }
                else
                {
                    tacString = tacString + " = " + temp;
                    outputTac(tacString);
                }
                token = extractToken(newReader);
                if (token[0].contains("';'"))
                {
                    attachString = " ;";
                    outputParser(parserWriter, attachString);
                }
                else
                {
                    submitSyntaxError();
                }
            
            }
            else
            {
                submitSyntaxError();
            }
        }
        else
        {
            submitSyntaxError();
        }
        arrow--;
    }
    
    static String SelectOption(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String getValue = "";
        String attachString;
        if(newReader.hasNext(Pattern.quote("('(',^)")))
        {
            attachString = " FunctionCall";
            outputParser(parserWriter, attachString);
            getValue = "funcCall" + FunctionCall(token, parserWriter, newReader);
            
        }
        else
        {
            attachString = " FCParam";
            outputParser(parserWriter, attachString);
            getValue = FCParam(token, parserWriter, newReader);
        }
        
        arrow--;
        
        return getValue;
    }
    
    static String forFCParam(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String getValue = "";
        if(newReader.hasNext(Pattern.quote("(')',^)")))
        {
            attachString = " ^";
            outputParser(parserWriter, attachString);   
        }
        else
        {
            token = extractToken(newReader);
            attachString = " sendFCParam";
            outputParser(parserWriter, attachString);
            getValue = sendFCParam(token, parserWriter, newReader);
        }
        arrow--;
        return getValue;
    }
    
    static String sendFCParam(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String getValue = "";
        String attachString = " FCParam";
        outputParser(parserWriter, attachString);
        getValue = FCParam(token, parserWriter, newReader);
        
        attachString = " nextFCParam";
        outputParser(parserWriter, attachString);
        if(newReader.hasNext(Pattern.quote("(',',^)")))
        {
            getValue = getValue + "," + nextFCParam(token, parserWriter, newReader);
        }
        else
        {
            nextFCParam(token, parserWriter, newReader);
        }
        
        
        arrow--;
        return getValue;
    }
    
    static String nextFCParam(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String getValue = "";
        if(newReader.hasNext(Pattern.quote("(',',^)")))
        {
            token = extractToken(newReader);
            attachString = " ,";
            outputParser(parserWriter, attachString);
            
            token = extractToken(newReader);
            attachString = " FCParam";
            outputParser(parserWriter, attachString);
            getValue = FCParam(token, parserWriter, newReader);
        
            attachString = " nextFCParam";
            outputParser(parserWriter, attachString);
            nextFCParam(token, parserWriter, newReader);
        }
        else
        {
            attachString = " ^";
            outputParser(parserWriter, attachString);
        }
        
        arrow--;
        return getValue;
    }
    
    static String FCParam(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String getValue = "";
        String attachString;
        
        if(newReader.hasNext(Pattern.quote("('+',^)")) || newReader.hasNext(Pattern.quote("('-',^)")) || newReader.hasNext(Pattern.quote("('*',^)")) || newReader.hasNext(Pattern.quote("('/',^)")))
        {
            attachString = " Expression";
            outputParser(parserWriter, attachString);
            getValue = Expression(token, parserWriter, newReader);
        }
        
        else if(token[0].contentEquals("ID"))
        {
            checkIDError(token[1]);
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1].substring(1, token[1].length() - 1);
        }
        else if(token[0].contentEquals("NUM"))
        {
            attachString = " NUM(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1];
        }
        else if(token[0].contentEquals("LC"))
        {
            attachString = " LC(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1];
        }
        else
        {
            submitSyntaxError();
        }
        arrow--;
        return getValue;
    }
    
    static void AO(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString = " :=";
        outputParser(parserWriter, attachString);
        arrow--;
    }
    
    static void IO(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString = " >>";
        outputParser(parserWriter, attachString);
        arrow--;
    }
    
    static void InputDelimiter(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        if (token[0].contains("';'"))
        {
            attachString = " ;";
            outputParser(parserWriter, attachString);
        }
        else if (token[0].contains("','"))
        {
            attachString = " ,";
            outputParser(parserWriter, attachString);
            
            token = extractToken(newReader);
            attachString = " nextInput";
            outputParser(parserWriter, attachString);
            nextInput(token, parserWriter, newReader);
        }
        else
        {
            submitSyntaxError();
        }
        
        arrow--;
    }
    
    static void nextInput(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String tacString = "In ";
        if (token[0].equalsIgnoreCase("ID"))
        {
            checkFunctionIDError(token[1]);
            checkIDError(token[1]);
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            tacString = tacString + token[1].substring(1, token[1].length() - 1);
            outputTac(tacString);
        }
        else
        {
            submitSyntaxError();
        }
        
        token = extractToken(newReader);
        attachString = " InputDelimiter";
        outputParser(parserWriter, attachString);
        InputDelimiter(token, parserWriter, newReader);
        
        arrow--;
    }
    
    static void TD(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        if (token[0].equalsIgnoreCase("integer"))
        {
            attachString = " Integer";
            outputParser(parserWriter, attachString);
        }
        else if (token[0].equalsIgnoreCase("char"))
        {
            attachString = " char";
            outputParser(parserWriter, attachString);
        }
        else
        {
            submitSyntaxError();
        }
        
        arrow--;
    }
    
    static void VDO(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString = " :";
        outputParser(parserWriter, attachString);
        arrow--;
    }
    
    static void Variable(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String tacString = "";
        String attachString = " TD";
        outputParser(parserWriter, attachString);
        TD(token, parserWriter, newReader);
        String typeName = token[0];
        
        token = extractToken(newReader);
        if(token[0].contains("':'"))
        {
            attachString = " VDO";
            outputParser(parserWriter, attachString);
            VDO(token, parserWriter, newReader);
        }
        else
        {
            submitSyntaxError();
        }
           
        token = extractToken(newReader);
        if (token[0].equalsIgnoreCase("ID"))
        {
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            updateParserSymbolTable(token[1], typeName);
             
            String tacVarString = token[1].substring(1,token[1].length() - 1);
            writeToTranslatorSymbolTable(tacVarString, typeName, "");
            
            tacString = typeName.toLowerCase() + " " + tacVarString;
            outputTac(tacString);
        }
        else
        {
            submitSyntaxError();
        }
            
        token = extractToken(newReader);
        attachString = " VariableDelimiter";
        outputParser(parserWriter, attachString);
        VariableDelimiter(token, parserWriter, newReader, typeName);
        
        arrow--;
    }
    
    static void VariableDelimiter(String[] token, FileWriter parserWriter, Scanner newReader, String typeName) throws IOException
    {
        arrow++;
        String attachString;
        if (token[0].contains("';'"))
        {
            attachString = " ;";
            outputParser(parserWriter, attachString);
        }
        else if (token[0].contains("','"))
        {
            attachString = " ,";
            outputParser(parserWriter, attachString);
            token = extractToken(newReader);
            attachString = " nextVariable";
            outputParser(parserWriter, attachString);
            nextVariable(token, parserWriter, newReader, typeName);
        }
        else
        {
            submitSyntaxError();
        }
        
        arrow--;
    }
    
    static void nextVariable(String[] token, FileWriter parserWriter, Scanner newReader, String typeName) throws IOException
    {
        arrow++;
        String tacString;
        String attachString;
        if (token[0].equalsIgnoreCase("ID"))
        {
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            updateParserSymbolTable(token[1], typeName);
            
            String tacVarString = token[1].substring(1,token[1].length() - 1);
            writeToTranslatorSymbolTable(tacVarString, typeName, "");
            
            tacString = typeName.toLowerCase() + " " + tacVarString;
            outputTac(tacString);
            
            token = extractToken(newReader);
            attachString = " VariableDelimiter";
            outputParser(parserWriter, attachString);
            VariableDelimiter(token, parserWriter, newReader, typeName);
        }
        else if(token[0].equalsIgnoreCase("integer") || token[0].equalsIgnoreCase("char"))
        {
            attachString = " Variable";
            outputParser(parserWriter, attachString);
            Variable(token, parserWriter, newReader);
        }
        else
        {
            submitSyntaxError();
        }
        
        
        arrow--;
    }
    
    static String OutputOptions(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        
        arrow++;
        String attachString;
        String getValue = "";
        token = extractToken(newReader);
        
        // STR check
        if(token[0].equalsIgnoreCase("STR"))
        {
            attachString = " STR";
            outputParser(parserWriter, attachString);
            
            String tempVar = genTemp();
            String tacString = tempVar + " = " + getSTR();
            outputTac(tacString);
            
            writeToTranslatorSymbolTable(tempVar, "STRING", getSTR());
            
            getValue = tempVar;
        }
        
        
        // LC check
        else if (token[0].equalsIgnoreCase("LC"))
        {
            attachString = " LC(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1];
            if(newReader.hasNext(Pattern.quote("('+',^)")) || newReader.hasNext(Pattern.quote("('-',^)")) || newReader.hasNext(Pattern.quote("('*',^)")) || newReader.hasNext(Pattern.quote("('/',^)")))
            {    
                attachString = " Expression";
                outputParser(parserWriter, attachString);
                getValue = Expression(token, parserWriter, newReader);   
            }
            
        }
        
        // ID check
        else if (token[0].equalsIgnoreCase("ID"))
        {
            if (newReader.hasNext(Pattern.quote("('+',^)")) || newReader.hasNext(Pattern.quote("('-',^)")) || newReader.hasNext(Pattern.quote("('*',^)")) || newReader.hasNext(Pattern.quote("('/',^)")))
            {
                attachString = " Expression";
                outputParser(parserWriter, attachString);
                getValue = Expression(token, parserWriter, newReader);
                
            }
            else
            {
                checkIDError(token[1]);
                attachString = " ID(" + token[1] + ")";
                outputParser(parserWriter, attachString);
                getValue = token[1].substring(1, token[1].length() - 1);
            }
            
        }
        
        // NC check
        else if (token[0].equalsIgnoreCase("NUM"))
        {
            if (newReader.hasNext(Pattern.quote("('+',^)")) || newReader.hasNext(Pattern.quote("('-',^)")) || newReader.hasNext(Pattern.quote("('*',^)")) || newReader.hasNext(Pattern.quote("('/',^)")))
            {
                attachString = " Expression";
                outputParser(parserWriter, attachString);
                getValue = Expression(token, parserWriter, newReader);
                
                
            }
            else
            {
                attachString = " NUM(" + token[1] + ")";
                outputParser(parserWriter, attachString);
                getValue = token[1];
            }
            
        }
        
        else
        {
            attachString = " ^";
            outputParser(parserWriter, attachString);
        }
        
        arrow--;
        
        return getValue;
    }
    
    static String getSTR() throws IOException
    {
        File tempObj = new File(fileName);
        Scanner tempReader = new Scanner(tempObj);
        String temp = "";
        
        String getValue = "";
        for(int i = 0; i <= lineIndex; i++)
        {
            temp = tempReader.nextLine();
        }
        
        String newTemp = temp.trim();
        
        boolean strFlag = false;
        for(int i = 0; i < newTemp.length(); i++)
        {
            if(strFlag)
            {
                getValue = getValue + Character.toString(newTemp.charAt(i));
                if(newTemp.charAt(i) == '"')
                {
                    if(!(newTemp.charAt(i-1) == '\\'))
                    {
                        strFlag = false;
                    }
                }
            }
            
            else if(newTemp.charAt(i) == '"')
            {
                getValue = getValue + Character.toString(newTemp.charAt(i));
                strFlag = true;
            }
            
            
        }
        
        return getValue;
    }
    
    static String Operand(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String attachString;
        String getValue = "";
        if (token[0].equalsIgnoreCase("ID"))
        {
            checkIDError(token[1]);
            attachString = " ID(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1].substring(1, token[1].length() - 1);
            
        }
        else if (token[0].equalsIgnoreCase("NUM"))
        {
            attachString = " NUM(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1];
        }
        else if (token[0].equalsIgnoreCase("LC"))
        {
            attachString = " LC(" + token[1] + ")";
            outputParser(parserWriter, attachString);
            getValue = token[1];
        }
        else
        {
            submitSyntaxError();
        }
        
        arrow--;
        
        return getValue;
    }
    
    static String addOperatorPart1(String[] token, FileWriter parserWriter, Scanner newReader, String tacV) throws IOException
    {
        arrow++;
        String tacTemp = genTemp();
        
        String typeName1 = getDataTypeForGenTemp();
        String typeName2 = checkAndGetDataTypeOfFunction();
        if(typeName1.length() > 1)
        {
            writeToTranslatorSymbolTable(tacTemp, typeName1, "");
        }
        else if(typeName2.length() > 1)
        {
            writeToTranslatorSymbolTable(tacTemp, typeName2, "");
        }
        else
        {
            writeToTranslatorSymbolTable(tacTemp, checkTacVDataType(tacV), "");
        }
        
        
        String getValue = tacTemp;
        String tacString = tacTemp + " = " + tacV;
        String attachString;
        if(token[0].contains("'+'"))
        {
            attachString = " +";
            outputParser(parserWriter, attachString);
            tacString = tacString + " + ";
        }
        else if(token[0].contains("'-'"))
        {
            attachString = " -";
            outputParser(parserWriter, attachString);
            tacString = tacString + " - ";
        }
        else
        {
            submitSyntaxError();
        }
        
        
        attachString = " addOperand";
        outputParser(parserWriter, attachString);
        token = extractToken(newReader);
        
        String temp = addOperand(token, parserWriter, newReader);
        tacString = tacString + temp;
        
        outputTac(tacString);
        
        attachString = " addOperatorPart1";
        outputParser(parserWriter, attachString);
        
        if (newReader.hasNext(Pattern.quote("('+',^)")) || newReader.hasNext(Pattern.quote("('-',^)")))
        { 
            token = extractToken(newReader);
            getValue = addOperatorPart1(token, parserWriter, newReader, tacTemp);
        }
        else
        {
            arrow++;
            attachString = " ^";
            outputParser(parserWriter, attachString);
            arrow--;
        }
        arrow--;
        
        return getValue;
    }
    
    static String addOperatorPart2(String[] token, FileWriter parserWriter, Scanner newReader, String tacV) throws IOException
    {
        arrow++;
        String tacTemp = genTemp();
        
        String typeName1 = getDataTypeForGenTemp();
        String typeName2 = checkAndGetDataTypeOfFunction();
        if(typeName1.length() > 1)
        {
            writeToTranslatorSymbolTable(tacTemp, typeName1, "");
        }
        else if(typeName2.length() > 1)
        {
            writeToTranslatorSymbolTable(tacTemp, typeName2, "");
        }
        else
        {
            writeToTranslatorSymbolTable(tacTemp, checkTacVDataType(tacV), "");
        }
        
        String getValue = tacTemp;
        String tacString = tacTemp + " = " + tacV;
        String attachString;
        if(token[0].contains("'*'"))
        {
            attachString = " *";
            outputParser(parserWriter, attachString);
            tacString = tacString + " * ";
        }
        else if(token[0].contains("'/'"))
        {
            attachString = " /";
            outputParser(parserWriter, attachString);
            tacString = tacString + " / ";
        }
        else
        {
            submitSyntaxError();
        }
        
        attachString = " Operand";
        outputParser(parserWriter, attachString);
        token = extractToken(newReader);
        String temp = Operand(token, parserWriter, newReader);
        
        tacString = tacString + temp;
        outputTac(tacString);
        
        attachString = " addOperatorPart2";
        outputParser(parserWriter, attachString);
        
        if (newReader.hasNext(Pattern.quote("('*',^)")) || newReader.hasNext(Pattern.quote("('/',^)")))
        { 
            token = extractToken(newReader);
            getValue = addOperatorPart2(token, parserWriter, newReader, tacTemp);
        }
        else
        {
            arrow++;
            attachString = " ^";
            outputParser(parserWriter, attachString);
            arrow--;
        }
        
        arrow--;
        return getValue;
    }
    
    static String addOperand(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String getValue = "";
        
        String attachString = " Operand";
        outputParser(parserWriter, attachString);
        
        String newTemp = Operand(token, parserWriter, newReader);
        
        attachString = " addOperatorPart2";
        outputParser(parserWriter, attachString);
        
        if (newReader.hasNext(Pattern.quote("('*',^)")) || newReader.hasNext(Pattern.quote("('/',^)")))
        { 
            token = extractToken(newReader);
            getValue = addOperatorPart2(token, parserWriter, newReader, newTemp);
        }
        else
        {
            arrow++;
            attachString = " ^";
            outputParser(parserWriter, attachString);
            arrow--;
            getValue = newTemp;
        }
        arrow--;
        
        return getValue;
    }
    
    static String Expression(String[] token, FileWriter parserWriter, Scanner newReader) throws IOException
    {
        arrow++;
        String getValue = "";
        
        String attachString = " addOperand";
        outputParser(parserWriter, attachString);
        
        String newTemp = addOperand(token, parserWriter, newReader);
        
        attachString = " addOperatorPart1";
        outputParser(parserWriter, attachString);
        
        if (newReader.hasNext(Pattern.quote("('+',^)")) || newReader.hasNext(Pattern.quote("('-',^)")))
        {
            token = extractToken(newReader);
            getValue = addOperatorPart1(token, parserWriter, newReader, newTemp);
        }
        else
        {
            arrow++;
            attachString = " ^";
            outputParser(parserWriter, attachString);
            arrow--;
            getValue = newTemp;
        }
        
        arrow--;
        
        return getValue;
    }
    
    static String genTemp()
    {
        String temp = "t" + t;
        t++;
        return temp;
    }
    
    static String getDataTypeForGenTemp() throws FileNotFoundException
    {
        File tempObj = new File(fileName);
        Scanner tempReader = new Scanner(tempObj);
        String tempLine = "";
        
        String getDataType = "";
        for(int i = 0; i <= lineIndex; i++)
        {
            tempLine = tempReader.nextLine();
        }
        
        tempLine = tempLine.trim();
        String[] extractVar = tempLine.split("(?=:=)");
        extractVar[0] = extractVar[0].trim();
        tempReader.close();
        
        tempObj = new File("translator-Symboltable.txt");
        tempReader = new Scanner(tempObj);
        
        while(tempReader.hasNext())
        {
            String getLine = tempReader.nextLine();
            String[] newTempLine = getLine.split("\t");
            
            if(newTempLine[0].contains(extractVar[0]))
            {
                getDataType = newTempLine[1];
            }
        }
        
        return getDataType;
    }
    
    static String checkAndGetDataTypeOfFunction() throws FileNotFoundException, IOException
    {
        int tempLineIndex = lineIndex;
        String tempLine = "";
        
        String getDataType = "";
        boolean cont = false;
        
        String tempFileName = fileName;
        Path path = Paths.get(tempFileName);
        List<String> lines = Files.readAllLines(path);
        
        for(int i = tempLineIndex; i >= 0; i--)
        {
            tempLine = lines.get(i);
            tempLine = tempLine.trim();
            if(tempLine.startsWith("ret"))
            {
                cont = true;
            }
            if(tempLine.startsWith("func "))
            {
                break;
            }
        }
        
        if(cont)
        {
            tempLine = tempLine.substring(5, tempLine.length() - 1);
            if(tempLine.startsWith("void"))
            { }
            else
            {
                String[] extractDataType = tempLine.split("(?=:)");
                getDataType = extractDataType[0];
            }
        }
        return getDataType.toUpperCase();
    }
    
    static String checkTacVDataType(String tacV) throws FileNotFoundException
    {
        String getDataType = "";
        if(tacV.charAt(0) >= '0' && tacV.charAt(0) <= '9')
        {
            getDataType = "INTEGER";
        }
        else if(tacV.startsWith("'") && tacV.endsWith("'"))
        {
            getDataType = "CHAR";
        }
        else
        {
            File tempObj = new File("translator-Symboltable.txt");
            Scanner tempReader = new Scanner(tempObj);

            while(tempReader.hasNext())
            {
                String getLine = tempReader.nextLine();
                String[] newTempLine = getLine.split("\t");

                if(newTempLine[0].contains(tacV))
                {
                    getDataType = newTempLine[1];
                }
            }
        }
        return getDataType;
    }
    
    //////////////////////////
    //  ------------------  //
    //////////////////////////
    

    public void startParseAndTranslate() throws IOException
    {
        File newObj = new File("tokens.txt");
            
            
        FileWriter parserWriter = new FileWriter("parsetree.txt");      // Creates parser.txt
        FileWriter tempWriter = new FileWriter("parser-symboltable.txt");
        tempWriter.close();
            
        tempWriter = new FileWriter("tac.txt");
        tempWriter.close();
            
        tempWriter = new FileWriter("translator-symboltable.txt");
        tempWriter.close();
            
            
        Scanner newReader = new Scanner(newObj);      // Scanner object for reading file
            
            
        String[] token; // Token variable
                
        token = extractToken(newReader);
            
        parserWriter.write("Start");     // Adding new line for readability
        parserWriter.write("\n");
            

        writeToSymbolTable("SCOPE START");
            
        Start(token, parserWriter, newReader);
            
        writeToSymbolTable("SCOPE END");
            
            
        parserWriter.close();
            
        newReader.close();   
        System.out.println("Parser successfully generated. Check textfile!");
        System.out.println("Three-Address Code successfully generated. Check textfile!");

    }
}