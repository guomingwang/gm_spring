package com.gm_spring.formework.webmvc.servlet;

import com.gm_spring.formework.annotation.GMController;
import com.gm_spring.formework.annotation.GMRequestMapping;
import com.gm_spring.formework.context.GMApplicationContext;
import com.gm_spring.formework.webmvc.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet 只是作为一个 MVC 的启动入口
 *
 * @author WangGuoMing
 * @since 2019/10/29
 */
@Slf4j
public class GMDispatcherServlet extends HttpServlet {

    //初始参数
    private final String LOCATION = "contextConfigLocation";
    //读者可以思考一下这样设计的经典之处
    //GMHandlerMapping 最核心的设计，也是最经典的
    //它直接干掉了 Struts、Webwork 等 MVC 框架
    private List<GMHandlerMapping> handlerMappings = new ArrayList<GMHandlerMapping>();
    //
    private Map<GMHandlerMapping, GMHandlerAdapter> handlerAdapters = new HashMap<GMHandlerMapping, GMHandlerAdapter>();
    //
    private List<GMViewResolver> viewResolvers = new ArrayList<GMViewResolver>();
    //GMApplicationContext
    private GMApplicationContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //相当于把 IoC 容器初始化了
        context = new GMApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    private void initStrategies(GMApplicationContext context) {
        //有九种策略
        //针对每个用户请求，都会经过一些处理策略处理，最终才能有结果输出
        //每种策略可以自定义干预，但是最终的结果都一致
        // =============== 这里说的就是传说中的九大组件 ===============
        //文件上传解析，如果请求类型是 multipart，将通过 MultipartResolver 进行文件上传解析
        initMultipartResolver(context);
        //本地化解析
        initLocaleResolver(context);
        //主题解析
        initThemeResolver(context);
        /**
         * 我们自己会实现
         */
        //GMHandlerMapping 用来保存 Controller 中配置的 RequestMapping 和 Method 的对应关系
        //通过 HandlerMapping 将请求映射到处理器
        initHandlerMappings(context);
        //HandlerAdapters 用来动态匹配 Method 参数，包括类转换、动态赋值
        //如果执行过程中遇到异常，则交给 HandlerExceptionResolver 来解析
        //直接将请求解析到视图名
        initReuqestToViewNameTranslator(context);
        /**
         * 我们自己会实现
         */
        //通过 ViewResolvers 实现动态模板的解析
        //自己解析一套模板语言
        //通过 viewResolver 将逻辑视图解析到具体视图实现
        initViewResolvers(context);
        //Flash 映射管理器
        initFlashMapManager(context);
    }

    private void initMultipartResolver(GMApplicationContext context) {
        return;
    }

    private void initLocaleResolver(GMApplicationContext context) {
        return;
    }

    private void initThemeResolver(GMApplicationContext context) {
        return;
    }

    private void initReuqestToViewNameTranslator(GMApplicationContext context) {
        return;
    }

    private void initFlashMapManager(GMApplicationContext context) {
        return;
    }

    //将 Controller 中配置的 RequestMapping 和 Method 进行一一对应
    private void initHandlerMappings(GMApplicationContext context) {
        //按照我们通常的理解应该是一个 Map
        //Map <String, Method> map;
        //map.put(url, Method)
        //首先从容器中获取所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName :
                    beanNames) {
                //到了 MVC 层，对外提供的方法只有一个 getBean() 方法
                //返回的对象不是 BeanWrapper，怎么办？
                Object controller = context.getBean(beanName);
                //Object controller = GMAopUtils.getTargetObject(proxy);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(GMController.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(GMRequestMapping.class)) {
                    GMRequestMapping requestMapping = clazz.getAnnotation(GMRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //扫描所有的 public 类型的方法
                Method[] methods = clazz.getMethods();
                for (Method method :
                        methods) {
                    if (!method.isAnnotationPresent(GMRequestMapping.class)) {
                        continue;
                    }
                    GMRequestMapping requestMapping = method.getAnnotation(GMRequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*"))
                            .replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new GMHandlerMapping(pattern, controller, method));
                    log.info("Mapping: " + regex + ", " + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(GMApplicationContext context) {
        //在初始化阶段，我们能做的就是，将这些参数的名字或者类型按照一定的顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置 index，逐个从数组中取值，这样就和参数的顺序无关了
        for (GMHandlerMapping handlerMapping :
                this.handlerMappings) {
            //每个方法都有一个参数列表，这里保存的是形参列表
            this.handlerAdapters.put(handlerMapping, new GMHandlerAdapter());
        }
    }

    private void initViewResolvers(GMApplicationContext context) {
        //在页面中输入 Http://location/first.template
        //解决页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template :
                templateRootDir.listFiles()) {
            this.viewResolvers.add(new GMViewResolver(templateRoot));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //根据用户请求的 URL 来获得一个 Handler
        GMHandlerMapping handler = getHandler(req);
        if (handler == null) {
            Map<String, String> model = new HashMap<>();
            model.put("code", "404");
            model.put("message", "页面去火星了");
            porcessDispatchResult(req, resp, new GMModelAndView("404", model));
            return;
        }
        GMHandlerAdapter ha = getHandlerAdapter(handler);
        //这一步只是调用方法，得到返回值
        GMModelAndView mv = ha.handle(req, resp, handler);
        //这一步才是真的输出
        porcessDispatchResult(req, resp, mv);
    }

    private void porcessDispatchResult(HttpServletRequest request, HttpServletResponse response, GMModelAndView mv)
            throws Exception {
        //调用 viewResolver 的 resolverViewName() 方法
        if (null == mv) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }
        for (GMViewResolver viewResolver :
                this.viewResolvers) {
            GMView view = viewResolver.resolveViewName(mv.getViewName(), null);
            if (view != null) {
                view.render(mv.getModel(), request, response);
                return;
            }
        }
    }

    private GMHandlerAdapter getHandlerAdapter(GMHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        GMHandlerAdapter ha = this.handlerAdapters.get(handler);
        if (ha.supports(handler)) {
            return ha;
        }
        return null;
    }

    private GMHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (GMHandlerMapping handler :
                this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }
}
