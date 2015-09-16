package ce2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TextBuddy program helps to write the user's input into a text file where
 * the user could manage the content in the text file on the go. The TextBuddy
 * will always create a new text file with the given file name input and
 * contents will be updated and saved upon each command.
 * 
 * Currently supported commands: (Case-sensitive) exit, clear, display, add
 * <String>, delete <Integer>, sort, search <String>
 * 
 * Assuming TextBuddy only writes text, it is safe to use a PrintWriter for the
 * writing into output file.
 * 
 * @author SeeSK
 */
public class TextBuddy {

	// Private variables
	private String _fileName;
	private PrintWriter _writer;
	private Scanner _scanner;
	private Boolean _isExitCalled = false;

	// Encoding used by PrintWriter
	final String CHAR_ENCODING = "UTF-8";
	
	// Messages
	static final String MSG_WELCOME = "Welcome to TextBuddy. ";
	static final String MSG_READY = " is ready for use";
	static final String MSG_DELETE_CONFIRM = "All content deleted from ";
	static final String MSG_ADD = "Added to ";
	static final String MSG_DELETE = "Delete from ";
	static final String MSG_SORT_CONFIRM = "All content sorted for ";
	static final String MSG_EMPTY = " is empty.";
	static final String MSG_COMMAND = "Command: ";
	
	static final String MSG_ERR_NOTHING_TO_SORT = "Error: No contents to sort";
	static final String MSG_ERR_MISSING_FILE_NAME = "Error: Missing output file name.";
	static final String MSG_ERR_CANNOT_WRITE_FILE = "Error: File is not writable.";
	static final String MSG_ERR_UNSUPPORTED_ENCODING = "Error: Encoding is not supported - ";
	static final String MSG_ERR_NON_INTEGER_INPUT = "Error: Invalid input. Input must be an integer.";
	static final String MSG_ERR_INVALID_COMMAND = "Error: Invalid Command";
	static final String MSG_ERR_UNABLE_READ_FILE = "Error: Unable to read file";
	static final String MSG_ERR_INVALID_LINE_NUM = "Error: Invalid line number";
	static final String MSG_ERR_MISSING_FILE = "Error: Missing file.";
	static final String MSG_ERR_IO_EXCEPTION = "Error: IO exception.";
	
	// Commands
	static final String COMMAND_ADD = "add";
	static final String COMMAND_DELETE = "delete";
	static final String COMMAND_SORT = "sort";
	static final String COMMAND_SEARCH = "search";
	static final String COMMAND_DISPLAY = "display";
	static final String COMMAND_CLEAR = "clear";
	static final String COMMAND_EXIT = "exit";
	static final String COMMAND_INVALID = "invalid";

	// Constructor
	public TextBuddy(String outputPath) {
		_fileName = outputPath;
		_scanner = new Scanner(System.in);
	}

	// Main Method
	public static void main(String[] args) {
		TextBuddy textBuddy = createTextBuddy(args);
		textBuddy.createOutputFile();
		textBuddy.runCommandTillExit();
		textBuddy.closeTextBuddy();
	}

	// Helper Methods
	private static TextBuddy createTextBuddy(String[] args) {
		try {
			TextBuddy textBuddy = new TextBuddy(args[0]);
			return textBuddy;
		} catch (Exception e) {
			handleException(e, MSG_ERR_MISSING_FILE_NAME);
			return null;
		}
	}

	private void createOutputFile() {
		try {
			PrintWriter writer = new PrintWriter(_fileName, CHAR_ENCODING);
			_writer = writer;
			System.out.println(MSG_WELCOME + _fileName + MSG_READY);
		} catch (FileNotFoundException e) {
			handleException(e, MSG_ERR_CANNOT_WRITE_FILE);
		} catch (UnsupportedEncodingException e) {
			handleException(e, MSG_ERR_UNSUPPORTED_ENCODING + CHAR_ENCODING);
		}
	}

