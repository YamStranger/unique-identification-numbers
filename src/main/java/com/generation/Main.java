package com.generation;

import com.util.ArgumentsParser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * User: YamStranger
 * Date: 4/23/15
 * Time: 11:23 PM
 */
public class Main {
    public static void main(String... args) throws Exception {
        Map<String, String> params = new ArgumentsParser(args).arguments();
        String mode = params.get("mode");
        String name = params.get("file");
        String mask = params.get("mask");

        if (mode == null || !"stream".equalsIgnoreCase(mode)) {
            //file mode
            System.out.println("File mode");
            if (name == null && mask == null) {
                System.out.println("For file mode mask or file name must be specified");
                return;
            } else if (name != null && mask != null) {
                System.out.println("For file mode only one of \"mask\" or \"file name\" can be specified");
                return;
            }
            if (name != null) {
                final Path file = Paths.get(name);
                if (check(file.toAbsolutePath().toString())) {
                    process(file);
                } else {
                    System.out.println("Not supported format of file " + file.getFileName());
                }
            } else {
                boolean find = false;
                try (DirectoryStream<Path> directories = Files.newDirectoryStream(Paths.get(""), mask)) {
                    for (final Path file : directories) {
                        if (!Files.isDirectory(file)) {
                            if (check(file.toAbsolutePath().toString())) {
                                process(file);
                                find = true;
                            } else {
                                System.out.println("Not supported format of file " + file.getFileName());
                            }
                        }
                    }
                    if (!find) {
                        System.out.println("files by mask " + mask + " not found");
                    }
                } catch (IOException | PatternSyntaxException e) {
                    System.out.println("Exception during processing files by mask" + mask);
                    System.out.println(e);
                }
            }
            System.out.println("done");
        } else {
            while (!Thread.currentThread().isInterrupted()) {
                Scanner scanner = new Scanner(System.in);
                int numbers = scanner.nextInt();
                final Generator generator = new Generator();
                while (--numbers >= 0) {
                    System.out.println(generator.generate());
                }
            }
        }

    }

    public static boolean check(String file) {
        Pattern pattern = Pattern.compile("((xlsx)|(csv))$");
        Matcher matcher = pattern.matcher(file);
        if (!matcher.find()) {
            return false;
        } else return true;
    }

    public static void process(Path file) throws IOException {
        Pattern pattern = Pattern.compile("(xlsx)$");
        Matcher matcher = pattern.matcher(file.toAbsolutePath().toString());
        if (matcher.find()) {
            System.out.println("processing xslx file \"" + file.getFileName() + "\"");
            new ExcelFilesProcessor(file).process();
            return;
        } else {
            pattern = Pattern.compile("(csv)$");
            matcher = pattern.matcher(file.toAbsolutePath().toString());
            if (matcher.find()) {
                System.out.println("processing csv file \"" + file.getFileName() + "\"");
                new CSVFilesProcessor(file).process();
            } else {
                throw new IllegalArgumentException("unsupported format");
            }
        }

    }

}
