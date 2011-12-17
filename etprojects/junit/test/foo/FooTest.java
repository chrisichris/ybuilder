package foo;

import junit.framework.*;

import org.apache.commons.lang3.StringUtils;

public class FooTest extends TestCase {
    
    public String myString = StringUtils.chomp("hier");

    public void foo(String name) {
        junit.framework.Assert.assertEquals(1,1);
    }
    
    public void testSome() {
        assertTrue(true);
    }
}

