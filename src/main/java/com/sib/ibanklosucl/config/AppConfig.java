import com.sib.ibanklosucl.config.CustomSessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ServletListenerRegistrationBean<CustomSessionListener> sessionListener() {
        return new ServletListenerRegistrationBean<>(new CustomSessionListener());
    }
}
