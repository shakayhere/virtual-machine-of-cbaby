package VirtualMachine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImplementLex {
    
    static String fileName;                                             // File name entered by user
    static List <Integer> tokensInLine = new ArrayList <Integer>();     // To check how many tokens are there in 1 line
    
    public void setFileName(String file)
    {
        ImplementLex.fileName = file;
    }
    
    public List <Integer> getTokensInLine()
    {
        return ImplementLex.tokensInLine;
    }
    
    public ImplementLex()
    {
        
    }

    //////////////////////////
    //    Lex Functions     //
    //////////////////////////

    // Functions used to check lexeme pattern
    
    static boolean checkKeyword(String x)
    {
        List <String> keywords = new ArrayList <String>();
        keywords.add("Integer");
        keywords.add("char");
        keywords.add("if");
        keywords.add("elif");
        keywords.add("else");
        keywords.add("while");
        keywords.add("In");
        keywords.add("print");
        keywords.add("println");
        keywords.add("func");
        keywords.add("ret");
        keywords.add("true");
        keywords.add("false");
        keywords.add("void");
        
        for(String i : keywords)
        {
            if(i.equalsIgnoreCase(x))
            {
                return true;
            }
        }
        return false;
    }
    
    static boolean checkArithmeticOperator(String x)
    {
        if(x.contentEquals("+") || x.contentEquals("-") || x.contentEquals("*") || x.contentEquals("/"))
        {
            return true;
        }
        return false;
    }
    
    static boolean checkRelationalOperator(String x)
    {
        if(x.contentEquals("<") || x.contentEquals("<=") || x.contentEquals(">") || x.contentEquals(">=") || x.contentEquals("==") || x.contentEquals("/="))
        {
            return true;
        }
        return false;
    }
    
    static String getRelationalOperator(String x)
    {
        if(x.contentEquals("<"))
        {
            return "LT";
        }
        if(x.contentEquals("<="))
        {
            return "LE";
        }
        if(x.contentEquals(">"))
        {
            return "GT";
        }
        if(x.contentEquals(">="))
        {
            return "GE";
        }
        if(x.contentEquals("=="))
        {
            return "EQ";
        }
        if(x.contentEquals("/="))
        {
            return "NE";
        }
        return null;
    }
    
    static boolean checkSingleMultiLineComment(String x)
    {
        if(x.contains("/*") || x.contains("*/"))
        {
            return true;
        }
        return false;
    }
    
    static boolean checkIdentifier(String x)
    {
            if ((x.charAt(0) >= 'a' && x.charAt(0) <= 'z') || (x.charAt(0) >= 'A' && x.charAt(0) <= 'Z'))
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
    
    static boolean checkString(String x)
    {
        if(x.startsWith("\"") || x.endsWith("\""))
        {
            return true;
        }
        return false;
    }
    
    static boolean checkBrackets(String x)
    {
        if(x.contentEquals("(") || x.contentEquals(")") || x.contentEquals("{") || x.contentEquals("}") || x.contentEquals("[") || x.contentEquals("]"))
        {
            return true;
        }
        return false;
    }
    
    static boolean checkAssignmentOperator(String x)
    {
        if(x.contentEquals(":="))
        {
            return true;
        }
        return false;
    }
    
    static boolean checkInputOperator(String x)
    {
        if(x.contentEquals(">>"))
        {
            return true;
        }
        return false;
    }
    
    static boolean checkSemicolonColonComma(String x)
    {
        if(x.contentEquals(";") || x.contentEquals(":") || x.contentEquals(","))
        {
            return true;
        }
        return false;
    }
    
    
    //////////////////////////
    //  ------------------  //
    //////////////////////////
    

    public void startLex() throws IOException
    {
        boolean stringFlag = false;         // For checking String
        boolean slcFlag = false;            // For checking Single Line Comment
        boolean mlcFlag = false;            // For checking Multi Line Comment
        int checkLine = 0;                  // To check which line we are currently reading
        int checkForMLC = 0;                // Compares with "checkLine" to enable "mlcFlag"
        
        // Creating pattern to detect special characters in Identifiers
        Pattern pattern1 = Pattern.compile("[^a-zA-Z0-9]");
        
        // Creating pattern to detect special characters and alphabets in Numeric Constants
        Pattern pattern2 = Pattern.compile("[^0-9]");
        
        // Opening file given by user
        File myObj = new File(fileName);
        
        
        FileWriter myWriter = new FileWriter("tokens(readable).txt");    // Creates write(readable).txt
        FileWriter myOriginalWriter = new FileWriter("tokens.txt");      // Creates write.txt
        
        Scanner myReader = new Scanner(myObj);      // Scanner object for reading file
        
        
        // Loops until Scanner reaches end of file
        while (myReader.hasNextLine()) 
        {
            
            checkLine++;
            String data = myReader.nextLine();      // Read line from file
            int tokenInLineCount = 0;
            
            // Removing tabs and new lines 
            String removedWhiteSpace;
            removedWhiteSpace = data.trim();
            
            myWriter.write(removedWhiteSpace + "\n");                   // Writing in write(readable).txt

            // Removing white spaces
            String[] finalString = removedWhiteSpace.split(" ");
            
            // Checking each string for lexeme patterns
            for (String h : finalString) 
            {
                // Checking for Single Line or Multi Line Comments
                if(checkSingleMultiLineComment(h))
                {
                    slcFlag = true;
                    checkForMLC = checkLine;
                    
                }
                
                // Checking if it is a Multi Line Comment
                if (mlcFlag == true)
                {
                    if(h.endsWith("*/"))
                    {
                        // Creating token and adding in textfile
                        String token = "(MLC,^)\n";
                        myWriter.write(token);
                        myOriginalWriter.write(token);
                        slcFlag = false;
                        mlcFlag = false;
                        tokenInLineCount++;

                    }
                    
                }
                
                // Checking if it is a Single Line Comment
                else if(slcFlag == true)
                {
                    if(h.endsWith("*/"))
                    {
                        // Creating token and adding in textfile
                        String token = "(SLC,^)\n";
                        myWriter.write(token);
                        myOriginalWriter.write(token);
                        slcFlag = false;
                        tokenInLineCount++;
                    }
                    
                    // Comparing line numbers for activation of Multi Line Comment flag
                    else if(checkLine - checkForMLC > 0)
                    {
                        mlcFlag = true;
                    }
                }
                
                
                else
                {
                    // Splitting brackets, colon, semi colon, operators, commas
                    String[] subFinal = h.split("(?=\\()|(?<=\\()|(?<=\\))|(?=\\))|(?=:)|(?<=:)(?=[ ])|(?<=:)(?=[a-zA-Z])|(?<=:)(?<==)|(?=;)|(?<=;)|(?=,)|(?<=,)|(?=>>)|(?<=>>)(?=[a-zA-Z])|(?=\\==)|(?<=\\==)|(?=\\{)|(?<=:=)|(?=\\+)|(?<=\\+)|(?=\\-)|(?<=\\-)|(?=\\*)|(?<=\\*)|(?=<)(?<=[a-zA-Z0-9'])|(?<=<)(?=[a-zA-Z0-9'])|(?=>)(?<=[a-zA-Z0-9'])|(?<=>)(?=[a-zA-Z0-9'])|(?=<=)(?<=[a-zA-Z0-9'])|(?<=<=)(?=[a-zA-Z0-9'])|(?=>=)(?<=[a-zA-Z0-9'])|(?<=>=)(?=[a-zA-Z0-9'])|(?=/)|(?<=/)(?=[a-zA-Z0-9'])|(?=/=)|(?<=/=)");
                    
                    // Checking each string for lexeme patterns
                    for(String i : subFinal)
                    {
                        
                        // Checking if String flag is active
                        if(stringFlag == true)
                        {
                            if(i.endsWith("\""))
                            {
                                // Creating token and adding in textfile
                                String token = "(STR,^)\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                stringFlag = false;
                                tokenInLineCount++;
                            }
                        }
                        else
                        {
                            // Checking if it is a Keyword
                            if(checkKeyword(i))
                            {
                                // Creating token and adding in textfile
                                String s1 = i;
                                s1 = i.toUpperCase();
                                String token = "(" + s1 + ",^)\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            
                            // Checking ig it is a empty line
                            else if(i.contentEquals(""))
                            {
                                
                            }
                            
                            // Checking if it is an Identifier
                            else if(checkIdentifier(i))
                            {
                                //Checking if it contains special characters
                                Matcher matcher = pattern1.matcher(i);
                                boolean isStringContainsSpecialCharacter = matcher.find();
                                if(isStringContainsSpecialCharacter)
                                {
                                   System.out.println("Lex Analyzer detected a special characteer for identifier in line " + checkLine + "\n" + checkLine + "\t" + removedWhiteSpace + "\nPlease check your file and try again!");
                                   return;
                                }
                                else
                                {
                                    // Creating token and adding in textfile
                                    String token = "(ID,\"" + i +"\")\n";
                                    myWriter.write(token);
                                    myOriginalWriter.write(token);
                                    tokenInLineCount++;
                                }
                                     
                                
                            }
                            
                            // Checking if it is a Numeric Constant
                            else if(checkNumericConstant(i))
                            {
                                //Checking if it contains special characters
                                Matcher matcher = pattern2.matcher(i);
                                boolean isStringContainsSpecialCharacterOrAlphabet = matcher.find();
                                if(isStringContainsSpecialCharacterOrAlphabet)
                                {
                                   System.out.println("Lex Analyzer detected a characteer with numeric constant in line " + checkLine + "\n" + checkLine + "\t" + removedWhiteSpace + "\nPlease check your file and try again!");
                                   return;
                                }
                                else
                                {
                                    // Creating token and adding in textfile
                                    String token = "(NUM," + i +")\n";
                                    myWriter.write(token);
                                    myOriginalWriter.write(token);
                                    tokenInLineCount++;
                                }
                                
                            }
                            
                            // Checking if it is an Arithmetic Operator
                            else if(checkArithmeticOperator(i))
                            {
                                // Creating token and adding in textfile
                                String token = "('" + i + "',^)\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            
                            // Checking if it is a Relational Operator
                            else if(checkRelationalOperator(i))
                            {
                                // Creating token and adding in textfile
                                String token = "(RO," + getRelationalOperator(i) + ")\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            
                            // Checking if it is a Literal Constant
                            else if(checkLiteralConstant(i))
                            {
                                // Creating token and adding in textfile
                                String token = "(LC," + i + ")\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            
                            // Checking if it is a String
                            else if(checkString(i))
                            {
                                stringFlag = true;
                                if((i.startsWith("\"") && i.endsWith("\"")) && i.length() > 1)
                                {
                                    // Creating token and adding in textfile
                                    String token = "(STR,^)\n";
                                    myWriter.write(token);
                                    myOriginalWriter.write(token);
                                    stringFlag = false;
                                    tokenInLineCount++;
                                }
                                else if(i.endsWith("\""))
                                { }
                                else if(i.startsWith("\""))
                                { }
                                else
                                {
                                    System.out.println("String not enclosed in line " + checkLine + "\n" + checkLine + "\t" + removedWhiteSpace +  "\nPlease check your file and try again!");
                                    return;
                                }
                            }
                            
                            // Checking for paranthesis, braces and square brackets
                            else if(checkBrackets(i))
                            {
                                // Creating token and adding in textfile
                                String token = "('" + i + "',^)\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            
                            // Checking for Assignment Operator
                            else if(checkAssignmentOperator(i))
                            {
                                // Creating token and adding in textfile
                                String token = "(AO,^)\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            
                            // Checking for Colon, Semi colon and Commas
                            else if(checkSemicolonColonComma(i))
                            {
                                // Creating token and adding in textfile
                                String token = "('" + i + "',^)\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            
                            // Checking if it is an Input Operator
                            else if(checkInputOperator(i))
                            {
                                // Creating token and adding in textfile
                                String token = "(IO,^)\n";
                                myWriter.write(token);
                                myOriginalWriter.write(token);
                                tokenInLineCount++;
                            }
                            else
                            {
                                System.out.println("Could not identify lexeme pattern in line " + checkLine + "\n" + checkLine + "\t" + removedWhiteSpace +  "\nIt is not a part of GO language. Please check your file and try again!");
                                return;
                            }
                            
                        }

                    }
                }
                
            }
            
            tokensInLine.add(tokenInLineCount);
            
            // Adding new line after adding token in textfile
            myWriter.write("\n");
        }
        
        System.out.println("Execution successful. Check textfile for generated tokens!");                                
        
        // Closing all file writers and readers
        myWriter.close();
        myOriginalWriter.close();
        myReader.close();

        //////////////////////////
        //  ------------------  //
        //////////////////////////
    }
}