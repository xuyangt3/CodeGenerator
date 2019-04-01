import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeGenerator {
    public enum Code {
        ClassSummary("<pre>(?<Type>.+?)<span class=\"typeNameLabel\">(?<Name>\\w+?)</span>\r?\n" +
                "(?<InheritanceAndOther>.+?)</pre>\r?\n" +
                "(?:<div class=\"block\">(?<Comment>(?:.(?!/div>))+)</div>\r?\n)?" +
                "(?:<dl>\r?\n" +
                "(?:<dt><span class=\"simpleTagLabel\">Since:</span></dt>\r?\n<dd>(?<Since>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"seeLabel\">See Also:</span></dt>\r?\n<dd>(?<SeeAlso>.+?)</dd>\r?\n)?" +
                "</dl>)?",
                new String[]{Code.Type, Code.Name, Code.InheritanceAndOther, Code.Comment, Code.Since, Code.SeeAlso},
                "<!-- ======== START OF CLASS DATA ======== -->",
                "<!-- [= \\w]{37} -->"),
        FieldDetail("<li class=\"blockList\">\r?\n" +
                "<h4>\\w+?</h4>\r?\n" +
                "<pre>(?<Name>(?:.(?!/pre>))+)</pre>\r?\n" +
                "(?:<div class=\"block\">(?<Comment>(?:.(?!/div>))+)</div>\r?\n)?" +
                "(?:<dl>\r?\n" +
                "(?:<dt><span class=\"simpleTagLabel\">Since:</span></dt>\r?\n<dd>(?<Since>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"seeLabel\">See Also:</span></dt>\r?\n<dd>(?<SeeAlso>.+?)</dd>\r?\n)?" +
                "</dl>\r?\n)?",
                new String[]{Code.Name, Code.Comment, Code.Since, Code.SeeAlso},
                "<!-- ============ FIELD DETAIL =========== -->",
                "<!-- [= \\w]{37} -->"),
        ConstructorDetail("<li class=\"blockList\">\r?\n" +
                "<h4>\\w+?</h4>\r?\n" +
                "<pre>(?<Name>(?:.(?!/pre>))+)</pre>\r?\n" +
                "(?:<div class=\"block\">(?<Comment>(?:.(?!/div>))+)</div>\r?\n)?" +
                "(?:<dl>\r?\n" +
                "(?:<dt><span class=\"overrideSpecifyLabel\">Overrides:</span></dt>\r?\n<dd>(?<Override>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"paramLabel\">Parameters:</span></dt>\r?\n(?<Param>.+?)\r?\n)" +
                "(?:<dt><span class=\"throwsLabel\">Throws:</span></dt>\r?\n<dd>(?<Throw>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"simpleTagLabel\">Since:</span></dt>\r?\n<dd>(?<Since>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"seeLabel\">See Also:</span></dt>\r?\n<dd>(?<SeeAlso>.+?)</dd>\r?\n)?" +
                "</dl>\r?\n)?",
                new String[]{Code.Name, Code.Comment, Code.Override, Code.Param, Code.Throw, Code.Since, Code.SeeAlso},
                "<!-- ========= CONSTRUCTOR DETAIL ======== -->",
                "<!-- [= \\w]{37} -->"),
        MethodDetail("<li class=\"blockList\">\r?\n" +
                "<h4>\\w+?</h4>\r?\n" +
                "<pre>(?<Name>(?:.(?!/pre>))+)</pre>\r?\n" +
                "(?:<div class=\"block\">(?<Comment>(?:.(?!/div>))+)</div>\r?\n)?" +
                "(?:<dl>\r?\n" +
                "(?:<dt><span class=\"overrideSpecifyLabel\">Overrides:</span></dt>\r?\n<dd>(?<Override>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"paramLabel\">Parameters:</span></dt>\r?\n(?<Param>.+?)\r?\n)?" +
                "(?:<dt><span class=\"returnLabel\">Returns:</span></dt>\r?\n<dd>(?<Return>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"throwsLabel\">Throws:</span></dt>\r?\n<dd>(?<Throw>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"simpleTagLabel\">Since:</span></dt>\r?\n<dd>(?<Since>.+?)</dd>\r?\n)?" +
                "(?:<dt><span class=\"seeLabel\">See Also:</span></dt>\r?\n<dd>(?<SeeAlso>.+?)</dd>\r?\n)?" +
                "</dl>\r?\n)?",
                new String[]{Code.Name, Code.Comment, Code.Override, Code.Param, Code.Return, Code.Throw, Code.Since, Code.SeeAlso},
                "<!-- ============ METHOD DETAIL ========== -->",
                "<!-- [= \\w]{37} -->");

        public static final String Type = "Type",
                Name = "Name",
                InheritanceAndOther = "InheritanceAndOther",
                Comment = "Comment",
                Override = "Override",
                Param = "Param",
                Return = "Return",
                Throw = "Throw",
                Since = "Since",
                SeeAlso = "SeeAlso";
        public static boolean PrintMessageEnabled = true;

        private final String regStr, startStr, endStr;
        public final String[] parts;
        //count is for test only
        private int count;

        Code(String regStr, String[] parts, String startStr, String endStr) {
            this.regStr = regStr;
            this.parts = parts;
            this.startStr = startStr;
            this.endStr = endStr;
        }

        public Matcher getMatcher(String content) {
            try {
                return Pattern.compile(regStr, Pattern.DOTALL).matcher(content);
            } catch (IllegalStateException e) {
                if (PrintMessageEnabled) {
                    StackTraceElement[] eStack = e.getStackTrace();
                    System.out.println(name() + ".getMatcher() failed: " + e.getMessage());
                    System.out.println("\tat " + eStack[eStack.length - 1]);
                }
            }
            return null;
        }

        public int getStart(String content) {
            try {
                Matcher m = Pattern.compile(startStr).matcher(content);
                m.find();
                return m.end();
            } catch (IllegalStateException e) {
                if (PrintMessageEnabled) {
                    StackTraceElement[] eStack = e.getStackTrace();
                    System.out.println(name() + ".getStart() failed: " + e.getMessage());
                    System.out.println("\tat " + eStack[eStack.length - 1]);
                }
            }
            return -1;
        }

        public long getRegion(String content) {
            try {
                Matcher m = Pattern.compile(endStr).matcher(content);
                int start = getStart(content);
                if (start != -1) {
                    m.find(start);
                    return ((long) start << 32) + m.start();
                }
            } catch (IllegalStateException e) {
                if (PrintMessageEnabled) {
                    StackTraceElement[] eStack = e.getStackTrace();
                    System.out.println(name() + ".getEnd() failed: " + e.getMessage());
                    System.out.println("\tat " + eStack[eStack.length - 1]);
                }
            }
            return -1L;
        }

    }

    public static void main(String[] args) throws IOException {

        Scanner keyBoardScan = new Scanner(System.in);
        String content = "";

        System.out.println("Using dir: " + System.getProperty("user.dir"));

        Scanner htmlFileScanner = null;
        while (htmlFileScanner == null) {
            System.out.print("Path of the source file:");
            String fileName = keyBoardScan.nextLine();
            try {
                FileReader temprdr = new FileReader(fileName);
                htmlFileScanner = new Scanner(new BufferedReader(temprdr));
                content = htmlFileScanner.useDelimiter("\\A").next();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.print("File Not Found: ");
            } catch (NoSuchElementException e) {
                System.out.print("Imput File Empty: ");
            } finally {
                System.out.println("Source:" + fileName);
            }
        }
        htmlFileScanner.close();

        BufferedWriter output = null;
        while (output == null) {
            System.out.print("Path and name of the output file:");
            String fileName = keyBoardScan.nextLine();
            try {
                FileWriter tempwtr = new FileWriter(fileName);
                output = new BufferedWriter(tempwtr);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.print("Failed to Create Output Stream: ");
            } finally {
                System.out.println("Output:" + fileName);
            }
        }

        {
            Matcher match = Code.ClassSummary.getMatcher(content);
            match.find();

            String temp;

            temp = match.group(Code.Comment);
            output.write("/**\n* ");
            if (temp != null) {
                output.write(temp
                        .replaceAll(" {2,}", " ")
                        .replaceAll("(\r?\n)", "\n*"));
            }

            temp = match.group(Code.Since);
            if (temp != null) {
                output.write("\n* @since ");
                output.write(temp);
            }

            temp = match.group(Code.SeeAlso);
            if (temp != null) {
                String[] tempArr = temp.split("\\s+,\\s+");
                for (String i : tempArr) {
                    output.write("\n* @see ");
                    output.write(i);
                }
            }
            output.write("\n*/\n");


            output.write(match.group(Code.Type));
            output.write(match.group(Code.Name));
            output.write(' ');
            output.write(match.group(Code.InheritanceAndOther)
                    .replaceAll("<(?:.(?<![<>]))+>", "")
                    .replaceAll("\r?\n", " "));
            output.write('{');
        }

        {
            Matcher match = Code.FieldDetail.getMatcher(content);
            long StartEnd = Code.FieldDetail.getRegion(content);
            if (StartEnd != -1) {
                match.region((int) (StartEnd >> 32), (int) StartEnd);
                while (match.find()) {
                    String temp;

                    temp = match.group(Code.Comment);
                    output.write("\n/**\n* ");
                    if (temp != null) {
                        output.write(temp
                                .replaceAll(" {2,}", " ")
                                .replaceAll("(\r?\n)", "\n*"));
                    }

                    temp = match.group(Code.Since);
                    if (temp != null) {
                        output.write("\n* @since ");
                        output.write(temp);
                    }

                    temp = match.group(Code.SeeAlso);
                    if (temp != null) {
                        String[] tempArr = temp.split("\\s+,\\s+");
                        for (String i : tempArr) {
                            output.write("\n* @see ");
                            output.write(i);
                        }
                    }

                    output.write("\n*/\n");


                    output.write(match.group(Code.Name).replace("&nbsp;", " "));
                    output.write(';');
                }
            }
        }
        {
            Matcher match = Code.ConstructorDetail.getMatcher(content);
            long StartEnd = Code.ConstructorDetail.getRegion(content);
            if (StartEnd != -1) {
                match.region((int) (StartEnd >> 32), (int) StartEnd);
                while (match.find()) {
                    String temp;

                    temp = match.group(Code.Comment);
                    output.write("\n/**\n* ");
                    if (temp != null) {
                        output.write(temp
                                .replaceAll(" {2,}", " ")
                                .replaceAll("(\r?\n)", "\n*"));
                    }

                    temp = match.group(Code.Override);
                    if (temp != null) {
                        output.write("\n* @Override ");
                        output.write(temp);
                    }

                    temp = match.group(Code.Param);
                    if (temp != null) {
                        String[] tempArr = temp.split("(?<=</dd>)\r?\n?(?=<dd>)");
                        for (String i : tempArr) {
                            output.write("\n* @param ");
                            output.write(i
                                    .replace(" - ", " ")
                                    .replaceAll("<(?:.(?<![<>]))+>", ""));
                        }
                    }

                    temp = match.group(Code.Throw);
                    if (temp != null) {
                        String[] tempArr = temp.split("(?<=</dd>)\r?\n?(?=<dd>)");
                        for (String i : tempArr) {
                            output.write("\n* @throws ");
                            output.write(i
                                    .replaceAll("<(?:.(?<![<>]))+>", ""));
                        }
                    }

                    temp = match.group(Code.Since);
                    if (temp != null) {
                        output.write("\n* @since ");
                        output.write(temp);
                    }

                    temp = match.group(Code.SeeAlso);
                    if (temp != null) {
                        String[] tempArr = temp.split("\\s+,\\s+");
                        for (String i : tempArr) {
                            output.write("\n* @see ");
                            output.write(i);
                        }
                    }

                    output.write("\n*/\n");


                    output.write(match.group(Code.Name)
                            .replace("&nbsp;", " ")
                            .replaceAll("&#[0-9]+;", "")
                            .replaceAll("<(?:.(?<![<>]))+>", "")
                            .replaceAll("\r?\n", " "));
                    output.write("{}");
                }
            }
        }
        {
            Matcher match = Code.MethodDetail.getMatcher(content);
            long StartEnd = Code.MethodDetail.getRegion(content);
            if (StartEnd != -1) {
                match.region((int) (StartEnd >> 32), (int) StartEnd);
                while (match.find()) {
                    String temp;

                    temp = match.group(Code.Comment);
                    output.write("\n/**\n* ");
                    if (temp != null) {
                        output.write(temp
                                .replaceAll(" {2,}", "")
                                .replaceAll("(\r?\n)", "\n*"));
                    }

                    temp = match.group(Code.Override);
                    if (temp != null) {
                        output.write("\n* @Override ");
                        output.write(temp);
                    }

                    temp = match.group(Code.Param);
                    if (temp != null) {
                        String[] tempArr = temp.split("(?<=</dd>)\r?\n?(?=<dd>)");
                        for (String i : tempArr) {
                            output.write("\n* @param ");
                            output.write(i
                                    .replace(" - ", " ")
                                    .replaceAll("<(?:.(?<![<>]))+>", ""));
                        }
                    }

                    temp = match.group(Code.Return);
                    if (temp != null) {
                        output.write("\n* @return ");
                        output.write(temp);
                    }

                    temp = match.group(Code.Throw);
                    if (temp != null) {
                        String[] tempArr = temp.split("(?<=</dd>)\r?\n?(?=<dd>)");
                        for (String i : tempArr) {
                            output.write("\n* @throws ");
                            output.write(i
                                    .replaceAll("<(?:.(?<![<>]))+>", ""));
                        }
                    }

                    temp = match.group(Code.Since);
                    if (temp != null) {
                        output.write("\n* @since ");
                        output.write(temp);
                    }

                    temp = match.group(Code.SeeAlso);
                    if (temp != null) {
                        String[] tempArr = temp.split("\\s+,\\s+");
                        for (String i : tempArr) {
                            output.write("\n* @see ");
                            output.write(i);
                        }
                    }

                    output.write("\n*/\n");


                    output.write(match.group(Code.Name)
                            .replace("&nbsp;", " ")
                            .replaceAll("&#[0-9]+;", "")
                            .replaceAll("<(?:.(?<![<>]))+>", "")
                            .replaceAll("\r?\n", " "));
                    output.write("{}");
                }
            }
        }
        output.write("\n}");
        output.flush();
        output.close();
    }

    /**
     * Test for each part of Code.
     *
     * @param content String of the html file.
     */
    public static void test(String content) {
        Matcher match;
        /*
        content = content.substring(Code.ConstructorDetail.getStart(content), Code.ConstructorDetail.getEnd(content));
        System.out.println(content);
        match = Code.ConstructorDetail.getMatcher(content);
        System.out.println();
        System.out.println(match.find());
        for (String i : Code.ConstructorDetail.parts) {
            System.out.println(i + ": " + match.group(i));
        }

//        System.out.println("begin["+match.group("test")+"]end");
        //*/

        //*
        for (Code c : Code.values()) {
            System.out.println();
            System.out.println();
            match = c.getMatcher(content);
            if (c.getStart(content) == -1) {
                System.out.println("This Javadoc has no " + c.name());
                System.out.println();
                continue;
            }
            System.out.println("[" + c.getStart(content) + ", " + (int) c.getRegion(content) + "]");
            match.region(c.getStart(content), (int) c.getRegion(content));
            while (match.find()) {
                c.count++;
                System.out.println("Found a " + c.name());
                for (String i : c.parts) {
                    System.out.println(i + ": " + match.group(i));
                }
                System.out.println();
            }
        }
        System.out.println("ClassSummary:" + Code.ClassSummary.count);
        System.out.println("ConstructorDetail:" + Code.ConstructorDetail.count);
        System.out.println("FieldDetail:" + Code.FieldDetail.count);
        System.out.println("MethodDetail:" + Code.MethodDetail.count);
        //*/
    }

}
