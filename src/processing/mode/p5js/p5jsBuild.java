package processing.mode.p5js;

import java.io.File;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ECMAException;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.ParserException;
import jdk.nashorn.internal.runtime.options.Options;

import processing.app.Base;
import processing.app.Sketch;
import processing.app.SketchException;
import processing.app.ui.Editor;
import processing.core.PApplet;


public class p5jsBuild {
  ScriptEngine engine;
//  static ScriptEngine engine =
//    new ScriptEngineManager().getEngineByName("javascript");


  public p5jsBuild(final Editor editor, Sketch sketch) throws SketchException {
    //engine = new ScriptEngineManager(null).getEngineByName("javascript");
    //engine = new ScriptEngineManager(null).getEngineByName("nashorn");
    //engine = new ScriptEngineManager(ClassLoader.getSystemClassLoader()).getEngineByName("javascript");
    //ScriptEngineManager manager = new ScriptEngineManager();

    /*
    ScriptEngineManager manager = new ScriptEngineManager(Base.class.getClassLoader());
    engine = manager.getEngineByName("js");
    if (engine == null) {
      throw new SketchException("script engine is null");
    }
//    try {
//      engine.eval("load(\"nashorn:mozilla_compat.js\")");
//      //System.out.println(engine.eval("1+1"));
////      System.out.println(engine.eval("void setup() {}"));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    String[] paths = Util.listFiles(sketch.getFolder(), false, ".js");
//    PApplet.println("files:");
//    PApplet.printArray(paths);
    File mainFile = sketch.getMainFile();
//    System.out.println("main file is " + mainFile);
    //File[] files = new File[paths.length];

//    for (int i = 0; i < paths.length; i++) {
//      //files[i] = new File(paths[i]);
//      File file = new File(paths[i]);
//      if (!file.equals(mainFile)) {
//        handleFile(file);
//      }
//    }

    handleFile(new File(sketch.getFolder(), "libraries/p5.js"));
    handleFile(new File(sketch.getFolder(), "libraries/p5.dom.js"));
    handleFile(new File(sketch.getFolder(), "libraries/p5.sound.js"));
    handleFile(mainFile);
    */

    Options options = new Options("nashorn");
    options.set("anon.functions", true);
    options.set("parse.only", true);
    options.set("scripting", true);

    ErrorManager errors = new ErrorManager() {
      @Override
      public void error(ParserException ex) {
        //handleError(ex, true);
        //sketch.getMode().
        editor.statusError(new SketchException(ex.getMessage(), 0, ex.getLineNumber()));
      }

//      @Override
//      public void warning(ParserException ex) {
//        throw new SketchException(ex.getMessage(), 0, ex.getLineNumber());
//      }
    };
    //Context context = new Context(options, errors, Thread.currentThread().getContextClassLoader());
    Context context = new Context(options, errors, Base.class.getClassLoader());
//    Source source = Source.sourceFor("test", "var a = 10; var b = a + 1;" +
//        "function someFunction() { return b + 1; }  ");
    /*
    try {
      Source source = Source.sourceFor(sketch.getName(), sketch.getMainFile());
      Parser parser = new Parser(context.getEnv(), source, errors);
      FunctionNode functionNode = parser.parse();
      Block block = functionNode.getBody();
      List<Statement> statements = block.getStatements();
      for (Statement s : statements) {
        System.out.println(s);
      }
      System.out.println("errors = " + errors.getNumberOfErrors());
      System.out.println("warnings = " + errors.getNumberOfWarnings());
    } catch (IOException e) {
      e.printStackTrace();
    }
       */
    Context.setGlobal(context.createGlobal());
    String code = PApplet.join(PApplet.loadStrings(sketch.getMainFile()), "\n");
    try {
      /*String json =*/ ScriptUtils.parse(code, sketch.getName(), true);
      //System.out.println(json);
    } catch (ECMAException ecma) {
      //System.out.println("err: " + ecma.getEcmaError());
      String message = ecma.getMessage();
      String[] parts = PApplet.split(message, ':');
      if (parts.length > 3) {
        message = parts[3];
        message = message.substring(message.indexOf(' '));
      }
      throw new SketchException(parts[3], 0, ecma.getLineNumber(),
                                ecma.getColumnNumber(), false);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getClass().getName());
    }
  }


  protected void handleFile(File file) throws SketchException {
    try {
      System.out.println("handling " + file);
      engine.eval(PApplet.createReader(file));
    } catch (ScriptException se) {
      int line = se.getLineNumber();
      throw new SketchException(se.getMessage(), 0, line);
    }
  }


  //        throw new SketchException(ex.getMessage(), 0, ex.getLineNumber());
//  protected void handleError()


  public boolean export() {
    return false;
  }
}