package MiniJava;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import MiniJava.errorHandler.ErrorHandler;
import MiniJava.parser.Parser;


import MiniJava.parser.ParserFacade;

public class Main {
    public static void main(String[] args) {
        ParserFacade parserFacade = new ParserFacade();
        parserFacade.parse("src/main/resources/code");
    }
}