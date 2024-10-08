package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;

public class RegisterController extends AbstractController {

    private final ResourceController resourceController;

    public RegisterController() {
        this.resourceController = new ResourceController();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        resourceController.doGet(request.updatePath("register.html"), responseBuilder);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.Builder responseBuilder) {
        Map<String, String> body = request.extractUrlEncodedBody();
        if (isInvalidBody(body)) {
            responseBuilder.status(Status.BAD_REQUEST);
            return;
        }
        if (existsAccount(body.get("account"))) {
            responseBuilder.status(Status.CONFLICT);
            return;
        }
        saveUserAndRedirectHome(responseBuilder, body);
    }

    private boolean isInvalidBody(Map<String, String> body) {
        return !body.containsKey("account") ||
                !body.containsKey("password") ||
                !body.containsKey("email");
    }

    private boolean existsAccount(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    private void saveUserAndRedirectHome(HttpResponse.Builder responseBuilder, Map<String, String> body) {
        InMemoryUserRepository.save(new User(body));
        responseBuilder.status(Status.FOUND)
                .location("/index.html");
    }
}