	private void runCommandTillExit() {
		String commandText = null;

		while (!_isExitCalled) {
			commandText = getNextCommand();
			runCommand(commandText);
		}

	}

	private void closeTextBuddy() {
		// Close all resources
		_writer.close();
	}
	
	private String getNextCommand() {
		
		String commandText;
		
		// UI: Show user that app is ready to accept command.
		System.out.println("");
		System.out.print(MSG_COMMAND);
		
		// Listen for input
		commandText = _scanner.nextLine();
		
		return commandText;
	}

	/**
	 * Currently supported commands: (Case-Sensitive)
	 * 
	 * exit, clear, display, add <String>, delete <Integer>, sort, search <String>
	 * 
	 * @param commandText
	 */
	private void runCommand(String commandText) {
		
		String keyWord = getKeyWord(commandText);
		
		switch(keyWord) {
			case COMMAND_ADD : 		addLine(commandText);
									break;
									
			case COMMAND_DELETE : 	deleteLine(commandText);
									break;
									
			case COMMAND_SORT : 	sortContent(true);
									System.out.println(MSG_SORT_CONFIRM + _fileName);
									break;
									
			case COMMAND_SEARCH : 	search(commandText);
									break;
									
			case COMMAND_DISPLAY : 	displayText();
									break;
									
			case COMMAND_CLEAR : 	clearFile(true);
									break;
									
			case COMMAND_EXIT : 	_isExitCalled = true;
									break;
									
			case COMMAND_INVALID : 	handleError(MSG_ERR_INVALID_COMMAND);
									break;
									
			default : 				handleError(MSG_ERR_INVALID_COMMAND);
									break;
		}

	}
	
	private String getKeyWord(String commandText) {
		
		if (commandText.isEmpty()) {
			return COMMAND_INVALID;
		}
		String[] keyWord = commandText.split(" ");
		return keyWord[0];
	}

	private void addLine(String commandText) {
		try {
			String textAdded = commandText.substring(COMMAND_ADD.length() + 1);
			_writer.println(textAdded);
			_writer.flush();
			System.out.println(MSG_ADD + _fileName + ": \"" + textAdded + "\"");
		} catch (IndexOutOfBoundsException e) {
			handleError(MSG_ERR_INVALID_COMMAND);
		}
	}

	private void displayText() {
		try {

			// Open the file and read using BufferedReader
			FileReader dataFile = new FileReader(_fileName);
			BufferedReader bufferedDataFile = new BufferedReader(dataFile);
			String line = bufferedDataFile.readLine();
			
			// UI: Line Numbering
			int lineNumber = 1;

			// Check if file is empty
			if (line == null) {
				System.out.println(_fileName + MSG_EMPTY);
			}

			while (line != null) {
				System.out.println(lineNumber + ": " + line);
				lineNumber++;
				line = bufferedDataFile.readLine();
			}

			// Close when done.
			bufferedDataFile.close();
			dataFile.close();

		} catch (Exception e) {
			// If file not found
			handleException(e, MSG_ERR_UNABLE_READ_FILE);
		}
	}

	private void clearFile(Boolean confirmation) {
		try {
			_writer = new PrintWriter(_fileName, CHAR_ENCODING);
			if (confirmation) {
				System.out.println(MSG_DELETE_CONFIRM + _fileName);
			}
		} catch (FileNotFoundException e) {
			handleException(e, MSG_ERR_CANNOT_WRITE_FILE);
		} catch (UnsupportedEncodingException e) {
			handleException(e, MSG_ERR_UNSUPPORTED_ENCODING + CHAR_ENCODING);
		}
	}

