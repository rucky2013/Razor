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


package com.fedepot.mvc.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Router matcher
 *
 * @author Touchumind
 * @since 0.0.1
 */
@Slf4j
public class RouteMatcher {

    private static Pattern ROUTE_PREFIX_PATTERN = Pattern.compile("^/([0-9a-zA-Z-_/]+)?$");

    private static Pattern ROUTE_PATTERN = Pattern.compile("^(([^/])([0-9a-zA-Z-_/{}:.]+)([^/]))?$");

    /**
     * Route prefix string, applied to a controller, regex is not supported
     */
    private String routePrefix;

    /**
     * Route string, applied to a action, support `path/{string:category}/{int:id}.html` format
     */
    private String route;

    /**
     * Type of parameters in route path, int or string
     */
    private String[] paramTypes;

    /**
     * Name of parameters in route path
     */
    private String[] paramNames;

    /**
     * Match pattern for route prefix combining with route
     */
    private Pattern pattern;

    private boolean isValid = true;

    boolean isValid() {

        return isValid;
    }

    /**
     * Indicate the route has a universal match or not
     */
    private boolean isUniversal = false;

    boolean isUniversal() {

        return isUniversal;
    }

    String getRoutePrefix() {

        return routePrefix;
    }

    public String getRoute() {

        return route;
    }

    public String[] getParamNames() {

        return paramNames;
    }

    public String[] getParamTypes() {

        return paramTypes;
    }

    /**
     * Get url parameters for one request
     *
     * @param path url path
     * @return parameter values in url
     */
    public PathParameter[] getParams(String path) {

        int paramCount = Math.min(paramTypes.length, paramNames.length);

        if (paramCount < 1) {

            return null;
        }

        Matcher matcher = pattern.matcher(path);

        if (!matcher.find()) {

            return null;
        }

        PathParameter[] params = new PathParameter[paramCount];

        for (int i = 0; i < paramCount; i++) {

            String value = matcher.group(i + 1);

            if (StringUtils.equals(paramTypes[i], "int")) {

                params[i] = new PathParameter(paramNames[i], Integer.parseInt(value));
            } else if (StringUtils.equals(paramTypes[i], "long")) {

                params[i] = new PathParameter(paramNames[i], Long.parseLong(value));
            } else {

                params[i] = new PathParameter(paramNames[i], value);
            }
        }

        return params;
    }

    Pattern getPattern() {

        return pattern;
    }

    RouteMatcher(String routePrefix, String route) {

        this.routePrefix = routePrefix.startsWith("/") ? routePrefix : "/".concat(routePrefix);
        // books/{string:category}/{int:id}.html
        this.route = route;

        isUniversal = "*".equals(route) || route.contains("/*") || Pattern.compile("\\{([0-9a-zA-Z]+)?:?([0-9a-zA-Z_]+)}").matcher(route).find();

        if (!this.validateRoutes()) {
            isValid = false;
            return;
        }

        // find '{type:param}' pairs
        int start = 0;
        boolean inPair = false;
        boolean inName = false;
        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        StringBuilder currentParamType = new StringBuilder();
        StringBuilder currentParamName = new StringBuilder();
        StringBuilder patternBuilder = new StringBuilder("^");
        patternBuilder.append(this.routePrefix);

        if (!this.routePrefix.endsWith("/")) {
            patternBuilder.append("/");
        }

        while (start < route.length()) {

            String ch = route.substring(start, start + 1);

            if (StringUtils.equals(ch, "{")) {

                inPair = true;
                inName = false;
                currentParamType.setLength(0);
                currentParamName.setLength(0);
            } else if (StringUtils.equals(ch, "}")) {

                inPair = false;
                inName = false;

                if (StringUtils.isEmpty(currentParamName.toString())) {

                    types.add("String");
                    names.add(currentParamType.toString());
                    patternBuilder.append("([0-9a-zA-Z-_]+)");
                } else {

                    boolean isNumber = StringUtils.equals(currentParamType.toString().toLowerCase(), "int") || StringUtils.equals(currentParamType.toString().toLowerCase(), "long");
                    if (isNumber) {

                        types.add(currentParamType.toString().toLowerCase());
                    } else {

                        types.add("String");
                    }
                    names.add(currentParamName.toString());
                    patternBuilder.append(isNumber ? "([0-9]+)" : "([0-9a-zA-Z-_]+)");
                }
            } else if (StringUtils.equals(ch, ":")) {

                inName = true;
            } else if (inPair) {

                if (inName) {

                    currentParamName.append(ch);
                } else {

                    currentParamType.append(ch);
                }
            } else {

                patternBuilder.append("*".equals(ch) ? "([0-9a-zA-Z-_./]+)?" : ch);
            }

            start++;
        }

        patternBuilder.append("$");

        pattern = Pattern.compile(patternBuilder.toString());
        paramNames = names.toArray(new String[0]);
        paramTypes = types.toArray(new String[0]);
    }

    private boolean validateRoutes() {

        Matcher matcher = ROUTE_PREFIX_PATTERN.matcher(routePrefix);

        if (!matcher.matches()) {

            log.error("Route Prefix {} is illegal, should consist of '0-9 a-z A-Z - _ /'", routePrefix);
            return false;
        }

        if ("*".equals(route)) {

            return true;
        }

        matcher = ROUTE_PATTERN.matcher(route.replace(" ", ""));

        if (!matcher.matches()) {

            log.error("Route {} is illegal", route);
        }

        return true;
    }
}
