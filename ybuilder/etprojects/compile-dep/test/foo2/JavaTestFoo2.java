package foo2;

import org.apache.commons.lang3.StringUtils;

public class JavaTestFoo2 {
    
    public String myString = StringUtils.chomp("hier");

    public void foo(String name) {
        new JavaTestFoo2();
        junit.framework.Assert.assertEquals(1,1);
    }
}