	private void deleteLine(String commandText) {
				
		try {
			// Get line number from input
			int lineNumberToDelete = Integer.parseInt(commandText.substring(COMMAND_DELETE.length() + 1));
			
			// Read from the original file and add to ArrayList unless
			// content matches data to be removed.
			FileReader fileReader = new FileReader(_fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			ArrayList<String> remainingStrings = new ArrayList<String>();
			String deletedLine = null;
			String line = bufferedReader.readLine();
			int currentLineNumber = 1;

			while (line != null) {

				if (currentLineNumber != lineNumberToDelete) {
					remainingStrings.add(line);
				} else {
					deletedLine = line;
				}
				currentLineNumber++;
				line = bufferedReader.readLine();
			}

			// Clear file and add back edited content
			clearFile(false);
			for (String s : remainingStrings) {
				_writer.println(s);
			}
			_writer.flush();

			// Close when done
			bufferedReader.close();
			fileReader.close();

			// Check if command is valid
			if (deletedLine == null) {
				handleError(MSG_ERR_INVALID_LINE_NUM);
			} else {
				System.out.println(MSG_DELETE + _fileName + ": " + "\""
						+ deletedLine + "\"");
			}

		} catch (FileNotFoundException e) {
			// File not found
			handleException(e, MSG_ERR_MISSING_FILE);
		} catch (IOException e) {
			// Cannot write to file
			handleException(e, MSG_ERR_IO_EXCEPTION);
		} catch (NumberFormatException e) {
			// Invalid input for line number
			handleException(e, MSG_ERR_NON_INTEGER_INPUT);
		}

	}
	
	private void sortContent(Boolean confirmation) {
		
		try {
			//Open file
			FileReader fileReader = new FileReader(_fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			ArrayList<String> content = new ArrayList<String>();
			String line = bufferedReader.readLine();
			
			//Read file to array
			while (line != null) {
				content.add(line);
				line = bufferedReader.readLine();
			}
			
			//Sort and replace file
			if (content.isEmpty()) {
				handleError(MSG_ERR_NOTHING_TO_SORT);
			}
			else {
				Collections.sort(content);
				clearFile(false);
				for (String s : content) {
					_writer.println(s);
				}
				_writer.flush();
				if (confirmation) {
					System.out.println(MSG_SORT_CONFIRM);
				}	
			}
			
			//Close when done
			bufferedReader.close();
			fileReader.close();
			
		} catch (FileNotFoundException e) {
			// File not found
			handleException(e, MSG_ERR_MISSING_FILE);
		} catch (IOException e) {
			// Cannot write to file
			handleException(e, MSG_ERR_IO_EXCEPTION);
		}
		
	}
	
	// The search function will not accept null input. Input is not case-sensitive.
	private void search(String commandText) {
		
		try {
			// Validate command and setup match terms.
			String searchTerm = commandText.substring(COMMAND_SEARCH.length() + 1);
			if (searchTerm.isEmpty()) {
				handleError(MSG_ERR_INVALID_COMMAND);
				return;
			}
			searchTerm = "\\b" + searchTerm + "\\b";
			Pattern match = Pattern.compile(searchTerm);
			Matcher matcher;
			
			// Open file
			FileReader fileReader = new FileReader(_fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			ArrayList<String> matchingContent = new ArrayList<String>();
			String line = bufferedReader.readLine();
			String lineNumbered = null;
			int lineNumber = 1;
			
			// Read through file for matching content
			while (line != null) {
				matcher = match.matcher(line);
				if (matcher.find()) {
					lineNumbered = lineNumber + ": " + line;
					matchingContent.add(lineNumbered);
				}
				lineNumber++;
				line = bufferedReader.readLine();
			}
			
			// Display matching content
			for (String s : matchingContent) {
				System.out.println(s);
			}
			
			// Close when done
			bufferedReader.close();
			fileReader.close();
			
		} catch (FileNotFoundException e) {
			// File not found
			handleException(e, MSG_ERR_MISSING_FILE);
		} catch (IOException e) {
			// Cannot write to file
			handleException(e, MSG_ERR_IO_EXCEPTION);
		}
	}
	
	// Do not terminate errors.
	private static void handleError(String error) {
		System.out.println(error);
	}

	// Always terminate errors.
	private static void handleException(Exception e, String error) {
		if (e != null) {
			e.printStackTrace();
		}
		System.out.println(error);
		System.exit(1);
	}

}
