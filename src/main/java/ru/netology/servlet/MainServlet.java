package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("ru.netology");
    private PostController controller;

    @Override
    public void init() {
        controller = context.getBean("postController", PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals(GET) && path.equals("/api/posts")) {
                controller.all(resp);
                return;
            }
            if (method.equals(GET) && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = parseId(path);
                controller.getById(id, resp);
                return;
            }
            if (method.equals(POST) && path.equals("/api/posts")) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(DELETE) && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = parseId(path);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static Long parseId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}