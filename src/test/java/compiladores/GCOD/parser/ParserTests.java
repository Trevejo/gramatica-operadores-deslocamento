package compiladores.GCOD.parser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ParserTests {

    @Test
    public void testValidSimpleIdentifier() {
        Parser parser = new Parser("id");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidParenthesizedExpression() {
        Parser parser = new Parser("(id)");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidAddition() {
        Parser parser = new Parser("id + id");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidSubtraction() {
        Parser parser = new Parser("id - id");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidLeftShift() {
        Parser parser = new Parser("id << id");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidRightShift() {
        Parser parser = new Parser("id >> id");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidComplexExpression() {
        Parser parser = new Parser("(id + id) << (id - id)");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidNestedParentheses() {
        Parser parser = new Parser("((id))");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testValidComplexOperatorPrecedence() {
        Parser parser = new Parser("id + id << id - id >> id + id");
        assertNotNull(parser.parse());
        assertTrue(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidSingleLeftShift() {
        Parser parser = new Parser("<");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidSingleRightShift() {
        Parser parser = new Parser(">");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidUnmatchedParentheses() {
        Parser parser = new Parser("(id");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidEmptyParentheses() {
        Parser parser = new Parser("()");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidOperatorSequence() {
        Parser parser = new Parser("id + + id");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidEmptyInput() {
        Parser parser = new Parser("");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidMissingOperand() {
        Parser parser = new Parser("id +");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }

    @Test
    public void testInvalidSpecialCharacters() {
        Parser parser = new Parser("id @ id");
        assertNull(parser.parse());
        assertFalse(parser.getErrors().isEmpty());
    }
} 