package com.bhf.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A processor to create a Reusable class that implements clear() and
 * copyFrom() semantics. Useful for flyweights, elements in a RingBuffer etc.
 * Uses composition with delegation.
 */
@SupportedAnnotationTypes("com.bhf.annotations.Reusable")
@AutoService(Processor.class)
public class ReusableProcessor extends AbstractProcessor {
    private static final String DEFAULT_BOOLEAN_VALUE = "false";
    private static final String DEFAULT_BYTE_VALUE = "0";
    private static final String DEFAULT_SHORT_VALUE = "0";
    private static final String DEFAULT_INT_VALUE = "0";
    private static final String DEFAULT_LONG_VALUE = "0";
    private static final String DEFAULT_CHAR_VALUE = "0";
    private static final String DEFAULT_FLOAT_VALUE = "0";
    private static final String DEFAULT_DOUBLE_VALUE = "0";
    public static final String fieldAccessPrefix = "        object.";
    public static final String fieldEquals = " = ";
    public static final String SEMI_COLON = ";";
    private Messager msg;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.msg = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            msg.printMessage(Diagnostic.Kind.NOTE, "Processing Annotation: " + annotation.getSimpleName());
            var rootElements = roundEnv.getRootElements();

            rootElements.stream().findFirst().ifPresent(firstElement -> {
                String className = firstElement.asType().toString();

                msg.printMessage(Diagnostic.Kind.NOTE, "Classname: " + className);

                Map<Name, TypeMirror> fields = new HashMap<>();
                roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {
                    var name = element.getSimpleName();
                    var type = element.asType();
                    msg.printMessage(Diagnostic.Kind.NOTE, "Element: " + element.getSimpleName() + ", Type: " + type);
                    fields.put(name, type);
                });

                try {
                    writeReusableFile(className, fields);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        msg.printMessage(Diagnostic.Kind.NOTE, "Finished Processing all annotations");

        return true;
    }

    /**
     * Using the provided className and field to type mapping will create
     * a Reusable{className} file that implements clear() and copyFrom().
     *
     * @param className  The className of the annotated file.
     * @param nameToType A mapping of the field name to the type.
     * @throws IOException Throws exception if it can't write the file.
     */
    private void writeReusableFile(String className, Map<Name, TypeMirror> nameToType) throws IOException {

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String reusableSimpleClassName = "Reusable" + simpleClassName;
        String reusableQualifiedClassName = packageName + "." + reusableSimpleClassName;

        msg.printMessage(Diagnostic.Kind.NOTE, "Package: " + packageName
                + ", simpleClassName: " + simpleClassName
                + ", reusableSimpleClassName: " + reusableSimpleClassName
                + ", reusableQualifiedClassName: " + reusableQualifiedClassName);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(reusableSimpleClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            startFile(packageName, out, reusableSimpleClassName);
            implementConstructor(simpleClassName, out);
            implementBuildMethod(simpleClassName, out);
            implementClearMethod(nameToType, out);
            implementCopyFromMethod(nameToType, reusableSimpleClassName, out);
            endFile(out);
        }
    }

    /**
     * Terminate the file.
     *
     * @param out The output {@link PrintWriter}.
     */
    private static void endFile(PrintWriter out) {
        out.println("}");
    }

    /**
     * Start the file by adding the package name, class name.
     *
     * @param packageName             The package name.
     * @param out                     The output {@link PrintWriter}.
     * @param reusableSimpleClassName The simple classname for the Reusable class that is generated.
     */
    private static void startFile(String packageName, PrintWriter out, String reusableSimpleClassName) {
        if (packageName != null) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println();
        }

        out.print("public class ");
        out.print(reusableSimpleClassName);
        out.println(" {");
        out.println();
    }

    /**
     * Implement a constructor.
     *
     * @param simpleClassName The simple class name.
     * @param out             The output {@link PrintWriter}.
     */
    private static void implementConstructor(String simpleClassName, PrintWriter out) {
        out.print("    private ");
        out.print(simpleClassName);
        out.print(" object = new ");
        out.print(simpleClassName);
        out.println("();");
        out.println();
    }

    /**
     * Implement a build method.
     *
     * @param simpleClassName The simple class name.
     * @param out             The output {@link PrintWriter}.
     */
    private static void implementBuildMethod(String simpleClassName, PrintWriter out) {
        out.print("    public ");
        out.print(simpleClassName);
        out.println(" build() {");
        out.println("        return object;");
        out.println("    }");
        out.println();
    }

    /**
     * Implement the clear method for a reusable object.
     *
     * @param fieldTypeMap The Name to Type mapping.
     * @param out          The output.
     */
    private void implementClearMethod(Map<Name, TypeMirror> fieldTypeMap, PrintWriter out) {
        out.print("    public ");
        out.print("void");
        out.print(" ");
        out.print("clear");
        out.print("() {");
        out.println();

        fieldTypeMap.forEach((fieldName, argumentType) -> {
            switch (argumentType.getKind()) {
                case BOOLEAN ->
                        out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_BOOLEAN_VALUE + SEMI_COLON);
                case BYTE -> out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_BYTE_VALUE + SEMI_COLON);
                case SHORT ->
                        out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_SHORT_VALUE + SEMI_COLON);
                case INT -> out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_INT_VALUE + SEMI_COLON);
                case LONG -> out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_LONG_VALUE + SEMI_COLON);
                case CHAR -> out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_CHAR_VALUE + SEMI_COLON);
                case FLOAT ->
                        out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_FLOAT_VALUE + SEMI_COLON);
                case DOUBLE ->
                        out.println(fieldAccessPrefix + fieldName + fieldEquals + DEFAULT_DOUBLE_VALUE + SEMI_COLON);
                default ->
                        msg.printMessage(Diagnostic.Kind.NOTE, "Unhandled type for field " + fieldName + ", with type " + argumentType.getKind());
            }
        });

        out.println("    }");
        out.println();
    }

    /**
     * Implement the copyFrom method for a reusable object.
     *
     * @param fieldTypeMap The Name to Type mapping.
     * @param out          The output.
     */
    private void implementCopyFromMethod(Map<Name, TypeMirror> fieldTypeMap, String reusableClassName, PrintWriter out) {
        out.print("    public ");
        out.print("void");
        out.print(" ");
        out.print("copyFrom");
        out.print("(" + reusableClassName + " source) {");
        out.println();

        fieldTypeMap.forEach((fieldName, argumentType) -> {
            switch (argumentType.getKind()) {
                case BOOLEAN, DOUBLE, FLOAT, CHAR, LONG, INT, SHORT, BYTE ->
                        out.println(fieldAccessPrefix + fieldName + fieldEquals + "source.object." + fieldName + SEMI_COLON);
                default ->
                        msg.printMessage(Diagnostic.Kind.NOTE, "Unhandled type for field " + fieldName + ", with type " + argumentType.getKind());
            }
        });

        out.println("    }");
        out.println();
    }
}
