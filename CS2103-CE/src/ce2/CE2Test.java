package ce2;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

public class CE2Test {
	
	// Messages
	static final String MSG_WELCOME = "Welcome to TextBuddy. ";
	static final String MSG_READY = " is ready for use";
	static final String MSG_DELETE_CONFIRM = "All content deleted from ";
	static final String MSG_ADD = "Added to ";
	static final String MSG_DELETE = "Delete from ";
	static final String MSG_SORT_CONFIRM = "All content sorted for ";
	static final String MSG_EMPTY = " is empty.";
	static final String MSG_COMMAND = "Command: ";
	static final String MSG_NO_MATCH = "No match found";
	
	static final String MSG_ERR_NOTHING_TO_SORT = "Error: No contents to sort";
	static final String MSG_ERR_MISSING_FILE_NAME = "Error: Missing output file name.";
	static final String MSG_ERR_CANNOT_WRITE_FILE = "Error: File is not writable.";
	static final String MSG_ERR_UNSUPPORTED_ENCODING = "Error: Encoding is not supported - ";
	static final String MSG_ERR_NON_INTEGER_INPUT = "Error: Invalid input. Input must be an integer.";
	static final String MSG_ERR_INVALID_INPUT = "Error: Invalid input. Input is empty.";
	static final String MSG_ERR_INVALID_COMMAND = "Error: Invalid Command";
	static final String MSG_ERR_UNABLE_READ_FILE = "Error: Unable to read file";
	static final String MSG_ERR_INVALID_LINE_NUM = "Error: Invalid line number";
	static final String MSG_ERR_MISSING_FILE = "Error: Missing file.";
	static final String MSG_ERR_IO_EXCEPTION = "Error: IO exception.";
	
	// Test Functions
	public Boolean compareFile(String fileName, ArrayList<String> content) throws Exception {
		
		FileReader reader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(reader);
		Boolean match = true;
		int index = 0;
		String line = bufferedReader.readLine();
		
		while (line != null) {
			if (index >= content.size()) {
				match = false;
				break;
			}
			if (line.equals(content.get(index))) {
				line = bufferedReader.readLine();
				index++;
			}
			else {
				match = false;
				break;
			}
		}
		if (index < content.size() - 1) {
			match = false;
		}
		
		bufferedReader.close();
		reader.close();
		
		return match;
	}

	@Test
	public void testAddFunction() throws Exception {
		String fileName = "testAddFunction.txt";
		String[] argument = {fileName};
		TextBuddy textBuddy = new TextBuddy(argument);
		textBuddy.createOutputFile();
		textBuddy.runCommand("add nyancat");
		textBuddy.runCommand("add  ");
		assertTrue("Expected: " + MSG_ERR_INVALID_INPUT + ", Actual: " + textBuddy.getOut(), 
					MSG_ERR_INVALID_INPUT.equals(textBuddy.getOut()));
		textBuddy.runCommand("add putin");
		textBuddy.closeTextBuddy();
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("nyancat");
		expected.add("putin");
		assertTrue(compareFile(fileName, expected));
	}
	
	@Test
	public void testDisplayFunction() throws Exception {
		String fileName = "testDisplayFunction.txt";
		String[] argument = {fileName};
		TextBuddy textBuddy = new TextBuddy(argument);
		textBuddy.createOutputFile();
		textBuddy.runCommand("display");
		assertTrue("Expected: " + fileName + MSG_EMPTY + ", Actual: " + textBuddy.getOut(), 
					(fileName + MSG_EMPTY).equals(textBuddy.getOut()));
		textBuddy.runCommand("add william");
		textBuddy.runCommand("add lukA");
		textBuddy.runCommand("add eDen");
		textBuddy.runCommand("add Hildegard");
		textBuddy.closeTextBuddy();
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("william");
		expected.add("lukA");
		expected.add("eDen");
		expected.add("Hildegard");
		assertTrue(compareFile(fileName, expected));
	}
	
	@Test
	public void testClearFunction() throws Exception {
		String fileName = "testClearFunction.txt";
		String[] argument = {fileName};
		TextBuddy textBuddy = new TextBuddy(argument);
		textBuddy.createOutputFile();
		textBuddy.runCommand("add william");
		textBuddy.runCommand("add lukA");
		textBuddy.runCommand("add eDen");
		textBuddy.runCommand("add Hildegard");
		textBuddy.runCommand("clear");
		assertTrue("Expected: " + MSG_DELETE_CONFIRM + fileName + ", Actual: " + textBuddy.getOut(), 
					(MSG_DELETE_CONFIRM + fileName).equals(textBuddy.getOut()));
		textBuddy.closeTextBuddy();
		
		ArrayList<String> expected = new ArrayList<String>();
		assertTrue(compareFile(fileName, expected));
	}
	
