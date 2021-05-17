package org.wolkenproject.papaya.compiler;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.archive.ArchivedMember;
import org.wolkenproject.papaya.archive.ArchivedMethod;
import org.wolkenproject.papaya.archive.ArchivedStructureI;
import org.wolkenproject.papaya.parser.Node;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FunctionScope {
    private final ArchivedMethod        method;
    private final ArchivedStructureI    parent;
    private final Stack<ArchivedMember> stack;
    private final Map<String, Integer>  tracker;
    private final CompilationScope      compilationScope;
    private final ByteArrayOutputStream outputStream;

    public FunctionScope(ArchivedMethod method, ArchivedStructureI parent, CompilationScope scope) {
        this.method = method;
        this.parent = parent;
        stack = new Stack<>();
        tracker = new HashMap<>();
        compilationScope = scope;
        outputStream = new ByteArrayOutputStream();
    }

    public void declare(ArchivedMember member) throws PapayaException {
        if (tracker.containsKey(member.getName())) {
            throw new PapayaException("redeclaration of symbol '" + member.getName() + "' at " + member.getLineInfo());
        }

        tracker.put(member.getName(), stack.size());
        stack.push(member);
    }

    public boolean isTopOfStack(String name, LineInfo info) throws PapayaException {
        if (!tracker.containsKey(name)) {
            throw new PapayaException("reference to undeclared symbol '" + name + "' at " + info);
        }

        return tracker.get(name).equals(stack.size() - 1);
    }

    public ArchivedStructureI getParent() {
        return parent;
    }

    public Stack<ArchivedMember> getStack() {
        return stack;
    }

    public Map<String, Integer> getTracker() {
        return tracker;
    }

    public CompilationScope getCompilationScope() {
        return compilationScope;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void traverse(Node node, FunctionScope scope, CompilationScope compilationScope) throws PapayaException {
        System.out.println(node.getTokenRule() + " " + node.getLineInfo());
        if (compilationScope.getTraverserMap().containsKey(node.getTokenRule())) {
            compilationScope.getTraverserMap().get(node.getTokenRule()).onEnter(node, scope, compilationScope);
        } else if (node.getChildren().size() > 0) {
            for (Node child : node) {
                traverse(child, scope, compilationScope);
            }
        } else {
            throw new PapayaException("traverse-err: reached unknown rule '" + node.getTokenRule() + "' at " + node.getLineInfo());
        }
    }

    public FunctionScope push() {
        return new FunctionScope(method, parent, compilationScope);
    }

    public boolean contains(String ident) {
        return tracker.containsKey(ident) || (parent.containsMember(ident));
    }

    public void makeTop(String ident, LineInfo info) throws PapayaException {
        if (tracker.containsKey(ident)) {
            if (!isTopOfStack(ident, info)) {
                int distance = stack.size() - tracker.get(ident);
                if (distance <= 16) {
                    outputStream.write(compilationScope.getOpcodeRegister().forName("dup" + distance));
                } else {
                    throw new PapayaException("'" + ident + "' is too far back in the stack.");
                }
            }
        } else if (parent.containsMember(ident)) {
            ArchivedMember member = parent.getMember(ident);

            if (method.isStatic() && !member.isStatic()) {
                throw new PapayaException("reference to '" + ident + "' from a static function at " + info);
            }

            if (member.isStatic()) {
                outputStream.write(compilationScope.getOpcodeRegister().forName("loadstatic"));
                outputStream.write(0);
            } else {
                outputStream.write(compilationScope.getOpcodeRegister().forName("load"));
                outputStream.write(0);
            }
        }
    }

    public ArchivedMethod getMethod() {
        return method;
    }
}
