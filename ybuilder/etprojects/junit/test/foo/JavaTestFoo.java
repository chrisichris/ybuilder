package foo;

import org.apache.commons.lang3.StringUtils;

public class JavaTestFoo {
    
    public String myString = StringUtils.chomp("hier");

    public void foo(String name) {
        junit.framework.Assert.assertEquals(1,1);
    }
}

