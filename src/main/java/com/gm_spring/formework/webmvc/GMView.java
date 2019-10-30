package com.gm_spring.formework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangGuoMing
 * @since 2019/10/30
 */
public class GMView {

    public static final String DEFUALT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public GMView(File viewFile) {
        this.viewFile = viewFile;
    }

    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replaceAll("*", "\\*")
                .replace("+", "\\+").replaceAll("|", "\\|")
                .replace("{", "\\{").replaceAll("}", "\\}")
                .replace("(", "\\(").replaceAll(")", "\\)")
                .replace("^", "\\^").replaceAll("$", "\\$")
                .replace("[", "\\[").replaceAll("]", "\\]")
                .replace("?", "\\?").replaceAll(",", "\\,")
                .replace(".", "\\.").replaceAll("&", "\\$");
    }

    public String getContentType() {
        return DEFUALT_CONTENT_TYPE;
    }

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");
        try {
            String line = null;
            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("￥\\{|\\}}", "");
                    Object paramValue = model.get(paramName);
                    if (null == paramValue) {
                        continue;
                    }
                    //要把￥{}中间这个字符串取出来
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }
        } finally {
            ra.close();
        }
        response.setCharacterEncoding("utf-8");
        //response.setContentType(DEFUALT_CONTENT_TYPE);
        response.getWriter().write(sb.toString());
    }
}
