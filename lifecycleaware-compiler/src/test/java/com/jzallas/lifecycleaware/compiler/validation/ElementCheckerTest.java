package com.jzallas.lifecycleaware.compiler.validation;

import com.google.common.collect.ImmutableList;
import com.jzallas.lifecycleaware.compiler.ProcessingException;
import com.jzallas.lifecycleaware.compiler.validation.rule.ProcessingRule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.jzallas.lifecycleaware.compiler.TestUtils.assertError;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ElementCheckerTest {
    @Mock
    private Elements mockElementUtils;

    @Mock
    private Types mockTypeUtils;

    @Mock
    private Messager mockMessager;

    @Mock
    private Element mockElement;

    @Mock
    private ProcessingRule mockRule;

    private ElementChecker testElementChecker;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testElementChecker = spy(new ElementChecker(mockElementUtils, mockTypeUtils, mockMessager));

        doReturn(ImmutableList.of(mockRule))
                .when(testElementChecker)
                .getRules();

        doReturn("testString")
                .when(mockRule)
                .getMessage();
    }

    @Test
    public void testValidateSuccess() throws Exception {
        doReturn(true)
                .when(mockRule)
                .apply(mockElement);

        assertTrue(testElementChecker.validate(mockElement));
    }

    @Test
    public void testValidateFailure() throws Exception {
        doReturn(false)
                .when(mockRule)
                .apply(mockElement);

        assertError(ProcessingException.class,
                () -> testElementChecker.validate(mockElement));

        // validate that we at least notify the developer
        verify(mockMessager, times(1))
                .printMessage(
                        eq(Diagnostic.Kind.ERROR),
                        any(CharSequence.class),
                        eq(mockElement)
                );
    }
}