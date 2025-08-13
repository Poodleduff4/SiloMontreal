//package de.tum.bgu.msm.utils;
//
//import javax.script.*;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.util.List;
//
//public class GraalJSCalculator {
//    private ScriptEngine engine;
//    private CompiledScript compiledScript;
//    private Invocable invocable;
//
//    public GraalJSCalculator(Reader reader) {
//        logger.debug("Reading script...");
//        String script = this.readScript(reader);
//        logger.debug("Compiling script: " + script);
//        this.compileScript(script);
//
//
//    }
//
//    public String readScript() {
//        BufferedReader bufferedReader = new BufferedReader(reader);
//        StringBuilder scriptBuilder = new StringBuilder();
//
//        try {
//            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
//                scriptBuilder.append(line).append("\n");
//            }
//
//            bufferedReader.close();
//        } catch (IOException e) {
////            logger.fatal("Error in reading script!", e);
//        }
//
//        return scriptBuilder.toString();
//    }
//
//    public void compileScript(String script) {
//        ScriptEngineManager manager = new ScriptEngineManager();
//
//        // Try to get GraalVM JavaScript engine
//        engine = manager.getEngineByName("graal.js");
//
//        if (engine == null) {
//            // Fallback to other engines
//            engine = manager.getEngineByName("javascript");
//        }
//
//        if (engine == null) {
//            throw new RuntimeException("No JavaScript engine available");
//        }
//
//        System.out.println("Using engine: " + engine.getFactory().getEngineName());
//        System.out.println("Version: " + engine.getFactory().getEngineVersion());
//
//        Compilable compileEngine = (Compilable)engine;
//
//        try {
//            this.compiledScript = compileEngine.compile(script);
//            this.compiledScript.eval();
//            this.invocable = (Invocable)this.compiledScript.getEngine();
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public Object evaluate(String script) throws ScriptException {
//        return engine.eval(script);
//    }
//
//    public final T calculate(String function, Object... args) {
//        try {
//            return (T)this.invocable.invokeFunction(function, args);
//        } catch (NoSuchMethodException | ScriptException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public double calculateFormula(String formula, String... variables) throws ScriptException {
//        // Set variables in the engine context
//        for (int i = 0; i < variables.length; i += 2) {
//            if (i + 1 < variables.length) {
//                String varName = variables[i];
//                double value = Double.parseDouble(variables[i + 1]);
//                engine.put(varName, value);
//            }
//        }
//
//        // Evaluate the formula
//        Object result = engine.eval(formula);
//        return ((Number) result).doubleValue();
//    }
//
//    public static void main(String[] args) {
//        try {
//            GraalJSCalculator<Number> calculator = new GraalJSCalculator<Number>();
//
//            // Test basic calculation
//            Object result = calculator.evaluate("Math.pow(2, 3) + Math.sqrt(16)");
//            System.out.println("Basic calculation: " + result);
//
//            // Test with variables
//            double formulaResult = calculator.calculateFormula(
//                    "Math.log(distance) * 0.5 + income * 0.001",
//                    "distance", "10.0",
//                    "income", "50000.0"
//            );
//            System.out.println("Formula result: " + formulaResult);
//
//            // Test modern JavaScript features
//            calculator.evaluate("const data = [1, 2, 3, 4, 5];");
//            calculator.evaluate("const processed = data.filter(x => x > 2).map(x => x * x);");
//            Object processed = calculator.engine.get("processed");
//            System.out.println("Modern JS features: " + processed);
//
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // List all available engines
//    public static void listAvailableEngines() {
//        ScriptEngineManager manager = new ScriptEngineManager();
//        List<ScriptEngineFactory> factories = manager.getEngineFactories();
//
//        System.out.println("Available Script Engines:");
//        for (ScriptEngineFactory factory : factories) {
//            System.out.println("Engine: " + factory.getEngineName());
//            System.out.println("Version: " + factory.getEngineVersion());
//            System.out.println("Language: " + factory.getLanguageName());
//            System.out.println("Names: " + factory.getNames());
//            System.out.println("---");
//        }
//    }
//}
