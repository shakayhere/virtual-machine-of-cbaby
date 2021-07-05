# Virtual Machine of CBaby
The attached project is the final step of a simple compiler that converts three address code to machine code and then executes the (.cbaby) code in a virtual machine.

[About CBaby Language](https://github.com/shakayhere/lexical-analyzer-of-cbaby#about-cbaby-language)
<br />
[Lexical Analyzer of CBaby](https://github.com/shakayhere/lexical-analyzer-of-cbaby)
<br />
[Parser and Three Addresss Code Generator of CBaby](https://github.com/shakayhere/parser-and-tac-generator-of-cbaby)

## About Virtual Machine
The Three Address code is produced in the form of `tac.txt`. Before executing the code, it is converted into machine code.
Since, every statement of Three Address Code has three operands at max, therefore, the machine code will somewhat looks like this:

```
5 0 1 4
opcode | firstOperand | secondOperand | thirdOperand
```

Opcode determines the type of statement (defined in `opcode-mapping.txt`), and operands are the relative addresses of our variables which can be seen in `translator-symboltable.txt`. Invalid operand has a value of -1 and the last valid operand is the destination variable of a statement. Machine code is produced in the form of a textfile called `machine-code.txt`

#### Sample Output of textfile
<table>
<tr>
<th>test.cbaby</th>
<th>machine-code.txt</th>
</tr>
<tr>
<td>
<pre>
func void: main()		
{
	Integer: a, b;	
	a := 65;
	b := 75;
	println(a + b);
	println(a - b);
	println(a * b);
	println(a / b);
	ret;
}

</pre>
</td>
<td>

```
40 -1 -1 -1
30 0 -1 -1
30 4 -1 -1
5 32 0 -1
5 36 4 -1
1 0 4 8
21 8 -1 -1
5 12 -1 -1
21 12 -1 -1
2 0 4 14
21 14 -1 -1
5 18 -1 -1
21 18 -1 -1
3 0 4 20
21 20 -1 -1
5 24 -1 -1
21 24 -1 -1
4 0 4 26
21 26 -1 -1
5 30 -1 -1
21 30 -1 -1
22 -1 -1 -1
```

</td>
</tr>
</table>


For every function defined in code(.cbaby), an opcode is generated and stored in `function-mapping.txt`. This is used to jump the program counter to the start of called function.

#### Sample Output of textfile
<table>
<tr>
<th>test.cbaby</th>
<th>function-mapping.txt</th>
</tr>
<tr>
<td>
<pre>
func void: write(Integer: c)
{
	println(c);
	ret;
}

func void: writeChar(char: ct)
{
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;println(ct);
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;ret;
}

func void: main()		
{
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Integer: a;	
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;a := 65;
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;println("'write' function");
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;write(a);
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;println("'writeChar' function");
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;writeChar(a);	
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;ret;
}
</pre>
</td>
<td>

```
write	40
writeChar	41
main	42

```

</td>
</tr>
</table>

#### NOTE:
The code will not execute without a `main` function of type `void` in (.cbaby) file. And every function should have return statement even if its of type 'void'

After converting Three Address code to machine code, the virtual machine will take `machine-code.txt` as input and then start executing the code written in (.cbaby) file. 

#### Sample Output
<table>
<tr>
<th>test.cbaby</th>
<th>Output</th>
</tr>
<tr>
<td>
<pre>
func void: write(Integer: c)
{
	println(c);
	ret;
}

func void: writeChar(char: ct)
{
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;println(ct);
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;ret;
}

func void: main()		
{
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Integer: a;	
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;a := 65;
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;println("'write' function");
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;write(a);
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;println("'writeChar' function");
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;writeChar(a);	
	&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;ret;
}
</pre>
</td>
<td>

```
Execution successful. Check textfile for generated tokens!
Parser successfully generated. Check textfile!
Three-Address Code successfully generated. Check textfile!
Executing code...
'write' function
65
'writeChar' function
A

```

</td>
</tr>
</table>

### Before you Start
The program is made and tested in java version "1.8.0_291". So, you should have Java JDK 8 installed on your system. If not, you can see the following link for installation
<br />
[How to Install JDK 8 (on Windows, Mac OS & Ubuntu)](http://cnaiman.com/COMP170/Orientation/How%20to%20Install%20JDK%208%20%28on%20Windows%2C%20Mac%20OS%2C%20Ubuntu%29%20and%20Get%20Started%20with%20Java%20Programming.html "How to Install JDK 8")


## How to Build and Run
In src folder, enter the following command to compile the code. Binary files will be produced with extention `.class`
```
javac VirtualMachine/*.java
```

To run this file, enter the following in src folder:
```
java VirtualMachine.Source
```

## License
Copyright (c) 2021 shakayhere

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
