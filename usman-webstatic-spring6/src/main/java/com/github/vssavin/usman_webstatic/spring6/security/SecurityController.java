package com.github.vssavin.usman_webstatic.spring6.security;

import com.github.vssavin.usmancore.config.ArgumentsProcessedNotifier;
import com.github.vssavin.usmancore.config.UsmanConfigurer;
import com.github.vssavin.usmancore.config.UsmanSecureServiceArgumentsHandler;
import com.github.vssavin.usmancore.security.SecureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Provides data for using secure algorithms.
 *
 * @author vssavin on 20.12.2023.
 */
@RestController
@RequestMapping("/usman/security")
class SecurityController implements ArgumentsProcessedNotifier {

    private final List<String> secureServiceNames;

    private final UsmanConfigurer usmanConfigurer;

    private SecureService secureService;

    @Autowired
    public SecurityController(UsmanConfigurer usmanConfigurer, List<SecureService> secureServices) {
        this.usmanConfigurer = usmanConfigurer;
        this.secureService = usmanConfigurer.getSecureService();
        this.secureServiceNames = secureServices.stream().map(Object::toString).toList();
    }

    @GetMapping(value = "/key")
    public ResponseEntity<String> key(HttpServletRequest request) {
        String addr = request.getRemoteAddr();
        return new ResponseEntity<>(secureService.getPublicKey(addr), HttpStatus.OK);
    }

    @GetMapping(value = "/algorithm")
    public ResponseEntity<String> algorithm() {
        return new ResponseEntity<>(secureService.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/algorithms")
    public ResponseEntity<List<String>> algorithms() {
        return new ResponseEntity<>(secureServiceNames, HttpStatus.OK);
    }

    @Override
    public void notifyArgumentsProcessed(Class<?> aClass) {
        if (aClass != null && UsmanSecureServiceArgumentsHandler.class.isAssignableFrom(aClass)) {
            this.secureService = usmanConfigurer.getSecureService();
        }
    }

}
