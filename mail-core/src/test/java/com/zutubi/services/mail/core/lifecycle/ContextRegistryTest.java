package com.zutubi.services.mail.core.lifecycle;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ContextRegistryTest {

    private ContextRegistry registry;

    /**
     * A record of the order in which application contexts are closed by the registry.
     */
    private List<ApplicationContext> closedOrder;

    @BeforeMethod
    public void setUp() {
        registry = new ContextRegistry();
        closedOrder = newArrayList();
    }

    @Test
    public void testShutdownNoContexts() {
        registry.register();
        registry.shutdown();
    }

    @Test
    public void testShutdownSingleRootContext() {

        ConfigurableApplicationContext context = activeContext();

        registry.register(context);
        verify(context, never()).close();

        registry.shutdown();
        verify(context, times(1)).close();
    }

    @Test
    public void testShutdownMultipleRootContexts() {
        ConfigurableApplicationContext contextA = activeContext();
        ConfigurableApplicationContext contextB = activeContext();

        registry.register(contextA, contextB);
        verify(contextA, never()).close();
        verify(contextB, never()).close();

        registry.shutdown();
        verify(contextA, times(1)).close();
        verify(contextB, times(1)).close();
    }

    @Test
    public void testShutdownNestedContexts() {
        ConfigurableApplicationContext root = activeContext();
        ConfigurableApplicationContext childA = activeContext(root);
        ConfigurableApplicationContext childB = activeContext(root);
        ConfigurableApplicationContext grandchildA = activeContext(childA);

        registry.register(root, childA, childB, grandchildA);
        verify(root, never()).close();
        verify(childA, never()).close();
        verify(childB, never()).close();
        verify(grandchildA, never()).close();

        registry.shutdown();
        verify(root, times(1)).close();
        verify(childA, times(1)).close();
        verify(childB, times(1)).close();
        verify(grandchildA, times(1)).close();

        assertRelativeCloseOrder(childA, root);
        assertRelativeCloseOrder(grandchildA, childA);
        assertRelativeCloseOrder(childB, root);
    }

    @Test
    public void testShutdownOnInactiveContexts() {
        ConfigurableApplicationContext context = inActiveContext();

        registry.register(context);
        verify(context, never()).close();

        registry.shutdown();
        verify(context, never()).close();
    }

    private ConfigurableApplicationContext context() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                closedOrder.add((ApplicationContext) invocation.getMock());
                return null;
            }
        }).when(context).close();
        return context;
    }

    private ConfigurableApplicationContext activeContext() {
        ConfigurableApplicationContext context = context();
        when(context.isActive()).thenReturn(true);
        return context;
    }

    private ConfigurableApplicationContext inActiveContext() {
        ConfigurableApplicationContext context = context();
        when(context.isActive()).thenReturn(false);
        return context;
    }

    private ConfigurableApplicationContext activeContext(ApplicationContext parent) {
        ConfigurableApplicationContext context = activeContext();
        when(context.getParent()).thenReturn(parent);
        return context;
    }

    private void assertRelativeCloseOrder(ConfigurableApplicationContext contextA, ConfigurableApplicationContext contextB) {
        assertThat(closedOrder.indexOf(contextB), greaterThan(closedOrder.indexOf(contextA)));
    }
}
