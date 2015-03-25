package com.yarenty.hj;

/**
 * Created by yarenty on 25/03/2015.
 */

import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module1.async;
import static edu.rice.hj.Module1.finish;

public class HelloWorld {
    public static void main(final String[] args) throws SuspendableException {
        finish(() -> {
            async(() ->
                            System.out.println("Hello World - 1")
            );
            async(() ->
                            System.out.println("Hello World - 2")
            );
            async(() ->
                            System.out.println("Hello World - 3")
            );
        });
    }
}
