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


package com.razor.mvc.middleware;

import com.razor.mvc.http.HttpMethod;
import com.razor.mvc.http.Request;
import com.razor.mvc.http.Response;

import java.util.Arrays;

import static com.razor.mvc.http.HttpHeaderNames.*;

/**
 * Cors middleware default implement
 */
public class CorsMiddleware implements Middleware {

    private String[] whitelist;

    public CorsMiddleware() {

        whitelist = new String[]{"*"};
    }

    public CorsMiddleware(String... originWhitelist) {

        whitelist = originWhitelist;
    }

    @Override
    public void apply(Request req, Response res) {

        if (req.method().equals(HttpMethod.OPTIONS)) {

            String allowOrigin = null;
            String origin = req.getOrigin();

            if (whitelist.length > 0 && whitelist[0].equals("*")) {

                allowOrigin = "*";
            } else if (Arrays.asList(whitelist).contains(origin)) {

                allowOrigin = origin;
            }

            if (allowOrigin != null) {

                res.header(VARY, "Origin");
                res.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                res.header(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin);
                res.header(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
                res.header(ACCESS_CONTROL_ALLOW_HEADERS, "X-Requested-With, Content-Type, Ajax");
                res.end();
            } else {

                res.sendStatus(405);
            }
        }
    }
}
