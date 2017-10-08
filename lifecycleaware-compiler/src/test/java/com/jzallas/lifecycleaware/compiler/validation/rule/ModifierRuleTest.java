package com.jzallas.lifecycleaware.compiler.validation.rule;

import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class ModifierRuleTest {
    @Mock
    private Elements mockElementUtils;

    @Mock
    private Types mockTypeUtils;

    @Mock
    private Element mockElement;

    private ModifierRule testRule;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testRule = new ModifierRule(mockElementUtils, mockTypeUtils);
    }

    @Test
    public void testApplyPass() throws Exception {
        doReturn(ImmutableSet.of(),
                ImmutableSet.of(Modifier.PUBLIC))
                .when(mockElement)
                .getModifiers();

        // test package-private
        assertTrue(testRule.apply(mockElement));

        // test public
        assertTrue(testRule.apply(mockElement));
    }

    @Test
    public void testApplyFail() throws Exception {
        doReturn(ImmutableSet.of(Modifier.PRIVATE),
                ImmutableSet.of(Modifier.PROTECTED),
                ImmutableSet.of(Modifier.FINAL))
                .when(mockElement)
                .getModifiers();

        // test private
        assertFalse(testRule.apply(mockElement));

        // test protected
        assertFalse(testRule.apply(mockElement));

        // test final
        assertFalse(testRule.apply(mockElement));
    }

    @Test
    public void getMessage() throws Exception {
        String message = testRule.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }
}