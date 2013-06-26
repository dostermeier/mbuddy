package com.zutubi.services.mail.core.lifecycle;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;

/**
 * The context registry is used in conjunction with the {@link LifecycleComponent}
 * to record all of the {@link ApplicationContext}s that contain lifecycle components.
 *
 * These contexts are then organised into a graph representation of there relative
 * dependencies based on the contexts parent hierarchy.
 */
@Component
public class ContextRegistry {

    private Map<ApplicationContext, Node<ApplicationContext>> registrations = newHashMap();

    public void register(ApplicationContext... contexts) {

        for (ApplicationContext context : contexts) {
            if (!registrations.containsKey(context)) {

                Node<ApplicationContext> contextNode = new Node<ApplicationContext>(context);
                registrations.put(context, contextNode);

                ApplicationContext parent = context.getParent();
                if (parent != null) {

                    register(parent);

                    Node<ApplicationContext> parentContextNode = registrations.get(parent);
                    parentContextNode.addChildren(contextNode);
                    contextNode.setParent(parentContextNode);
                }
            }
        }
    }

    /**
     * Trigger an ordered shutdown of all the registered contexts.  Child contexts
     * will be closed first, root contexts (those without parents) will be closed
     * last.
     *
     * @see ConfigurableApplicationContext#close()
     */
    public void shutdown() {

        ShutdownContextProcedure shutdownContext = new ShutdownContextProcedure();

        // Find the root nodes. We can have multiple.
        IsRootPredicate predicate = new IsRootPredicate();

        for (Node<ApplicationContext> node : filter(registrations.values(), predicate)) {
            visitDepthFirst(shutdownContext, node);
        }
    }

    /**
     * Depth first traversal of the nodes, executing the given procedure when a node is visited.
     *
     * A depth first traversal is one where a nodes children are visited before itself.
     *
     * @param procedure the procedure to be executed on each node.
     * @param node      the node current node being visited.
     */
    private void visitDepthFirst(UnaryProcedure<ApplicationContext> procedure, Node<ApplicationContext> node) {
        for (Node<ApplicationContext> child : node.getChildren()) {
            visitDepthFirst(procedure, child);
        }
        procedure.run(node.getContent());
    }

    /**
     * A predicate implementation that is satisfied by root application contexts.
     */
    private static class IsRootPredicate implements Predicate<Node<ApplicationContext>> {
        @Override public boolean apply(Node<ApplicationContext> node) {
            return node.getParent() == null;
        }
    }

    /**
     * A procedure implementation that triggers a {@link ConfigurableApplicationContext#close()} on
     * any active contexts it receives as a parameter to the {@link #run(ApplicationContext)} method.
     */
    private static class ShutdownContextProcedure implements UnaryProcedure<ApplicationContext> {
        @Override public void run(ApplicationContext context) {
            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
            if (configurableContext.isActive()) {
                configurableContext.close();
            }
        }
    }

    /**
     * A simple node structure for storing the hierarchical nature of registered contexts.
     *
     * @param <T> the type of content stored in this node.
     */
    private static class Node<T> {

        /**
         * The hierarchical content of this node.
         */
        private T content;

        /**
         * The parent of this node.  This will be null for root nodes.
         */
        private Node<T> parent;

        /**
         * This nodes children.
         */
        private List<Node<T>> children = newArrayList();

        private Node(T content) {
            this.content = content;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public Node<T> getParent() {
            return parent;
        }

        public T getContent() {
            return content;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public void addChildren(Node<T>... child) {
            this.children.addAll(Arrays.asList(child));
        }
    }

    /**
     * A generic procedure with a single argument, used as a callback type.
     * @param <T>
     */
    private static interface UnaryProcedure<T> {
        /**
         * Execute the procedure.
         *
         * @param t the single argument.
         */
        void run(T t);
    }
}
