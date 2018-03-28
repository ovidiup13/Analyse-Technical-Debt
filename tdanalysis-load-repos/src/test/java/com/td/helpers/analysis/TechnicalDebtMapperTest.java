package com.td.helpers.analysis;

import static org.junit.Assert.assertEquals;

import com.td.models.CommitTD.CodeLocation;

import org.junit.Test;

public class TechnicalDebtMapperTest {

    @Test
    public void testCodeLocation() {
        String line = "L D RCN: Redundant nullcheck of mapping, which is known to be non-null in org.springframework.hateoas.core.AnnotationMappingDiscoverer.getMapping(Class, Method)  Redundant null check At AnnotationMappingDiscoverer.java:[line 108]";
        CodeLocation expected = new CodeLocation("AnnotationMappingDiscoverer.java", "108");
        CodeLocation actual = TechnicalDebtMapper.parseLocation(line);

        assertEquals(expected, actual);
    }

    @Test
    public void testCodeLocationMultipleLines() {
        String line = "H B ES: Comparison of String parameter using == or != in org.springframework.hateoas.hal.forms.HalFormsTemplate.withTitle(String)   At HalFormsTemplate.java:[lines 62-80]";
        CodeLocation expected = new CodeLocation("HalFormsTemplate.java", "62-80");
        CodeLocation actual = TechnicalDebtMapper.parseLocation(line);
        assertEquals(expected, actual);
    }

    @Test
    public void testCodeLocationAtSmall() {
        String line = "M D RCN: Redundant nullcheck of $affordances, which is known to be non-null in org.springframework.hateoas.Link.hashCode()  Redundant null check at Link.java:[line 55]";
        CodeLocation expected = new CodeLocation("Link.java", "55");
        CodeLocation actual = TechnicalDebtMapper.parseLocation(line);
        assertEquals(expected, actual);
    }

    @Test
    public void testClassLocation() {
        String line = "In Link.java";
        CodeLocation expected = new CodeLocation("Link.java", null);
        CodeLocation actual = TechnicalDebtMapper.parseLocation(line);

        assertEquals(expected.getFileName(), actual.getFileName());
    }
}