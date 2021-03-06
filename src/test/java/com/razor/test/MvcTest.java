package com.razor.test;

import com.fedepot.Razor;
import com.fedepot.mvc.middleware.CookieParserMiddleware;
import com.fedepot.mvc.middleware.CorsMiddleware;
import com.razor.test.controllers.BookController;

/**
 * Copyright (c) 2017, Touchumind<chinash2010@gmail.com>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


public class MvcTest {

    public static void main(String[] args) {
        Razor razor = Razor.self();

        razor.webRoot("WWW");
        razor.addStatic("/txt/");
        razor.set404("404.htm");
//        razor.addStatic("/images/");

//        razor.use((req, res) -> {
//            System.out.println(req.path());
//        });
//
//        razor.use((req, res) -> {
//            System.out.println(req.getHost());
//            System.out.println(req.getHostname());
//        });
//
//        razor.use(BookController.class, (req, res) -> {
//
//            res.status(404);
//
//            System.out.println("BookController middleware call " + res.flushed());
//
//            res.end("haha");
//        });

        razor.mapStatic("abc", "images/abc");

        razor.use(new CorsMiddleware("http://localhost"));

        razor.use(new CookieParserMiddleware());

        //razor.useTemplateEngine("Jtwig");

        razor.start(MvcTest.class, "127.0.0.1", 8090, args);
    }
}
