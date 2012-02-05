package org.shamdata.text;

import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: tom
* Date: 01/02/2012
* Time: 09:22
* To change this template use File | Settings | File Templates.
*/
class SpewInstance {
    String text;
    int weight = 1;
    SpewClass spewClass;

    String render(Character variantChar, Map<String,SpewClass> extraClasses) {
//        System.out.println("SpewInstance(" + text + ").render(" + variantChar + ")");
        StringBuilder sb = new StringBuilder();
        char[] ar = text.toCharArray();
        boolean writing = true;
        int inCurly = 0;
        int variant = spewClass.variants.indexOf(variantChar);
        for(int i = 0; i < ar.length; i++) {
            switch(ar[i]) {
                case '\\':
                    if(i == ar.length) {
                        // shouldn't get this unless someone puts a \ at the end of the line
                        break;
                    }
                    i++;
                    if(ar[i] == '!' ) {
                         if(writing) sb.append('\n'); /* \! = newline */
                    } else if(Character.isLetterOrDigit(ar[i])) { /* reference */
                        int start = i;
                        for(i = i + 1; i < ar.length && Character.isLetterOrDigit(ar[i]); i++);
                        String classref = text.substring(start, i);
                        Character classRefVariant = null;
                        if((i <= ar.length - 2) && ar[i] == '/') {
                            classRefVariant = ar[i + 1];
                            i += 2;
                            if(classRefVariant == '&') {
                                classRefVariant = variantChar;
                            }
                        }
                        SpewClass referredClass = findClass(classref, extraClasses);
                        if(referredClass == null) {
                            throw new IllegalStateException("While rendering line [" + text + "] Attempt to render class with name [" + classref + "], but no such class found");
                        }
                        sb.append(referredClass.render(classRefVariant, extraClasses));
                                 /* skip variant tag */
                        i--;
                    } else {
                        if(writing) sb.append(ar[i]);
                    }
                    break;
                case '{':
                    if(inCurly == 0) {
                        inCurly = 1;
                        writing = (variant == 0);
                    } else if(writing) {
                        sb.append('{');
                    }
                    break;
                case '|':
                    if(inCurly > 0) {
                        writing = (variant == inCurly++ );
                    } else {
                        sb.append('|');
                    }
                    break;
                case '}':
                    if(inCurly > 0) {
                        writing = true;
                        inCurly = 0;
                    } else {
                        sb.append('}');
                    }
                    break;
                default:
                    if( writing) sb.append(ar[i]);
            }
        }

        return sb.toString();
    }

    private SpewClass findClass(String classref, Map<String,SpewClass> extraClasses) {
        SpewClass cls = extraClasses == null ? null : extraClasses.get(classref);
        return cls != null ? cls : spewClass.generator.spewClasses.get(classref);
    }
}
