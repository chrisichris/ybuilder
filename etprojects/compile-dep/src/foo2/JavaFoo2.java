package foo2;

import org.apache.commons.lang3.StringUtils;
import foo.JavaFoo;

public class JavaFoo2 {
    public String myString = StringUtils.chomp("hier");
    JavaFoo fo = new JavaFoo();
    public String myString2 = fo.myString + "foo";
}

