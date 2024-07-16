/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.repl;

import ie.francis.fspnew.builtin.type.Lambda;
import ie.francis.fspnew.compiler.Artifact;
import ie.francis.fspnew.compiler.Compiler;
import ie.francis.fspnew.exception.SyntaxErrorException;
import ie.francis.fspnew.exception.TypeErrorException;
import ie.francis.fspnew.node.LambdaNode;
import ie.francis.fspnew.node.Node;
import ie.francis.fspnew.visitor.JavaClassGeneratorVisitor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Repl {

  private final ReplClassLoader replClassLoader;
  private final Environment environment;

  public Repl() {
    replClassLoader = new ReplClassLoader();
    environment = new Environment();
  }

  public void loop() throws IOException {
    while (true) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("> ");
      String input = reader.readLine();
      // String input = "'((lambda (x y z) (if false x y)) 1 2 3)";
      if (input.equalsIgnoreCase("")) {
        continue;
      }
      if (input.equalsIgnoreCase("exit")) {
        break;
      }

      Compiler compiler = new Compiler(new JavaClassGeneratorVisitor(environment));
      //      Compiler compiler = new Compiler(new Generator("Repl"));
      try {
        List<Node> nodes = compiler.compileToAst(input);

        // Wrap in a lambda if running in REPL mode
        List<Node> tree = nodes;
        for (Node node : nodes) {
          LambdaNode lambda = new LambdaNode();
          lambda.setBody(node);
          tree = new ArrayList<>();
          tree.add(lambda);
        }

        List<Artifact> artifacts = compiler.compileAst(tree);
        for (Artifact artifact : artifacts) {
          replClassLoader.defineClass(artifact.getName(), artifact.getData());
          //          try (FileOutputStream fos = new FileOutputStream(artifact.getName() +
          // ".class")) {
          //            fos.write(artifact.getData());
          //          } catch (IOException ex) {
          //            throw new RuntimeException(ex);
          //          }
        }
        Artifact last = artifacts.get(artifacts.size() - 1);
        Class<?> c = replClassLoader.loadClass(last.getName());
        Constructor<?> ctor = c.getConstructor();
        Object object = ctor.newInstance();
        Lambda l = ((Lambda) object);
        l.call();
        System.out.println(String.format("= %s", ((Lambda) object).call()));

        //        System.out.println(String.format(">> %s", c.getMethod("call").invoke(null)));
      } catch (SyntaxErrorException | TypeErrorException ex) {
        ex.printStackTrace();
      } catch (ClassNotFoundException
          | InvocationTargetException
          | IllegalAccessException
          | NoSuchMethodException
          | InstantiationException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
