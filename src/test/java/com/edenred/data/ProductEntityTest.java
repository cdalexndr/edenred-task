package com.edenred.data;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductEntityTest {

    public static Object[][] normNames() {
        return new Object[][]{
                {"", ""},
                {"name", "name"},
                {"nAMe", "name"},
                {" \t  name \t ", "name"},
                {" a\tB ", "ab"},
                };
    }

    @ParameterizedTest
    @MethodSource("normNames")
    public void dataProviderTest( String name, String expected ) {
        assertEquals( expected, ProductEntity.normalizeName( name ) );
    }
}