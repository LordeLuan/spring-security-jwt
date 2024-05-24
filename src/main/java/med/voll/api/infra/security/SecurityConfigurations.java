package med.voll.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
	
	@Autowired
	private SecurityFilter securityFilter;

	// Desabilita o processo de autenticacao STATEFULL (via sessão do sistema) e permite que a autenticação seja personalizada
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf().disable()
	            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // nao guarda o status de autenticacao por sessao, toda nova requisicao deve autenticar novamente o usuario
	            .and().authorizeRequests()
	            .antMatchers(HttpMethod.POST, "/login").permitAll() // permite requisicao sem autenticacao ao endpoint /login
	            .anyRequest().authenticated() // qualquer outra requisicao deve ser autenticada
	            .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) // seta a ordem de execucao dos filtros, primeiro o filtro personalizado e depois o filtro padrao do spring
	            .build();
	}
	
	// utilizado pelo spring para injecao de dependencia da classe AuthenticationManager
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	// informa ao Spring qual o formato de criptografia da senha
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