	@Test
	public void testDeleteFunction() throws Exception {
		String fileName = "testClearFunction.txt";
		String expectedOut = null;
		String[] argument = {fileName};
		TextBuddy textBuddy = new TextBuddy(argument);
		
		textBuddy.createOutputFile();
		textBuddy.runCommand("add william");
		textBuddy.runCommand("add lukA");
		textBuddy.runCommand("add eDen");
		textBuddy.runCommand("add Hildegard");
		textBuddy.runCommand("add Yuri");
		textBuddy.runCommand("add ViTos");
		textBuddy.runCommand("delete -1");
		assertTrue("Expected: " + MSG_ERR_INVALID_LINE_NUM + ", Actual: " + textBuddy.getOut(), 
					(MSG_ERR_INVALID_LINE_NUM).equals(textBuddy.getOut()));
		
		textBuddy.runCommand("delete 10");
		assertTrue("Expected: " + MSG_ERR_INVALID_LINE_NUM + ", Actual: " + textBuddy.getOut(), 
					(MSG_ERR_INVALID_LINE_NUM).equals(textBuddy.getOut()));
		
		textBuddy.runCommand("delete 3");
		textBuddy.runCommand("display");
		expectedOut = "1: william, 2: lukA, 3: Hildegard, 4: Yuri, 5: ViTos";
		assertTrue("Expected: " + expectedOut + ", Actual: " + textBuddy.getOut(), 
					expectedOut.equals(textBuddy.getOut()));
		
		textBuddy.runCommand("delete 3");
		textBuddy.runCommand("display");
		expectedOut = "1: william, 2: lukA, 3: Yuri, 4: ViTos";
		assertTrue("Expected: " + expectedOut + ", Actual: " + textBuddy.getOut(), 
					expectedOut.equals(textBuddy.getOut()));
		
		textBuddy.closeTextBuddy();
	}
	
	@Test
	public void testSortFunction() throws Exception {
		String fileName = "testSortFunction.txt";
		String[] argument = {fileName};
		TextBuddy textBuddy = new TextBuddy(argument);
		
		textBuddy.createOutputFile();
		textBuddy.runCommand("sort");
		assertTrue("Expected: " + MSG_ERR_NOTHING_TO_SORT + ", Actual: " + textBuddy.getOut(), 
					MSG_ERR_NOTHING_TO_SORT.equals(textBuddy.getOut()));
		
		textBuddy.runCommand("add I have searched for good documentation on these methods, but haven't found anything.");
		textBuddy.runCommand("add I think I see what was confusing me.");
		textBuddy.runCommand("add so that the message would print out and tell me WHY it failed.");
		textBuddy.runCommand("add assertTrue as well as assertFalse display the string when the second parameter evaluates to false.");
		textBuddy.runCommand("add Book was already checked out.");
		textBuddy.runCommand("add I'm pretty new to Java.");
		textBuddy.runCommand("sort");
		textBuddy.runCommand("display");
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("assertTrue as well as assertFalse display the string when the second parameter evaluates to false.");
		expected.add("Book was already checked out.");
		expected.add("I have searched for good documentation on these methods, but haven't found anything.");
		expected.add("I think I see what was confusing me.");
		expected.add("I'm pretty new to Java.");
		expected.add("so that the message would print out and tell me WHY it failed.");
		assertTrue(compareFile(fileName, expected));
	}
	
	@Test
	public void testSearchFunction() throws Exception {
		String fileName = "testSearchFunction.txt";
		String expectedOut = null;
		String[] argument = {fileName};
		TextBuddy textBuddy = new TextBuddy(argument);
		
		textBuddy.createOutputFile();		
		textBuddy.runCommand("add I have searched for good documentation on these methods, but haven't found anything.");
		textBuddy.runCommand("add I think I see what was confusing me.");
		textBuddy.runCommand("add so that the message would print out and tell me WHY it failed.");
		textBuddy.runCommand("add assertTrue as well as assertFalse display the string when the second parameter evaluates to false.");
		textBuddy.runCommand("add Book was already checked out.");
		textBuddy.runCommand("add I'm pretty new to Java.");
		textBuddy.runCommand("search why");
		expectedOut = "3: so that the message would print out and tell me WHY it failed.";
		assertTrue("Expected: " + expectedOut + ", Actual: " + textBuddy.getOut(), 
					expectedOut.equals(textBuddy.getOut()));
		
		textBuddy.runCommand("search I");
		expectedOut = "1: I have searched for good documentation on these methods, but haven't found anything."
						+ ", 2: I think I see what was confusing me."
						+ ", 6: I'm pretty new to Java.";
		assertTrue("Expected: " + expectedOut + ", Actual: " + textBuddy.getOut(), 
					expectedOut.equals(textBuddy.getOut()));

	}
}
